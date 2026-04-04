shop_code = """local TweenService = game:GetService("TweenService")
local ShopApp = {}

function ShopApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeCorner = env.makeCorner

  -- Header
  local title = Instance.new("TextLabel")
  title.Size = UDim2.new(1, 0, 0, 45)
  title.BackgroundTransparency = 1
  title.Text = "🚀 Upgrade Studio"
  title.TextColor3 = Color3.fromRGB(255, 255, 255)
  title.Font = Enum.Font.GothamBlack
  title.TextSize = 22
  title.Parent = content

  local titleStroke = Instance.new("UIStroke")
  titleStroke.Color = Color3.fromRGB(0, 0, 0)
  titleStroke.Thickness = 2
  titleStroke.Transparency = 0.5
  titleStroke.Parent = title

  local scrollFrame = Instance.new("ScrollingFrame")
  scrollFrame.Size = UDim2.new(1, 0, 1, -45)
  scrollFrame.Position = UDim2.new(0, 0, 0, 45)
  scrollFrame.BackgroundTransparency = 1
  scrollFrame.BorderSizePixel = 0
  scrollFrame.ScrollBarThickness = 3
  scrollFrame.ScrollBarImageColor3 = Color3.fromRGB(120, 120, 130)
  scrollFrame.Parent = content

  local uiPadding = Instance.new("UIPadding")
  uiPadding.PaddingTop = UDim.new(0, 5)
  uiPadding.PaddingLeft = UDim.new(0, 15)
  uiPadding.PaddingRight = UDim.new(0, 15)
  uiPadding.PaddingBottom = UDim.new(0, 20)
  uiPadding.Parent = scrollFrame

  local listLayout = Instance.new("UIListLayout")
  listLayout.Parent = scrollFrame
  listLayout.Padding = UDim.new(0, 12)
  listLayout.SortOrder = Enum.SortOrder.LayoutOrder

  local upgrades = Remotes:WaitForChild("GetUpgrades"):InvokeServer()
  if not upgrades then upgrades = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0 } end

  local upgs = {
    { id = "ContentQuality", name = "📹 Content Quality", desc = "+0.3 views/post per level" },
    { id = "EditSpeed",      name = "⚡ Edit Speed",      desc = "+0.05 coin rate per level" },
    { id = "PostFrequency",  name = "⏰ Post Frequency",  desc = "+1 passive view/sec" },
    { id = "EngagementRate", name = "❤️ Engagement Rate", desc = "Lowers views per follower" }
  }

  local function getCost(level)
    return math.floor(50 * (1.35 ^ level))
  end

  local MAX_LEVEL = 10
  local refreshFuncs = {}

  for i, upg in ipairs(upgs) do
    local card = Instance.new("Frame")
    card.Size = UDim2.new(1, 0, 0, 95)
    card.BackgroundColor3 = Color3.fromRGB(25, 25, 30)
    card.Parent = scrollFrame
    makeCorner(card, 10)
    
    local cardStroke = Instance.new("UIStroke")
    cardStroke.Color = Color3.fromRGB(50, 50, 60)
    cardStroke.Thickness = 1.5
    cardStroke.Parent = card

    local leftContainer = Instance.new("Frame")
    leftContainer.Size = UDim2.new(0.65, 0, 1, 0)
    leftContainer.BackgroundTransparency = 1
    leftContainer.Parent = card

    local nameLbl = Instance.new("TextLabel")
    nameLbl.Size = UDim2.new(1, -10, 0, 25)
    nameLbl.Position = UDim2.new(0, 12, 0, 10)
    nameLbl.BackgroundTransparency = 1
    nameLbl.Text = upg.name
    nameLbl.TextColor3 = Color3.fromRGB(255, 255, 255)
    nameLbl.Font = Enum.Font.GothamBold
    nameLbl.TextSize = 14
    nameLbl.TextXAlignment = Enum.TextXAlignment.Left
    nameLbl.Parent = leftContainer

    local lvlLbl = Instance.new("TextLabel")
    lvlLbl.Size = UDim2.new(1, -10, 0, 20)
    lvlLbl.Position = UDim2.new(0, 12, 0, 36)
    lvlLbl.BackgroundTransparency = 1
    lvlLbl.Text = "Lv. 0"
    lvlLbl.TextColor3 = Color3.fromRGB(150, 150, 160)
    lvlLbl.Font = Enum.Font.GothamBold
    lvlLbl.TextSize = 13
    lvlLbl.TextXAlignment = Enum.TextXAlignment.Left
    lvlLbl.Parent = leftContainer

    local descLbl = Instance.new("TextLabel")
    descLbl.Size = UDim2.new(1, -10, 0, 20)
    descLbl.Position = UDim2.new(0, 12, 0, 60)
    descLbl.BackgroundTransparency = 1
    descLbl.Text = upg.desc
    descLbl.TextColor3 = Color3.fromRGB(120, 120, 130)
    descLbl.Font = Enum.Font.Gotham
    descLbl.TextSize = 11
    descLbl.TextXAlignment = Enum.TextXAlignment.Left
    descLbl.Parent = leftContainer

    local rightContainer = Instance.new("Frame")
    rightContainer.Size = UDim2.new(0.35, 0, 1, 0)
    rightContainer.Position = UDim2.new(0.65, 0, 0, 0)
    rightContainer.BackgroundTransparency = 1
    rightContainer.Parent = card

    local costLbl = Instance.new("TextLabel")
    costLbl.Size = UDim2.new(1, -15, 0, 30)
    costLbl.Position = UDim2.new(0, 0, 0, 14)
    costLbl.BackgroundTransparency = 1
    costLbl.Text = "0 🪙"
    costLbl.TextColor3 = Color3.fromRGB(255, 210, 50)
    costLbl.Font = Enum.Font.GothamBold
    costLbl.TextSize = 14
    costLbl.TextXAlignment = Enum.TextXAlignment.Right
    costLbl.Parent = rightContainer
    
    local costStroke = Instance.new("UIStroke")
    costStroke.Color = Color3.fromRGB(60, 40, 0)
    costStroke.Thickness = 1
    costStroke.Transparency = 0.5
    costStroke.Parent = costLbl

    local btn = Instance.new("TextButton")
    btn.Size = UDim2.new(1, -15, 0, 34)
    btn.Position = UDim2.new(0, 0, 0, 48)
    btn.BackgroundColor3 = Color3.fromRGB(60, 200, 80)
    btn.Text = "UPGRADE"
    btn.TextColor3 = Color3.fromRGB(255, 255, 255)
    btn.Font = Enum.Font.GothamBold
    btn.TextSize = 12
    btn.Parent = rightContainer
    makeCorner(btn, 8)
    
    local btnStroke = Instance.new("UIStroke")
    btnStroke.Color = Color3.fromRGB(30, 120, 40)
    btnStroke.Thickness = 2
    btnStroke.ApplyStrokeMode = Enum.ApplyStrokeMode.Border
    btnStroke.Parent = btn

    local function updateUI()
      local coins = 0
      if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
          coins = player.leaderstats.Coins.Value
      end

      local clvl = upgrades[upg.id] or 0
      lvlLbl.Text = "Lv. " .. clvl

      if clvl >= MAX_LEVEL then
          costLbl.Text = "MAXED"
          costLbl.TextColor3 = Color3.fromRGB(150, 255, 150)
          btn.Text = "MAX"
          btn.BackgroundColor3 = Color3.fromRGB(40, 160, 60)
          btnStroke.Color = Color3.fromRGB(20, 80, 30)
          btn.TextColor3 = Color3.fromRGB(200, 255, 200)
          btn.Active = false
          btn.AutoButtonColor = false
      else
          local cost = getCost(clvl)
          costLbl.Text = cost .. " 🪙"
          costLbl.TextColor3 = Color3.fromRGB(255, 210, 50)
          if coins >= cost then
              btn.BackgroundColor3 = Color3.fromRGB(60, 200, 80)
              btnStroke.Color = Color3.fromRGB(30, 120, 40)
              btn.Text = "UPGRADE"
              btn.Active = true
              btn.AutoButtonColor = true
              btn.TextColor3 = Color3.fromRGB(255, 255, 255)
          else
              btn.BackgroundColor3 = Color3.fromRGB(50, 50, 55)
              btnStroke.Color = Color3.fromRGB(30, 30, 35)
              btn.Text = "LOCKED"
              btn.Active = false
              btn.AutoButtonColor = false
              btn.TextColor3 = Color3.fromRGB(130, 130, 140)
          end
      end
    end

    table.insert(refreshFuncs, updateUI)

    btn.MouseButton1Click:Connect(function()
      if not btn.Active then return end
      
      local tinfo = TweenInfo.new(0.08, Enum.EasingStyle.Quad, Enum.EasingDirection.Out, 0, true)
      TweenService:Create(btn, tinfo, {Size = UDim2.new(1, -19, 0, 30), Position = UDim2.new(0, 2, 0, 50)}):Play()
      
      local success, newLevel = Remotes:WaitForChild("PurchaseUpgrade"):InvokeServer(upg.id)
      if success then
        upgrades[upg.id] = newLevel
        for _, f in ipairs(refreshFuncs) do f() end
      end
    end)
  end

  scrollFrame.CanvasSize = UDim2.new(0, 0, 0, #upgs * 115)

  local function globalRefresh()
      for _, f in ipairs(refreshFuncs) do f() end
  end

  globalRefresh()
  if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
      player.leaderstats.Coins.Changed:Connect(globalRefresh)
  end
end

return ShopApp
"""

with open("src/client/PhoneModules/ShopApp.lua", "w", encoding="utf-8") as f:
    f.write(shop_code)
print("Finished rewriting ShopApp")
