local Players = game:GetService("Players")
local VideoManager = require(script.Parent:WaitForChild("PCModules"):WaitForChild("VideoManager"))
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")

local player = Players.LocalPlayer
local PlayerGui = player:WaitForChild("PlayerGui")
local Remotes = ReplicatedStorage:WaitForChild("Remotes")

local HasPC = Remotes:WaitForChild("HasPC")
local BuyPC = Remotes:WaitForChild("BuyPC")
local BuySlot = Remotes:WaitForChild("BuySlot")
local GetUnlockedSlots = Remotes:WaitForChild("GetUnlockedSlots")

-- Fonts
local fontBold = Enum.Font.GothamBold
local fontReg = Enum.Font.Gotham

-- Slot State
local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "locked", rank = "", timeLeft = 0, cpm = 0},
    {state = "locked", rank = "", timeLeft = 0, cpm = 0}
}
local slotGuis = {}

task.spawn(function()
    -- Ask server for actual slot info, which already accounts for unlocked slots AND timers
    local GetSlotData = Remotes:WaitForChild("GetSlotData")
    local ok, sData = pcall(function() return GetSlotData:InvokeServer() end)
    if ok and sData then
        for i = 1, 3 do
            if sData[tostring(i)] then
                vmSlotsData[i] = sData[tostring(i)]
            elseif sData[i] then
                vmSlotsData[i] = sData[i]
            end
            
            -- RESET ACTIVE VIDEOS
            if vmSlotsData[i] and vmSlotsData[i].state == "filled" then
                vmSlotsData[i].state = "empty"
                vmSlotsData[i].timeLeft = 0
                vmSlotsData[i].cpm = 0
                vmSlotsData[i].rank = ""
                pcall(function() Remotes:WaitForChild("SyncSlotData"):FireServer(i, vmSlotsData[i]) end)
            end
        end
    else
        -- Fallback
        local unlockedCount = GetUnlockedSlots:InvokeServer() or 1
        for i = 1, 3 do
            if i <= unlockedCount then
                if vmSlotsData[i].state == "locked" then
                    vmSlotsData[i].state = "empty"
                end
            end
        end
    end
end)

local SlotIncome = Remotes:WaitForChild("SlotIncome")
local GetUpgrades = Remotes:WaitForChild("GetUpgrades")
local passiveTimer = 0
local textEffectTimer = 0
local incomeAccumulator = 0

local editMultiplier = 1
local seoMultiplier = 1

task.spawn(function()
    while true do
        pcall(function()
            local upg = GetUpgrades:InvokeServer()
            if upg then
                editMultiplier = 1 + ((upg.EditSpeed or 0) * 0.05)
                seoMultiplier = 2 ^ (upg.SEOAlgorithm or 0)
            end
        end)
        task.wait(5)
    end
end)

RunService.Heartbeat:Connect(function(dt)
    passiveTimer = passiveTimer + dt
    textEffectTimer = textEffectTimer + dt
    local fireIncome = false
    local textEffect = false
    
    if passiveTimer >= 1 then
        passiveTimer = passiveTimer - 1
        fireIncome = true
    end
    if textEffectTimer >= 5 then
        textEffectTimer = textEffectTimer - 5
        textEffect = true
    end

    local totalIncome = 0
    for i = 1, 3 do
        local slot = vmSlotsData[i]
        if slot.state == "filled" then
            slot.timeLeft = slot.timeLeft - dt
            if slot.timeLeft <= 0 then
                slot.state = "empty"
                slot.timeLeft = 0
                slot.rank = ""
                slot.cpm = 0
                  pcall(function() Remotes:WaitForChild("SyncSlotData"):FireServer(i, slot) end)
                if slotGuis[i] then
                    local exp = Instance.new("TextLabel")
                    exp.Size = UDim2.new(1,0,1,0)
                    exp.BackgroundTransparency = 1
                    exp.Text = "EXPIRED"
                    exp.TextColor3 = Color3.new(1,0,0)
                    exp.TextSize = 24
                    exp.Font = Enum.Font.GothamBold
                    exp.ZIndex = 110
                    exp.Parent = slotGuis[i]
                    task.delay(1.5, function() if exp then exp:Destroy() end end)
                    
                    for _, c in ipairs(slotGuis[i]:GetChildren()) do
                        if c.Name == "FilledUI" then c.Visible = false end
                        if c.Name == "EmptyUI" then c.Visible = true end
                    end
                end
            else
                if fireIncome then
                    totalIncome = totalIncome + slot.cpm
                      if math.floor(slot.timeLeft) % 20 == 0 then
                          pcall(function() Remotes:WaitForChild("SyncSlotData"):FireServer(i, slot) end)
                      end
                    if slotGuis[i] then
                        local notif = Instance.new("TextLabel")
                        notif.Size = UDim2.new(1,0,0,30)
                        notif.Position = UDim2.new(0,0,0, -20)
                        notif.BackgroundTransparency = 1
                        notif.Text = "+"..tostring(math.floor(slot.cpm * editMultiplier)).." coins"
                        notif.TextColor3 = Color3.fromRGB(0,255,0)
                        notif.TextStrokeTransparency = 0
                        notif.TextSize = 16
                        notif.Font = Enum.Font.GothamBold
                        notif.ZIndex = 110
                        notif.Parent = slotGuis[i]
                        
                        local tw = TweenService:Create(notif, TweenInfo.new(2), {Position = UDim2.new(0,0,0,-60), TextTransparency = 1, TextStrokeTransparency = 1})
                        tw:Play()
                        game.Debris:AddItem(notif, 2)
                    end
                end
            end
        end
    end
    
    if fireIncome and totalIncome > 0 then
        incomeAccumulator = incomeAccumulator + totalIncome
        local fireAmt = math.floor(incomeAccumulator)
        if fireAmt > 0 then
            incomeAccumulator = incomeAccumulator - fireAmt
            SlotIncome:FireServer(fireAmt)
        end
    end
    
    for i = 1, 3 do
        local slot = vmSlotsData[i]
        if slot.state == "filled" and slotGuis[i] then
            local fill = slotGuis[i]:FindFirstChild("FilledUI")
            if fill then
                local barBg = fill:FindFirstChild("BarBg")
                if barBg then
                    local bar = barBg:FindFirstChild("Bar")
                    local pct = math.clamp(slot.timeLeft / 180, 0, 1)
                    if bar then bar.Size = UDim2.new(pct, 0, 1, 0) end
                end
            end
        end
    end
end)

-- Main GUI
local pcosGui = Instance.new("ScreenGui")
pcosGui.Name = "PCOS_Shell"
pcosGui.IgnoreGuiInset = true
pcosGui.ScreenInsets = Enum.ScreenInsets.None
pcosGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
pcosGui.ResetOnSpawn = false
pcosGui.DisplayOrder = 100
pcosGui.Enabled = false
pcosGui.Parent = PlayerGui

-- Main Container (for scaling animation)
local mainContainer = Instance.new("Frame")
mainContainer.Name = "MainContainer"
mainContainer.Size = UDim2.new(1, 0, 1, 0)
mainContainer.AnchorPoint = Vector2.new(0.5, 0.5)
mainContainer.Position = UDim2.new(0.5, 0, 0.5, 0)
mainContainer.BackgroundTransparency = 1
mainContainer.Parent = pcosGui

-- Desktop Wallpaper (Animated Abstract Waves)
local desktop = Instance.new("Frame")
desktop.Name = "Desktop"
desktop.Size = UDim2.new(1, 0, 1, 0)
desktop.BackgroundColor3 = Color3.fromRGB(12, 10, 20)
desktop.BorderSizePixel = 0
desktop.Parent = mainContainer
desktop.ClipsDescendants = true

-- Animated Gradient Background
local bgGradientFrame = Instance.new("Frame")
bgGradientFrame.Size = UDim2.new(2, 0, 2, 0)
bgGradientFrame.Position = UDim2.new(-0.5, 0, -0.5, 0)
bgGradientFrame.BorderSizePixel = 0
bgGradientFrame.BackgroundColor3 = Color3.new(1, 1, 1)
bgGradientFrame.Parent = desktop

local uiGradient = Instance.new("UIGradient")
uiGradient.Color = ColorSequence.new({
    ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 60, 100)),
    ColorSequenceKeypoint.new(0.33, Color3.fromRGB(150, 40, 255)),
    ColorSequenceKeypoint.new(0.66, Color3.fromRGB(40, 100, 255)),
    ColorSequenceKeypoint.new(1, Color3.fromRGB(0, 210, 255))
})
uiGradient.Rotation = 45
uiGradient.Parent = bgGradientFrame

-- Moving floating abstract circles
for i = 1, 4 do
    local orb = Instance.new("Frame")
    local size = math.random(400, 800)
    orb.Size = UDim2.new(0, size, 0, size)
    orb.AnchorPoint = Vector2.new(0.5, 0.5)
    orb.BackgroundColor3 = Color3.new(1, 1, 1)
    orb.BackgroundTransparency = 0.85
    orb.BorderSizePixel = 0
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0.5, 0)
    corner.Parent = orb
    
    local grad = Instance.new("UIGradient")
    grad.Transparency = NumberSequence.new({
        NumberSequenceKeypoint.new(0, 0),
        NumberSequenceKeypoint.new(1, 1)
    })
    grad.Rotation = math.random(0, 360)
    grad.Parent = orb
    
    orb.Parent = desktop
    
    -- Animation logic setup
    task.spawn(function()
        local t = math.random() * 100
        local speedX = (math.random() - 0.5) * 0.3
        local speedY = (math.random() - 0.5) * 0.3
        local radiusX = math.random(20, 40) / 100
        local radiusY = math.random(20, 40) / 100
        local centerX = math.random(20, 80) / 100
        local centerY = math.random(20, 80) / 100
        
        RunService.RenderStepped:Connect(function(dt)
            t = t + dt
            orb.Position = UDim2.new(
                centerX + math.sin(t * speedX) * radiusX, 0,
                centerY + math.cos(t * speedY) * radiusY, 0
            )
        end)
    end)
end

-- Subtle moving rotation for the background
task.spawn(function()
    local rot = 45
    RunService.RenderStepped:Connect(function(dt)
        rot = rot + (dt * 5)
        uiGradient.Rotation = rot % 360
    end)
end)

-- Windows 11 Style Taskbar
local taskbar = Instance.new("Frame")
taskbar.Name = "Taskbar"
taskbar.Size = UDim2.new(1, 0, 0.065, 0) -- Scales according to screen size!
taskbar.AnchorPoint = Vector2.new(0, 1)
taskbar.Position = UDim2.new(0, 0, 1, 0) -- FAR BOTTOM
taskbar.BackgroundColor3 = Color3.fromRGB(12, 12, 16)
taskbar.BackgroundTransparency = 0.05
taskbar.BorderSizePixel = 0
  taskbar.ZIndex = 100
  taskbar.Parent = mainContainer

local taskbarTopLine = Instance.new("Frame")
taskbarTopLine.Size = UDim2.new(1, 0, 0, 1)
taskbarTopLine.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
taskbarTopLine.BackgroundTransparency = 0.5
taskbarTopLine.BorderSizePixel = 0
taskbarTopLine.Parent = taskbar

-- Windows Start Button
local startBtn = Instance.new("TextButton")
startBtn.Name = "StartButton"
startBtn.Size = UDim2.new(1, 0, 1, 0) -- Scale dynamically 
local startAspect = Instance.new("UIAspectRatioConstraint")
startAspect.AspectRatio = 1
startAspect.Parent = startBtn
startBtn.Position = UDim2.new(0, 0, 0, 0)
startBtn.BackgroundTransparency = 1
startBtn.Text = ""
startBtn.Parent = taskbar

local startHover = Instance.new("Frame")
startHover.Size = UDim2.new(0, 40, 0, 40)
startHover.Position = UDim2.new(0.5, -20, 0.5, -20)
startHover.BackgroundColor3 = Color3.new(1, 1, 1)
startHover.BackgroundTransparency = 1
startHover.Parent = startBtn
local startHoverCorner = Instance.new("UICorner")
startHoverCorner.CornerRadius = UDim.new(0, 6)
startHoverCorner.Parent = startHover

-- Custom Start Logo
local startLogo = Instance.new("TextLabel")
startLogo.Size = UDim2.new(1, 0, 1, 0)
startLogo.BackgroundTransparency = 1
startLogo.Text = "▶"
startLogo.TextColor3 = Color3.fromRGB(200, 255, 255)
  startLogo.TextSize = 28
  startLogo.Parent = startBtn

  local logoStroke = Instance.new("UIStroke")
  logoStroke.Color = Color3.fromRGB(0, 180, 255)
  logoStroke.Thickness = 1.2
  logoStroke.Parent = startLogo

local logoGradient = Instance.new("UIGradient")
logoGradient.Color = ColorSequence.new(Color3.fromRGB(255, 100, 255), Color3.fromRGB(0, 255, 255))
logoGradient.Rotation = 45
logoGradient.Parent = startLogo

startBtn.MouseEnter:Connect(function() startHover.BackgroundTransparency = 0.9 end)
startBtn.MouseLeave:Connect(function() startHover.BackgroundTransparency = 1 end)
  -- Search Bar
  local searchBar = Instance.new("Frame")
  searchBar.Size = UDim2.new(0, 110, 0, 26)
  searchBar.Position = UDim2.new(0, 56, 0.5, -13)
  searchBar.BackgroundColor3 = Color3.fromRGB(40, 40, 50)
  searchBar.BackgroundTransparency = 0.5
  searchBar.BorderSizePixel = 0
  searchBar.Parent = taskbar
  local searchCorner = Instance.new("UICorner")
  searchCorner.CornerRadius = UDim.new(0, 13)
  searchCorner.Parent = searchBar

  local searchIcon = Instance.new("TextLabel")
  searchIcon.Size = UDim2.new(0, 24, 1, 0)
  searchIcon.Position = UDim2.new(0, 6, 0, 0)
  searchIcon.BackgroundTransparency = 1
  searchIcon.Text = "🔍"
  searchIcon.TextColor3 = Color3.new(1, 1, 1)
  searchIcon.TextSize = 12
  searchIcon.Parent = searchBar

  local searchBox = Instance.new("TextBox")
  searchBox.Size = UDim2.new(1, -30, 1, 0)
  searchBox.Position = UDim2.new(0, 26, 0, 0)
  searchBox.BackgroundTransparency = 1
  searchBox.Text = "Search..."
  searchBox.TextColor3 = Color3.fromRGB(220, 220, 225)
  searchBox.TextXAlignment = Enum.TextXAlignment.Left
  searchBox.TextSize = 12
  searchBox.Font = fontReg
  searchBox.Parent = searchBar  -- Open Apps Taskbar Container
  local taskbarApps = Instance.new("Frame")
  taskbarApps.Name = "TaskbarApps"
  taskbarApps.Size = UDim2.new(0.5, 0, 1, 0)
  taskbarApps.Position = UDim2.new(0.25, 0, 0, 0)
  taskbarApps.BackgroundTransparency = 1
  taskbarApps.Parent = taskbar
  
  local taskbarAppsLayout = Instance.new("UIListLayout")
  taskbarAppsLayout.FillDirection = Enum.FillDirection.Horizontal
  taskbarAppsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
  taskbarAppsLayout.VerticalAlignment = Enum.VerticalAlignment.Center
  taskbarAppsLayout.Padding = UDim.new(0, 8)
  taskbarAppsLayout.Parent = taskbarApps
-- System Tray (Right Side)
local sysTray = Instance.new("Frame")
sysTray.Size = UDim2.new(0, 250, 1, 0)
sysTray.Position = UDim2.new(1, -250, 0, 0)
sysTray.BackgroundTransparency = 1
sysTray.Parent = taskbar

local trayLayout = Instance.new("UIListLayout")
trayLayout.FillDirection = Enum.FillDirection.Horizontal
trayLayout.HorizontalAlignment = Enum.HorizontalAlignment.Right
trayLayout.VerticalAlignment = Enum.VerticalAlignment.Center
trayLayout.Padding = UDim.new(0, 4)
trayLayout.Parent = sysTray
local trayPad = Instance.new("UIPadding")
trayPad.PaddingRight = UDim.new(0, 10)
trayPad.Parent = sysTray

-- Clock/Date
local clockContainer = Instance.new("TextButton")
clockContainer.Name = "Clock"
clockContainer.Size = UDim2.new(0, 75, 0, 40)
clockContainer.BackgroundTransparency = 1
clockContainer.Text = ""
clockContainer.LayoutOrder = 3
clockContainer.Parent = sysTray

local clockHover = Instance.new("Frame")
clockHover.Size = UDim2.new(1, 0, 1, 0)
clockHover.BackgroundColor3 = Color3.new(1, 1, 1)
clockHover.BackgroundTransparency = 1
clockHover.Parent = clockContainer
local clockHoverCorner = Instance.new("UICorner")
clockHoverCorner.CornerRadius = UDim.new(0, 6)
clockHoverCorner.Parent = clockHover

local clockLabel = Instance.new("TextLabel")
clockLabel.Size = UDim2.new(1, 0, 1, 0)
clockLabel.BackgroundTransparency = 1
clockLabel.Text = "12:00 PM\n1/1/2026"
clockLabel.TextColor3 = Color3.new(1, 1, 1)
  clockLabel.TextSize = 13
  clockLabel.Font = fontBold
  clockLabel.TextXAlignment = Enum.TextXAlignment.Right
  clockLabel.Parent = clockContainer

  local clockStroke = Instance.new("UIStroke")
  clockStroke.Transparency = 0.2
  clockStroke.Thickness = 1.2
  clockStroke.Parent = clockLabel
clockContainer.MouseLeave:Connect(function() clockHover.BackgroundTransparency = 1 end)

RunService.Heartbeat:Connect(function()
    local date = os.date("*t")
    local suffix = date.hour >= 12 and "PM" or "AM"
    local hour = date.hour % 12
    if hour == 0 then hour = 12 end
    clockLabel.Text = string.format("%d:%02d %s\n%d/%d/%d", hour, date.min, suffix, date.month, date.day, date.year)
end)

-- System Icons (Wifi, Sound, Battery)
local iconsContainer = Instance.new("TextButton")
iconsContainer.Size = UDim2.new(0, 95, 0, 40)
iconsContainer.BackgroundTransparency = 1
iconsContainer.Text = ""
iconsContainer.LayoutOrder = 2
iconsContainer.Parent = sysTray

local iconsHover = Instance.new("Frame")
iconsHover.Size = UDim2.new(1, 0, 1, 0)
iconsHover.BackgroundColor3 = Color3.new(1, 1, 1)
iconsHover.BackgroundTransparency = 1
iconsHover.Parent = iconsContainer
local iconsHoverCorner = Instance.new("UICorner")
iconsHoverCorner.CornerRadius = UDim.new(0, 6)
iconsHoverCorner.Parent = iconsHover

local sysIconsLabel = Instance.new("TextLabel")
sysIconsLabel.Size = UDim2.new(1, -10, 1, 0)
sysIconsLabel.Position = UDim2.new(0, 0, 0, 0)
sysIconsLabel.BackgroundTransparency = 1
sysIconsLabel.Text = "∧   📶  🔊  🔋"
sysIconsLabel.TextColor3 = Color3.new(1, 1, 1)
  sysIconsLabel.TextSize = 17
  sysIconsLabel.Font = fontBold
  sysIconsLabel.TextXAlignment = Enum.TextXAlignment.Right
  sysIconsLabel.Parent = iconsContainer

  local iconStroke = Instance.new("UIStroke")
  iconStroke.Transparency = 0.2
  iconStroke.Thickness = 1.2
  iconStroke.Parent = sysIconsLabel
iconsContainer.MouseEnter:Connect(function() iconsHover.BackgroundTransparency = 0.9 end)
iconsContainer.MouseLeave:Connect(function() iconsHover.BackgroundTransparency = 1 end)

-- Buy Prompt Overlay
local buyPrompt = Instance.new("Frame")
buyPrompt.Name = "BuyPrompt"
buyPrompt.Size = UDim2.new(1, 0, 1, 0)
buyPrompt.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
buyPrompt.BackgroundTransparency = 0.5
buyPrompt.Visible = false
buyPrompt.ZIndex = 50
buyPrompt.Parent = mainContainer

local buyBox = Instance.new("Frame")
buyBox.Size = UDim2.new(0, 360, 0, 200)
buyBox.AnchorPoint = Vector2.new(0.5, 0.5)
buyBox.Position = UDim2.new(0.5, 0, 0.5, 0)
buyBox.BackgroundColor3 = Color3.fromRGB(30, 30, 35)
buyBox.BorderSizePixel = 0
buyBox.Parent = buyPrompt

local buyCorner = Instance.new("UICorner")
buyCorner.CornerRadius = UDim.new(0, 12)
buyCorner.Parent = buyBox

local buyStroke = Instance.new("UIStroke")
buyStroke.Color = Color3.fromRGB(60, 60, 70)
buyStroke.Thickness = 2
buyStroke.Parent = buyBox

local buyTitle = Instance.new("TextLabel")
buyTitle.Size = UDim2.new(1, 0, 0, 50)
buyTitle.Position = UDim2.new(0, 0, 0, 10)
buyTitle.BackgroundTransparency = 1
buyTitle.Text = "No PC Available!"
buyTitle.TextColor3 = Color3.new(1, 1, 1)
buyTitle.TextSize = 24
buyTitle.Font = fontBold
buyTitle.Parent = buyBox

local buyDesc = Instance.new("TextLabel")
buyDesc.Size = UDim2.new(1, -40, 0, 60)
buyDesc.Position = UDim2.new(0, 20, 0, 60)
buyDesc.BackgroundTransparency = 1
buyDesc.Text = "You need a PC to access PCOS.\nCost: 5,000 Coins"
buyDesc.TextColor3 = Color3.fromRGB(200, 200, 200)
buyDesc.TextSize = 16
buyDesc.TextWrapped = true
buyDesc.Font = fontReg
buyDesc.Parent = buyBox

local buyBtn = Instance.new("TextButton")
buyBtn.Size = UDim2.new(0, 160, 0, 44)
buyBtn.AnchorPoint = Vector2.new(0.5, 0)
buyBtn.Position = UDim2.new(0.5, 0, 1, -60)
buyBtn.BackgroundColor3 = Color3.fromRGB(0, 120, 215)
buyBtn.Text = "Buy PC"
buyBtn.TextColor3 = Color3.new(1, 1, 1)
buyBtn.TextSize = 18
buyBtn.Font = fontBold
buyBtn.Parent = buyBox

local buyBtnCorner = Instance.new("UICorner")
buyBtnCorner.CornerRadius = UDim.new(0, 8)
buyBtnCorner.Parent = buyBtn

local closeBuyBtn = Instance.new("TextButton")
closeBuyBtn.Size = UDim2.new(0, 30, 0, 30)
closeBuyBtn.Position = UDim2.new(1, -15, 0, -15)
closeBuyBtn.BackgroundColor3 = Color3.fromRGB(220, 50, 50)
closeBuyBtn.Text = "X"
closeBuyBtn.TextColor3 = Color3.new(1, 1, 1)
closeBuyBtn.Font = fontBold
closeBuyBtn.Parent = buyBox

local closeBuyCorner = Instance.new("UICorner")
closeBuyCorner.CornerRadius = UDim.new(1, 0)
closeBuyCorner.Parent = closeBuyBtn

-- Window Management
local windows = {}
local draggingWindow = nil
local maxWindowZIndex = 70

local function openWindow(appData)
    if windows[appData.name] then
        local win = windows[appData.name]
        win.frame.Visible = true
        maxWindowZIndex = maxWindowZIndex + 1
        win.frame.ZIndex = maxWindowZIndex
        return
    end

    maxWindowZIndex = maxWindowZIndex + 1

    local targetWidth = (appData.name == "Video Manager") and 1000 or 600
    local targetHeight = (appData.name == "Video Manager") and 550 or 400
    local winFrame = Instance.new("Frame")
    winFrame.Name = appData.name .. "Window"
    winFrame.Size = UDim2.new(0, targetWidth, 0, targetHeight)

    local winScale = Instance.new("UIScale")
    winScale.Name = "WinScale"
    winScale.Scale = 1
    winScale.Parent = winFrame

    local function updateWinScale()
        if appData.name ~= "Video Manager" then return end
        local cam = workspace.CurrentCamera
        if not cam then return end
        local vp = cam.ViewportSize
        if vp.X > 0 and vp.Y > 0 then
            local scaleX = (vp.X * 0.95) / targetWidth
            local scaleY = (vp.Y * 0.85) / targetHeight
            winScale.Scale = math.clamp(math.min(scaleX, scaleY), 0.3, 1)
        end
    end

    if appData.name == "Video Manager" then
        workspace:GetPropertyChangedSignal("CurrentCamera"):Connect(function()
            if workspace.CurrentCamera then
                workspace.CurrentCamera:GetPropertyChangedSignal("ViewportSize"):Connect(updateWinScale)
                updateWinScale()
            end
        end)
        if workspace.CurrentCamera then
            workspace.CurrentCamera:GetPropertyChangedSignal("ViewportSize"):Connect(updateWinScale)
        end
    end
    updateWinScale()

    winFrame.Position = UDim2.new(0.5, -(targetWidth * winScale.Scale)/2, 0.5, -(targetHeight * winScale.Scale)/2)
    winFrame.BackgroundColor3 = Color3.fromRGB(20, 20, 25)
    winFrame.BorderSizePixel = 0
    winFrame.ZIndex = maxWindowZIndex
    winFrame.ClipsDescendants = true
    winFrame.Parent = desktop
    
    local winCorner = Instance.new("UICorner")
    winCorner.CornerRadius = UDim.new(0, 8)
    winCorner.Parent = winFrame
    
    local winStroke = Instance.new("UIStroke")
    winStroke.Color = Color3.fromRGB(50, 50, 60)
    winStroke.Thickness = 1
    winStroke.Parent = winFrame

    -- Title Bar
    local titleBar = Instance.new("Frame")
    titleBar.Size = UDim2.new(1, 0, 0, 30)
    titleBar.BackgroundColor3 = Color3.fromRGB(30, 30, 35)
    titleBar.BorderSizePixel = 0
    titleBar.ZIndex = 71
    titleBar.Parent = winFrame

    local titleCorner = Instance.new("UICorner")
    titleCorner.CornerRadius = UDim.new(0, 8)
    titleCorner.Parent = titleBar
    
    local titleFix = Instance.new("Frame")
    titleFix.Size = UDim2.new(1, 0, 0, 8)
    titleFix.Position = UDim2.new(0, 0, 1, -8)
    titleFix.BackgroundColor3 = Color3.fromRGB(30, 30, 35)
    titleFix.BorderSizePixel = 0
    titleFix.ZIndex = 71
    titleFix.Parent = titleBar

    local titleLabel = Instance.new("TextLabel")
    titleLabel.Size = UDim2.new(1, 0, 1, 0)
    titleLabel.Position = UDim2.new(0, 0, 0, 0)
    titleLabel.BackgroundTransparency = 1
    titleLabel.Text = appData.icon .. " " .. appData.name
    titleLabel.TextColor3 = Color3.new(1, 1, 1)
    titleLabel.TextSize = 14
    titleLabel.Font = fontReg
    titleLabel.TextXAlignment = Enum.TextXAlignment.Center
    titleLabel.ZIndex = 72
    titleLabel.Parent = titleBar

    -- Controls
    local ctrlContainer = Instance.new("Frame")
    ctrlContainer.Size = UDim2.new(0, 120, 1, 0)
    ctrlContainer.Position = UDim2.new(1, -120, 0, 0)
    ctrlContainer.BackgroundTransparency = 1
    ctrlContainer.ZIndex = 72
    ctrlContainer.Parent = titleBar

    local ctrlLayout = Instance.new("UIListLayout")
    ctrlLayout.FillDirection = Enum.FillDirection.Horizontal
    ctrlLayout.HorizontalAlignment = Enum.HorizontalAlignment.Right
    ctrlLayout.SortOrder = Enum.SortOrder.LayoutOrder
    ctrlLayout.Parent = ctrlContainer
    
    local function createBtn(text, color, hoverColor)
        local btn = Instance.new("TextButton")
        btn.Size = UDim2.new(0, 40, 1, 0)
        btn.BackgroundColor3 = color
        btn.BackgroundTransparency = 0
        btn.Text = text
        btn.TextColor3 = Color3.new(1, 1, 1)
        btn.TextSize = 14
        btn.Font = fontReg
        btn.ZIndex = 72
        btn.Parent = ctrlContainer
        
        btn.MouseEnter:Connect(function() btn.BackgroundColor3 = hoverColor end)
        btn.MouseLeave:Connect(function() btn.BackgroundColor3 = color end)
        return btn
    end

    local minBtn = createBtn("—", Color3.fromRGB(40, 40, 45), Color3.fromRGB(60, 60, 70))
    minBtn.LayoutOrder = 1
    local maxBtn = createBtn("□", Color3.fromRGB(40, 40, 45), Color3.fromRGB(60, 60, 70))
    maxBtn.LayoutOrder = 2
    local closeBtn = createBtn("X", Color3.fromRGB(220, 50, 50), Color3.fromRGB(255, 80, 80))
    closeBtn.LayoutOrder = 3

    -- Window Content Container
    local contentFrame = Instance.new("Frame")
    contentFrame.Size = UDim2.new(1, 0, 1, -30)
    contentFrame.Position = UDim2.new(0, 0, 0, 30)
    contentFrame.BackgroundTransparency = 1
    contentFrame.ZIndex = 70
    contentFrame.Parent = winFrame

    -- Taskbar App Icon
    local taskIconBtn = Instance.new("TextButton")
    taskIconBtn.Size = UDim2.new(0, 32, 0, 32)
    taskIconBtn.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
    taskIconBtn.BackgroundTransparency = 0.9
    taskIconBtn.Text = appData.icon
    taskIconBtn.TextSize = 20
    taskIconBtn.Parent = taskbarApps

    local taskIconCorner = Instance.new("UICorner")
    taskIconCorner.CornerRadius = UDim.new(0, 6)
    taskIconCorner.Parent = taskIconBtn
    
    local activeIndicator = Instance.new("Frame")
    activeIndicator.Size = UDim2.new(0, 10, 0, 3)
    activeIndicator.AnchorPoint = Vector2.new(0.5, 0)
    activeIndicator.Position = UDim2.new(0.5, 0, 1, 2)
    activeIndicator.BackgroundColor3 = Color3.fromRGB(0, 120, 215)
    activeIndicator.Parent = taskIconBtn
    
    local activeCorner = Instance.new("UICorner")
    activeCorner.CornerRadius = UDim.new(1, 0)
    activeCorner.Parent = activeIndicator

    -- App Specific Content
    if appData.name == "Video Manager" then
        VideoManager.create(winFrame, contentFrame, {
            Remotes = Remotes, player = player, formatViews = formatViews,
            playShakeAnimation = playShakeAnimation, maxWindowZIndex = maxWindowZIndex,
            fontBold = fontBold, fontReg = fontReg, floatText = floatText,
            spawnStarBurst = spawnStarBurst, vmSlotsData = vmSlotsData, slotGuis = slotGuis,
            TweenService = TweenService, HasPC = HasPC, updateViewsUI = updateViewsUI,
            BuyPC = BuyPC, SlotIncome = SlotIncome, BuySlot = BuySlot
        })
    elseif appData.name == "PC Settings" then
        local scroll = Instance.new("ScrollingFrame")
        scroll.Size = UDim2.new(1, -16, 1, -16)
        scroll.Position = UDim2.new(0, 8, 0, 8)
        scroll.BackgroundTransparency = 1
        scroll.ZIndex = 72
        scroll.ScrollBarThickness = 6
        scroll.ScrollBarImageColor3 = Color3.fromRGB(80, 70, 110)
        scroll.Parent = contentFrame

        local layout = Instance.new("UIListLayout")
        layout.Padding = UDim.new(0, 10)
        layout.Parent = scroll

        local GetPCUpgrades = Remotes:FindFirstChild("GetPCUpgrades")
        local PurchasePCUpgrade = Remotes:FindFirstChild("PurchasePCUpgrade")
        local GetPrestigeInfo = Remotes:FindFirstChild("GetPrestigeInfo")

        local pcUpgrades = {}
        pcall(function() pcUpgrades = GetPCUpgrades:InvokeServer() or {} end)

        local prestigeInfo = {}
        pcall(function() prestigeInfo = GetPrestigeInfo:InvokeServer() or {} end)
        local prestigeCount = prestigeInfo.prestigeCount or 0
        local bonusPercent = prestigeInfo.bonusPercent or 0
        local maxLevelBase = 5

        local function fmt(n)
            n = math.floor(n or 0)
            if n >= 1e6 then return string.format("%.1fM", n/1e6)
            elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
            else return tostring(n) end
        end

        local upgradeData = {
            {
                id = "seoBoost",
                name = "🔍 SEO Algorithm",
                desc = "Passive income earns more views per second",
                effectDesc = "Each level: +15% bonus views from slots",
                color = Color3.fromRGB(99, 102, 241),
                baseCost = 2000,
                costMult = 1.8,
            },
            {
                id = "adRevenue",
                name = "💰 Ad Revenue",
                desc = "Earn more coins every time you post a video",
                effectDesc = "Each level: +20% coins from posting",
                color = Color3.fromRGB(34, 197, 94),
                baseCost = 3000,
                costMult = 2.0,
            },
            {
                id = "brandDeal",
                name = "🤝 Brand Deals",
                desc = "Sponsorships pay out bigger coin rewards",
                effectDesc = "Each level: +25% sponsor income",
                color = Color3.fromRGB(236, 72, 153),
                baseCost = 5000,
                costMult = 2.2,
            },
            {
                id = "uploadSpeed",
                name = "🚀 Upload Speed",
                desc = "Video slots earn coins faster over time",
                effectDesc = "Each level: +10% slot income speed",
                color = Color3.fromRGB(59, 130, 246),
                baseCost = 2500,
                costMult = 1.9,
            },
            {
                id = "retention",
                name = "👀 Audience Retention",
                desc = "Videos get recommended to more people",
                effectDesc = "Each level: +30% views per post",
                color = Color3.fromRGB(251, 146, 60),
                baseCost = 4000,
                costMult = 2.1,
            },
        }

        local header = Instance.new("TextLabel")
        header.Size = UDim2.new(1, 0, 0, 30)
        header.BackgroundTransparency = 1
        header.Text = "⚡ PC Upgrades"
        header.TextColor3 = Color3.new(1, 1, 1)
        header.Font = fontBold
        header.TextSize = 20
        header.ZIndex = 72
        header.TextXAlignment = Enum.TextXAlignment.Left
        header.Parent = scroll

        local prestigeBanner = Instance.new("Frame")
        prestigeBanner.Size = UDim2.new(1, 0, 0, 36)
        prestigeBanner.BackgroundColor3 = Color3.fromRGB(40, 20, 60)
        prestigeBanner.ZIndex = 72
        prestigeBanner.Parent = scroll
        Instance.new("UICorner", prestigeBanner).CornerRadius = UDim.new(0, 10)

        local prestigeLbl = Instance.new("TextLabel")
        prestigeLbl.Size = UDim2.new(1, 0, 0, 20)
        prestigeLbl.BackgroundTransparency = 1
        prestigeLbl.Text = "Prestige " .. prestigeCount .. " | +" .. bonusPercent .. "% coins bonus"
        prestigeLbl.TextColor3 = prestigeCount > 0 and Color3.fromRGB(180, 80, 255) or Color3.fromRGB(150, 150, 155)
        prestigeLbl.Font = fontBold
        prestigeLbl.TextSize = 12
        prestigeLbl.ZIndex = 73
        prestigeLbl.TextXAlignment = Enum.TextXAlignment.Center
        prestigeLbl.Parent = prestigeBanner

        local prestigeSubLbl = Instance.new("TextLabel")
        prestigeSubLbl.Size = UDim2.new(1, 0, 0, 14)
        prestigeSubLbl.Position = UDim2.new(0, 0, 1, -14)
        prestigeSubLbl.BackgroundTransparency = 1
        prestigeSubLbl.Text = "Cost scales +15% per prestige | Max Lv: " .. (maxLevelBase + prestigeCount)
        prestigeSubLbl.TextColor3 = Color3.fromRGB(120, 120, 140)
        prestigeSubLbl.Font = fontReg
        prestigeSubLbl.TextSize = 10
        prestigeSubLbl.ZIndex = 73
        prestigeSubLbl.TextXAlignment = Enum.TextXAlignment.Center
        prestigeSubLbl.Parent = prestigeBanner

        local function refreshUpgradeUI(upg, currentLevel, maxLevel, cost, buyBtn, levelLbl)
            local coins = 0
            local ls = player:FindFirstChild("leaderstats")
            if ls and ls:FindFirstChild("Coins") then
                coins = ls.Coins.Value
            end
            levelLbl.Text = "Lv " .. currentLevel .. " / " .. maxLevel
            if currentLevel >= maxLevel then
                buyBtn.Text = "MAX"
                buyBtn.BackgroundColor3 = Color3.fromRGB(40, 160, 60)
                buyBtn.TextColor3 = Color3.fromRGB(200, 255, 200)
                buyBtn.Active = false
            elseif coins >= cost then
                buyBtn.Text = fmt(cost)
                buyBtn.BackgroundColor3 = upg.color
                buyBtn.TextColor3 = Color3.new(1, 1, 1)
                buyBtn.Active = true
            else
                buyBtn.Text = fmt(cost)
                buyBtn.BackgroundColor3 = Color3.fromRGB(50, 50, 60)
                buyBtn.TextColor3 = Color3.fromRGB(120, 120, 130)
                buyBtn.Active = false
            end
        end

        for _, upg in ipairs(upgradeData) do
            local currentLevel = pcUpgrades[upg.id] or 0
            local prestigeBonus = 1 + prestigeCount * 0.15
            local maxLevel = maxLevelBase + prestigeCount
            local cost = math.floor((upg.baseCost * prestigeBonus) * (upg.costMult ^ currentLevel))

            local card = Instance.new("Frame")
            card.Size = UDim2.new(1, 0, 0, 110)
            card.BackgroundColor3 = Color3.fromRGB(22, 22, 28)
            card.ZIndex = 72
            card.Parent = scroll
            Instance.new("UICorner", card).CornerRadius = UDim.new(0, 10)

            local leftStripe = Instance.new("Frame")
            leftStripe.Size = UDim2.new(0, 5, 1, -16)
            leftStripe.Position = UDim2.new(0, 0, 0, 8)
            leftStripe.BackgroundColor3 = upg.color
            leftStripe.ZIndex = 72
            leftStripe.Parent = card
            Instance.new("UICorner", leftStripe).CornerRadius = UDim.new(0, 4)

            local nameLbl = Instance.new("TextLabel")
            nameLbl.Size = UDim2.new(1, -100, 0, 22)
            nameLbl.Position = UDim2.new(0, 14, 0, 8)
            nameLbl.BackgroundTransparency = 1
            nameLbl.Text = upg.name
            nameLbl.TextColor3 = Color3.new(1, 1, 1)
            nameLbl.Font = fontBold
            nameLbl.TextSize = 15
            nameLbl.ZIndex = 72
            nameLbl.TextXAlignment = Enum.TextXAlignment.Left
            nameLbl.Parent = card

            local levelBadge = Instance.new("Frame")
            levelBadge.Size = UDim2.new(0, 0, 0, 22)
            levelBadge.AutomaticSize = Enum.AutomaticSize.X
            levelBadge.Position = UDim2.new(1, -90, 0, 8)
            levelBadge.BackgroundColor3 = upg.color
            levelBadge.BackgroundTransparency = 0.6
            levelBadge.ZIndex = 72
            levelBadge.Parent = card
            Instance.new("UICorner", levelBadge).CornerRadius = UDim.new(0, 11)

            local levelLbl = Instance.new("TextLabel")
            levelLbl.Size = UDim2.new(1, -10, 1, 0)
            levelLbl.Position = UDim2.new(0, 5, 0, 0)
            levelLbl.BackgroundTransparency = 1
            levelLbl.Text = "Lv " .. currentLevel .. " / " .. maxLevel
            levelLbl.TextColor3 = Color3.new(1, 1, 1)
            levelLbl.Font = fontBold
            levelLbl.TextSize = 11
            levelLbl.ZIndex = 73
            levelLbl.TextXAlignment = Enum.TextXAlignment.Center
            levelLbl.Parent = levelBadge

            local descLbl = Instance.new("TextLabel")
            descLbl.Size = UDim2.new(1, -100, 0, 16)
            descLbl.Position = UDim2.new(0, 14, 0, 30)
            descLbl.BackgroundTransparency = 1
            descLbl.Text = upg.desc
            descLbl.TextColor3 = Color3.fromRGB(150, 155, 160)
            descLbl.Font = fontReg
            descLbl.TextSize = 11
            descLbl.ZIndex = 72
            descLbl.TextXAlignment = Enum.TextXAlignment.Left
            descLbl.TextWrapped = true
            descLbl.Parent = card

            local effectLbl = Instance.new("TextLabel")
            effectLbl.Size = UDim2.new(1, -100, 0, 14)
            effectLbl.Position = UDim2.new(0, 14, 0, 48)
            effectLbl.BackgroundTransparency = 1
            effectLbl.Text = upg.effectDesc
            effectLbl.TextColor3 = upg.color
            effectLbl.Font = fontBold
            effectLbl.TextSize = 11
            effectLbl.ZIndex = 72
            effectLbl.TextXAlignment = Enum.TextXAlignment.Left
            effectLbl.Parent = card

            local prestigeInfoLbl = Instance.new("TextLabel")
            prestigeInfoLbl.Size = UDim2.new(1, -100, 0, 14)
            prestigeInfoLbl.Position = UDim2.new(0, 14, 0, 64)
            prestigeInfoLbl.BackgroundTransparency = 1
            prestigeInfoLbl.Text = prestigeCount > 0 and ("Prestige bonus: x" .. string.format("%.2f", prestigeBonus) .. " | +" .. prestigeCount .. " max levels") or ""
            prestigeInfoLbl.TextColor3 = Color3.fromRGB(160, 100, 220)
            prestigeInfoLbl.Font = fontReg
            prestigeInfoLbl.TextSize = 10
            prestigeInfoLbl.ZIndex = 72
            prestigeInfoLbl.TextXAlignment = Enum.TextXAlignment.Left
            prestigeInfoLbl.Parent = card

            local buyBtn = Instance.new("TextButton")
            buyBtn.Size = UDim2.new(0, 80, 0, 32)
            buyBtn.Position = UDim2.new(1, -90, 0, 68)
            buyBtn.BackgroundColor3 = currentLevel >= maxLevel and Color3.fromRGB(40, 160, 60) or upg.color
            buyBtn.Text = currentLevel >= maxLevel and "MAX" or fmt(cost)
            buyBtn.TextColor3 = Color3.new(1, 1, 1)
            buyBtn.Font = fontBold
            buyBtn.TextSize = 13
            buyBtn.ZIndex = 72
            buyBtn.Parent = card
            Instance.new("UICorner", buyBtn).CornerRadius = UDim.new(0, 8)
            buyBtn.Active = currentLevel < maxLevel

            if currentLevel < maxLevel then
                buyBtn.MouseButton1Click:Connect(function()
                    local coins = 0
                    local ls = player:FindFirstChild("leaderstats")
                    if ls and ls:FindFirstChild("Coins") then
                        coins = ls.Coins.Value
                    end
                    if coins < cost then
                        buyBtn.Text = "Need more!"
                        buyBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
                        task.delay(1.2, function()
                            if buyBtn and buyBtn.Parent then
                                refreshUpgradeUI(upg, currentLevel, maxLevel, cost, buyBtn, levelLbl)
                            end
                        end)
                        return
                    end
                    buyBtn.Text = "..."
                    buyBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 70)
                    buyBtn.Active = false
                    local result = PurchasePCUpgrade:InvokeServer(upg.id)
                    if result then
                        local newLevel = pcUpgrades[upg.id] + 1
                        pcUpgrades[upg.id] = newLevel
                        currentLevel = newLevel
                        maxLevel = maxLevelBase + prestigeCount
                        local newPrestigeInfo = {}
                        pcall(function() newPrestigeInfo = GetPrestigeInfo:InvokeServer() or {} end)
                        local newPrestigeCount = newPrestigeInfo.prestigeCount or prestigeCount
                        local newPrestigeBonus = 1 + newPrestigeCount * 0.15
                        maxLevel = maxLevelBase + newPrestigeCount
                        local newCost = math.floor((upg.baseCost * newPrestigeBonus) * (upg.costMult ^ newLevel))
                        refreshUpgradeUI(upg, newLevel, maxLevel, newCost, buyBtn, levelLbl)
                    else
                        buyBtn.Text = "Failed"
                        buyBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
                        task.delay(1.2, function()
                            if buyBtn and buyBtn.Parent then
                                refreshUpgradeUI(upg, currentLevel, maxLevel, cost, buyBtn, levelLbl)
                            end
                        end)
                    end
                end)
            end
        end

        scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 24)
        layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
            scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 24)
        end)
    elseif appData.name == "My Computer" then
        local specsList = Instance.new("UIListLayout")
        specsList.Padding = UDim.new(0, 15)
        specsList.HorizontalAlignment = Enum.HorizontalAlignment.Center
        specsList.VerticalAlignment = Enum.VerticalAlignment.Center
        specsList.Parent = contentFrame
        
        local pName = Instance.new("TextLabel")
        pName.Size = UDim2.new(1, 0, 0, 40)
        pName.BackgroundTransparency = 1
        pName.Text = "User: " .. player.Name
        pName.TextColor3 = Color3.new(1, 1, 1)
        pName.Font = fontBold
        pName.TextSize = 24
        pName.ZIndex = 72
        pName.Parent = contentFrame
        
        local osVer = Instance.new("TextLabel")
        osVer.Size = UDim2.new(1, 0, 0, 30)
        osVer.BackgroundTransparency = 1
        osVer.Text = "GoingViral OS 1.0"
        osVer.TextColor3 = Color3.fromRGB(200, 200, 200)
        osVer.Font = fontReg
        osVer.TextSize = 18
        osVer.ZIndex = 72
        osVer.Parent = contentFrame
        
        local pcSpec = Instance.new("TextLabel")
        pcSpec.Size = UDim2.new(1, 0, 0, 30)
        pcSpec.BackgroundTransparency = 1
        pcSpec.Text = "Budget PC"
        pcSpec.TextColor3 = Color3.fromRGB(150, 150, 150)
        pcSpec.Font = fontReg
        pcSpec.TextSize = 18
        pcSpec.ZIndex = 72
        pcSpec.Parent = contentFrame
    end

    windows[appData.name] = {
        frame = winFrame,
        taskIcon = taskIconBtn
    }

    -- Bring Window to Front on Click
    winFrame.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            maxWindowZIndex = maxWindowZIndex + 1
            winFrame.ZIndex = maxWindowZIndex
        end
    end)

    -- Dragging Logic
    local dragging = false
    local dragInput, dragOffset

    titleBar.InputBegan:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
            maxWindowZIndex = maxWindowZIndex + 1
            winFrame.ZIndex = maxWindowZIndex

            if draggingWindow == nil then
                draggingWindow = winFrame
                dragging = true
                dragOffset = Vector2.new(input.Position.X - winFrame.AbsolutePosition.X, input.Position.Y - winFrame.AbsolutePosition.Y)
                
                local connection
                connection = input.Changed:Connect(function()
                    if input.UserInputState == Enum.UserInputState.End then
                        dragging = false
                        if draggingWindow == winFrame then
                            draggingWindow = nil
                        end
                        if connection then
                            connection:Disconnect()
                        end
                    end
                end)
            end
        end
    end)
    
    titleBar.InputChanged:Connect(function(input)
        if input.UserInputType == Enum.UserInputType.MouseMovement or input.UserInputType == Enum.UserInputType.Touch then
            dragInput = input
        end
    end)
    
    UserInputService.InputChanged:Connect(function(input)
        if input == dragInput and dragging then
            local maxX = math.max(0, desktop.AbsoluteSize.X - winFrame.AbsoluteSize.X)
            local maxY = math.max(0, desktop.AbsoluteSize.Y - winFrame.AbsoluteSize.Y - taskbar.AbsoluteSize.Y)
            local px = math.clamp(input.Position.X - dragOffset.X, 0, maxX)
            local py = math.clamp(input.Position.Y - dragOffset.Y, 0, maxY)
            winFrame.Position = UDim2.new(0, px, 0, py)
        end
    end)

    -- Window Controls Actions
    closeBtn.MouseButton1Click:Connect(function()
        winFrame:Destroy()
        taskIconBtn:Destroy()
        windows[appData.name] = nil
    end)
    
    minBtn.MouseButton1Click:Connect(function()
        winFrame.Visible = false
    end)
    
    local isMaximized = false
    local preMaxPos, preMaxSize
    maxBtn.MouseButton1Click:Connect(function()
        if isMaximized then
            winFrame.Size = preMaxSize
            winFrame.Position = preMaxPos
            isMaximized = false
        else
            preMaxSize = winFrame.Size
            preMaxPos = winFrame.Position
            winFrame.Size = UDim2.new(1, 0, 0.935, 0)
            winFrame.Position = UDim2.new(0, 0, 0, 0)
            isMaximized = true
        end
    end)

    taskIconBtn.MouseButton1Click:Connect(function()
        if winFrame.Visible then
            winFrame.Visible = false
        else
            winFrame.Visible = true
            winFrame.ZIndex = winFrame.ZIndex + 10
        end
    end)

    -- Scale in animation
    winFrame.Size = UDim2.new(0, 0, 0, 0)
    TweenService:Create(winFrame, TweenInfo.new(0.2, Enum.EasingStyle.Back, Enum.EasingDirection.Out), {
        Size = UDim2.new(0, targetWidth, 0, targetHeight)
    }):Play()
end

-- Desktop Icons Setup
local apps = {
    {name = "Video Manager", icon = "🎬"},
    {name = "PC Settings",   icon = "⚙️"},
    {name = "My Computer",   icon = "💻"},
}

local selectedIcon = nil
local lastClickTime = 0

-- Un-highlight when clicking desktop background
desktop.InputBegan:Connect(function(input)
    if input.UserInputType == Enum.UserInputType.MouseButton1 then
        if selectedIcon then
            selectedIcon.BackgroundTransparency = 1
            selectedIcon = nil
        end
    end
end)

for i, appData in ipairs(apps) do
    local iconBtn = Instance.new("TextButton")
    iconBtn.Name = appData.name:gsub(" ", "") .. "Icon"
    iconBtn.Size = UDim2.new(0, 80, 0, 100)
    local col = (i - 1) % 3
    local row = math.floor((i - 1) / 3)
    iconBtn.Position = UDim2.new(0, 40 + col * 100, 0, 70 + row * 115)
    iconBtn.BackgroundTransparency = 1
    iconBtn.BackgroundColor3 = Color3.fromRGB(0, 120, 215)
    iconBtn.Text = ""
    iconBtn.ZIndex = 55
    iconBtn.Parent = desktop
    
    local corner = Instance.new("UICorner")
    corner.CornerRadius = UDim.new(0, 6)
    corner.Parent = iconBtn

    local emojiLabel = Instance.new("TextLabel")
    emojiLabel.Size = UDim2.new(1, 0, 0, 50)
    emojiLabel.Position = UDim2.new(0, 0, 0, 10)
    emojiLabel.BackgroundTransparency = 1
    emojiLabel.Text = appData.icon
    emojiLabel.TextSize = 48
    emojiLabel.ZIndex = 56
    emojiLabel.Parent = iconBtn

    local nameLabel = Instance.new("TextLabel")
    nameLabel.Size = UDim2.new(1, 0, 0, 40)
    nameLabel.Position = UDim2.new(0, 0, 0, 60)
    nameLabel.BackgroundTransparency = 1
    nameLabel.Text = appData.name
    nameLabel.TextColor3 = Color3.new(1, 1, 1)
    nameLabel.TextSize = 12
    nameLabel.Font = fontReg
    nameLabel.TextWrapped = true
    nameLabel.TextYAlignment = Enum.TextYAlignment.Top
    nameLabel.ZIndex = 56
    nameLabel.Parent = iconBtn

    local textStroke = Instance.new("UIStroke")
    textStroke.Color = Color3.fromRGB(0, 0, 0)
    textStroke.Thickness = 1
    textStroke.Transparency = 0.5
    textStroke.Parent = nameLabel

    iconBtn.MouseButton1Click:Connect(function()
        local now = tick()
        
        if selectedIcon and selectedIcon ~= iconBtn then
            selectedIcon.BackgroundTransparency = 1
        end
        
        iconBtn.BackgroundTransparency = 0.6
        
        if selectedIcon == iconBtn and (now - lastClickTime) < 0.5 then
            -- Double click detected
              openWindow(appData)
            -- Highlight blinks briefly on double click
            iconBtn.BackgroundTransparency = 0.4
            task.delay(0.1, function()
                if selectedIcon == iconBtn then
                    iconBtn.BackgroundTransparency = 0.6
                end
            end)
        end
        
        selectedIcon = iconBtn
        lastClickTime = now
    end)
end

-- UI State Management
local isOpen = false
local isAnimating = false

local function setPCOpen(open)
    if isOpen == open or isAnimating then return end
    isAnimating = true
    isOpen = open
    
    if open then
        pcosGui.Enabled = true

        -- Check ownership
        -- TODO: re-enable PC ownership check before launch
        -- local ownsPC = HasPC:InvokeServer()
        local ownsPC = true
        if ownsPC then
            buyPrompt.Visible = false
            desktop.Visible = true
        else
            buyPrompt.Visible = true
            desktop.Visible = false
        end

        mainContainer.Size = UDim2.new(0.8, 0, 0.8, 0)
        local tweenInfo = TweenInfo.new(0.3, Enum.EasingStyle.Back, Enum.EasingDirection.Out)
        local openTween = TweenService:Create(mainContainer, tweenInfo, { Size = UDim2.new(1, 0, 1, 0) })
        openTween:Play()
        openTween.Completed:Wait()
    else
        local tweenInfo = TweenInfo.new(0.2, Enum.EasingStyle.Quad, Enum.EasingDirection.In)
        local closeTween = TweenService:Create(mainContainer, tweenInfo, { Size = UDim2.new(0.8, 0, 0.8, 0) })
        closeTween:Play()
        closeTween.Completed:Wait()
        pcosGui.Enabled = false
    end
    isAnimating = false
end

-- Input Listeners
UserInputService.InputBegan:Connect(function(input, processed)
    if processed then return end
    
    if input.KeyCode == Enum.KeyCode.F then
        setPCOpen(not isOpen)
    elseif input.KeyCode == Enum.KeyCode.Escape then
        if isOpen then
            setPCOpen(false)
        end
    end
end)

-- Buy PC Logic
buyBtn.MouseButton1Click:Connect(function()
    buyBtn.Text = "Purchasing..."
    local success = BuyPC:InvokeServer()
    if success then
        buyBtn.Text = "Success!"
        buyBtn.BackgroundColor3 = Color3.fromRGB(50, 200, 50)
        task.wait(1)
        buyPrompt.Visible = false
        desktop.Visible = true
        buyBtn.Text = "Buy PC"
        buyBtn.BackgroundColor3 = Color3.fromRGB(0, 120, 215)
    else
        buyBtn.Text = "Not enough Coins!"
        buyBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
        task.wait(1.5)
        buyBtn.Text = "Buy PC"
        buyBtn.BackgroundColor3 = Color3.fromRGB(0, 120, 215)
    end
end)

closeBuyBtn.MouseButton1Click:Connect(function()
    setPCOpen(false)
end)

print("[PCOS] Loaded successfully")





