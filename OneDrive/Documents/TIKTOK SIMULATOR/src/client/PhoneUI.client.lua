local Players = game:GetService("Players")
local CloutApp = require(script.Parent.PhoneModules.CloutApp)
local SettingsApp = require(script.Parent.PhoneModules.SettingsApp)
local ShopApp = require(script.Parent.PhoneModules.ShopApp)
local HackApp = require(script.Parent.PhoneModules.HackApp)
local PrestigeApp = require(script.Parent.PhoneModules.PrestigeApp)
local RealEstateApp = require(script.Parent.PhoneModules.RealEstateApp)
local StatsApp = require(script.Parent.PhoneModules.StatsApp)
local SponsorshipsApp = require(script.Parent.PhoneModules.SponsorshipsApp)
local QuestsApp = require(script.Parent.PhoneModules.QuestsApp)
local LeaderboardApp = require(script.Parent.PhoneModules.LeaderboardApp)
local TweenService = game:GetService("TweenService")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local Config = require(ReplicatedStorage.Shared.Config)
local player = Players.LocalPlayer
local Remotes = ReplicatedStorage:WaitForChild('Remotes')

local FONT_MAIN = Enum.Font.FredokaOne
local FONT_BOLD = Enum.Font.FredokaOne

-- Sleek Modern TikTok dark theme
local C_DARK = {
  bg = Color3.fromRGB(0, 0, 0), -- Pure Black for AMOLED feel
  card = Color3.fromRGB(18, 18, 18), -- Deep gray for contrast
  indigo = Color3.fromRGB(37, 244, 238), -- TikTok Cyan
  green = Color3.fromRGB(37, 244, 238), -- Cyan
  amber = Color3.fromRGB(254, 44, 85), -- TikTok Red
  red = Color3.fromRGB(254, 44, 85), -- TikTok Red
  pink = Color3.fromRGB(254, 44, 85), -- TikTok Red
  cyan = Color3.fromRGB(37, 244, 238), -- TikTok Cyan
  white = Color3.fromRGB(255, 255, 255),
  muted = Color3.fromRGB(150, 150, 150),
  border = Color3.fromRGB(40, 40, 40),
}

local C_LIGHT = {
  bg = Color3.fromRGB(245, 245, 245), -- Cool white
  card = Color3.fromRGB(255, 255, 255), -- Pure white for contrast
  indigo = Color3.fromRGB(37, 244, 238), -- TikTok Cyan
  green = Color3.fromRGB(37, 244, 238), -- Cyan
  amber = Color3.fromRGB(254, 44, 85), -- TikTok Red
  red = Color3.fromRGB(254, 44, 85), -- TikTok Red
  pink = Color3.fromRGB(254, 44, 85), -- TikTok Red
  cyan = Color3.fromRGB(37, 244, 238), -- TikTok Cyan
  white = Color3.fromRGB(0, 0, 0), -- Pure black text
  muted = Color3.fromRGB(120, 120, 120),
  border = Color3.fromRGB(210, 210, 210),
}

-- Default to sleek modern dark mode!
local isDarkMode = true
local C = isDarkMode and C_DARK or C_LIGHT

local ThemedFrames = {}
local ThemedLabels = {}
local ThemedStrokes = {}
local ThemedAppIcons = {}
local GlobalSettingsBtn = nil

local function findThemeKey(color)
  if not color then return "bg" end
  for k, v in pairs(C) do
    if v == color then return k end
  end
  return nil -- return nil if it's not a themed color!
end

local function addThemedText(instance, colorObj)
  local key = findThemeKey(colorObj)
  if key then table.insert(ThemedLabels, { label = instance, key = key }) end
  instance.TextColor3 = key and C[key] or colorObj
end

-- Wait until we define phoneScreen and stuff to define applyTheme
local applyTheme -- We will define this further down when all components exist

local PHONE_WIDTH = 380
local PHONE_HEIGHT = 660
local STATUS_HEIGHT = 36

local APP_META = {
  CloutApp = { color = Color3.fromRGB(99, 102, 241), lightColor = Color3.fromRGB(165, 180, 252), emoji = "📱" },
  ShopApp = { color = Color3.fromRGB(245, 158, 11), lightColor = Color3.fromRGB(252, 211, 77), emoji = "🛒" },
  RealEstate = { color = Color3.fromRGB(52, 211, 153), lightColor = Color3.fromRGB(110, 231, 183), emoji = "🏠" },
  Sponsorships = { color = Color3.fromRGB(236, 72, 153), lightColor = Color3.fromRGB(244, 114, 182), emoji = "🤝" },
  HackApp = { color = Color3.fromRGB(239, 68, 68), lightColor = Color3.fromRGB(248, 113, 113), emoji = "💀" },
  Stats = { color = Color3.fromRGB(6, 182, 212), lightColor = Color3.fromRGB(103, 232, 249), emoji = "📊" },
  Prestige = { color = Color3.fromRGB(180, 80, 255), lightColor = Color3.fromRGB(200, 130, 255), emoji = "⭐" },
  Quests = { color = Color3.fromRGB(255, 80, 120), lightColor = Color3.fromRGB(255, 150, 170), emoji = "🎯" },
  Leaderboard = { color = Color3.fromRGB(255, 215, 0), lightColor = Color3.fromRGB(255, 230, 100), emoji = "🏆" },
  Settings = { color = Color3.fromRGB(107, 114, 128), lightColor = Color3.fromRGB(156, 163, 175), emoji = "⚙️" },
}

local function makeFrame(parent, bg, size, position, transparency)
  local frame = Instance.new("Frame")
  frame.BorderSizePixel = 0
  
  local themeKey = findThemeKey(bg)
  frame.BackgroundColor3 = themeKey and C[themeKey] or (bg or C.bg)

  -- ULTRA VIBRANT GLASSMORPHISM MODE
  local function updateBg()
    if isDarkMode then
        frame.BackgroundTransparency = transparency or 0
    else
        -- Let the master gradient bleed through backgrounds organically
        if themeKey == "bg" then
            frame.BackgroundTransparency = 1
        -- Frosty glass effect for cards so they look premium but kid friendly!       
        elseif themeKey == "card" and (transparency == nil or transparency == 0) then       
            frame.BackgroundTransparency = 0.25
        else
            frame.BackgroundTransparency = transparency or 0
        end
    end
  end
  updateBg()
  
  if themeKey then
    table.insert(ThemedFrames, { frame = frame, key = themeKey, update = updateBg })
  end
  
  frame.Size = size or UDim2.new(0, 100, 0, 100)
  frame.Position = position or UDim2.new(0, 0, 0, 0)
  frame.Parent = parent
  return frame
end

local function makeLabel(parent, text, size, color, font, sizeUDim, posUDim)
  local label = Instance.new("TextLabel")
  label.BackgroundTransparency = 1
  label.BorderSizePixel = 0
  label.Text = text or ""
  
  -- Globally scaled up all fonts by 4 pixels to make everything BIGGER and EASIER to read!
  label.TextSize = (size or 14) + 4

  local themeKey = findThemeKey(color)
  label.TextColor3 = themeKey and C[themeKey] or (color or C.white)
  
  if themeKey then
      table.insert(ThemedLabels, { label = label, key = themeKey })
  end
  
  label.Font = font or FONT_MAIN
  label.Size = sizeUDim or UDim2.new(1, 0, 0, 20)
  label.Position = posUDim or UDim2.new(0, 0, 0, 0)
  label.Parent = parent
  return label
end

local function makeCorner(parent, radius)
  local corner = Instance.new("UICorner")
  corner.CornerRadius = UDim.new(0, radius)
  corner.Parent = parent
  return corner
end

local function makeStroke(parent, color, thickness, transparency)
  local stroke = Instance.new("UIStroke")
  
  local themeKey = findThemeKey(color)
  stroke.Color = themeKey and C[themeKey] or color
  
  if themeKey then
      table.insert(ThemedStrokes, { stroke = stroke, key = themeKey })
  end
  
  stroke.Thickness = thickness
  stroke.Transparency = transparency or 0
  stroke.Parent = parent
  return stroke
end

local function tween(instance, tweenInfo, props)
  local tw = TweenService:Create(instance, tweenInfo, props)
  tw:Play()
  return tw
end

local function greetingForHour(hour)
  if hour >= 6 and hour < 12 then
    return "Good morning 👋"
  end
  if hour >= 12 and hour < 18 then
    return "Good afternoon ✌️"
  end
  if hour >= 18 and hour < 24 then
    return "Good evening 🌙"
  end
  return "Up late grinding? 👀"
end

local playerGui = player:WaitForChild("PlayerGui")
local oldGui = playerGui:FindFirstChild("GoingViralUI")
if oldGui then
  oldGui:Destroy()
end

local screenGui = Instance.new("ScreenGui")
screenGui.Name = "GoingViralUI"
screenGui.IgnoreGuiInset = true
screenGui.ResetOnSpawn = false
screenGui.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
screenGui.Parent = playerGui

local toggleButton = Instance.new("TextButton")
toggleButton.Name = "PhoneToggleButton"
toggleButton.BorderSizePixel = 0
toggleButton.Size = UDim2.new(0, 150, 0, 40)
toggleButton.AnchorPoint = Vector2.new(0.5, 1)
toggleButton.Position = UDim2.new(0.5, 0, 1, -20)
toggleButton.BackgroundColor3 = C.card; table.insert(ThemedFrames, { frame = toggleButton, key = "card", update = function() end })
toggleButton.Text = "📱  PHONE  [E]"
toggleButton.Font = FONT_BOLD
toggleButton.TextSize = 17
addThemedText(toggleButton, C.white)
toggleButton.AutoButtonColor = false
toggleButton.Parent = screenGui
toggleButton.ZIndex = 100
makeCorner(toggleButton, 20)
makeStroke(toggleButton, C.indigo, 1.5, 0)

local phoneClosedPos = UDim2.new(0.5, 0, 1.5, 0)
local phoneOpenPos = UDim2.new(0.5, 0, 0.5, 0)

local BEZEL_PAD = 14
local SHELL_W = PHONE_WIDTH + (BEZEL_PAD * 2)
local SHELL_H = PHONE_HEIGHT + (BEZEL_PAD * 2)

local phoneFrame = Instance.new("Frame")
phoneFrame.Name = "PhoneFrame"
phoneFrame.BackgroundTransparency = 1
phoneFrame.Size = UDim2.new(0, SHELL_W, 0, SHELL_H)
phoneFrame.Position = phoneClosedPos
phoneFrame.AnchorPoint = Vector2.new(0.5, 0.5)
phoneFrame.Visible = false
phoneFrame.Parent = screenGui

local caseColor = Color3.fromRGB(24, 24, 28)
local caseStroke = Color3.fromRGB(56, 56, 68)

local btnPower = makeFrame(phoneFrame, caseColor, UDim2.new(0, 5, 0, 64), UDim2.new(1, -2, 0, 180), 0)
makeCorner(btnPower, 4)
makeStroke(btnPower, caseStroke, 1, 0)
btnPower.ZIndex = 0

local btnVolUp = makeFrame(phoneFrame, caseColor, UDim2.new(0, 5, 0, 50), UDim2.new(0, -3, 0, 160), 0)
makeCorner(btnVolUp, 4)
makeStroke(btnVolUp, caseStroke, 1, 0)
btnVolUp.ZIndex = 0

local btnVolDown = makeFrame(phoneFrame, caseColor, UDim2.new(0, 5, 0, 50), UDim2.new(0, -3, 0, 224), 0)
makeCorner(btnVolDown, 4)
makeStroke(btnVolDown, caseStroke, 1, 0)
btnVolDown.ZIndex = 0

local phoneBezel = makeFrame(phoneFrame, caseColor, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0)
phoneBezel.Name = "PhoneBezel"
phoneBezel.ZIndex = 1
makeCorner(phoneBezel, 52)
makeStroke(phoneBezel, caseStroke, 2, 0)

-- Replaced CanvasGroup with Frame so it doesn't compress and blur the resolution,
-- and set ClipsDescendants=true to correctly mask apps scrolling off screen!
local phoneScreen = Instance.new("Frame")
phoneScreen.ClipsDescendants = true
phoneScreen.Name = "PhoneScreen"
phoneScreen.BorderSizePixel = 0
phoneScreen.BackgroundColor3 = C.bg
phoneScreen.Size = UDim2.new(0, PHONE_WIDTH, 0, PHONE_HEIGHT)
phoneScreen.Position = UDim2.new(0, BEZEL_PAD, 0, BEZEL_PAD)
phoneScreen.ZIndex = 2
phoneScreen.Parent = phoneFrame
-- Changed to 38 math-perfectly: 52 (outer corner) minus 14 (bezel pad)
makeCorner(phoneScreen, 38)

local notch = makeFrame(phoneScreen, Color3.fromRGB(0, 0, 0), UDim2.new(0, 126, 0, 34), UDim2.new(0.5, -63, 0, 8), 0)
notch.Name = "Notch"
notch.ZIndex = 100
makeCorner(notch, 17)

-- UIGradient has been removed as per requested White Theme with blobs

local phoneScale = Instance.new("UIScale")
phoneScale.Name = "PhoneScale"
phoneScale.Parent = phoneFrame

local function applyTheme(dark)
  isDarkMode = dark
  C = isDarkMode and C_DARK or C_LIGHT

  for _, t in ipairs(ThemedFrames) do
     t.frame.BackgroundColor3 = C[t.key]
     if t.update then t.update() end 
  end

  for _, t in ipairs(ThemedLabels) do
     t.label.TextColor3 = C[t.key]
  end

  for _, t in ipairs(ThemedStrokes) do
     t.stroke.Color = C[t.key]
  end

  for _, t in ipairs(ThemedAppIcons) do
     local activeColor = isDarkMode and t.meta.color or (t.meta.lightColor or t.meta.color)
     t.icon.BackgroundColor3 = activeColor
     if t.stroke then
         t.stroke.Color = activeColor
     end
  end

  if phoneScreen then
     phoneScreen.BackgroundColor3 = C.bg
  end
  if GlobalSettingsBtn then
     GlobalSettingsBtn.Text = isDarkMode and "Switch to Light Mode" or "Switch to Dark Mode"
  end
end

local function updateScale()
  local camera = workspace.CurrentCamera
  if not camera then return end
  local vp = camera.ViewportSize
  if vp.Y > 0 and vp.X > 0 then
    -- Increased viewport coverage from 85% to 94% making the phone physically larger on screen!
    local sY = (vp.Y * 0.94) / SHELL_H
    local sX = (vp.X * 0.94) / SHELL_W
    phoneScale.Scale = math.clamp(math.min(sX, sY), 0.3, 2.5)
  end
end

local function hookCamera()
  local camera = workspace.CurrentCamera
  if camera then
    if camera:FindFirstChild("ViewportSizeObj") then return end
    camera:GetPropertyChangedSignal("ViewportSize"):Connect(updateScale)
    updateScale()
  end
end

workspace:GetPropertyChangedSignal("CurrentCamera"):Connect(hookCamera)
hookCamera()

local statusBar = makeFrame(phoneScreen, C.bg, UDim2.new(1, 0, 0, STATUS_HEIGHT), UDim2.new(0, 0, 0, 0), 1)
statusBar.Name = "StatusBar"

local statusTime = makeLabel(statusBar, "00:00", 13, C.white, FONT_BOLD, UDim2.new(0, 120, 1, 0), UDim2.new(0, 16, 0, 0))
statusTime.TextXAlignment = Enum.TextXAlignment.Left
statusTime.TextYAlignment = Enum.TextYAlignment.Center

local statusRight = makeLabel(statusBar, "●●● WiFi 100%", 10, C.muted, FONT_MAIN, UDim2.new(0, 150, 1, 0), UDim2.new(1, -166, 0, 0))
statusRight.TextXAlignment = Enum.TextXAlignment.Right
statusRight.TextYAlignment = Enum.TextYAlignment.Center

local homeScreen = makeFrame(phoneScreen, C.bg, UDim2.new(1, 0, 1, -STATUS_HEIGHT), UDim2.new(0, 0, 0, STATUS_HEIGHT), 1)
homeScreen.Name = "HomeScreen"

-- The beautiful background blobs for the Dark "Old Sleek Modern" Layout
local blobsWrapper = Instance.new("CanvasGroup") -- CanvasGroup enforces rounded clipping for child shapes!
blobsWrapper.Name = "BlobsWrapper"
blobsWrapper.Size = UDim2.new(1, 0, 1, STATUS_HEIGHT)
blobsWrapper.Position = UDim2.new(0, 0, 0, -STATUS_HEIGHT)
blobsWrapper.BackgroundTransparency = 1
makeCorner(blobsWrapper, 38) -- Perfectly matches the phone corner safely
blobsWrapper.Parent = homeScreen

local decoration1 = makeFrame(blobsWrapper, C.indigo, UDim2.new(0, 320, 0, 320), UDim2.new(0, -60, 0, -80), 0.45)
makeCorner(decoration1, 999)
local decoration2 = makeFrame(blobsWrapper, C.pink, UDim2.new(0, 260, 0, 260), UDim2.new(1, -160, 1, -120), 0.55)
makeCorner(decoration2, 999)
local decoration3 = makeFrame(blobsWrapper, C.cyan, UDim2.new(0, 180, 0, 180), UDim2.new(1, -60, 0, 100), 0.65)
makeCorner(decoration3, 999)

local heroTime = makeLabel(homeScreen, "00:00", 48, C.white, FONT_BOLD, UDim2.new(1, 0, 0, 56), UDim2.new(0, 0, 0, 40))
heroTime.TextXAlignment = Enum.TextXAlignment.Center

local heroDate = makeLabel(homeScreen, "", 14, C.muted, FONT_MAIN, UDim2.new(1, 0, 0, 22), UDim2.new(0, 0, 0, 100))
heroDate.TextXAlignment = Enum.TextXAlignment.Center

local heroGreeting = makeLabel(homeScreen, "", 13, C.muted, FONT_MAIN, UDim2.new(1, 0, 0, 22), UDim2.new(0, 0, 0, 126))
heroGreeting.TextXAlignment = Enum.TextXAlignment.Center

local homeIndicator = makeFrame(phoneScreen, C.white, UDim2.new(0, 120, 0, 4), UDim2.new(0.5, -60, 1, -14), 0.6)
homeIndicator.Name = "HomeIndicator"
makeCorner(homeIndicator, 999)

local appScreens = {}
local currentAppName = nil
local phoneOpen = false
local phoneAnimating = false


local function createHeader(parent, appName, appColor)
  local header = makeFrame(parent, C.card, UDim2.new(1, 0, 0, 52), UDim2.new(0, 0, 0, 0), 0)
  
  local back = Instance.new("TextButton")
  back.BorderSizePixel = 0
  back.BackgroundTransparency = 1
  back.Text = "← Back"
  back.TextColor3 = appColor
  back.Font = FONT_BOLD
  back.TextSize = 17
  back.Size = UDim2.new(0, 90, 1, 0)
  back.Position = UDim2.new(0, 12, 0, 0)
  back.Parent = header

  local title = makeLabel(header, appName, 15, C.white, FONT_BOLD, UDim2.new(1, -120, 1, 0), UDim2.new(0, 60, 0, 0))
  title.TextXAlignment = Enum.TextXAlignment.Center
  title.TextYAlignment = Enum.TextYAlignment.Center

  return back
end

local function closeCurrentApp()
  if not currentAppName then return end
  local screen = appScreens[currentAppName]
  if not screen then
    currentAppName = nil
    homeScreen.Visible = true
    return
  end
  local appName = currentAppName
  local out = tween(screen, TweenInfo.new(0.2, Enum.EasingStyle.Quad, Enum.EasingDirection.In), { Position = UDim2.new(1, 0, 0, STATUS_HEIGHT) })
  out.Completed:Connect(function()
    if currentAppName == appName then
      currentAppName = nil
      homeScreen.Visible = true
    end
    screen.Visible = false
  end)
end

local function fmt(n)
    n = math.floor(n or 0)
    if n >= 1e12 then return string.format("%.1fT", n/1e12)
    elseif n >= 1e9 then return string.format("%.1fB", n/1e9)
    elseif n >= 1e6 then return string.format("%.1fM", n/1e6)
    elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
    else return tostring(n) end
end

local function openApp(appName)
    local screen = appScreens[appName]
    if not screen then return end

    if appName == "CloutApp" then
        local leaderstats = player:FindFirstChild("leaderstats")
        if leaderstats then
            local c = leaderstats:FindFirstChild("Coins")
            local v = leaderstats:FindFirstChild("Views")
            local f = leaderstats:FindFirstChild("Followers")
            local vCLbl = screen:FindFirstChild("vCoinsLabel", true)
            local vVLbl = screen:FindFirstChild("vViewsLabel", true)
            local vFLbl = screen:FindFirstChild("vFollLabel", true)
            if c and vCLbl then vCLbl.Text = fmt(c.Value) end
            if v and vVLbl then vVLbl.Text = fmt(v.Value) end
            if f and vFLbl then vFLbl.Text = fmt(f.Value) end
        end
    end

  if currentAppName and currentAppName ~= appName then
    local current = appScreens[currentAppName]
    if current then
      current.Visible = false
      current.Position = UDim2.new(1, 0, 0, STATUS_HEIGHT)
    end
  end

  currentAppName = appName
  homeScreen.Visible = false
  screen.Position = UDim2.new(1, 0, 0, STATUS_HEIGHT)
  screen.Visible = true
  tween(screen, TweenInfo.new(0.28, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), { Position = UDim2.new(0, 0, 0, STATUS_HEIGHT) })
end


local appEnv = {
    TweenService = TweenService,
    RunService = game:GetService("RunService"),
    player = player,
    Remotes = Remotes,
    Config = Config,
    C = C,
    C_DARK = C_DARK,
    C_LIGHT = C_LIGHT,
    makeFrame = makeFrame,
    makeLabel = makeLabel,
    makeCorner = makeCorner,
    makeStroke = makeStroke,
    tween = tween,
    fmt = fmt,
    ThemedFrames = ThemedFrames,
    ThemedLabels = ThemedLabels,
    ThemedStrokes = ThemedStrokes,
    addThemedText = addThemedText,
    applyTheme = applyTheme,
    getIsDarkMode = function() return isDarkMode end,
    FONT_MAIN = FONT_MAIN,
    FONT_BOLD = FONT_BOLD,
    STATUS_HEIGHT = STATUS_HEIGHT,
    phoneScreen = phoneScreen,
    setGlobalSettingsBtn = function(btn) GlobalSettingsBtn = btn end
}

for _, app in ipairs(Config.APPS) do
  local meta = APP_META[app.name] or { color = C.indigo, emoji = "•" }
  local appScreen = makeFrame(phoneScreen, C.bg, UDim2.new(1, 0, 1, -STATUS_HEIGHT), UDim2.new(1, 0, 0, STATUS_HEIGHT), 1)
  appScreen.Name = app.name .. "Screen"
  appScreen.Visible = false
  appScreens[app.name] = appScreen
  
  local backButton = createHeader(appScreen, app.name, meta.color)
  local content = makeFrame(appScreen, C.bg, UDim2.new(1, 0, 1, -52), UDim2.new(0, 0, 0, 52), 1)
  
  if app.name == "CloutApp" then
    CloutApp.create(content, appEnv)
  elseif app.name == 'HackApp' then
    HackApp.create(content, appEnv)
  elseif app.name == "Settings" then
    SettingsApp.create(content, appEnv)
  elseif app.name == 'ShopApp' then
    ShopApp.create(content, appEnv)
  elseif app.name == 'Prestige' then
    PrestigeApp.create(content, appEnv)
  elseif app.name == 'RealEstate' then
    RealEstateApp.create(content, appEnv)
  elseif app.name == 'Stats' then
    StatsApp.create(content, appEnv)
  elseif app.name == 'Sponsorships' then
    SponsorshipsApp.create(content, appEnv)
  elseif app.name == 'Quests' then
    QuestsApp.create(content, appEnv)
  elseif app.name == 'Leaderboard' then
    LeaderboardApp.create(content, appEnv)
  else
    local label = makeLabel(content, "Coming soon", 16, C.muted, FONT_MAIN, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0.5, -10))
    label.TextXAlignment = Enum.TextXAlignment.Center
  end
  
  backButton.MouseButton1Click:Connect(function()
    closeCurrentApp()
  end)
end

local gridTop = 190
local tileWidth = 90
local tileHeight = 110
local gapX = 20
local gapY = 18
local rowWidth = (tileWidth * 3) + (gapX * 2)
local rowStartX = math.floor((PHONE_WIDTH - rowWidth) / 2)

for i, app in ipairs(Config.APPS) do
  local meta = APP_META[app.name] or { color = C.indigo, emoji = "•" }
  local row = math.floor((i - 1) / 3)
  local col = (i - 1) % 3
  local x = rowStartX + (col * (tileWidth + gapX))
  local y = gridTop + (row * (tileHeight + gapY))

  local tile = makeFrame(homeScreen, C.bg, UDim2.new(0, tileWidth, 0, tileHeight), UDim2.new(0, x, 0, y), 1)
  
  local iconButton = Instance.new("TextButton")
  iconButton.BorderSizePixel = 0
  iconButton.Size = UDim2.new(0, 82, 0, 82)
  iconButton.Position = UDim2.new(0, 4, 0, 0)
  iconButton.BackgroundColor3 = meta.color
  iconButton.BackgroundTransparency = 0.1
  iconButton.Text = ""
  iconButton.AutoButtonColor = false
  iconButton.Parent = tile
  makeCorner(iconButton, 22)
  local iconStroke = makeStroke(iconButton, meta.color, 2, 0)
  table.insert(ThemedAppIcons, { icon = iconButton, stroke = iconStroke, meta = meta })

  -- Increased Emoji Size for major visibility
  local emoji = makeLabel(iconButton, meta.emoji, 36, C.white, FONT_MAIN, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
  emoji.TextXAlignment = Enum.TextXAlignment.Center
  emoji.TextYAlignment = Enum.TextYAlignment.Center

  -- Increased App Text size for much better readability
  local name = makeLabel(tile, app.name, 13, C.white, FONT_MAIN, UDim2.new(1, 0, 0, 24), UDim2.new(0, 0, 0, 88))
  name.TextXAlignment = Enum.TextXAlignment.Center
  name.TextYAlignment = Enum.TextYAlignment.Top
  name.TextWrapped = true

  iconButton.MouseButton1Click:Connect(function()
    local down = tween(iconButton, TweenInfo.new(0.08, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), { Size = UDim2.new(0, 72, 0, 72), Position = UDim2.new(0, 9, 0, 5) })
    down.Completed:Connect(function()
      local up = tween(iconButton, TweenInfo.new(0.1, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), { Size = UDim2.new(0, 82, 0, 82), Position = UDim2.new(0, 4, 0, 0) })
      up.Completed:Connect(function()
        openApp(app.name)
      end)
    end)
  end)
end

local function openPhone()
  if phoneAnimating or phoneOpen then return end
  phoneAnimating = true
  phoneFrame.Visible = true
  phoneFrame.Position = phoneClosedPos
  toggleButton.Visible = false

  local tw = tween(phoneFrame, TweenInfo.new(0.35, Enum.EasingStyle.Back, Enum.EasingDirection.Out), { Position = phoneOpenPos })
  tw.Completed:Connect(function()
    phoneOpen = true
    phoneAnimating = false
  end)
end

local function closePhone()
  if phoneAnimating or not phoneOpen then return end
  phoneAnimating = true
  local tw = tween(phoneFrame, TweenInfo.new(0.25, Enum.EasingStyle.Quad, Enum.EasingDirection.In), { Position = phoneClosedPos })
  tw.Completed:Connect(function()
    phoneFrame.Visible = false
    phoneOpen = false
    phoneAnimating = false
    toggleButton.Visible = true
  end)
end

local function togglePhone()
  if not phoneOpen then
    openPhone()
    return
  end
  if currentAppName then
    closeCurrentApp()
    return
  end
  closePhone()
end

toggleButton.MouseButton1Click:Connect(togglePhone)

UserInputService.InputBegan:Connect(function(input, processed)
  if processed then return end
  if input.KeyCode == Enum.KeyCode.E then
    togglePhone()
  end
end)

task.spawn(function()
  while true do
    local now = os.date("*t")
    local timeText = string.format("%02d:%02d", now.hour, now.min)
    statusTime.Text = timeText
    heroTime.Text = timeText
    heroDate.Text = os.date("%A, %B %d")
    heroGreeting.Text = greetingForHour(now.hour)
    task.wait(1)
  end
end)

-- ================================================================
-- GOING VIRAL NOTIFICATION
-- ================================================================
local VIRAL_THRESHOLD = 50000000

local function fmtBig(n)
  n = math.floor(n or 0)
  if n >= 1e12 then return string.format("%.1fT", n/1e6)
  elseif n >= 1e9 then return string.format("%.1fB", n/1e6)
  elseif n >= 1e6 then return string.format("%.1fM", n/1e3)
  elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
  else return tostring(n) end
end

local function showViralNotification(playerName, totalViews)
  local screenGui = playerGui:FindFirstChild("GoingViralUI")
  if not screenGui then
    screenGui = Instance.new("ScreenGui")
    screenGui.Name = "GoingViralUI"
    screenGui.IgnoreGuiInset = true
    screenGui.ResetOnSpawn = false
    screenGui.DisplayOrder = 999
    screenGui.Parent = playerGui
  end

  local overlay = Instance.new("Frame")
  overlay.Size = UDim2.new(1, 0, 1, 0)
  overlay.BackgroundColor3 = Color3.fromRGB(5, 3, 15)
  overlay.BackgroundTransparency = 0.4
  overlay.ZIndex = 500
  overlay.Parent = screenGui

  local card = Instance.new("Frame")
  card.Size = UDim2.new(0, 360, 0, 240)
  card.Position = UDim2.new(0.5, -180, 0.5, -120)
  card.BackgroundColor3 = Color3.fromRGB(15, 5, 30)
  card.ZIndex = 501
  card.Parent = overlay
  Instance.new("UICorner", card).CornerRadius = UDim.new(0, 20)

  local cardStroke = Instance.new("UIStroke")
  cardStroke.Color = Color3.fromRGB(255, 215, 0)
  cardStroke.Thickness = 3
  cardStroke.Parent = card

  local grad = Instance.new("UIGradient")
  grad.Color = ColorSequence.new({
    ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 60, 100)),
    ColorSequenceKeypoint.new(0.5, Color3.fromRGB(180, 60, 255)),
    ColorSequenceKeypoint.new(1, Color3.fromRGB(255, 215, 0))
  })
  grad.Rotation = 45
  grad.Parent = card

  task.spawn(function()
    local r = 0
    while card.Parent do
      r = r + 3
      grad.Rotation = r % 360
      task.wait(0.03)
    end
  end)

  local fireLbl = makeLabel(card, "🔥🔥🔥", 40, Color3.fromRGB(255, 215, 0), Enum.Font.GothamBlack, UDim2.new(1, 0, 0, 30), UDim2.new(0, 0, 0, 0))
  fireLbl.TextXAlignment = Enum.TextXAlignment.Center

  local viralLbl = makeLabel(card, "GOING VIRAL!", 32, Color3.fromRGB(255, 215, 0), Enum.Font.GothamBlack, UDim2.new(1, 0, 0, 50), UDim2.new(0, 0, 0, 0))
  viralLbl.TextXAlignment = Enum.TextXAlignment.Center

  local nameLbl = makeLabel(card, playerName .. " hit " .. fmtBig(totalViews) .. " views!", 16, Color3.new(1,1,1), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 30), UDim2.new(0, 0, 0, 0))
  nameLbl.TextXAlignment = Enum.TextXAlignment.Center

  local subLbl = makeLabel(card, "The whole server is watching!", 14, Color3.fromRGB(200, 180, 255), Enum.Font.Gotham, UDim2.new(1, 0, 0, 24), UDim2.new(0, 0, 0, 0))
  subLbl.TextXAlignment = Enum.TextXAlignment.Center

  local coinsLbl = makeLabel(card, "💰 +50,000 coins!", 18, Color3.fromRGB(255, 210, 50), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 28), UDim2.new(0, 0, 0, 0))
  coinsLbl.TextXAlignment = Enum.TextXAlignment.Center

  task.spawn(function()
    local emojis = {"⭐","🔥","✨","💥","🌟","🎉"}
    for i = 1, 10 do
      local star = Instance.new("TextLabel")
      star.Text = emojis[math.random(6)]
      star.Size = UDim2.new(0, 30, 0, 30)
      star.BackgroundTransparency = 1
      star.TextSize = math.random(20, 36)
      star.Font = Enum.Font.GothamBold
      star.TextColor3 = Color3.fromRGB(255, 215, 0)
      star.Position = UDim2.new(math.random() * 0.8 + 0.1, 0, math.random() * 0.6 + 0.2, 0)
      star.ZIndex = 502
      star.Parent = overlay
      TweenService:Create(star, TweenInfo.new(1.5, Enum.EasingStyle.Back), {
        Size = UDim2.new(0, 60, 0, 60),
        TextTransparency = 1,
        Rotation = math.random(-180, 180)
      }):Play()
      task.delay(1.5, function() if star then star:Destroy() end end)
    end
  end)

  task.delay(5, function()
    TweenService:Create(overlay, TweenInfo.new(0.5), {BackgroundTransparency = 1}):Play()
    TweenService:Create(card, TweenInfo.new(0.5), {Size = UDim2.new(0, 0, 0, 0)}):Play()
    task.delay(0.5, function() overlay:Destroy() end)
  end)
end

Remotes:WaitForChild("GoingViral").OnClientEvent:Connect(showViralNotification)

-- Initialize the visual theme on startup so the backgrounds match the default setting!
applyTheme(isDarkMode)



