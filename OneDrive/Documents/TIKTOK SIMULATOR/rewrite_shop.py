shop_code = """local ShopApp = {}

function ShopApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeCorner = env.makeCorner

  -- Header
  local title = Instance.new("TextLabel")
  title.Size = UDim2.new(1, 0, 0, 40)
  title.BackgroundTransparency = 1
  title.Text = "Upgrade Studio"
  title.TextColor3 = Color3.fromRGB(255, 255, 255)
  title.Font = Enum.Font.GothamBold
  title.TextSize = 24
  title.Parent = content

  local scrollFrame = Instance.new("ScrollingFrame")
  scrollFrame.Size = UDim2.new(1, 0, 1, -40)
  scrollFrame.Position = UDim2.new(0, 0, 0, 40)
  scrollFrame.BackgroundTransparency = 1
  scrollFrame.BorderSizePixel = 0
  scrollFrame.ScrollBarThickness = 4
  scrollFrame.Parent = content

  local uiPadding = Instance.new("UIPadding")
  uiPadding.PaddingTop = UDim.new(0, 10)
  uiPadding.PaddingLeft = UDim.new(0, 15)
  uiPadding.PaddingRight = UDim.new(0, 15)
  uiPadding.Parent = scrollFrame

  local listLayout = Instance.new("UIListLayout")
  listLayout.Parent = scrollFrame
  listLayout.Padding = UDim.new(0, 15)
  listLayout.SortOrder = Enum.SortOrder.LayoutOrder

  local upgrades = Remotes:WaitForChild("GetUpgrades"):InvokeServer()
  if not upgrades then upgrades = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0 } end

  local upgs = {
    { id = "ContentQuality", name = "Content Quality", desc = "+0.3 views per post per level" },
    { id = "EditSpeed",      name = "Edit Speed",      desc = "+0.05 coin rate per level" },
    { id = "PostFrequency",  name = "Post Frequency",  desc = "+1 passive view per second" },
    { id = "EngagementRate", name = "Engagement Rate", desc = "Lowers views needed per follower" }
  }

  local function getCost(level)
    return math.floor(50 * (1.35 ^ level))
  end

  local MAX_LEVEL = 10
  local refreshFuncs = {}

  for i, upg in ipairs(upgs) do
    local lvl = upgrades[upg.id] or 0

    local card = Instance.new("Frame")
    card.Size = UDim2.new(1, 0, 0, 85)
    card.BackgroundColor3 = Color3.fromRGB(30, 30, 35)
    card.Parent = scrollFrame
    makeCorner(card, 8)

    local leftContainer = Instance.new("Frame")
    leftContainer.Size = UDim2.new(0.65, 0, 1, 0)
    leftContainer.BackgroundTransparency = 1
    leftContainer.Parent = card

    local nameLbl = Instance.new("TextLabel")
    nameLbl.Size = UDim2.new(1, -10, 0, 25)
    nameLbl.Position = UDim2.new(0, 10, 0, 5)
    nameLbl.BackgroundTransparency = 1
    nameLbl.Text = upg.name
    nameLbl.TextColor3 = Color3.fromRGB(255, 255, 255)
    nameLbl.Font = Enum.Font.GothamBold
    nameLbl.TextSize = 14
    nameLbl.TextXAlignment = Enum.TextXAlignment.Left
    nameLbl.Parent = leftContainer

    local lvlLbl = Instance.new("TextLabel")
    lvlLbl.Size = UDim2.new(1, -10, 0, 20)
    lvlLbl.Position = UDim2.new(0, 10, 0, 30)
    lvlLbl.BackgroundTransparency = 1
    lvlLbl.Text = "Lv. " .. lvl
    lvlLbl.TextColor3 = Color3.fromRGB(150, 150, 160)
    lvlLbl.Font = Enum.Font.GothamBold
    lvlLbl.TextSize = 13
    lvlLbl.TextXAlignment = Enum.TextXAlignment.Left
    lvlLbl.Parent = leftContainer

    local descLbl = Instance.new("TextLabel")
    descLbl.Size = UDim2.new(1, -10, 0, 20)
    descLbl.Position = UDim2.new(0, 10, 0, 55)
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
    costLbl.Size = UDim2.new(1, -10, 0, 30)
    costLbl.Position = UDim2.new(0, 0, 0, 10)
    costLbl.BackgroundTransparency = 1
    costLbl.Text = getCost(lvl) .. " Coins"
    costLbl.TextColor3 = Color3.fromRGB(245, 158, 11) -- Gold
    costLbl.Font = Enum.Font.GothamBold
    costLbl.TextSize = 13
    costLbl.TextXAlignment = Enum.TextXAlignment.Right
    costLbl.Parent = rightContainer

    local btn = Instance.new("TextButton")
    btn.Size = UDim2.new(1, -10, 0, 30)
    btn.Position = UDim2.new(0, 0, 0, 45)
    btn.BackgroundColor3 = Color3.fromRGB(80, 200, 80)
    btn.Text = "UPGRADE"
    btn.TextColor3 = Color3.fromRGB(255, 255, 255)
    btn.Font = Enum.Font.GothamBold
    btn.TextSize = 13
    btn.Parent = rightContainer
    makeCorner(btn, 6)

    local function updateUI()
      local coins = 0
      if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
          coins = player.leaderstats.Coins.Value
      end

      local clvl = upgrades[upg.id] or 0
      lvlLbl.Text = "Lv. " .. clvl

      if clvl >= MAX_LEVEL then
          costLbl.Text = "---"
          btn.Text = "MAX"
          btn.BackgroundColor3 = Color3.fromRGB(80, 200, 80)
          btn.Active = false
          btn.AutoButtonColor = false
      else
          local cost = getCost(clvl)
          costLbl.Text = cost .. " Coins"
          if coins >= cost then
              btn.BackgroundColor3 = Color3.fromRGB(80, 200, 80)
              btn.Text = "UPGRADE"
              btn.Active = true
              btn.AutoButtonColor = true
          else
              btn.BackgroundColor3 = Color3.fromRGB(150, 150, 150)
              btn.Text = "UPGRADE"
              btn.Active = false
              btn.AutoButtonColor = false
          end
      end
    end

    table.insert(refreshFuncs, updateUI)

    btn.MouseButton1Click:Connect(function()
      if not btn.Active then return end
      local success, newLevel = Remotes:WaitForChild("PurchaseUpgrade"):InvokeServer(upg.id)
      if success then
        upgrades[upg.id] = newLevel
        for _, f in ipairs(refreshFuncs) do f() end
      end
    end)
  end

  scrollFrame.CanvasSize = UDim2.new(0, 0, 0, #upgs * 100)

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
