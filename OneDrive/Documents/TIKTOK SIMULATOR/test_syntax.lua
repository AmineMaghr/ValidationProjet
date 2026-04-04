local Players = game:GetService("Players")
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")

local player = Players.LocalPlayer
local PlayerGui = player:WaitForChild("PlayerGui")
local Remotes = ReplicatedStorage:WaitForChild("Remotes")

local HasPC = Remotes:WaitForChild("HasPC")
local BuyPC = Remotes:WaitForChild("BuyPC")

-- Fonts
local fontBold = Enum.Font.GothamBold
local fontReg = Enum.Font.Gotham

-- Slot State
local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0}
}
local slotGuis = {}
local SlotIncome = Remotes:WaitForChild("SlotIncome")
local passiveTimer = 0
local textEffectTimer = 0
local incomeAccumulator = 0

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
                    if slotGuis[i] then
                        local notif = Instance.new("TextLabel")
                        notif.Size = UDim2.new(1,0,0,30)
                        notif.Position = UDim2.new(0,0,0, -20)
                        notif.BackgroundTransparency = 1
                        notif.Text = "+"..tostring(slot.cpm).." coins"
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
    winFrame.Position = UDim2.new(0.5, -targetWidth/2, 0.5, -targetHeight/2)
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
        print("[VideoManager] Layout loaded")

          -- =========================================================================
          -- ? GORGEOUS VIDEO APP LAYOUT ?
          -- =========================================================================

          -- 1) MAIN CONTAINER (Sleek deep purple/dark aesthetic)
          local vmMaster = Instance.new("Frame")
          vmMaster.Name = "VideoAppMaster"
          vmMaster.Size = UDim2.new(1, 0, 1, 0)
          vmMaster.BackgroundColor3 = Color3.fromRGB(18, 16, 26) -- Rich dark indigo
          vmMaster.BorderSizePixel = 0
          vmMaster.ZIndex = 50
          vmMaster.Parent = contentFrame
          vmMaster.Visible = false

          -- SLOT SELECTION SCREEN
          local slotSelectionScreen = Instance.new("Frame")
          slotSelectionScreen.Name = "SlotSelectionScreen"
          slotSelectionScreen.Size = UDim2.new(1, 0, 1, 0)
          slotSelectionScreen.BackgroundColor3 = Color3.fromRGB(15, 15, 20)
          slotSelectionScreen.ZIndex = 60
          slotSelectionScreen.Parent = contentFrame
          
          local pName = game.Players.LocalPlayer.Name
          local headerContainer = Instance.new("Frame")
          headerContainer.Size = UDim2.new(1, 0, 0, 40)
          headerContainer.Position = UDim2.new(0, 0, 0, 15)
          headerContainer.BackgroundTransparency = 1
          headerContainer.Parent = slotSelectionScreen
          
          local hLayout = Instance.new("UIListLayout")
          hLayout.FillDirection = Enum.FillDirection.Horizontal
          hLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          hLayout.VerticalAlignment = Enum.VerticalAlignment.Center
          hLayout.Padding = UDim.new(0, 10)
          hLayout.SortOrder = Enum.SortOrder.LayoutOrder
          hLayout.Parent = headerContainer

          local pfp = Instance.new("ImageLabel")
          pfp.Size = UDim2.new(0, 40, 0, 40)
          pfp.BackgroundTransparency = 1
          pfp.LayoutOrder = 1
          local userId = game.Players.LocalPlayer.UserId
          pfp.Image = game.Players:GetUserThumbnailAsync(userId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size420x420)
          pfp.ZIndex = 61
          Instance.new("UICorner", pfp).CornerRadius = UDim.new(1, 0)
          pfp.Parent = headerContainer

          local ssTitle = Instance.new("TextLabel")
          ssTitle.Size = UDim2.new(0, 0, 0, 40)
          ssTitle.AutomaticSize = Enum.AutomaticSize.X
          ssTitle.BackgroundTransparency = 1
          ssTitle.Text = pName .. "'s Channel"
          ssTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
          ssTitle.Font = Enum.Font.GothamBold
          ssTitle.TextSize = 36
          ssTitle.LayoutOrder = 2
          ssTitle.ZIndex = 61
          ssTitle.Parent = headerContainer
          
          local ssSub = Instance.new("TextLabel")
          ssSub.Size = UDim2.new(1, 0, 0, 20)
          ssSub.Position = UDim2.new(0, 0, 0, 65)
          ssSub.BackgroundTransparency = 1
          ssSub.Text = "Select a slot to upload a video"
          ssSub.TextColor3 = Color3.fromRGB(180, 180, 180)
          ssSub.Font = Enum.Font.Gotham
          ssSub.TextSize = 18
          ssSub.ZIndex = 61
          ssSub.Parent = slotSelectionScreen
          
          local slotsContainer = Instance.new("Frame")
          slotsContainer.Size = UDim2.new(1, 0, 1, -60)
          slotsContainer.Position = UDim2.new(0, 0, 0, 70)
          slotsContainer.BackgroundTransparency = 1
          slotsContainer.ZIndex = 61
          slotsContainer.Parent = slotSelectionScreen
          
          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100
          local resetGameUI = function() end
                    local slotsLayout = Instance.new("UIListLayout")
          slotsLayout.FillDirection = Enum.FillDirection.Horizontal
          slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder
          slotsLayout.Padding = UDim.new(0, 20)
          slotsLayout.Parent = slotsContainer
          
          local function renderSlots()
              for _, c in ipairs(slotsContainer:GetChildren()) do
                  if c:IsA("Frame") then c:Destroy() end
              end
              for i = 1, 3 do
                  local slotData = vmSlotsData[i]
                  local card = Instance.new("Frame")
                  card.Name = "SlotCard_" .. i
                  card.Size = UDim2.new(0, 280, 0, 350)
                  card.BackgroundColor3 = Color3.fromRGB(25, 25, 30)
                  card.ZIndex = 62
                  card.Parent = slotsContainer
                  Instance.new("UICorner", card).CornerRadius = UDim.new(0, 12)
                  
                  local btn = Instance.new("TextButton")
                  btn.Size = UDim2.new(1, 0, 1, 0)
                  btn.BackgroundTransparency = 1
                  btn.Text = ""
                  btn.ZIndex = 65
                  btn.Parent = card
                  
                  if slotData.state == "empty" then
                      local stroke = Instance.new("UIStroke")
                      stroke.Color = Color3.fromRGB(80, 80, 90)
                      stroke.Thickness = 2
                      stroke.LineJoinMode = Enum.LineJoinMode.Round
                      stroke.Parent = card
                      
                      local plus = Instance.new("TextLabel")
                      plus.Size = UDim2.new(1, 0, 0, 80)
                      plus.Position = UDim2.new(0, 0, 0.5, -50)
                      plus.BackgroundTransparency = 1
                      plus.Text = "+"
                      plus.TextColor3 = Color3.fromRGB(150, 150, 160)
                      plus.Font = Enum.Font.GothamBold
                      plus.TextSize = 80
                      plus.ZIndex = 63
                      plus.Parent = card
                      
                      local lbl = Instance.new("TextLabel")
                      lbl.Size = UDim2.new(1, 0, 0, 30)
                      lbl.Position = UDim2.new(0, 0, 0.5, 15)
                      lbl.BackgroundTransparency = 1
                      lbl.Text = "Empty  Click to Upload"
                      lbl.TextColor3 = Color3.fromRGB(150, 150, 160)
                      lbl.Font = Enum.Font.Gotham
                      lbl.TextSize = 16
                      lbl.ZIndex = 63
                      lbl.Parent = card
                      
                      btn.MouseButton1Click:Connect(function()
                            print("[Game] Clicked Empty Slot!")
                            slotSelectionScreen.Visible = false
                            vmMaster.Visible = true
                            if resetGameUI then resetGameUI() end
                        end)
                  else
                      local thumb = Instance.new("Frame")
                      thumb.Size = UDim2.new(1, -20, 0, 115)
                      thumb.Position = UDim2.new(0, 10, 0, 10)
                      thumb.BackgroundColor3 = Color3.fromRGB(40, 30, 60)
                      thumb.ZIndex = 63
                      thumb.Parent = card
                      Instance.new("UICorner", thumb).CornerRadius = UDim.new(0, 8)
                       
                       local playIcon = Instance.new("ImageLabel")
                       playIcon.Size = UDim2.new(0, 40, 0, 40)
                       playIcon.Position = UDim2.new(0.5, -20, 0.5, -20)
                       playIcon.BackgroundTransparency = 1
                       playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                       playIcon.ImageTransparency = 0.3
                       playIcon.ZIndex = 64
                       playIcon.Parent = thumb
                       
                       local playIcon = Instance.new("ImageLabel")
                       playIcon.Size = UDim2.new(0, 40, 0, 40)
                       playIcon.Position = UDim2.new(0.5, -20, 0.5, -20)
                       playIcon.BackgroundTransparency = 1
                       playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                       playIcon.ImageTransparency = 0.3
                       playIcon.ZIndex = 64
                       playIcon.Parent = thumb
                      
                      local title = Instance.new("TextLabel")
                      title.Size = UDim2.new(1, -20, 0, 25)
                      title.Position = UDim2.new(0, 10, 0, 130)
                      title.BackgroundTransparency = 1
                      title.Text = "Video #" .. i
                      title.TextColor3 = Color3.fromRGB(255, 255, 255)
                      title.Font = Enum.Font.GothamBold
                      title.TextSize = 16
                      title.TextXAlignment = Enum.TextXAlignment.Left
                      title.ZIndex = 63
                      title.Parent = card
                      
                      local rankBadge = Instance.new("TextLabel")
                      rankBadge.Size = UDim2.new(0, 24, 0, 24)
                      rankBadge.Position = UDim2.new(1, -34, 0, 130)
                      if slotData.rank == "S" then rankBadge.BackgroundColor3 = Color3.fromRGB(255,215,0)
                      elseif slotData.rank == "A" or slotData.rank == "B" then rankBadge.BackgroundColor3 = Color3.fromRGB(100,255,100)
                      else rankBadge.BackgroundColor3 = Color3.fromRGB(255,100,100) end
                      rankBadge.Text = slotData.rank
                      rankBadge.TextColor3 = Color3.fromRGB(0,0,0)
                      rankBadge.Font = Enum.Font.GothamBold
                      rankBadge.TextSize = 14
                      rankBadge.ZIndex = 63
                      rankBadge.Parent = card
                      Instance.new("UICorner", rankBadge).CornerRadius = UDim.new(1,0)
                      
                      local coins = Instance.new("TextLabel")
                      coins.Size = UDim2.new(1, -20, 0, 20)
                      coins.Position = UDim2.new(0, 10, 0, 155)
                      coins.BackgroundTransparency = 1
                      coins.Text = slotData.cpm .. " coins/sec"
                       coins.Name = "CoinsLbl"
                      coins.TextColor3 = Color3.fromRGB(80, 220, 80)
                      coins.Font = Enum.Font.Gotham
                      coins.TextSize = 14
                      coins.TextXAlignment = Enum.TextXAlignment.Left
                      coins.ZIndex = 63
                      coins.Parent = card
                      
                      local live = Instance.new("TextLabel")
                      live.Size = UDim2.new(0, 80, 0, 20)
                      live.Position = UDim2.new(0, 10, 1, -25)
                      live.BackgroundTransparency = 1
                      live.Name = "LiveLbl"
                       local m = math.floor(slotData.timeLeft / 60)
                       local s = math.floor(slotData.timeLeft % 60)
                       live.Text = string.format("? %d:%02d", m, s)
                       live.TextColor3 = Color3.fromRGB(255, 60, 60)
                      live.Font = Enum.Font.GothamBold
                      live.TextSize = 12
                      live.TextXAlignment = Enum.TextXAlignment.Left
                      live.ZIndex = 63
                      live.Parent = card
                      
                      local timerBg = Instance.new("Frame")
                       timerBg.Name = "TimerBg"
                       timerBg.Name = "TimerBg"
                      timerBg.Size = UDim2.new(1, -20, 0, 6)
                      timerBg.Position = UDim2.new(0, 10, 1, -8)
                      timerBg.BackgroundColor3 = Color3.fromRGB(40, 40, 40)
                      timerBg.ZIndex = 63
                      timerBg.Parent = card
                      Instance.new("UICorner", timerBg).CornerRadius = UDim.new(1,0)
                      
                      local timerFill = Instance.new("Frame")
                      local pct = math.clamp(slotData.timeLeft / 180, 0, 1)
                      timerFill.Size = UDim2.new(pct, 0, 1, 0)
                      timerFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                      timerFill.Name = "ProgFill"
                       timerFill.Name = "ProgFill"
                       timerFill.ZIndex = 64
                       timerFill.Parent = timerBg
                      Instance.new("UICorner", timerFill).CornerRadius = UDim.new(1,0)
                  end
              end
          end
          renderSlots()
          print("[SlotSelection] Loaded")
          

          -- 2) TOP NAVBAR (Glassmorphism feel)
          local topBar = Instance.new("Frame")
          topBar.Name = "TopNav"
          topBar.Size = UDim2.new(1, 0, 0, 60)
          topBar.Position = UDim2.new(0, 0, 0, 0)
          topBar.BackgroundColor3 = Color3.fromRGB(28, 25, 40)
          topBar.BackgroundTransparency = 0.2
          topBar.BorderSizePixel = 0
          topBar.ZIndex = 51
          topBar.Parent = vmMaster
          
          local topBarStroke = Instance.new("UIStroke")
          topBarStroke.Color = Color3.fromRGB(50, 45, 70)
          topBarStroke.Thickness = 1
          topBarStroke.Parent = topBar

          -- App Logo (Gradient Text)
          local logoWrapper = Instance.new("Frame")
          logoWrapper.Size = UDim2.new(0, 200, 1, 0)
          logoWrapper.BackgroundTransparency = 1
          logoWrapper.Position = UDim2.new(0, 20, 0, 0)
          logoWrapper.ZIndex = 52
          logoWrapper.Parent = topBar

          local ytLogo = Instance.new("TextLabel")
          ytLogo.Name = "LogoText"
          ytLogo.Size = UDim2.new(1, 0, 1, 0)
          ytLogo.BackgroundTransparency = 1
          ytLogo.Text = "▶ GoingViral Pro"
          ytLogo.TextColor3 = Color3.fromRGB(255, 255, 255)
          ytLogo.Font = Enum.Font.GothamBlack
          ytLogo.TextSize = 22
          ytLogo.TextXAlignment = Enum.TextXAlignment.Left
          ytLogo.ZIndex = 53
          ytLogo.Parent = logoWrapper

          local logoGradient = Instance.new("UIGradient")
          logoGradient.Color = ColorSequence.new({
            ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 60, 100)),   -- Vibrant pink
            ColorSequenceKeypoint.new(1, Color3.fromRGB(255, 170, 50))    -- Bright orange
          })
          logoGradient.Parent = ytLogo

          -- Top Right Icons
          local rightIcons = Instance.new("TextLabel")
          rightIcons.Size = UDim2.new(0, 120, 1, 0)
          rightIcons.Position = UDim2.new(1, -140, 0, 0)
          rightIcons.BackgroundTransparency = 1
          rightIcons.Text = "🔔   ✨   👤"
          rightIcons.TextColor3 = Color3.fromRGB(230, 230, 255)
          rightIcons.Font = Enum.Font.Gotham
          rightIcons.TextSize = 18
          rightIcons.TextXAlignment = Enum.TextXAlignment.Right
          rightIcons.ZIndex = 52
          rightIcons.Parent = topBar

          -- 3) MAIN CONTENT LAYOUT
          local mainBody = Instance.new("Frame")
          mainBody.Name = "MainBody"
          mainBody.Size = UDim2.new(1, 0, 1, -60)
          mainBody.Position = UDim2.new(0, 0, 0, 60)
          mainBody.BackgroundTransparency = 1
          mainBody.ZIndex = 51
          mainBody.Parent = vmMaster

          -- ================= LEFT SIDE (VIDEO PLAYER) =================
          local leftSide = Instance.new("Frame")
          leftSide.Name = "LeftSideVideo"
          leftSide.Size = UDim2.new(0.65, 0, 1, 0)
          leftSide.Position = UDim2.new(0, 0, 0, 0)
          leftSide.BackgroundTransparency = 1
          leftSide.ZIndex = 52
          leftSide.Parent = mainBody

          -- Beautiful Video Player Canvas
          local vidPlayer = Instance.new("Frame")
          vidPlayer.Name = "VideoPlayerRect"
          vidPlayer.Size = UDim2.new(1, -40, 0, 320)
          vidPlayer.Position = UDim2.new(0, 20, 0, 20)
          vidPlayer.BackgroundColor3 = Color3.fromRGB(10, 9, 15)
          vidPlayer.ZIndex = 53
          vidPlayer.Parent = leftSide
          
          local vidCorner = Instance.new("UICorner")
          vidCorner.CornerRadius = UDim.new(0, 16)
          vidCorner.Parent = vidPlayer
          
          local vidStroke = Instance.new("UIStroke")
          vidStroke.Color = Color3.fromRGB(40, 35, 60)
          vidStroke.Thickness = 2
          vidStroke.Parent = vidPlayer

          -- Vibrant Thumbnail Gradient
          local thumbnail = Instance.new("Frame")
          thumbnail.Size = UDim2.new(1, 0, 1, 0)
          thumbnail.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
          thumbnail.ZIndex = 54
          thumbnail.Parent = vidPlayer
          local thumbCorner = Instance.new("UICorner")
          thumbCorner.CornerRadius = UDim.new(0, 16)
          thumbCorner.Parent = thumbnail
          local thumbGrad = Instance.new("UIGradient")
          thumbGrad.Color = ColorSequence.new({
            ColorSequenceKeypoint.new(0, Color3.fromRGB(20, 10, 30)),
            ColorSequenceKeypoint.new(1, Color3.fromRGB(45, 15, 60))
          })
          thumbGrad.Rotation = 45
          thumbGrad.Parent = thumbnail

          -- Big Play Button (Neon glowing effect)
          local playBtnOuter = Instance.new("Frame")
          playBtnOuter.Size = UDim2.new(0, 80, 0, 80)
          playBtnOuter.Position = UDim2.new(0.5, -40, 0.5, -40)
          playBtnOuter.BackgroundColor3 = Color3.fromRGB(255, 40, 80)
          playBtnOuter.ZIndex = 55
          playBtnOuter.Parent = vidPlayer
          local pbCorner = Instance.new("UICorner")
          pbCorner.CornerRadius = UDim.new(1, 0)
          pbCorner.Parent = playBtnOuter
          
          local playLabel = Instance.new("TextLabel")
          playLabel.Size = UDim2.new(1, 0, 1, 0)
          playLabel.BackgroundTransparency = 1
          playLabel.Text = "?"
          playLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
          playLabel.Font = Enum.Font.GothamBlack
          playLabel.TextSize = 36
          playLabel.ZIndex = 56
          playLabel.Parent = playBtnOuter

          -- Video Title
          local vidTitle = Instance.new("TextLabel")
          vidTitle.Name = "VideoTitle"
          vidTitle.Size = UDim2.new(1, -40, 0, 35)
          vidTitle.Position = UDim2.new(0, 25, 0, 350)
          vidTitle.BackgroundTransparency = 1
          vidTitle.Text = "My Awesome Video! ✨"
          vidTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
          vidTitle.Font = Enum.Font.GothamBlack
          vidTitle.TextSize = 26
          vidTitle.TextXAlignment = Enum.TextXAlignment.Left
          vidTitle.ZIndex = 53
          vidTitle.Parent = leftSide

          -- Views Counter (with a cool badge look)
          local viewsBadge = Instance.new("Frame")
          viewsBadge.Size = UDim2.new(0, 110, 0, 26)
          viewsBadge.Position = UDim2.new(0, 25, 0, 390)
          viewsBadge.BackgroundColor3 = Color3.fromRGB(40, 210, 130)
          viewsBadge.ZIndex = 53
          viewsBadge.Parent = leftSide
          local vbCorner = Instance.new("UICorner")
          vbCorner.CornerRadius = UDim.new(0, 6)
          vbCorner.Parent = viewsBadge

          local vidViews = Instance.new("TextLabel")
          vidViews.Size = UDim2.new(1, 0, 1, 0)
          vidViews.BackgroundTransparency = 1
          vidViews.Text = "👀  1,000 views"
          vidViews.TextColor3 = Color3.fromRGB(15, 40, 25)
          vidViews.Font = Enum.Font.GothamBold
          vidViews.TextSize = 14
          vidViews.ZIndex = 54
          vidViews.Parent = viewsBadge

          -- Video Health/Hype Meter
          local hpTitle = Instance.new("TextLabel")
          hpTitle.Size = UDim2.new(0, 100, 0, 20)
          hpTitle.Position = UDim2.new(0, 150, 0, 393)
          hpTitle.BackgroundTransparency = 1
          hpTitle.Text = "HYPE METER 🔥"
          hpTitle.TextColor3 = Color3.fromRGB(200, 190, 220)
          hpTitle.Font = Enum.Font.GothamBold
          hpTitle.TextSize = 14
          hpTitle.TextXAlignment = Enum.TextXAlignment.Left
          hpTitle.ZIndex = 53
          hpTitle.Parent = leftSide

          local hpBarBG = Instance.new("Frame")
          hpBarBG.Name = "HealthBarBG"
          hpBarBG.Size = UDim2.new(1, -280, 0, 14)
          hpBarBG.Position = UDim2.new(0, 270, 0, 396)
          hpBarBG.BackgroundColor3 = Color3.fromRGB(25, 20, 35)
          hpBarBG.ZIndex = 53
          hpBarBG.Parent = leftSide
          local hpBgCorner = Instance.new("UICorner")
          hpBgCorner.CornerRadius = UDim.new(1, 0)
          hpBgCorner.Parent = hpBarBG
          local hpStroke = Instance.new("UIStroke")
          hpStroke.Color = Color3.fromRGB(50, 40, 70)
          hpStroke.Parent = hpBarBG

          local hpBarFill = Instance.new("Frame")
          hpBarFill.Name = "HealthBarFill"
          hpBarFill.Size = UDim2.new(1, 0, 1, 0)
          hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
          hpBarFill.ZIndex = 54
          hpBarFill.Parent = hpBarBG
          local hpFillCorner = Instance.new("UICorner")
          hpFillCorner.CornerRadius = UDim.new(1, 0)
          hpFillCorner.Parent = hpBarFill
          
          local hpGradient = Instance.new("UIGradient")
          hpGradient.Color = ColorSequence.new({
            ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 200, 0)),    -- Gold
            ColorSequenceKeypoint.new(0.5, Color3.fromRGB(255, 0, 100)),  -- Hot Pink
            ColorSequenceKeypoint.new(1, Color3.fromRGB(150, 0, 255))     -- Purple
          })
          hpGradient.Parent = hpBarFill

          -- ================= RIGHT SIDE (COMMENTS) =================
          local rightSide = Instance.new("Frame")
          rightSide.Name = "RightSideComments"
          rightSide.Size = UDim2.new(0.35, -20, 1, -20)
          rightSide.Position = UDim2.new(0.65, 0, 0, 10)
          rightSide.BackgroundColor3 = Color3.fromRGB(26, 24, 38)
          rightSide.ZIndex = 52
          rightSide.Parent = mainBody

          local rsCorner = Instance.new("UICorner")
          rsCorner.CornerRadius = UDim.new(0, 16)
          rsCorner.Parent = rightSide
          
          local rsStroke = Instance.new("UIStroke")
          rsStroke.Color = Color3.fromRGB(45, 40, 65)
          rsStroke.Thickness = 2
          rsStroke.Parent = rightSide

          -- Comment Section Title (Cute badge design)
          local titleArea = Instance.new("Frame")
          titleArea.Size = UDim2.new(1, 0, 0, 60)
          titleArea.BackgroundTransparency = 1
          titleArea.ZIndex = 53
          titleArea.Parent = rightSide
          
          local cmtsTitle = Instance.new("TextLabel")
          cmtsTitle.Name = "CommentsTitle"
          cmtsTitle.Size = UDim2.new(1, -40, 1, 0)
          cmtsTitle.Position = UDim2.new(0, 20, 0, 0)
          cmtsTitle.BackgroundTransparency = 1
          cmtsTitle.Text = "💬 Live Comments"
          cmtsTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
          cmtsTitle.Font = Enum.Font.GothamBlack
          cmtsTitle.TextSize = 20
          cmtsTitle.TextXAlignment = Enum.TextXAlignment.Left
          cmtsTitle.ZIndex = 54
          cmtsTitle.Parent = titleArea
          
          local divider = Instance.new("Frame")
          divider.Size = UDim2.new(1, 0, 0, 2)
          divider.Position = UDim2.new(0, 0, 1, -2)
          divider.BackgroundColor3 = Color3.fromRGB(40, 35, 55)
          divider.BorderSizePixel = 0
          divider.ZIndex = 54
          divider.Parent = titleArea

          -- Comment Area ScrollFrame
          local cmtScroll = Instance.new("ScrollingFrame")
          cmtScroll.Name = "CommentsContainer"
          cmtScroll.Size = UDim2.new(1, -20, 1, -70)
          cmtScroll.Position = UDim2.new(0, 10, 0, 75)
          cmtScroll.BackgroundTransparency = 1
          cmtScroll.CanvasSize = UDim2.new(0, 0, 0, 0)
          cmtScroll.ScrollBarThickness = 6
          cmtScroll.ScrollBarImageColor3 = Color3.fromRGB(80, 70, 110)
          cmtScroll.ZIndex = 53
          cmtScroll.ClipsDescendants = true -- Prevents half-cutoff bottom elements from falling out of frame
          cmtScroll.Parent = rightSide
            local cmtPadding = Instance.new("UIPadding")
            cmtPadding.PaddingTop = UDim.new(0, 10)
            cmtPadding.PaddingBottom = UDim.new(0, 10) -- Added bottom padding so the last comment isn't flat against the wall
            cmtPadding.Parent = cmtScroll



          local cmtListLayout = Instance.new("UIListLayout")
          cmtListLayout.SortOrder = Enum.SortOrder.LayoutOrder
          cmtListLayout.Padding = UDim.new(0, 12)
          cmtListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
            cmtListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
            cmtListLayout.Parent = cmtScroll

            -- Comment Spawning Logic
            local goodComments = {
                "W video!",
                "this goes hard 🔥",
                "instant subscribe",
                "bro ate fr",
                "algorithm brought me here",
                "this slaps"
            }
            local badComments = {
                "this is trash 💀",
                "ratio + L",
                "who asked",
                "mid content",
                "fell off bro",
                "unsubscribed",
                "worst video ever",
                "delete your channel"
            }

            local commentCount = 0
            local lastShiftTime = 0
            
            local function formatViews(v)
                -- Helper to add commas
                local formatted = tostring(v)
                while true do
                    local str, k = string.gsub(formatted, "^(-?%d+)(%d%d%d)", '%1,%2')
                    formatted = str
                    if k == 0 then break end
                end
                return string.format("👀  %s views", formatted)
            end

            local function playShakeAnimation()
                if not thumbnail then return end
                task.spawn(function()
                    for i=1, 6 do
                        local offset = (i%2 == 0) and 3 or -3
                        TweenService:Create(thumbnail, TweenInfo.new(0.05), {Position = UDim2.new(0, offset, 0, 0)}):Play()
                        task.wait(0.05)
                    end
                    TweenService:Create(thumbnail, TweenInfo.new(0.05), {Position = UDim2.new(0, 0, 0, 0)}):Play()
                end)
            end

            local function floatText(textStr, color, parentFrame)
                local lbl = Instance.new("TextLabel")
                lbl.Text = textStr
                lbl.TextColor3 = color
                lbl.BackgroundTransparency = 1
                lbl.Font = Enum.Font.GothamBlack
                lbl.TextSize = 25
                lbl.Size = UDim2.new(0, 100, 0, 40)
                -- Start exactly in the middle of current parent, slightly offset
                lbl.AnchorPoint = Vector2.new(0.5, 0.5)
                lbl.Position = UDim2.new(0.5, 0, 0.5, 0)
                lbl.ZIndex = 99
                lbl.Parent = parentFrame

                TweenService:Create(lbl, TweenInfo.new(1, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), {
                    Position = UDim2.new(0.5, 0, 0, -40),
                    TextTransparency = 1
                }):Play()

                game.Debris:AddItem(lbl, 1)
            end

            local function spawnStarBurst(parentFrame)
                for i=1, 5 do
                    local star = Instance.new("TextLabel")
                    star.Text = "⭐"
                    star.BackgroundTransparency = 1
                    star.TextSize = 20
                    star.Size = UDim2.new(0, 30, 0, 30)
                    star.AnchorPoint = Vector2.new(0.5, 0.5)
                    star.Position = UDim2.new(0.5, 0, 0.5, 0)
                    star.ZIndex = 99
                    star.Parent = parentFrame

                    local angle = math.rad((360 / 5) * i)
                    local dist = 60
                    local tox = math.cos(angle) * dist
                    local toy = math.sin(angle) * dist

                    TweenService:Create(star, TweenInfo.new(0.6, Enum.EasingStyle.Back, Enum.EasingDirection.Out), {
                        Position = UDim2.new(0.5, tox, 0.5, toy),
                        TextTransparency = 1,
                        Rotation = math.random(-90, 90)
                    }):Play()

                    game.Debris:AddItem(star, 0.6)
                end
            end

            local function updateViewsUI(change)
                totalViews = math.max(0, totalViews + change)
                vidViews.Text = formatViews(totalViews)
                
                if totalViews < 500 then
                    vidViews.TextColor3 = Color3.fromRGB(255, 60, 60)
                elseif totalViews > 1200 then
                    vidViews.TextColor3 = Color3.fromRGB(255, 215, 0)
                else
                    vidViews.TextColor3 = Color3.fromRGB(255, 255, 255)
                end
            end

            local function updateHealthUI(change)
                totalHealth = math.clamp(totalHealth + change, 0, 100)
                local pct = totalHealth / 100

                TweenService:Create(hpBarFill, TweenInfo.new(0.3), {
                    Size = UDim2.new(pct, 0, 1, 0)
                }):Play()

                -- Force override gradient by destroying it
                for _, child in ipairs(hpBarFill:GetChildren()) do
                    if child:IsA("UIGradient") then
                        child:Destroy()
                    end
                end

                if pct > 0.6 then
                    hpBarFill.BackgroundColor3 = Color3.fromRGB(80, 220, 80)
                elseif pct > 0.3 then
                    hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 170, 50)
                else
                    hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                end
            end


            local function endGame()
                gameRunning = false
                if cmtScroll.Parent then
                    -- Clear existing comments
                    for _, c in ipairs(cmtScroll:GetChildren()) do
                        if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                            c:Destroy()
                        end
                    end
                    
                    local rank = "F"
                    local rtColor = Color3.fromRGB(255, 50, 50)
                    local rtCpm = 20
                    if totalViews >= 1600 then
                        rank = "S"; rtColor = Color3.fromRGB(255, 215, 0); rtCpm = 180
                    elseif totalViews >= 1400 then
                        rank = "A"; rtColor = Color3.fromRGB(50, 255, 50); rtCpm = 120
                    elseif totalViews >= 1200 then
                        rank = "B"; rtColor = Color3.fromRGB(50, 150, 255); rtCpm = 80
                    elseif totalViews >= 900 then
                        rank = "C"; rtColor = Color3.fromRGB(150, 150, 150); rtCpm = 50
                    end
                    print("[Game] Ended with views: " .. totalViews)
                    
                    -- Screen
                    local resScreen = Instance.new("Frame")
                    resScreen.Size = UDim2.new(1, 0, 1, 0)
                    resScreen.BackgroundColor3 = Color3.fromRGB(18, 16, 26)
                    resScreen.ZIndex = 100
                    resScreen.Parent = contentFrame
                    
                    local ytTop = Instance.new("Frame")
                    ytTop.Size = UDim2.new(1, 0, 0, 60)
                    ytTop.BackgroundColor3 = Color3.fromRGB(28, 25, 40)
                    ytTop.BorderSizePixel = 0
                    ytTop.ZIndex = 101
                    ytTop.Parent = resScreen
                    
                    local rTitle = Instance.new("TextLabel")
                    rTitle.Size = UDim2.new(1, 0, 1, 0)
                    rTitle.BackgroundTransparency = 1
                    rTitle.Text = "Video Performance"
                    rTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
                    rTitle.Font = Enum.Font.GothamBlack
                    rTitle.TextSize = 24
                    rTitle.ZIndex = 102
                    rTitle.Parent = ytTop

                    local rViews = Instance.new("TextLabel")
                    rViews.Size = UDim2.new(1, 0, 0, 60)
                    rViews.Position = UDim2.new(0, 0, 0, 100)
                    rViews.BackgroundTransparency = 1
                    rViews.Text = totalViews .. " Views"
                    rViews.TextColor3 = rtColor
                    rViews.Font = Enum.Font.GothamBlack
                    rViews.TextSize = 64
                    rViews.ZIndex = 102
                    rViews.Parent = resScreen
                    
                    local chartBg = Instance.new("Frame")
                    chartBg.Size = UDim2.new(0, 400, 0, 150)
                    chartBg.Position = UDim2.new(0.5, -200, 0, 180)
                    chartBg.BackgroundTransparency = 1
                    chartBg.ZIndex = 101
                    chartBg.Parent = resScreen
                    
                    local barWidth = 400 / 10
                    for i=1, 10 do
                        local barHeight = math.random(30, 130)
                        if i == 10 then barHeight = math.clamp(totalViews / 15, 30, 150) end
                        local bar = Instance.new("Frame")
                        bar.Size = UDim2.new(0, barWidth - 4, 0, barHeight)
                        bar.Position = UDim2.new(0, (i-1)*barWidth + 2, 1, -barHeight)
                        bar.BackgroundColor3 = rtColor
                        bar.ZIndex = 102
                        bar.Parent = chartBg
                        Instance.new("UICorner", bar).CornerRadius = UDim.new(0, 4)
                    end
                    
                    local rRankBadge = Instance.new("TextLabel")
                    rRankBadge.Size = UDim2.new(0, 100, 0, 100)
                    rRankBadge.Position = UDim2.new(0.5, -50, 0, 360)
                    rRankBadge.BackgroundColor3 = rtColor
                    rRankBadge.Text = rank
                    rRankBadge.TextColor3 = Color3.fromRGB(20, 20, 20)
                    rRankBadge.Font = Enum.Font.GothamBlack
                    rRankBadge.TextSize = 60
                    rRankBadge.ZIndex = 102
                    rRankBadge.Parent = resScreen
                    local rc = Instance.new("UICorner")
                    rc.CornerRadius = UDim.new(1,0)
                    rc.Parent = rRankBadge

                    local rEarn = Instance.new("TextLabel")
                    rEarn.Size = UDim2.new(1, 0, 0, 30)
                    rEarn.Position = UDim2.new(0, 0, 0, 480)
                    rEarn.BackgroundTransparency = 1
                    rEarn.Text = "Slot earnings: " .. rtCpm .. " coins/sec"
                    rEarn.TextColor3 = Color3.fromRGB(200, 200, 200)
                    rEarn.Font = Enum.Font.GothamMedium
                    rEarn.TextSize = 20
                    rEarn.ZIndex = 102
                    rEarn.Parent = resScreen

                    task.delay(3, function()
                        for i = 1, 3 do
                            if vmSlotsData[i] and vmSlotsData[i].state == "empty" then
                                vmSlotsData[i].state = "filled"
                                vmSlotsData[i].rank = rank
                                vmSlotsData[i].cpm = rtCpm
                                vmSlotsData[i].timeLeft = 180
                                
                                if slotGuis[i] then
                                    for _, c in ipairs(slotGuis[i]:GetChildren()) do
                                        if c.Name == "EmptyUI" then c.Visible = false end
                                        if c.Name == "FilledUI" then c.Visible = true end
                                    end
                                    
                                    local fUI = slotGuis[i]:FindFirstChild("FilledUI")
                                    if fUI then
                                        local title = fUI:FindFirstChild("Title")
                                        if title then title.Text = "Upload: " .. rank .. " Rank" end
                                        local earn = fUI:FindFirstChild("Earnings")
                                        if earn then earn.Text = rtCpm .. " coins/sec" end
                                    end
                                end
                                break
                            end
                        end
                        resScreen:Destroy()
                        vmMaster.Visible = false
                        slotSelectionScreen.Visible = true
                        renderSlots()
                    end)
                end
            end

            
            -- Timer UI
            local timerLbl = Instance.new("TextLabel")
            timerLbl.Size = UDim2.new(0, 100, 1, 0)
            timerLbl.Position = UDim2.new(0.5, -50, 0, 0)
            timerLbl.BackgroundTransparency = 1
            timerLbl.Text = "🔴 LIVE: 30s"
            timerLbl.TextColor3 = Color3.fromRGB(255, 60, 60)
            timerLbl.Font = Enum.Font.GothamBlack
            timerLbl.TextSize = 20
            timerLbl.ZIndex = 56
            timerLbl.Parent = topBar

             resetGameUI = function()
                 gameRunning = true
                 gameTime = 30
                 totalViews = 0
                 totalHealth = 100
                 
                 if vidViews then vidViews.Text = "??  0 views" end
                 if timerLbl then timerLbl.Text = string.format("00:%02d", gameTime) end
                 
                 if cmtScroll then
                     for _, c in ipairs(cmtScroll:GetChildren()) do
                         if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                             c:Destroy()
                         end
                     end
                 end
                 
                 minigameStarted = true
             end

             resetGameUI = function()
                 gameRunning = true
                 gameTime = 30
                 totalViews = 0
                 totalHealth = 100
                 
                 if vidViews then vidViews.Text = "??  0 views" end
                 if timerLbl then timerLbl.Text = string.format("00:%02d", gameTime) end
                 
                 if cmtScroll then
                     for _, c in ipairs(cmtScroll:GetChildren()) do
                         if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                             c:Destroy()
                         end
                     end
                 end
                 
                 minigameStarted = true
             end
            
            task.spawn(function()
                while contentFrame.Parent do
                    task.wait(1)
                    if not minigameStarted or not gameRunning then continue end
                    if gameTime > 0 then
                        gameTime = gameTime - 1
                        timerLbl.Text = string.format("00:%02d", gameTime)
                    end
                    if gameTime <= 0 then
                        endGame()
                    end
                end
            end)

            task.spawn(function()
                print("[Game] Click system active")
                while contentFrame.Parent do
                    if not minigameStarted or not gameRunning then 
                        task.wait(0.5)
                        continue 
                    end
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds 
                    if gameTime <= 10 then
                        spawnDelay = math.random(7, 10) / 10 -- 0.7 to 1.0 seconds (Dialed back the chaos for the finale)
                    elseif gameTime <= 20 then
                        spawnDelay = math.random(9, 14) / 10 -- 0.9 to 1.4 seconds
                    end
                    task.wait(spawnDelay)
                    if not gameRunning then continue end

                    local isGood = math.random() > 0.6
                    local list = isGood and goodComments or badComments
                    local txt = list[math.random(1, #list)]

                    commentCount = commentCount + 1

                    local cmtCard = Instance.new("Frame")
                    cmtCard.Name = "Comment_" .. commentCount
                    cmtCard.Size = UDim2.new(1, -24, 0, 80)
                    cmtCard.BackgroundColor3 = isGood and Color3.fromRGB(20, 50, 25) or Color3.fromRGB(60, 20, 20)
                    cmtCard.ZIndex = 54
                    cmtCard.LayoutOrder = commentCount

                    local cmtCorner = Instance.new("UICorner")
                    cmtCorner.CornerRadius = UDim.new(0, 10)
                    cmtCorner.Parent = cmtCard

                    local cmtStroke = Instance.new("UIStroke")
                    cmtStroke.Color = isGood and Color3.fromRGB(40, 100, 50) or Color3.fromRGB(120, 40, 40)
                    cmtStroke.Thickness = 1
                    cmtStroke.Parent = cmtCard

                    local avatar = Instance.new("Frame")
                    avatar.Size = UDim2.new(0, 40, 0, 40)
                    avatar.Position = UDim2.new(0, 10, 0.5, -20)
                    avatar.BackgroundColor3 = Color3.fromRGB(math.random(100,255), math.random(100,255), math.random(100,255))
                    avatar.ZIndex = 55
                    avatar.Parent = cmtCard

                    local avCorner = Instance.new("UICorner")
                    avCorner.CornerRadius = UDim.new(1, 0)
                    avCorner.Parent = avatar

                    local userLbl = Instance.new("TextLabel")
                    userLbl.Size = UDim2.new(1, -70, 0, 20)
                    userLbl.Position = UDim2.new(0, 60, 0, 15)
                    userLbl.BackgroundTransparency = 1
                    userLbl.Text = "@User" .. math.random(1000, 9999)
                    userLbl.TextColor3 = Color3.fromRGB(180, 180, 190)
                    userLbl.Font = Enum.Font.GothamMedium
                    userLbl.TextSize = 13
                    userLbl.TextXAlignment = Enum.TextXAlignment.Left
                    userLbl.ZIndex = 55
                    userLbl.Parent = cmtCard

                    local txtLbl = Instance.new("TextLabel")
                    txtLbl.Size = UDim2.new(1, -110, 0, 30)
                    txtLbl.Position = UDim2.new(0, 60, 0, 35)
                    txtLbl.BackgroundTransparency = 1
                    txtLbl.Text = txt
                    txtLbl.TextColor3 = Color3.fromRGB(255, 255, 255)
                    txtLbl.Font = Enum.Font.GothamBold
                    txtLbl.TextSize = 14
                    txtLbl.TextXAlignment = Enum.TextXAlignment.Left
                    txtLbl.ZIndex = 55
                    txtLbl.Parent = cmtCard

                    local thumbIcon = Instance.new("TextLabel")
                    thumbIcon.Size = UDim2.new(0, 40, 0, 40)
                    thumbIcon.Position = UDim2.new(1, -45, 0.5, -20)
                    thumbIcon.BackgroundTransparency = 1
                    thumbIcon.Text = "👍 " .. math.random(1, 99)
                    thumbIcon.TextColor3 = Color3.fromRGB(200, 200, 200)
                    thumbIcon.Font = Enum.Font.GothamMedium
                    thumbIcon.TextSize = 14
                    thumbIcon.TextXAlignment = Enum.TextXAlignment.Right
                    thumbIcon.ZIndex = 55
                    thumbIcon.Parent = cmtCard

                    -- The Click Button
                    local clickBtn = Instance.new("TextButton")
                    clickBtn.Size = UDim2.new(1, 0, 1, 0)
                    clickBtn.BackgroundTransparency = 1
                    clickBtn.Text = ""
                    clickBtn.ZIndex = 56
                    clickBtn.Parent = cmtCard

                    cmtCard.Parent = cmtScroll

                    local clicked = false

                    clickBtn.MouseButton1Click:Connect(function()
                        if clicked or not gameRunning then return end
                        if os.clock() - lastShiftTime < 0.2 then return end -- Grace period for layout shifts!
                        clicked = true

                        if isGood then
                            -- Good Comment Clicked (MISTAKE)
                            updateViewsUI(-40)
                            floatText("-40", Color3.fromRGB(255, 50, 50), cmtCard)
                            floatText("DON'T DELETE GOOD COMMENTS!", Color3.fromRGB(255, 50, 50), cmtCard.Parent.Parent)
                            
                            -- Card shake then disappear
                            task.spawn(function()
                                local startPos = cmtCard.Position
                                for i=1, 4 do
                                    local offset = (i%2 == 0) and 5 or -5
                                    TweenService:Create(cmtCard, TweenInfo.new(0.05), {Position = UDim2.new(startPos.X.Scale, startPos.X.Offset + offset, startPos.Y.Scale, startPos.Y.Offset)}):Play()
                                    task.wait(0.05)
                                    TweenService:Create(cmtCard, TweenInfo.new(0.05), {Position = startPos}):Play()
                                    task.wait(0.05)
                                end
                                
                                TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                                for _, desc in ipairs(cmtCard:GetDescendants()) do
                                    if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                        pcall(function()
                                            TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                        end)
                                    end
                                end
                                task.delay(0.3, function() 
                                    lastShiftTime = os.clock()
                                    if cmtCard then cmtCard:Destroy() end 
                                end)
                            end)
                        else
                            -- Bad Comment Clicked (SUCCESS)
                            updateViewsUI(60)
                            floatText("+60", Color3.fromRGB(80, 255, 80), cmtCard)
                            
                            TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                            for _, desc in ipairs(cmtCard:GetDescendants()) do
                                if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                    pcall(function()
                                        TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                    end)
                                end
                            end
                            task.delay(0.3, function() 
                                lastShiftTime = os.clock()
                                if cmtCard then cmtCard:Destroy() end 
                            end)
                        end
                    end)

                    -- Timer logic for expiration based on difficulty
                    local expTime = 5
                    if gameTime <= 10 then expTime = 3 elseif gameTime <= 20 then expTime = 4 end
                    
                    -- Good comments despawn a bit faster so they don't clog up the screen
                    if isGood then
                        expTime = expTime * 0.7 
                    end
                    
                    task.delay(expTime, function()
                        if not clicked and cmtCard.Parent == cmtScroll then
                            clicked = true
                            if isGood and gameRunning then
                                -- Good comment reached end (Bonus!)
                                updateViewsUI(100)
                                floatText("+100", Color3.fromRGB(255, 215, 0), cmtCard.Parent.Parent)
                            elseif not isGood and gameRunning then
                                -- Bad comment unclicked (Penalty)
                                playShakeAnimation()
                                updateViewsUI(-40)
                                floatText("-40", Color3.fromRGB(255, 50, 50), cmtCard.Parent.Parent) -- Float on rightSide container
                                updateHealthUI(-10)

                                -- Red flash on health bar container
                                local bgOrig = hpBarBG.BackgroundColor3
                                hpBarBG.BackgroundColor3 = Color3.fromRGB(255, 50, 50)
                                TweenService:Create(hpBarBG, TweenInfo.new(0.5), {BackgroundColor3 = bgOrig}):Play()
                            end
                            TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                            for _, desc in ipairs(cmtCard:GetDescendants()) do
                                if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                    pcall(function()
                                        TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                    end)
                                end
                            end
                            task.delay(0.3, function() 
                                lastShiftTime = os.clock()
                                if cmtCard then cmtCard:Destroy() end 
                            end)
                        end
                    end)

                    local frames = {}
                    for _, child in ipairs(cmtScroll:GetChildren()) do
                        if child:IsA("Frame") and string.sub(child.Name, 1, 8) == "Comment_" then
                            table.insert(frames, child)
                        end
                    end
                    table.sort(frames, function(a,b) return a.LayoutOrder < b.LayoutOrder end)
                    
                    while #frames > 6 do
                        local oldest = table.remove(frames, 1)
                        if oldest then 
                            lastShiftTime = os.clock()
                            oldest:Destroy() 
                        end
                    end
                    
                    cmtScroll.CanvasSize = UDim2.new(0, 0, 0, #frames * 92)
                    cmtScroll.CanvasPosition = Vector2.new(0, cmtScroll.CanvasSize.Y.Offset)
                end
                


            end)

            -- Minor float animation
            task.spawn(function()
                local elapsed = 0
                local rs = game:GetService("RunService")
                while playBtnOuter and playBtnOuter.Parent do
                    local dt = rs.RenderStepped:Wait()
                    elapsed = elapsed + dt
                    local scale = 1 + math.sin(elapsed * 4) * 0.05
                    playBtnOuter.Size = UDim2.new(0, 80 * scale, 0, 80 * scale)
                    playBtnOuter.Position = UDim2.new(0.5, -(40 * scale), 0.5, -(40 * scale))
                end
            end)

    elseif appData.name == "Editing Suite" then
        local comingSoon = Instance.new("TextLabel")
        comingSoon.Size = UDim2.new(1, 0, 1, 0)
        comingSoon.BackgroundTransparency = 1
        comingSoon.Text = "Editing Suite — Coming Soon"
        comingSoon.TextColor3 = Color3.new(1, 1, 1)
        comingSoon.Font = fontBold
        comingSoon.TextSize = 24
        comingSoon.ZIndex = 72
        comingSoon.Parent = contentFrame
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
    {name = "Editing Suite", icon = "🎨"},
    {name = "My Computer", icon = "💻"},
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
    iconBtn.Position = UDim2.new(0, 40, 0, 70 + (i - 1) * 115)
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





