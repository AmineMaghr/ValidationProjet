local RealEstateApp = {}

function RealEstateApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
  local fmt = env.fmt
  local C = env.C

  local HOME_TIERS = {
    { name = "Shack",      cost = 0,      income = 1,    emoji = "🏚️", color = Color3.fromRGB(139, 119, 101) },
    { name = "Studio",     cost = 5000,   income = 5,    emoji = "🏠", color = Color3.fromRGB(100, 150, 100) },
    { name = "Apartment",  cost = 25000,  income = 15,   emoji = "🏢", color = Color3.fromRGB(100, 150, 200) },
    { name = "House",      cost = 100000, income = 50,   emoji = "🏡", color = Color3.fromRGB(200, 150, 50) },
    { name = "Mansion",    cost = 500000, income = 200,  emoji = "🏰", color = Color3.fromRGB(200, 50, 50) },
    { name = "Penthouse",  cost = 2000000, income = 1000, emoji = "🌆", color = Color3.fromRGB(180, 80, 255) },
  }

  local homeData = nil
  pcall(function()
    homeData = Remotes:WaitForChild("GetHomeData"):InvokeServer()
  end)

  local ownedSlot = homeData and homeData.ownedSlot or 0
  local homeTier = homeData and homeData.homeTier or 0
  local slotCount = homeData and homeData.slotCount or 6

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

  -- Current home info
  if ownedSlot > 0 then
    local tier = HOME_TIERS[homeTier] or HOME_TIERS[1]
    local infoCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 100), nil, 0)
    infoCard.LayoutOrder = 1
    makeCorner(infoCard, 12)
    makeStroke(infoCard, tier.color, 2, 0)

    local tierIcon = makeLabel(infoCard, tier.emoji, 34, C.white, Enum.Font.GothamBold, UDim2.new(0, 44, 0, 44), UDim2.new(0, 12, 0, 8))
    tierIcon.TextXAlignment = Enum.TextXAlignment.Center
    tierIcon.TextYAlignment = Enum.TextYAlignment.Center

    makeLabel(infoCard, tier.name, 18, tier.color, Enum.Font.GothamBlack, UDim2.new(1, -66, 0, 24), UDim2.new(0, 60, 0, 8)).TextXAlignment = Enum.TextXAlignment.Left
    makeLabel(infoCard, "Slot #" .. ownedSlot, 12, C.muted, Enum.Font.Gotham, UDim2.new(1, -66, 0, 16), UDim2.new(0, 60, 0, 32)).TextXAlignment = Enum.TextXAlignment.Left
    makeLabel(infoCard, "+" .. tier.income .. " coins/min passive", 12, Color3.fromRGB(100, 255, 100), Enum.Font.GothamBold, UDim2.new(1, -66, 0, 16), UDim2.new(0, 60, 0, 50)).TextXAlignment = Enum.TextXAlignment.Left
    makeLabel(infoCard, "Sell value: " .. fmt(math.floor(tier.cost * 0.6)) .. " coins", 11, C.muted, Enum.Font.Gotham, UDim2.new(1, -66, 0, 14), UDim2.new(0, 60, 0, 72)).TextXAlignment = Enum.TextXAlignment.Left
  else
    local noHome = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 60), nil, 0)
    noHome.LayoutOrder = 1
    makeCorner(noHome, 12)
    local noLabel = makeLabel(noHome, "No home yet!\nClaim a slot below.", 13, C.muted, Enum.Font.GothamBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    noLabel.TextXAlignment = Enum.TextXAlignment.Center
    noLabel.TextYAlignment = Enum.TextYAlignment.Center
  end

  if ownedSlot == 0 then
    -- Show slot claiming grid
    local sectionTitle = makeLabel(scroll, "Claim a Slot (Free)", 14, C.white, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 22), nil)
    sectionTitle.LayoutOrder = 2
    sectionTitle.TextXAlignment = Enum.TextXAlignment.Center

    for i = 1, math.min(slotCount, 6) do
      local slotCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 50), nil, 0)
      slotCard.LayoutOrder = 2 + i
      makeCorner(slotCard, 10)

      makeLabel(slotCard, "Slot #" .. i, 15, C.white, Enum.Font.GothamBold, UDim2.new(0, 90, 1, 0), UDim2.new(0, 12, 0, 0)).TextYAlignment = Enum.TextYAlignment.Center

      local claimBtn = Instance.new("TextButton")
      claimBtn.Size = UDim2.new(0, 110, 0, 34)
      claimBtn.Position = UDim2.new(1, -125, 0.5, -17)
      claimBtn.BackgroundColor3 = Color3.fromRGB(60, 200, 80)
      claimBtn.Text = "CLAIM"
      claimBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
      claimBtn.Font = Enum.Font.GothamBold
      claimBtn.TextSize = 14
      claimBtn.Parent = slotCard
      makeCorner(claimBtn, 8)

      claimBtn.MouseButton1Click:Connect(function()
        claimBtn.Text = "..."
        claimBtn.Active = false
        local success = Remotes:WaitForChild("ClaimHome"):InvokeServer(i)
        if success then
          content:ClearAllChildren()
          RealEstateApp.create(content, env)
        else
          claimBtn.Text = "Failed"
          task.delay(1, function() claimBtn.Text = "CLAIM" claimBtn.Active = true end)
        end
      end)
    end
  else
    -- Show upgrade tiers
    local sectionTitle = makeLabel(scroll, "Upgrade Path", 14, C.white, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 22), nil)
    sectionTitle.LayoutOrder = 2
    sectionTitle.TextXAlignment = Enum.TextXAlignment.Center

    for i, tier in ipairs(HOME_TIERS) do
      local tierCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 65), nil, 0)
      tierCard.LayoutOrder = 2 + i
      makeCorner(tierCard, 10)
      local tierStroke = makeStroke(tierCard, C.border, 1.5, 0)

      local tierIcon = makeLabel(tierCard, tier.emoji, 26, C.white, Enum.Font.GothamBold, UDim2.new(0, 36, 0, 36), UDim2.new(0, 8, 0.5, -18))
      tierIcon.TextXAlignment = Enum.TextXAlignment.Center
      tierIcon.TextYAlignment = Enum.TextYAlignment.Center

      makeLabel(tierCard, tier.name, 14, C.white, Enum.Font.GothamBold, UDim2.new(1, -150, 0, 20), UDim2.new(0, 50, 0, 8)).TextXAlignment = Enum.TextXAlignment.Left
      makeLabel(tierCard, "+" .. tier.income .. " coins/min", 11, Color3.fromRGB(100, 255, 100), Enum.Font.Gotham, UDim2.new(1, -150, 0, 16), UDim2.new(0, 50, 0, 28)).TextXAlignment = Enum.TextXAlignment.Left
      makeLabel(tierCard, tier.cost > 0 and fmt(tier.cost) .. " 💰" or "FREE", 11, C.muted, Enum.Font.Gotham, UDim2.new(1, -150, 0, 14), UDim2.new(0, 50, 0, 44)).TextXAlignment = Enum.TextXAlignment.Left

      local actionBtn = Instance.new("TextButton")
      actionBtn.Size = UDim2.new(0, 90, 0, 32)
      actionBtn.Position = UDim2.new(1, -104, 0.5, -16)
      actionBtn.Font = Enum.Font.GothamBold
      actionBtn.TextSize = 13
      actionBtn.Parent = tierCard
      makeCorner(actionBtn, 8)

      if i == homeTier then
        actionBtn.Text = "CURRENT"
        actionBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
        actionBtn.TextColor3 = C.muted
        actionBtn.Active = false
        tierStroke.Color = tier.color
      elseif i == homeTier + 1 then
        actionBtn.Text = "UPGRADE"
        actionBtn.BackgroundColor3 = tier.color
        actionBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
        actionBtn.MouseButton1Click:Connect(function()
          actionBtn.Text = "..."
          actionBtn.Active = false
          local success = Remotes:WaitForChild("UpgradeHome"):InvokeServer()
          if success then
            content:ClearAllChildren()
            RealEstateApp.create(content, env)
          else
            actionBtn.Text = "No coins!"
            actionBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
            task.delay(1.5, function()
              actionBtn.Text = "UPGRADE"
              actionBtn.BackgroundColor3 = tier.color
              actionBtn.Active = true
            end)
          end
        end)
      elseif i < homeTier then
        actionBtn.Text = "OWNED"
        actionBtn.BackgroundColor3 = Color3.fromRGB(40, 120, 50)
        actionBtn.TextColor3 = Color3.fromRGB(150, 255, 150)
        actionBtn.Active = false
      else
        actionBtn.Text = "LOCKED"
        actionBtn.BackgroundColor3 = Color3.fromRGB(40, 40, 40)
        actionBtn.TextColor3 = Color3.fromRGB(100, 100, 100)
        actionBtn.Active = false
      end
    end
  end
end

return RealEstateApp
