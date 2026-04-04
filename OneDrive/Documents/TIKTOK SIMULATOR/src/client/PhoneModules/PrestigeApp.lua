local PrestigeApp = {}

function PrestigeApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
  local fmt = env.fmt
  local C = env.C

  local scroll = Instance.new("ScrollingFrame")
  scroll.Size = UDim2.new(1, 0, 1, 0)
  scroll.BackgroundTransparency = 1
  scroll.BorderSizePixel = 0
  scroll.ScrollBarThickness = 3
  scroll.ScrollBarImageColor3 = Color3.fromRGB(120, 120, 130)
  scroll.CanvasSize = UDim2.new(0, 0, 0, 600)
  scroll.Parent = content

  local pad = Instance.new("UIPadding")
  pad.PaddingTop = UDim.new(0, 8)
  pad.PaddingLeft = UDim.new(0, 12)
  pad.PaddingRight = UDim.new(0, 12)
  pad.PaddingBottom = UDim.new(0, 20)
  pad.Parent = scroll

  local layout = Instance.new("UIListLayout")
  layout.Padding = UDim.new(0, 10)
  layout.SortOrder = Enum.SortOrder.LayoutOrder
  layout.Parent = scroll

  layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 40)
  end)

  local prestigeInfo = nil
  pcall(function()
    prestigeInfo = Remotes:WaitForChild("GetPrestigeInfo"):InvokeServer()
  end)

  local prestigeCount = prestigeInfo and prestigeInfo.prestigeCount or 0
  local maxLevel = prestigeInfo and prestigeInfo.maxLevel or 10
  local canPrestige = prestigeInfo and prestigeInfo.canPrestige or false
  local nextMaxLevel = prestigeInfo and prestigeInfo.nextMaxLevel or 20
  local bonusPercent = prestigeInfo and prestigeInfo.bonusPercent or 0
  local followers = prestigeInfo and prestigeInfo.followers or 0
  local isMaxed = prestigeInfo and prestigeInfo.isMaxed or false

  -- Header card
  local header = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 70), nil, 0)
  header.LayoutOrder = 1
  makeCorner(header, 12)
  makeStroke(header, Color3.fromRGB(180, 80, 255), 2, 0)

  local hIcon = makeLabel(header, isMaxed and "👑" or "⭐", 30, C.white, Enum.Font.GothamBold, UDim2.new(0, 44, 0, 44), UDim2.new(0, 12, 0.5, -22))
  hIcon.TextXAlignment = Enum.TextXAlignment.Center
  hIcon.TextYAlignment = Enum.TextYAlignment.Center

  makeLabel(header, isMaxed and "PRESTIGE MAX" or ("Prestige " .. prestigeCount), 18, isMaxed and Color3.fromRGB(255, 215, 0) or Color3.fromRGB(180, 80, 255), Enum.Font.GothamBlack, UDim2.new(1, -66, 0, 24), UDim2.new(0, 60, 0, 10))
  makeLabel(header, "+" .. bonusPercent .. "% coin bonus active", 12, C.muted, Enum.Font.Gotham, UDim2.new(1, -66, 0, 18), UDim2.new(0, 60, 0, 36))

  -- Stats card
  local statsCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 90), nil, 0)
  statsCard.LayoutOrder = 2
  makeCorner(statsCard, 12)

  makeLabel(statsCard, "Current Status", 13, C.white, Enum.Font.GothamBold, UDim2.new(1, -16, 0, 18), UDim2.new(0, 8, 0, 6)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(statsCard, "Upgrade Cap: Lv. " .. maxLevel, 12, C.muted, Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 28)).TextXAlignment = Enum.TextXAlignment.Left
  local requiredFollowers = math.floor(10000 * (1.5 ^ prestigeCount))
  local followerLabel = makeLabel(statsCard, "Followers: " .. fmt(followers) .. " / " .. fmt(requiredFollowers) .. " needed", 12, canPrestige and Color3.fromRGB(100, 255, 100) or C.muted, Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 46))
  followerLabel.TextXAlignment = Enum.TextXAlignment.Left
  if isMaxed then
    makeLabel(statsCard, "MAX PRESTIGE REACHED", 12, Color3.fromRGB(255, 215, 0), Enum.Font.GothamBold, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 64)).TextXAlignment = Enum.TextXAlignment.Left
  else
    makeLabel(statsCard, "Next prestige: Lv." .. nextMaxLevel .. " cap, +" .. ((prestigeCount + 1) * 20) .. "% coins", 12, Color3.fromRGB(180, 80, 255), Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 64)).TextXAlignment = Enum.TextXAlignment.Left
  end

  -- Info card
  local infoCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 130), nil, 0)
  infoCard.LayoutOrder = 3
  makeCorner(infoCard, 12)

  makeLabel(infoCard, "What happens on Prestige?", 13, C.white, Enum.Font.GothamBold, UDim2.new(1, -16, 0, 18), UDim2.new(0, 8, 0, 6)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(infoCard, "Keeps: PC, PC Upgrades, Slots, Earnings Bonus", 11, Color3.fromRGB(100, 255, 100), Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 28)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(infoCard, "Loses: Followers, Views, Coins", 11, Color3.fromRGB(255, 100, 100), Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 46)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(infoCard, "Loses: Phone Upgrades, Real Estate", 11, Color3.fromRGB(255, 100, 100), Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 64)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(infoCard, "Gets: 500 coins, higher cap, +" .. ((prestigeCount + 1) * 20) .. "% coins bonus", 11, Color3.fromRGB(180, 80, 255), Enum.Font.Gotham, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 82)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(infoCard, "Need " .. fmt(requiredFollowers) .. " followers to prestige (scales)", 11, C.muted, Enum.Font.GothamBold, UDim2.new(1, -16, 0, 16), UDim2.new(0, 8, 0, 104)).TextXAlignment = Enum.TextXAlignment.Left

  -- Button
  local btnCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 56), nil, 0)
  btnCard.LayoutOrder = 4
  makeCorner(btnCard, 12)

  local prestigeBtn = Instance.new("TextButton")
  prestigeBtn.Size = UDim2.new(1, -30, 0, 40)
  prestigeBtn.Position = UDim2.new(0, 15, 0.5, -20)
  prestigeBtn.Font = Enum.Font.GothamBlack
  prestigeBtn.TextSize = 16
  prestigeBtn.Parent = btnCard
  makeCorner(prestigeBtn, 10)
  makeStroke(prestigeBtn, Color3.fromRGB(120, 60, 180), 2, 0)

  local requiredFollowers = math.floor(10000 * (1.5 ^ prestigeCount))
  if isMaxed then
    prestigeBtn.Text = "👑 MAX PRESTIGE 👑"
    prestigeBtn.BackgroundColor3 = Color3.fromRGB(200, 170, 50)
    prestigeBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
    prestigeBtn.Active = false
  elseif canPrestige then
    prestigeBtn.Text = "⭐ PRESTIGE NOW ⭐"
    prestigeBtn.BackgroundColor3 = Color3.fromRGB(120, 50, 200)
    prestigeBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
  else
    prestigeBtn.Text = "Need " .. fmt(requiredFollowers) .. " Followers"
    prestigeBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
    prestigeBtn.TextColor3 = C.muted
    prestigeBtn.Active = false
    prestigeBtn.AutoButtonColor = false
  end

  prestigeBtn.MouseButton1Click:Connect(function()
    if isMaxed or not canPrestige then return end
    prestigeBtn.Text = "PRESTIGING..."
    prestigeBtn.Active = false
    Remotes:WaitForChild("Prestige"):FireServer()
  end)

  local function updatePrestigeButton()
    if isMaxed then return end
    local requiredFollowers = math.floor(10000 * (1.5 ^ prestigeCount))
    if followers >= requiredFollowers and not canPrestige then
      canPrestige = true
      prestigeBtn.Text = "⭐ PRESTIGE NOW ⭐"
      prestigeBtn.BackgroundColor3 = Color3.fromRGB(120, 50, 200)
      prestigeBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
      prestigeBtn.Active = true
      prestigeBtn.AutoButtonColor = true
    end
    followerLabel.Text = "Followers: " .. fmt(followers) .. " / " .. fmt(requiredFollowers) .. " needed"
    if followers >= requiredFollowers then
      followerLabel.TextColor3 = Color3.fromRGB(100, 255, 100)
    end
  end

  Remotes:WaitForChild("FollowersUpdated").OnClientEvent:Connect(function(newF)
    followers = newF or 0
    updatePrestigeButton()
  end)

  Remotes:WaitForChild("PrestigeResult").OnClientEvent:Connect(function(newCount, newCoins)
    canPrestige = false
    prestigeCount = newCount
    isMaxed = prestigeCount >= 10
    followers = 0
    local newRequired = math.floor(10000 * (1.5 ^ prestigeCount))
    followerLabel.Text = "Followers: 0 / " .. fmt(newRequired) .. " needed"
    followerLabel.TextColor3 = C.muted
    if isMaxed then
      prestigeBtn.Text = "👑 MAX PRESTIGE 👑"
      prestigeBtn.BackgroundColor3 = Color3.fromRGB(200, 170, 50)
      prestigeBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
      prestigeBtn.Active = false
    else
      prestigeBtn.Text = "Need " .. fmt(newRequired) .. " Followers"
      prestigeBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
      prestigeBtn.TextColor3 = C.muted
      prestigeBtn.Active = false
    end
  end)
end

return PrestigeApp
