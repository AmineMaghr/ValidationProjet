local QuestsApp = {}

function QuestsApp.create(content, env)
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

  local selectedTab = "challenges"
  local selectedItem = nil
  local challengeData = {}
  local questData = {}

  local function fmtBig(n)
    n = math.floor(n or 0)
    if n >= 1e12 then return string.format("%.1fT", n/1e12)
    elseif n >= 1e9 then return string.format("%.1fB", n/1e9)
    elseif n >= 1e6 then return string.format("%.1fM", n/1e6)
    elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
    else return tostring(n) end
  end

  local mainContainer = makeFrame(content, C.bg, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0), 1)
  mainContainer.ClipsDescendants = true

  local header = makeFrame(mainContainer, C.card, UDim2.new(1, 0, 0, 50), UDim2.new(0, 0, 0, 0), 0)
  local backBtn = Instance.new("TextButton")
  backBtn.Size = UDim2.new(0, 90, 1, 0)
  backBtn.Position = UDim2.new(0, 12, 0, 0)
  backBtn.BackgroundTransparency = 1
  backBtn.Text = "← Back"
  backBtn.TextColor3 = Color3.fromRGB(255, 80, 120)
  backBtn.Font = FONT_BOLD
  backBtn.TextSize = 17
  backBtn.Visible = false
  backBtn.Parent = header

  local titleLbl = makeLabel(header, "🎯 Quests", 15, C.white, FONT_BOLD, UDim2.new(1, -120, 1, 0), UDim2.new(0, 60, 0, 0))
  titleLbl.TextXAlignment = Enum.TextXAlignment.Center
  titleLbl.TextYAlignment = Enum.TextYAlignment.Center

  local detailView = makeFrame(mainContainer, C.bg, UDim2.new(1, 0, 1, -50), UDim2.new(0, 0, 0, 50), 1)
  detailView.Visible = false
  detailView.ClipsDescendants = true

  local tabBar = makeFrame(mainContainer, C.bg, UDim2.new(1, 0, 0, 44), UDim2.new(0, 0, 0, 50), 1)

  local tabChallBtn = Instance.new("TextButton")
  tabChallBtn.Size = UDim2.new(0.5, 0, 1, 0)
  tabChallBtn.Position = UDim2.new(0, 0, 0, 0)
  tabChallBtn.BackgroundColor3 = selectedTab == "challenges" and Color3.fromRGB(255, 80, 120) or C.card
  tabChallBtn.Text = "🏆 Challenges"
  tabChallBtn.TextColor3 = C.white
  tabChallBtn.Font = FONT_BOLD
  tabChallBtn.TextSize = 13
  tabChallBtn.Parent = tabBar

  local tabQuestBtn = Instance.new("TextButton")
  tabQuestBtn.Size = UDim2.new(0.5, 0, 1, 0)
  tabQuestBtn.Position = UDim2.new(0.5, 0, 0, 0)
  tabQuestBtn.BackgroundColor3 = selectedTab == "daily" and Color3.fromRGB(255, 80, 120) or C.card
  tabQuestBtn.Text = "📅 Daily Quests"
  tabQuestBtn.TextColor3 = C.white
  tabQuestBtn.Font = FONT_BOLD
  tabQuestBtn.TextSize = 13
  tabQuestBtn.Parent = tabBar

  local listScroll = Instance.new("ScrollingFrame")
  listScroll.Size = UDim2.new(1, 0, 1, -100)
  listScroll.Position = UDim2.new(0, 0, 0, 100)
  listScroll.BackgroundTransparency = 1
  listScroll.ScrollBarThickness = 3
  listScroll.ScrollBarImageColor3 = Color3.fromRGB(120, 120, 130)
  listScroll.Parent = mainContainer
  listScroll.ClipsDescendants = true

  local listPad = Instance.new("UIPadding")
  listPad.PaddingTop = UDim.new(0, 8)
  listPad.PaddingLeft = UDim.new(0, 12)
  listPad.PaddingRight = UDim.new(0, 12)
  listPad.PaddingBottom = UDim.new(0, 20)
  listPad.Parent = listScroll

  local listLayout = Instance.new("UIListLayout")
  listLayout.Padding = UDim.new(0, 8)
  listLayout.SortOrder = Enum.SortOrder.LayoutOrder
  listLayout.Parent = listScroll

  listLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    listScroll.CanvasSize = UDim2.new(0, 0, 0, listLayout.AbsoluteContentSize.Y + 120)
  end)

  local detailScroll = Instance.new("ScrollingFrame")
  detailScroll.Size = UDim2.new(1, 0, 1, 0)
  detailScroll.BackgroundTransparency = 1
  detailScroll.ScrollBarThickness = 3
  detailScroll.ScrollBarImageColor3 = Color3.fromRGB(120, 120, 130)
  detailScroll.Parent = detailView

  local detailPad = Instance.new("UIPadding")
  detailPad.PaddingTop = UDim.new(0, 16)
  detailPad.PaddingLeft = UDim.new(0, 16)
  detailPad.PaddingRight = UDim.new(0, 16)
  detailPad.PaddingBottom = UDim.new(0, 20)
  detailPad.Parent = detailScroll

  local detailLayout = Instance.new("UIListLayout")
  detailLayout.Padding = UDim.new(0, 12)
  detailLayout.SortOrder = Enum.SortOrder.LayoutOrder
  detailLayout.Parent = detailScroll

  detailLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    detailScroll.CanvasSize = UDim2.new(0, 0, 0, detailLayout.AbsoluteContentSize.Y + 32)
  end)

  local function clearDetail()
    for _, c in ipairs(detailScroll:GetChildren()) do
      if c:IsA("Frame") or c:IsA("TextLabel") or c:IsA("TextButton") then
        c:Destroy()
      end
    end
  end

  local function showDetail(item, isDaily)
    clearDetail()
    selectedItem = item
    mainContainer.ClipsDescendants = false
    listScroll.Visible = false
    tabBar.Visible = false
    detailView.Visible = true
    backBtn.Visible = true
    titleLbl.Text = item.emoji .. " " .. item.name

    local pct = math.clamp(item.progress / item.target, 0, 1)

    local iconCard = makeFrame(detailScroll, C.card, UDim2.new(1, 0, 0, 80), nil, 0)
    iconCard.LayoutOrder = 1
    makeCorner(iconCard, 14)
    makeStroke(iconCard, item.claimable and Color3.fromRGB(100, 255, 100) or (item.completed and Color3.fromRGB(150, 150, 150) or Color3.fromRGB(255, 80, 120)), 2, 0)

    local iconLbl = makeLabel(iconCard, item.emoji, 36, C.white, FONT_BOLD, UDim2.new(0, 50, 0, 50), UDim2.new(0, 15, 0.5, -25))
    iconLbl.TextXAlignment = Enum.TextXAlignment.Center
    iconLbl.TextYAlignment = Enum.TextYAlignment.Center

    makeLabel(iconCard, item.name, 18, C.white, FONT_BOLD, UDim2.new(1, -80, 0, 22), UDim2.new(0, 70, 0, 10)).TextXAlignment = Enum.TextXAlignment.Left
    makeLabel(iconCard, item.completed and "✅ Completed" or (item.claimable and "🎉 Ready to claim!" or "🔄 In progress"), 13,
      item.completed and Color3.fromRGB(100, 255, 100) or (item.claimable and Color3.fromRGB(255, 215, 0) or C.muted),
      Enum.Font.Gotham, UDim2.new(1, -80, 0, 18), UDim2.new(0, 70, 0, 36)).TextXAlignment = Enum.TextXAlignment.Left

    local descCard = makeFrame(detailScroll, C.card, UDim2.new(1, 0, 0, 0), nil, 0)
    descCard.LayoutOrder = 2
    makeCorner(descCard, 12)
    local descAuto = Instance.new("UIListLayout")
    descAuto.Padding = UDim.new(0, 6)
    descAuto.Parent = descCard
    descAuto:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
      descCard.Size = UDim2.new(1, 0, 0, descAuto.AbsoluteContentSize.Y + 24)
    end)

    makeLabel(descCard, "Description", 13, Color3.fromRGB(180, 80, 255), FONT_BOLD, UDim2.new(1, -16, 0, 14), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
    makeLabel(descCard, item.desc, 14, C.white, Enum.Font.Gotham, UDim2.new(1, -24, 0, 20), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left

    local progCard = makeFrame(detailScroll, C.card, UDim2.new(1, 0, 0, 0), nil, 0)
    progCard.LayoutOrder = 3
    makeCorner(progCard, 12)
    local progAuto = Instance.new("UIListLayout")
    progAuto.Padding = UDim.new(0, 8)
    progAuto.Parent = progCard
    progAuto:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
      progCard.Size = UDim2.new(1, 0, 0, progAuto.AbsoluteContentSize.Y + 24)
    end)

    makeLabel(progCard, "Progress", 13, Color3.fromRGB(37, 244, 238), FONT_BOLD, UDim2.new(1, -16, 0, 14), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left

    local progBarBg = makeFrame(progCard, Color3.fromRGB(30, 30, 30), UDim2.new(1, -24, 0, 14), UDim2.new(0, 12, 0, 0), 0)
    makeCorner(progBarBg, 7)
    local progBarFill = makeFrame(progBarBg, item.claimable and Color3.fromRGB(100, 255, 100) or (item.completed and Color3.fromRGB(150, 150, 150) or Color3.fromRGB(255, 80, 120)), UDim2.new(pct, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0)
    makeCorner(progBarFill, 7)

    makeLabel(progCard, fmtBig(item.progress) .. " / " .. fmtBig(item.target), 14, C.white, FONT_BOLD, UDim2.new(1, -24, 0, 18), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Center

    local rewardCard = makeFrame(detailScroll, C.card, UDim2.new(1, 0, 0, 0), nil, 0)
    rewardCard.LayoutOrder = 4
    makeCorner(rewardCard, 12)
    local rewardAuto = Instance.new("UIListLayout")
    rewardAuto.Padding = UDim.new(0, 6)
    rewardAuto.Parent = rewardCard
    rewardAuto:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
      rewardCard.Size = UDim2.new(1, 0, 0, rewardAuto.AbsoluteContentSize.Y + 24)
    end)

    makeLabel(rewardCard, "Rewards", 13, Color3.fromRGB(255, 210, 50), FONT_BOLD, UDim2.new(1, -16, 0, 14), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
    if isDaily then
      makeLabel(rewardCard, "💰 " .. fmt(item.rewardCoins) .. " coins", 15, Color3.fromRGB(255, 210, 50), FONT_BOLD, UDim2.new(1, -24, 0, 20), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
    else
      makeLabel(rewardCard, "💰 " .. fmt(item.rewardCoins) .. " coins", 15, Color3.fromRGB(255, 210, 50), FONT_BOLD, UDim2.new(1, -24, 0, 20), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
      if item.rewardViews > 0 then
        makeLabel(rewardCard, "👀 " .. fmtBig(item.rewardViews) .. " views", 15, Color3.fromRGB(37, 244, 238), FONT_BOLD, UDim2.new(1, -24, 0, 20), UDim2.new(0, 12, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
      end
    end

    local claimCard = makeFrame(detailScroll, C.card, UDim2.new(1, 0, 0, 0), nil, 0)
    claimCard.LayoutOrder = 5
    makeCorner(claimCard, 12)
    local claimAuto = Instance.new("UIListLayout")
    claimAuto.Padding = UDim.new(0, 8)
    claimAuto.Parent = claimCard
    claimAuto:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
      claimCard.Size = UDim2.new(1, 0, 0, claimAuto.AbsoluteContentSize.Y + 24)
    end)

    local claimBtn = Instance.new("TextButton")
    claimBtn.Size = UDim2.new(1, -24, 0, 44)
    claimBtn.Position = UDim2.new(0, 12, 0, 12)
    claimBtn.Font = FONT_BOLD
    claimBtn.TextSize = 16
    claimBtn.Parent = claimCard

    if item.completed then
      claimBtn.Text = "✅ Already Claimed"
      claimBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
      claimBtn.TextColor3 = C.muted
      claimBtn.Active = false
    elseif item.claimable then
      claimBtn.Text = "🎁 CLAIM REWARD"
      claimBtn.BackgroundColor3 = Color3.fromRGB(255, 80, 120)
      claimBtn.TextColor3 = Color3.new(1, 1, 1)
      makeCorner(claimBtn, 10)
      claimBtn.MouseButton1Click:Connect(function()
        claimBtn.Text = "Claiming..."
        claimBtn.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
        claimBtn.Active = false
        local ok, a, b = pcall(function()
          if isDaily then
            return Remotes:WaitForChild("ClaimDailyQuest"):InvokeServer(item.id)
          else
            return Remotes:WaitForChild("ClaimChallenge"):InvokeServer(item.id)
          end
        end)
        if ok and a then
          item.completed = true
          item.claimable = false
          claimBtn.Text = "✅ Claimed!"
          claimBtn.BackgroundColor3 = Color3.fromRGB(40, 120, 50)
          claimBtn.TextColor3 = Color3.fromRGB(200, 255, 200)
          claimBtn.Active = false
        else
          claimBtn.Text = "Failed - try again"
          claimBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
          claimBtn.TextColor3 = Color3.new(1, 1, 1)
          claimBtn.Active = true
          task.delay(1.5, function()
            if claimBtn and claimBtn.Parent then
              claimBtn.Text = "🎁 CLAIM REWARD"
              claimBtn.BackgroundColor3 = Color3.fromRGB(255, 80, 120)
              claimBtn.TextColor3 = Color3.new(1, 1, 1)
            end
          end)
        end
      end)
    else
      claimBtn.Text = "🔄 In Progress"
      claimBtn.BackgroundColor3 = Color3.fromRGB(50, 50, 60)
      claimBtn.TextColor3 = C.muted
      claimBtn.Active = false
    end

    mainContainer.ClipsDescendants = true
  end

  local function renderList()
    for _, c in ipairs(listScroll:GetChildren()) do
      if not c:IsA("UIListLayout") and not c:IsA("UIPadding") then c:Destroy() end
    end

    local items = selectedTab == "challenges" and challengeData or questData
    local order = 1

    if selectedTab == "challenges" then
      makeLabel(listScroll, "🏆 Achievements & Milestones", 14, C.white, FONT_BOLD, UDim2.new(1, 0, 0, 20), nil).LayoutOrder = order; order = order + 1
    else
      makeLabel(listScroll, "📅 Resets every day at midnight", 12, C.muted, Enum.Font.Gotham, UDim2.new(1, 0, 0, 20), nil).LayoutOrder = order; order = order + 1
    end

    for _, item in ipairs(items) do
      local card = Instance.new("TextButton")
      card.Size = UDim2.new(1, 0, 0, 62)
      card.BackgroundColor3 = C.card
      card.Text = ""
      card.AutoButtonColor = false
      card.LayoutOrder = order; order = order + 1
      card.Parent = listScroll
      makeCorner(card, 10)

      local strokeColor = item.claimable and Color3.fromRGB(100, 255, 100) or (item.completed and Color3.fromRGB(100, 100, 100) or C.border)
      local stroke = makeStroke(card, strokeColor, item.claimable and 2 or 1, 0)

      local iconLbl = makeLabel(card, item.emoji, 28, C.white, FONT_BOLD, UDim2.new(0, 44, 0, 44), UDim2.new(0, 10, 0.5, -22))
      iconLbl.TextXAlignment = Enum.TextXAlignment.Center
      iconLbl.TextYAlignment = Enum.TextYAlignment.Center

      makeLabel(card, item.name, 14, C.white, FONT_BOLD, UDim2.new(1, -130, 0, 10), UDim2.new(0, 60, 0, 0)).TextXAlignment = Enum.TextXAlignment.Left
      makeLabel(card, item.desc, 11, C.muted, Enum.Font.Gotham, UDim2.new(1, -130, 0, 14), UDim2.new(0, 60, 0, 26)).TextXAlignment = Enum.TextXAlignment.Left

      local pct = math.clamp(item.progress / item.target, 0, 1)
      local progBg = makeFrame(card, Color3.fromRGB(30, 30, 30), UDim2.new(1, -130, 0, 6), UDim2.new(0, 60, 1, -16), 0)
      makeCorner(progBg, 3)
      makeFrame(progBg, item.claimable and Color3.fromRGB(100, 255, 100) or (item.completed and Color3.fromRGB(100, 100, 100) or Color3.fromRGB(255, 80, 120)), UDim2.new(pct, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0).LayoutOrder = 9999

      local statusLbl = makeLabel(card, item.completed and "✅" or (item.claimable and "🎁" or fmt(item.progress) .. "/" .. fmt(item.target)), 11,
        item.claimable and Color3.fromRGB(100, 255, 100) or (item.completed and Color3.fromRGB(100, 255, 100) or C.muted),
        FONT_BOLD, UDim2.new(0, 60, 1, 0), UDim2.new(1, -70, 0, 0))
      statusLbl.TextXAlignment = Enum.TextXAlignment.Right
      statusLbl.TextYAlignment = Enum.TextYAlignment.Center

      card.MouseButton1Click:Connect(function()
        showDetail(item, selectedTab == "daily")
      end)
    end
  end

  local function switchTab(tab)
    selectedTab = tab
    tabChallBtn.BackgroundColor3 = tab == "challenges" and Color3.fromRGB(255, 80, 120) or C.card
    tabQuestBtn.BackgroundColor3 = tab == "daily" and Color3.fromRGB(255, 80, 120) or C.card
    renderList()
  end

  tabChallBtn.MouseButton1Click:Connect(function() switchTab("challenges") end)
  tabQuestBtn.MouseButton1Click:Connect(function() switchTab("daily") end)

  backBtn.MouseButton1Click:Connect(function()
    selectedItem = nil
    detailView.Visible = false
    listScroll.Visible = true
    tabBar.Visible = true
    backBtn.Visible = false
    titleLbl.Text = "🎯 Quests"
    mainContainer.ClipsDescendants = false
    listScroll.ClipsDescendants = true
    renderList()
  end)

  local function fetchAll()
    pcall(function()
      challengeData = Remotes:WaitForChild("GetChallenges"):InvokeServer() or {}
    end)
    pcall(function()
      questData = Remotes:WaitForChild("GetDailyQuests"):InvokeServer() or {}
    end)
    renderList()
  end

  fetchAll()

  local function refresh()
    fetchAll()
  end

  Remotes:WaitForChild("PostContent").OnClientEvent:Connect(refresh)
  Remotes:WaitForChild("CoinsUpdated").OnClientEvent:Connect(refresh)
  Remotes:WaitForChild("FollowersUpdated").OnClientEvent:Connect(refresh)
  Remotes:WaitForChild("TotalViewsUpdated").OnClientEvent:Connect(refresh)
  Remotes:WaitForChild("PrestigeResult").OnClientEvent:Connect(refresh)
end

return QuestsApp
