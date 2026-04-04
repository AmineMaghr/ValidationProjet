local SponsorshipsApp = {}

function SponsorshipsApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local TweenService = env.TweenService
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
  pad.PaddingTop = UDim.new(0, 10)
  pad.PaddingLeft = UDim.new(0, 16)
  pad.PaddingRight = UDim.new(0, 16)
  pad.PaddingBottom = UDim.new(0, 20)
  pad.Parent = scroll

  local layout = Instance.new("UIListLayout")
  layout.Padding = UDim.new(0, 12)
  layout.SortOrder = Enum.SortOrder.LayoutOrder
  layout.Parent = scroll

  layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 40)
  end)

  local function fetchData()
    local ok, res = pcall(function()
      return Remotes:WaitForChild("GetSponsorships"):InvokeServer()
    end)
    if ok and res then return res end
    return { followers = 0, activeSponsor = nil, remaining = 0, available = {} }
  end

  local data = fetchData()
  local followers = data.followers or 0

  -- Title
  local title = makeLabel(scroll, "🤝 Sponsorships", 20, C.white, Enum.Font.GothamBlack, UDim2.new(1, 0, 0, 28), nil)
  title.LayoutOrder = 1
  title.TextXAlignment = Enum.TextXAlignment.Center

  -- Follower bar
  local followerBar = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 38), nil, 0)
  followerBar.LayoutOrder = 2
  makeCorner(followerBar, 10)

  local followerLabel = makeLabel(followerBar, "👥 " .. fmt(followers) .. " Followers", 14, Color3.fromRGB(37, 244, 238), Enum.Font.GothamBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
  followerLabel.TextXAlignment = Enum.TextXAlignment.Center
  followerLabel.TextYAlignment = Enum.TextYAlignment.Center

  local updateElements = {}

  local evtConn = nil
  evtConn = Remotes:WaitForChild("FollowersUpdated").OnClientEvent:Connect(function(newF) 
    if not scroll.Parent then 
        if evtConn then evtConn:Disconnect() end
        return 
    end
    followers = newF or 0
    followerLabel.Text = "?? " .. fmt(followers) .. " Followers"
    
    for _, el in ipairs(updateElements) do
        if el.type == "lock" then
            el.inst.Text = fmt(followers) .. " / 500"
            if followers >= 500 then
                 if evtConn then evtConn:Disconnect() end
                 content:ClearAllChildren()
                 SponsorshipsApp.create(content, env)
                 break
            end
        elseif el.type == "btn" then
            if followers >= el.cost and el.btn.Text:find("Need") then
                if evtConn then evtConn:Disconnect() end
                content:ClearAllChildren()
                SponsorshipsApp.create(content, env)
                break
            end
        end
    end
  end)

  -- Active sponsor
  if data.activeSponsor then
    local s = data.activeSponsor
    local hasTimer = (data.remaining or 0) > 0

    local activeCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, hasTimer and 110 or 90), nil, 0)
    activeCard.LayoutOrder = 3
    makeCorner(activeCard, 12)
    makeStroke(activeCard, Color3.fromRGB(100, 255, 100), 2, 0)

    local aPad = Instance.new("UIPadding")
    aPad.PaddingLeft = UDim.new(0, 12)
    aPad.PaddingRight = UDim.new(0, 12)
    aPad.PaddingTop = UDim.new(0, 10)
    aPad.Parent = activeCard

    local aLayout = Instance.new("UIListLayout")
    aLayout.Padding = UDim.new(0, 4)
    aLayout.Parent = activeCard

    makeLabel(activeCard, "ACTIVE: " .. (s.emoji or "") .. " " .. (s.name or "?"), 15, Color3.fromRGB(100, 255, 100), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 22), nil)
    makeLabel(activeCard, "+" .. fmt(s.coinsPerSec or 0) .. " coins/sec", 13, Color3.fromRGB(255, 210, 50), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 20), nil)

    local timerLbl = nil
    if hasTimer then
      timerLbl = makeLabel(activeCard, "", 12, C.muted, Enum.Font.Gotham, UDim2.new(1, 0, 0, 18), nil)
      local remaining = data.remaining
      local mins = math.floor(remaining / 60)
      local secs = remaining % 60
      timerLbl.Text = string.format("⏱ %dm %02ds remaining", mins, secs)

      task.spawn(function()
        while remaining > 0 and timerLbl and timerLbl.Parent do
          task.wait(1)
          remaining = remaining - 1
          local m = math.floor(remaining / 60)
          local s2 = remaining % 60
          timerLbl.Text = string.format("⏱ %dm %02ds remaining", m, s2)
        end
        if timerLbl and timerLbl.Parent then
          timerLbl.Text = "Deal complete! Getting new offers..."
          timerLbl.TextColor3 = Color3.fromRGB(255, 210, 50)
          task.wait(1)
          content:ClearAllChildren()
          SponsorshipsApp.create(content, env)
        end
      end)
    end

  -- Offers
  elseif followers < 500 then
    local lockCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 60), nil, 0)
    lockCard.LayoutOrder = 3
    makeCorner(lockCard, 12)
    local lockLabel = makeLabel(lockCard, "?? Unlocks at 500 Followers", 15, C.muted, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 24), UDim2.new(0, 0, 0, 10))   
    lockLabel.TextXAlignment = Enum.TextXAlignment.Center
    local lockProg = makeLabel(lockCard, fmt(followers) .. " / 500", 12, Color3.fromRGB(100, 100, 100), Enum.Font.Gotham, UDim2.new(1, 0, 0, 16), UDim2.new(0, 0, 0, 34))
    lockProg.TextXAlignment = Enum.TextXAlignment.Center
    
    table.insert(updateElements, {type = "lock", inst = lockProg})

  elseif #data.available > 0 then
    local chooseLabel = makeLabel(scroll, "Pick One", 14, C.white, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 20), nil)
    chooseLabel.LayoutOrder = 3
    chooseLabel.TextXAlignment = Enum.TextXAlignment.Center

    for i, offer in ipairs(data.available) do
      local cardColor = i == 1 and Color3.fromRGB(255, 180, 50) or Color3.fromRGB(50, 150, 255)
      local card = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 130), nil, 0)
      card.LayoutOrder = 3 + i
      makeCorner(card, 12)
      makeStroke(card, cardColor, 2, 0)

      local cPad = Instance.new("UIPadding")
      cPad.PaddingLeft = UDim.new(0, 12)
      cPad.PaddingRight = UDim.new(0, 12)
      cPad.PaddingTop = UDim.new(0, 10)
      cPad.Parent = card

      local cLayout = Instance.new("UIListLayout")
      cLayout.Padding = UDim.new(0, 3)
      cLayout.Parent = card

      makeLabel(card, (offer.emoji or "🤝") .. " " .. (offer.name or "?"), 16, C.white, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 24), nil)
      makeLabel(card, "Cost: " .. fmt(offer.followerCost) .. " followers", 12, Color3.fromRGB(255, 100, 100), Enum.Font.Gotham, UDim2.new(1, 0, 0, 18), nil)

      if offer.lumpSum then
        makeLabel(card, "+" .. fmt(offer.lumpSum) .. " coins upfront", 12, Color3.fromRGB(100, 255, 100), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 18), nil)
        makeLabel(card, "+" .. fmt(offer.coinsPerSec or 0) .. " coins/sec for " .. (offer.duration or 0) .. " min", 12, Color3.fromRGB(100, 255, 100), Enum.Font.Gotham, UDim2.new(1, 0, 0, 18), nil)
      else
        makeLabel(card, "+" .. fmt(offer.coinsPerSec or 0) .. " coins/sec for " .. (offer.duration or 0) .. " min", 12, Color3.fromRGB(100, 255, 100), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 18), nil)
      end

      local btn = Instance.new("TextButton")
      btn.Size = UDim2.new(1, 0, 0, 30)
      btn.Font = Enum.Font.GothamBold
      btn.TextSize = 14
      btn.Parent = card
      makeCorner(btn, 8)

      if followers >= (offer.followerCost or 0) then
        btn.Text = "ACCEPT DEAL"
        btn.BackgroundColor3 = cardColor
        btn.TextColor3 = Color3.fromRGB(255, 255, 255)
        local idx = i
        btn.MouseButton1Click:Connect(function()
          btn.Text = "Accepting..."
          btn.Active = false
          local success = Remotes:WaitForChild("AcceptSponsor"):InvokeServer(idx)
          if success then
            content:ClearAllChildren()
            SponsorshipsApp.create(content, env)
          else
            btn.Text = "Failed"
            btn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
          end
        end)
      else
        btn.Text = "Need " .. fmt(offer.followerCost) .. " followers"
        btn.BackgroundColor3 = Color3.fromRGB(50, 50, 50)
        btn.TextColor3 = C.muted
        btn.Active = false
        table.insert(updateElements, {type = "btn", btn = btn, cost = offer.followerCost or 0})
      end
    end
  end
end

return SponsorshipsApp
