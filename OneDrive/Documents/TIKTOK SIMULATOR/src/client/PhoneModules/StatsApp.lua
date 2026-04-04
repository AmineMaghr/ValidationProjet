local StatsApp = {}

function StatsApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
  local fmt = env.fmt
  local C = env.C

  local statsData = nil
  pcall(function()
    statsData = Remotes:WaitForChild("GetStats"):InvokeServer()
  end)

  if not statsData then
    statsData = {
      totalViews = 0, followers = 0, coins = 0,
      postCount = 0, prestigeCount = 0,
      contentType = "Short Videos", hasPC = false,
      unlockedSlots = 1, homeTier = 0,
    }
  end

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
  layout.Padding = UDim.new(0, 6)
  layout.SortOrder = Enum.SortOrder.LayoutOrder
  layout.Parent = scroll

  layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 40)
  end)

  local function statRow(label, value, order, color)
    local row = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 36), nil, 0)
    row.LayoutOrder = order
    makeCorner(row, 8)

    local lbl = makeLabel(row, label, 12, C.muted, Enum.Font.Gotham, UDim2.new(0.5, 0, 1, 0), UDim2.new(0, 10, 0, 0))
    lbl.TextXAlignment = Enum.TextXAlignment.Left
    lbl.TextYAlignment = Enum.TextYAlignment.Center

    local val = makeLabel(row, tostring(value), 14, color or C.white, Enum.Font.GothamBold, UDim2.new(0.45, -10, 1, 0), UDim2.new(0.55, 0, 0, 0))
    val.TextXAlignment = Enum.TextXAlignment.Right
    val.TextYAlignment = Enum.TextYAlignment.Center
  end

  local function sectionTitle(text, order, icon)
    local t = makeLabel(scroll, (icon or "") .. " " .. text, 13, C.white, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 20), nil)
    t.LayoutOrder = order
    t.TextXAlignment = Enum.TextXAlignment.Left
  end

  -- Profile header
  local header = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 56), nil, 0)
  header.LayoutOrder = 1
  makeCorner(header, 12)
  makeStroke(header, C.indigo, 2, 0)

  local pfp = Instance.new("ImageLabel")
  pfp.Size = UDim2.new(0, 40, 0, 40)
  pfp.Position = UDim2.new(0, 10, 0.5, -20)
  pfp.BackgroundTransparency = 1
  pfp.Parent = header
  makeCorner(pfp, 20)
  task.spawn(function()
    pcall(function()
      pfp.Image = game:GetService("Players"):GetUserThumbnailAsync(player.UserId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size100x100)
    end)
  end)

  makeLabel(header, player.Name, 16, C.white, Enum.Font.GothamBlack, UDim2.new(1, -62, 0, 22), UDim2.new(0, 56, 0, 8)).TextXAlignment = Enum.TextXAlignment.Left
  local rankLabel = makeLabel(header, statsData.prestigeCount > 0 and ("Prestige " .. statsData.prestigeCount) or "New Creator", 11, statsData.prestigeCount > 0 and Color3.fromRGB(180, 80, 255) or C.muted, Enum.Font.GothamBold, UDim2.new(1, -62, 0, 16), UDim2.new(0, 56, 0, 32))
  rankLabel.TextXAlignment = Enum.TextXAlignment.Left

  -- Overview
  sectionTitle("📊 Overview", 2)
  statRow("Total Views", fmt(statsData.totalViews), 3, C.indigo)
  statRow("Followers", fmt(statsData.followers), 4, Color3.fromRGB(37, 244, 238))
  statRow("Coins", fmt(statsData.coins), 5, Color3.fromRGB(255, 210, 50))
  statRow("Posts Made", fmt(statsData.postCount), 6, C.white)

  -- Progress
  sectionTitle("🚀 Progress", 7, "🚀")
  statRow("Content Type", statsData.contentType or "Short Videos", 8)
  statRow("Has PC", statsData.hasPC and "Yes ✓" or "No", 9, statsData.hasPC and Color3.fromRGB(100, 255, 100) or Color3.fromRGB(255, 100, 100))
  statRow("PC Level", "Lv. " .. (statsData.pcLevel or 1), 10, Color3.fromRGB(59, 130, 246))
  statRow("Video Slots", (statsData.unlockedSlots or 1) .. " unlocked", 11)
  statRow("Prestige", statsData.prestigeCount, 12, Color3.fromRGB(180, 80, 255))
  statRow("Coin Bonus", "+" .. (statsData.prestigeCount * 20) .. "%", 13, Color3.fromRGB(180, 80, 255))
end

return StatsApp
