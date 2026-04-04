local LeaderboardApp = {}

function LeaderboardApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local TweenService = env.TweenService
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
  local fmt = env.fmt
  local C = env.C
  local FONT_BOLD = env.FONT_BOLD

  local function fmtBig(n)
    n = math.floor(n or 0)
    if n >= 1e12 then return string.format("%.1fT", n/1e12)
    elseif n >= 1e9 then return string.format("%.1fB", n/1e9)
    elseif n >= 1e6 then return string.format("%.1fM", n/1e6)
    elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
    else return tostring(n) end
  end

  local scroll = Instance.new("ScrollingFrame")
  scroll.Size = UDim2.new(1, 0, 1, 0)
  scroll.BackgroundTransparency = 1
  scroll.ScrollBarThickness = 3
  scroll.ScrollBarImageColor3 = Color3.fromRGB(120, 120, 130)
  scroll.CanvasSize = UDim2.new(0, 0, 0, 700)
  scroll.Parent = content
  scroll.ClipsDescendants = true

  local pad = Instance.new("UIPadding")
  pad.PaddingTop = UDim.new(0, 8)
  pad.PaddingLeft = UDim.new(0, 12)
  pad.PaddingRight = UDim.new(0, 12)
  pad.PaddingBottom = UDim.new(0, 20)
  pad.Parent = scroll

  local layout = Instance.new("UIListLayout")
  layout.Padding = UDim.new(0, 8)
  layout.SortOrder = Enum.SortOrder.LayoutOrder
  layout.Parent = scroll

  layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 40)
  end)

  local titleCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 70), nil, 0)
  titleCard.LayoutOrder = 1
  makeCorner(titleCard, 14)
  makeStroke(titleCard, Color3.fromRGB(255, 215, 0), 2, 0)

  makeLabel(titleCard, "🏆 Top Creators", 20, Color3.fromRGB(255, 215, 0), FONT_BOLD, UDim2.new(1, 0, 0, 22), UDim2.new(0, 0, 0, 0)).TextXAlignment = Enum.TextXAlignment.Center
  makeLabel(titleCard, "All-Time Total Views", 12, C.muted, Enum.Font.Gotham, UDim2.new(1, 0, 0, 18), UDim2.new(0, 0, 0, 26)).TextXAlignment = Enum.TextXAlignment.Center

  local columnHeader = makeFrame(scroll, C.bg, UDim2.new(1, 0, 0, 30), nil, 0)
  columnHeader.LayoutOrder = 2
  makeLabel(columnHeader, "#", 12, C.muted, FONT_BOLD, UDim2.new(0, 30, 1, 0), UDim2.new(0, 8, 0, 0)).TextXAlignment = Enum.TextXAlignment.Center
  makeLabel(columnHeader, "Player", 12, C.muted, FONT_BOLD, UDim2.new(0.4, 0, 1, 0), UDim2.new(0.3, 0, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
  makeLabel(columnHeader, "Views", 12, C.muted, FONT_BOLD, UDim2.new(0.3, 0, 1, 0), UDim2.new(0.65, 0, 0, 0)).TextXAlignment = Enum.TextXAlignment.Right

  local listContainer = makeFrame(scroll, C.bg, UDim2.new(1, 0, 0, 10), nil, 0)
  listContainer.LayoutOrder = 3
  listContainer.ClipsDescendants = true
  local listAuto = Instance.new("UIListLayout")
  listAuto.Padding = UDim.new(0, 6)
  listAuto.Parent = listContainer
  listAuto:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    listContainer.Size = UDim2.new(1, 0, 0, listAuto.AbsoluteContentSize.Y)
  end)

  local myRankCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 50), nil, 0)
  myRankCard.LayoutOrder = 4
  myRankCard.BackgroundTransparency = 0.3
  makeCorner(myRankCard, 10)
  makeStroke(myRankCard, Color3.fromRGB(37, 244, 238), 2, 0)

  makeLabel(myRankCard, "Your Rank:", 14, Color3.fromRGB(37, 244, 238), FONT_BOLD, UDim2.new(0.5, 0, 1, 0), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left

  local myRankLbl = makeLabel(myRankCard, "#--", 18, C.white, FONT_BOLD, UDim2.new(0.5, 0, 1, 0), UDim2.new(0.5, -20, 0, 0))
  myRankLbl.TextXAlignment = Enum.TextXAlignment.Center

  local refreshBtn = Instance.new("TextButton")
  refreshBtn.Size = UDim2.new(0, 120, 0, 36)
  refreshBtn.BackgroundColor3 = Color3.fromRGB(40, 30, 60)
  refreshBtn.Text = "🔄 Refresh"
  refreshBtn.TextColor3 = C.white
  refreshBtn.Font = FONT_BOLD
  refreshBtn.TextSize = 14
  refreshBtn.Parent = scroll
  refreshBtn.LayoutOrder = 5
  makeCorner(refreshBtn, 10)
  makeStroke(refreshBtn, Color3.fromRGB(100, 60, 160), 1, 0)

  local rankColors = {
    Color3.fromRGB(255, 215, 0),
    Color3.fromRGB(192, 192, 192),
    Color3.fromRGB(205, 127, 50),
    Color3.fromRGB(100, 200, 100),
    Color3.fromRGB(100, 200, 100),
    Color3.fromRGB(100, 200, 100),
    Color3.fromRGB(100, 200, 100),
    Color3.fromRGB(150, 150, 150),
    Color3.fromRGB(150, 150, 150),
    Color3.fromRGB(150, 150, 150),
  }

  local function renderLeaderboard(data)
    for _, c in ipairs(listContainer:GetChildren()) do
      if c:IsA("Frame") then c:Destroy() end
    end

    local top = data.top or {}
    for i, entry in ipairs(top) do
      local isTop3 = i <= 3
      local bgColor = i == 1 and Color3.fromRGB(30, 25, 10)
                   or i == 2 and Color3.fromRGB(25, 25, 20)
                   or i == 3 and Color3.fromRGB(30, 20, 15)
                   or C.card

      local row = makeFrame(listContainer, bgColor, UDim2.new(1, 0, 0, isTop3 and 52 or 44), nil, 0)
      makeCorner(row, 8)
      if isTop3 then
        makeStroke(row, rankColors[i], 1.5, 0)
      end

      local rankLbl = makeLabel(row, "#" .. i, isTop3 and 16 or 14, isTop3 and rankColors[i] or C.muted, FONT_BOLD, UDim2.new(0, 36, 1, 0), UDim2.new(0, 6, 0, 0))
      rankLbl.TextXAlignment = Enum.TextXAlignment.Center

      local emoji = ""
      if i == 1 then emoji = "🥇"
      elseif i == 2 then emoji = "🥈"
      elseif i == 3 then emoji = "🥉"
      elseif entry.prestige and entry.prestige > 0 then emoji = "👑"
      else emoji = "🎬"
      end

      makeLabel(row, emoji .. " " .. tostring(entry.name or "Unknown"), isTop3 and 14 or 13, isTop3 and Color3.new(1,1,1) or C.white, FONT_BOLD, UDim2.new(0.4, 0, 1, 0), UDim2.new(0.35, 0, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left

      local viewLbl = makeLabel(row, fmtBig(entry.totalViews or 0) .. " views", isTop3 and 13 or 12, isTop3 and rankColors[i] or Color3.fromRGB(37, 244, 238), FONT_BOLD, UDim2.new(0.35, 0, 1, 0), UDim2.new(0.62, 0, 0, 0))
      viewLbl.TextXAlignment = Enum.TextXAlignment.Right

      if entry.prestige and entry.prestige > 0 then
        makeLabel(row, "P" .. entry.prestige, 10, Color3.fromRGB(180, 80, 255), FONT_BOLD, UDim2.new(0.15, 0, 1, 0), UDim2.new(0.82, 0, 0, 0)).TextXAlignment = Enum.TextXAlignment.Right
      end
    end

    if #top == 0 then
      makeLabel(listContainer, "No data yet — be the first!", 14, C.muted, Enum.Font.Gotham, UDim2.new(1, 0, 0, 40), nil).TextXAlignment = Enum.TextXAlignment.Center
    end

    myRankLbl.Text = "#" .. (data.myRank and data.myRank > 0 and data.myRank or "--")
  end

  local function fetch()
    pcall(function()
      local result = Remotes:WaitForChild("GetLeaderboard"):InvokeServer()
      if result then renderLeaderboard(result) end
    end)
  end

  fetch()

  refreshBtn.MouseButton1Click:Connect(function()
    refreshBtn.Text = "..."
    fetch()
    refreshBtn.Text = "🔄 Refresh"
  end)

  task.spawn(function()
    while scroll.Parent do
      task.wait(30)
      fetch()
    end
  end)
end

return LeaderboardApp
