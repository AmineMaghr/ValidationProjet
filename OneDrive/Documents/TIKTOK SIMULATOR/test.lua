local CloutApp = {}

function CloutApp.create(content, env)
  local TweenService = env.TweenService
  local player = env.player
  local Remotes = env.Remotes
  local Config = env.Config
  local C = env.C
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
  local tween = env.tween
  local fmt = env.fmt
  local makeImage = env.makeImage
  local showToast = env.showToast
  local PostDelay = env.PostDelay
  local dStats = env.dStats
  local vFollLabel = env.vFollLabel
  local vViewsLabel = env.vViewsLabel
  local vCoinsLabel = env.vCoinsLabel
  local LeaderstatsUpdated = env.LeaderstatsUpdated
  local FollowersUpdated = env.FollowersUpdated

    local scroll = Instance.new("ScrollingFrame")
    scroll.Size = UDim2.new(1, 0, 1, 0)
    scroll.BackgroundTransparency = 1
    scroll.BorderSizePixel = 0
    scroll.ScrollBarThickness = 0
    scroll.Parent = content
  
    local layout = Instance.new("UIListLayout")
    layout.Padding = UDim.new(0, 0)
    layout.SortOrder = Enum.SortOrder.LayoutOrder
    layout.FillDirection = Enum.FillDirection.Vertical
    layout.Parent = scroll
  
    layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
      scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y)
    end)
  
    local selectedContentType = nil
  
    local contentTypes = {
      { name = "Short Videos",  color = C.indigo, emoji = "📱", hint = "Starter content", rhythm = "Tap once on the beat", req = 0 },
      { name = "Vlogs",         color = C.amber,  emoji = "🎥", hint = "Unlock at 1K followers", rhythm = "Tap 3 times: fast → fast → slow", req = 1000 },
      { name = "Podcasts",      color = C.green,  emoji = "🎙️", hint = "Unlock at 5K followers", rhythm = "Hold the button for 2 seconds", req = 5000 },
      { name = "Livestreams",   color = C.red,    emoji = "📡", hint = "Unlock at 10K followers", rhythm = "Tap 5 times rapidly", req = 10000 },
      { name = "Movies",        color = C.pink,   emoji = "🎬", hint = "Unlock at 50K followers", rhythm = "4 beats with a pause in the middle", req = 50000 },
      { name = "Stadium Tours", color = C.cyan,   emoji = "🏟️", hint = "Unlock at 100K followers", rhythm = "6-beat pattern — changes every post", req = 100000 },
    }
  
    local fontRegular = Enum.Font.Gotham
    local fontBold = Enum.Font.GothamBold
  
    -- ==========================================
    -- SECTION 1 — PROFILE HEADER
    -- ==========================================
    local sec1 = makeFrame(scroll, C.bg, UDim2.new(1, 0, 0, 140), UDim2.new(0, 0, 0, 0), 1)
    sec1.LayoutOrder = 1
    
    local profileImage = Instance.new("ImageLabel")
    profileImage.Size = UDim2.new(0, 72, 0, 72)
    profileImage.Position = UDim2.new(0.5, -36, 0, 20)
    profileImage.BackgroundColor3 = C.card; table.insert(env.ThemedFrames, { frame = profileImage, key = "card", update = function() end })
    profileImage.BorderSizePixel = 0
    makeCorner(profileImage, 999)
    makeStroke(profileImage, C.indigo, 2, 0)
    profileImage.Parent = sec1
  
    task.spawn(function()
      local ok, thumbImage = pcall(function()
        return Players:GetUserThumbnailAsync(
          player.UserId, 
          Enum.ThumbnailType.HeadShot, 
          Enum.ThumbnailSize.Size100x100
        )
      end)
      if ok and thumbImage then
        profileImage.Image = thumbImage
      end
    end)
  
    local usernameLabel = makeLabel(sec1, player.Name, 15, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 100))
    usernameLabel.TextXAlignment = Enum.TextXAlignment.Center
  
    local followersLabel = makeLabel(sec1, "0 Followers", 12, C.muted, fontRegular, UDim2.new(1, 0, 0, 16), UDim2.new(0, 0, 0, 124))
    followersLabel.TextXAlignment = Enum.TextXAlignment.Center
  
    local rankBadge = makeFrame(sec1, C.indigo, UDim2.new(0, 70, 0, 20), UDim2.new(0.5, -35, 0, 146), 0.8)
    makeCorner(rankBadge, 999)
    makeStroke(rankBadge, C.indigo, 1, 0)
    
    local rankLabel = makeLabel(rankBadge, "Nobody", 10, C.indigo, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    rankLabel.TextXAlignment = Enum.TextXAlignment.Center
    rankLabel.TextYAlignment = Enum.TextYAlignment.Center
  
    sec1.Size = UDim2.new(1, 0, 0, 180)
  
    -- ==========================================
    -- SECTION 2 & 3 CONTAINERS
    -- ==========================================
    local sec2 = makeFrame(scroll, C.bg, UDim2.new(1, 0, 0, 390), UDim2.new(0, 0, 0, 0), 1)
    sec2.LayoutOrder = 2
  
    local sec3 = makeFrame(scroll, C.bg, UDim2.new(1, 0, 0, 420), UDim2.new(0, 0, 0, 0), 1)
    sec3.LayoutOrder = 3
    sec3.Visible = false
  
    -- ==========================================
    -- SECTION 2 — CONTENT TYPE SELECTOR
    -- ==========================================
    local sec2Title = makeLabel(sec2, "Choose your content type", 14, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 16))
    sec2Title.TextXAlignment = Enum.TextXAlignment.Center
    
    local sec2Sub = makeLabel(sec2, "You can only pick once. Choose wisely.", 11, C.muted, fontRegular, UDim2.new(1, 0, 0, 16), UDim2.new(0, 0, 0, 40))
    sec2Sub.TextXAlignment = Enum.TextXAlignment.Center
  
    local gridFrame = makeFrame(sec2, C.bg, UDim2.new(1, -32, 0, 240), UDim2.new(0, 16, 0, 72), 1)
    local confirmBtn = nil
    local activeChoice = nil
    local typeCards = {}
  
    for i, ct in ipairs(contentTypes) do
      local r = math.floor((i-1)/2)
      local c = (i-1)%2
      local cardW = 168
      local card = makeFrame(gridFrame, C.card, UDim2.new(0, cardW, 0, 72), UDim2.new(0, c * (cardW + 12), 0, r * (72 + 12)), 0)
      makeCorner(card, 12)
      local stroke = makeStroke(card, C.border, 1, 0)
      
      local emojiCircle = makeFrame(card, ct.color, UDim2.new(0, 32, 0, 32), UDim2.new(0, 10, 0.5, -16), 0)
      makeCorner(emojiCircle, 999)
      local emojiLab = makeLabel(emojiCircle, ct.emoji, 16, C.white, fontRegular, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
      emojiLab.TextXAlignment = Enum.TextXAlignment.Center
      emojiLab.TextYAlignment = Enum.TextYAlignment.Center
  
      local nLab = makeLabel(card, ct.name, 13, C.white, fontBold, UDim2.new(1, -52, 0, 16), UDim2.new(0, 52, 0, 18))
      nLab.TextXAlignment = Enum.TextXAlignment.Left
      
  			local dLab = makeLabel(card, ct.hint, 9, C.muted, fontRegular, UDim2.new(1, -60, 0, 30), UDim2.new(0, 52, 0, 36))
  			dLab.TextXAlignment = Enum.TextXAlignment.Left
  			dLab.TextYAlignment = Enum.TextYAlignment.Top
  			dLab.TextWrapped = true
      
      local check = makeLabel(card, "✓", 14, ct.color, fontBold, UDim2.new(0, 20, 0, 20), UDim2.new(1, -24, 0, 6))
      check.Visible = false
  
      local hit = Instance.new("TextButton", card)
      hit.Size = UDim2.new(1, 0, 1, 0)
      hit.BackgroundTransparency = 1
      hit.Text = ""
  
      local lockOverlay = Instance.new("Frame", card)
      lockOverlay.Name = "LockOverlay"
      lockOverlay.Size = UDim2.new(1, 0, 1, 0)
      lockOverlay.BackgroundColor3 = Color3.fromRGB(15, 15, 20)
      lockOverlay.BackgroundTransparency = 0.5
      lockOverlay.BorderSizePixel = 0
      lockOverlay.ZIndex = 5
      lockOverlay.Visible = false
      makeCorner(lockOverlay, 12)
  
      local lockIcon = makeLabel(lockOverlay, "🔒", 24, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
      lockIcon.TextXAlignment = Enum.TextXAlignment.Center
      lockIcon.TextYAlignment = Enum.TextYAlignment.Center
      lockIcon.ZIndex = 6
  
      table.insert(typeCards, {card=card, stroke=stroke, check=check, ct=ct, lockOverlay=lockOverlay})
  
      hit.MouseEnter:Connect(function()
        if activeChoice ~= ct then stroke.Color = ct.color end
      end)
      hit.MouseLeave:Connect(function()
        if activeChoice ~= ct then stroke.Color = C.border end
      end)
      hit.MouseButton1Click:Connect(function()
        local ls = player:FindFirstChild("leaderstats")
        local cFolls = ls and ls:FindFirstChild("Followers") and ls.Followers.Value or 0
        if cFolls < (ct.req or 0) then return end
        activeChoice = ct
        for _, t in ipairs(typeCards) do
          if t.ct == ct then
            t.stroke.Color = ct.color
            t.stroke.Thickness = 2
            t.check.Visible = true
            t.card.BackgroundTransparency = 0
            tween(t.card, TweenInfo.new(0.08, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), { Size = UDim2.new(0, cardW * 1.05, 0, 72 * 1.05) }).Completed:Wait()
            tween(t.card, TweenInfo.new(0.08, Enum.EasingStyle.Quad, Enum.EasingDirection.In), { Size = UDim2.new(0, cardW, 0, 72) })
          else
            t.stroke.Color = C.border
            t.stroke.Thickness = 1
            t.check.Visible = false
            t.card.BackgroundTransparency = 0.6
          end
        end
        if confirmBtn then confirmBtn.Visible = true end
      end)
    end
  
    confirmBtn = Instance.new("TextButton")
    confirmBtn.Parent = sec2
    confirmBtn.Size = UDim2.new(1, -32, 0, 44)
    confirmBtn.Position = UDim2.new(0, 16, 0, 330)
    confirmBtn.BackgroundColor3 = C.indigo
    confirmBtn.Text = "Start Creating →"
    confirmBtn.Font = fontBold
    confirmBtn.TextSize = 18
    env.addThemedText(confirmBtn, C.white)
    confirmBtn.Visible = false
    makeCorner(confirmBtn, 12)
  
    -- ==========================================
    -- SECTION 3 — POST SECTION
    -- ==========================================
    local sec3BadgeBg = makeFrame(sec3, C.indigo, UDim2.new(0, 140, 0, 24), UDim2.new(0.5, -109, 0, 16), 0)
    makeCorner(sec3BadgeBg, 999)
    local sec3BadgeText = makeLabel(sec3BadgeBg, "", 11, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    sec3BadgeText.TextXAlignment = Enum.TextXAlignment.Center
    sec3BadgeText.TextYAlignment = Enum.TextYAlignment.Center
  
    local changeBtn = Instance.new("TextButton")
    changeBtn.Parent = sec3
    changeBtn.Size = UDim2.new(0, 70, 0, 24)
    changeBtn.Position = UDim2.new(0.5, 39, 0, 16)
    changeBtn.BackgroundTransparency = 1
    changeBtn.Text = "Change ✎"
    changeBtn.Font = fontBold
    changeBtn.TextSize = 11
    changeBtn.TextColor3 = C.muted
    if env.addThemedText then env.addThemedText(changeBtn, C.muted) end
  
    changeBtn.MouseButton1Click:Connect(function()
      selectedContentType = nil
      activeChoice = nil
      sec3.Visible = false
      sec2.Visible = true
      if confirmBtn then confirmBtn.Visible = false end
      for _, t in ipairs(typeCards) do
        t.stroke.Color = C.border
        t.stroke.Thickness = 1
        t.check.Visible = false
        t.card.BackgroundTransparency = 0.6
      end
    end)
  
    local progBarBg = makeFrame(sec3, C.card, UDim2.new(1, -32, 0, 28), UDim2.new(0, 16, 0, 56), 0)
    makeCorner(progBarBg, 14)
    local progBarFill = makeFrame(progBarBg, C.indigo, UDim2.new(0, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0)
    makeCorner(progBarFill, 14)
    local progText = makeLabel(progBarBg, "0 / 50M views to go viral", 11, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    progText.TextXAlignment = Enum.TextXAlignment.Center
    progText.TextYAlignment = Enum.TextYAlignment.Center
    progText.ZIndex = 5
  
    local postBtn = Instance.new("TextButton")
    postBtn.Size = UDim2.new(0, 130, 0, 130)
    postBtn.Position = UDim2.new(0.5, -65, 0, 110)
    postBtn.BackgroundColor3 = C.indigo
    postBtn.Text = "POST"
    env.addThemedText(postBtn, C.white)
    postBtn.Font = fontBold
    postBtn.TextSize = 30
    postBtn.Parent = sec3
    makeCorner(postBtn, 999)
    local postStroke = makeStroke(postBtn, C.indigo, 3, 0.5)
  
    local rhythmHintLab = makeLabel(postBtn, "", 10, C.white, fontRegular, UDim2.new(1, 0, 0, 14), UDim2.new(0, 0, 1, -35))
    rhythmHintLab.TextXAlignment = Enum.TextXAlignment.Center
  
    task.spawn(function()
      while true do
        if postBtn.Parent then
          tween(postStroke, TweenInfo.new(0.8, Enum.EasingStyle.Sine, Enum.EasingDirection.InOut), {Transparency = 0.2})
          task.wait(0.8)
          tween(postStroke, TweenInfo.new(0.8, Enum.EasingStyle.Sine, Enum.EasingDirection.InOut), {Transparency = 0.8})
          task.wait(0.8)
        else
          break
        end
      end
    end)
  
    local hintCard = makeFrame(sec3, C.card, UDim2.new(1, -32, 0, 36), UDim2.new(0, 16, 0, 260), 0)
    makeCorner(hintCard, 10)
    local rhythmGuide = makeLabel(hintCard, "", 11, C.muted, fontRegular, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    rhythmGuide.TextXAlignment = Enum.TextXAlignment.Center
    rhythmGuide.TextYAlignment = Enum.TextYAlignment.Center
  
    local statsRow = makeFrame(sec3, C.bg, UDim2.new(1, -32, 0, 64), UDim2.new(0, 16, 0, 312), 1)
    local statW = (380 - 32 - 16) / 3
    
    local s1 = makeFrame(statsRow, C.card, UDim2.new(0, statW, 1, 0), UDim2.new(0, 0, 0, 0), 0)
    makeCorner(s1, 12)
    local vViews = makeLabel(s1, "0", 18, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))
      vViews.Name = "vViewsLabel"
    vViews.TextXAlignment = Enum.TextXAlignment.Center
    local lViews = makeLabel(s1, "VIEWS", 10, C.muted, fontRegular, UDim2.new(1, 0, 0, 12), UDim2.new(0, 0, 0, 38))
    lViews.TextXAlignment = Enum.TextXAlignment.Center
  
    local s2 = makeFrame(statsRow, C.card, UDim2.new(0, statW, 1, 0), UDim2.new(0, statW + 8, 0, 0), 0)
    makeCorner(s2, 12)
    local vCoins = makeLabel(s2, "0", 18, C.amber, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))
      vCoins.Name = "vCoinsLabel"
    vCoins.TextXAlignment = Enum.TextXAlignment.Center
    local lCoins = makeLabel(s2, "COINS", 10, C.muted, fontRegular, UDim2.new(1, 0, 0, 12), UDim2.new(0, 0, 0, 38))
    lCoins.TextXAlignment = Enum.TextXAlignment.Center
  
    local s3 = makeFrame(statsRow, C.card, UDim2.new(0, statW, 1, 0), UDim2.new(0, (statW * 2) + 16, 0, 0), 0)
    makeCorner(s3, 12)
    local vFoll = makeLabel(s3, "0", 18, C.green, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))
      vFoll.Name = "vFollLabel"
    vFoll.TextXAlignment = Enum.TextXAlignment.Center
    local lFoll = makeLabel(s3, "FOLLOWERS", 10, C.muted, fontRegular, UDim2.new(1, 0, 0, 12), UDim2.new(0, 0, 0, 38))
    lFoll.TextXAlignment = Enum.TextXAlignment.Center
  
    confirmBtn.MouseButton1Click:Connect(function()
      if not activeChoice then return end
      selectedContentType = activeChoice.name
      sec3BadgeBg.BackgroundColor3 = activeChoice.color
      sec3BadgeText.Text = activeChoice.emoji .. " " .. activeChoice.name
      postBtn.BackgroundColor3 = activeChoice.color
      postStroke.Color = activeChoice.color
      rhythmHintLab.Text = "Pattern"
      rhythmGuide.Text = activeChoice.rhythm
  
      sec2.Visible = false
      sec3.Visible = true
  
      if showNotification then
        showNotification("Content type locked in!", "You chose "..selectedContentType, C.indigo)
      end
      
      local setContentRemote = ReplicatedStorage:FindFirstChild("Remotes") and ReplicatedStorage.Remotes:FindFirstChild("SetContentType")
      if setContentRemote then
        setContentRemote:FireServer(selectedContentType)
      end
    end)
  
    local COOLDOWNS = {
      ["Short Videos"] = 3,
      ["Vlogs"] = 3,
      ["Podcasts"] = 3,
      ["Livestreams"] = 3,
      ["Movies"] = 3,
      ["Stadium Tours"] = 3
    }
  
    local rhythmWindow = makeFrame(content, Color3.fromRGB(12, 12, 18), UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0.4)
    rhythmWindow.ZIndex = 1000
    rhythmWindow.Visible = false
    rhythmWindow.ClipsDescendants = true
    
  
    local bullseyeScale = 0.22
    
    local bullseye = makeFrame(rhythmWindow, Color3.new(1, 1, 1), UDim2.new(bullseyeScale, 0, bullseyeScale, 0), UDim2.new(0.5, 0, 0.5, 0), 0)
    bullseye.AnchorPoint = Vector2.new(0.5, 0.5)
    bullseye.ZIndex = 1005
    makeCorner(bullseye, 999)
    
    local uiAspect = Instance.new("UIAspectRatioConstraint", bullseye)
    uiAspect.DominantAxis = Enum.DominantAxis.Width
  
  
    local maxCircleScale = 0.8
    local shrinkingCircle = makeFrame(rhythmWindow, C.indigo, UDim2.new(maxCircleScale, 0, maxCircleScale, 0), UDim2.new(0.5, 0, 0.5, 0), 0.5)
    shrinkingCircle.AnchorPoint = Vector2.new(0.5, 0.5)
    shrinkingCircle.ZIndex = 1002
    makeCorner(shrinkingCircle, 999)
    
    local uiAspect2 = Instance.new("UIAspectRatioConstraint", shrinkingCircle)
    uiAspect2.DominantAxis = Enum.DominantAxis.Width
    
    local scoreText = makeLabel(rhythmWindow, "", 10, C.white, fontBold, UDim2.new(1, 0, 0, 40), UDim2.new(0, 0, 0.5, -15))
    scoreText.TextXAlignment = Enum.TextXAlignment.Center
    scoreText.ZIndex = 1010
    
    local comboDisplay = makeLabel(rhythmWindow, "", 14, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0.5, 0, 0, 20))
    comboDisplay.AnchorPoint = Vector2.new(0.5, 0)
    comboDisplay.TextXAlignment = Enum.TextXAlignment.Center
    comboDisplay.ZIndex = 1010
    comboDisplay.Visible = false
  
    local speedHint = makeLabel(rhythmWindow, "", 36, C.white, fontBold, UDim2.new(1, 0, 0, 50), UDim2.new(0.5, 0, 0.5, -140))
    speedHint.AnchorPoint = Vector2.new(0.5, 0.5)
    speedHint.TextXAlignment = Enum.TextXAlignment.Center
    speedHint.ZIndex = 1010
    local speedStroke = Instance.new("UIStroke", speedHint)
    speedStroke.Color = Color3.fromRGB(0, 0, 0)
    speedStroke.Thickness = 3
    speedHint.Visible = false
    
    local isRhythmActive = false
    local isWaitingToStart = false
    local isOnCooldown = false
    local rhythmStartTime = 0
    local rhythmDuration = 1.5
    local currentCombo = 0
    local rhythmConnection = nil
    local inputConnection = nil
    
    local function processTap(autoMiss)
      isRhythmActive = false
      if rhythmConnection then rhythmConnection:Disconnect() rhythmConnection = nil end
      if inputConnection then inputConnection:Disconnect() inputConnection = nil end
      
      local shrinkingSize = shrinkingCircle.AbsoluteSize.X
      local bullseyeSize = bullseye.AbsoluteSize.X
      if bullseyeSize == 0 then bullseyeSize = 1 end
      local diff = math.abs(shrinkingSize - bullseyeSize) / bullseyeSize
      
      if autoMiss then
        diff = 1 -- Force miss
      end
      
      local scoreMsg, color, multiplier
      if diff <= 0.08 then
        scoreMsg = "ULTRA"
        color = Color3.fromRGB(245, 158, 11)
        multiplier = 3.0
      elseif diff <= 0.18 then
        scoreMsg = "PERFECT"
        color = Color3.fromRGB(99, 102, 241)
        multiplier = 1.8
      elseif diff <= 0.30 then
        scoreMsg = "GOOD"
        color = Color3.fromRGB(52, 211, 153)
        multiplier = 1.2
      elseif diff <= 0.45 then
        scoreMsg = "OK"
        color = Color3.fromRGB(148, 148, 170)
        multiplier = 0.8
      else
        scoreMsg = "MISS"
        color = Color3.fromRGB(239, 68, 68)
        multiplier = 0.5
      end
      
      if multiplier > 0.5 then
        currentCombo = currentCombo + 1
        comboDisplay.Visible = true
        comboDisplay.Text = "Combo " .. currentCombo
        if currentCombo >= 10 then
          comboDisplay.TextColor3 = Color3.fromRGB(250, 204, 21)
        elseif currentCombo >= 5 then
          comboDisplay.TextColor3 = Color3.fromRGB(245, 158, 11)
        else
          comboDisplay.TextColor3 = C.white
        end
      else
        currentCombo = 0
        comboDisplay.Visible = false
      end
      
      scoreText.Text = scoreMsg
      scoreText.TextColor3 = color
      scoreText.TextSize = 20
      scoreText.TextTransparency = 0
      tween(scoreText, TweenInfo.new(0.3, Enum.EasingStyle.Back, Enum.EasingDirection.Out), { TextSize = 64 })
      
      -- Flashing screen color
      local initialColor = rhythmWindow.BackgroundColor3
      rhythmWindow.BackgroundColor3 = color
      tween(rhythmWindow, TweenInfo.new(0.5, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), { BackgroundColor3 = initialColor })
      
      task.delay(0.8, function()
        tween(scoreText, TweenInfo.new(0.2), { TextTransparency = 1 })
        tween(shrinkingCircle, TweenInfo.new(0.3), { BackgroundTransparency = 1 })
        tween(rhythmWindow, TweenInfo.new(0.3), { BackgroundTransparency = 1 })
        task.delay(0.3, function()
          rhythmWindow.Visible = false
        end)
      end)
      
      local postRemote = ReplicatedStorage:FindFirstChild("Remotes") and ReplicatedStorage.Remotes:FindFirstChild("PostContent")
      if postRemote then
        postRemote:FireServer(multiplier)
      end
      
      isOnCooldown = true
      local cd = COOLDOWNS[selectedContentType] or 2
      postBtn.BackgroundColor3 = C.muted
      postStroke.Color = C.muted
      
      task.spawn(function()
        for i = cd, 1, -1 do
          if not postBtn.Parent then break end
          postBtn.Text = tostring(i) .. "s"
          task.wait(1)
        end
        if not postBtn.Parent then return end
        isOnCooldown = false
        postBtn.Text = "POST"
        if activeChoice then
          postBtn.BackgroundColor3 = activeChoice.color
          postStroke.Color = activeChoice.color
        else
          postBtn.BackgroundColor3 = C.indigo
          postStroke.Color = C.indigo
        end
      end)
    end
  
    local function triggerRhythmAction()
      if isWaitingToStart then return end
      if isOnCooldown then return end
  
      if isRhythmActive then
        processTap(false)
        return
      end
  
      tween(postBtn, TweenInfo.new(0.08), { 
        Size = UDim2.new(0, 114, 0, 114), 
        Position = UDim2.new(0.5, -57, 0, 118) 
      })
      task.delay(0.08, function()
        tween(postBtn, TweenInfo.new(0.12), { 
          Size = UDim2.new(0, 130, 0, 130), 
          Position = UDim2.new(0.5, -65, 0, 110) 
        })
      end)
  
      isWaitingToStart = true
      rhythmWindow.Visible = true
      rhythmWindow.BackgroundColor3 = Color3.fromRGB(12, 12, 18)
      tween(rhythmWindow, TweenInfo.new(0.3), { BackgroundTransparency = 0.4 })
      
      scoreText.Text = ""
      speedHint.Visible = false
      comboDisplay.Visible = (currentCombo > 0)
      
      local rnd = math.random(1, 100)
      local hintText, hintColor = "", C.white
      local doShake = false
      
      if rnd <= 5 then
        rhythmDuration = 2.2
        hintText = "EASY"
        hintColor = Color3.fromRGB(52, 211, 153)
      elseif rnd <= 30 then
        rhythmDuration = 1.5
      elseif rnd <= 80 then
        rhythmDuration = 0.9
        hintText = "FAST!"
        hintColor = Color3.fromRGB(245, 158, 11)
      else
        rhythmDuration = 0.5
        hintText = "CHAOTIC!!"
        hintColor = Color3.fromRGB(239, 68, 68)
        doShake = true
      end
      
      if hintText ~= "" then
        speedHint.Text = hintText
        speedHint.TextColor3 = hintColor
        speedHint.Visible = true
        speedHint.Position = UDim2.new(0.5, 0, 0.5, -140)
        if doShake then
          task.spawn(function()
            for i = 1, 16 do
              if not isWaitingToStart and not isRhythmActive then break end
              speedHint.Position = UDim2.new(0.5, math.random(-6, 6), 0.5, -140 + math.random(-6, 6))
              task.wait(0.05)
            end
            speedHint.Position = UDim2.new(0.5, 0, 0.5, -140)
          end)
        end
      end
  
      local colorType = activeChoice and activeChoice.color or C.indigo
      local colorType = activeChoice and activeChoice.color or C.indigo
      shrinkingCircle.BackgroundColor3 = colorType
      shrinkingCircle.BackgroundTransparency = 0.5
      shrinkingCircle.Size = UDim2.new(maxCircleScale, 0, maxCircleScale, 0)
      
      if inputConnection then inputConnection:Disconnect() end
      inputConnection = game:GetService("UserInputService").InputBegan:Connect(function(input, processed)
        if not processed then
          if input.KeyCode == Enum.KeyCode.Space or input.UserInputType == Enum.UserInputType.MouseButton1 then
            if isRhythmActive then
              triggerRhythmAction()
            end
          end
        end
      end)
      
      task.delay(0.8, function()
        isWaitingToStart = false
        isRhythmActive = true
        speedHint.Visible = false
        rhythmStartTime = tick()
        
        local playedPerfectSound = false
        local playedUltraSound = false
  
        if rhythmConnection then rhythmConnection:Disconnect() end
        rhythmConnection = game:GetService("RunService").RenderStepped:Connect(function()
          if not isRhythmActive then return end
          local el = tick() - rhythmStartTime
          
          -- Interpolate scale down to 0, matching bullseyeScale at el = rhythmDuration
          -- We calculate the velocity required to pass through maxCircleScale to bullseyeScale in rhythmDuration.
          local shrinkRate = (maxCircleScale - bullseyeScale) / rhythmDuration
          local currentScale = maxCircleScale - (shrinkRate * el)
          
          shrinkingCircle.Size = UDim2.new(currentScale, 0, currentScale, 0)
          
          local currentSizeX = shrinkingCircle.AbsoluteSize.X
          local bSizeX = bullseye.AbsoluteSize.X
          if bSizeX == 0 then bSizeX = 1 end
          local sizeDiff = bSizeX > 0 and (math.abs(currentSizeX - bSizeX) / bSizeX) or 1
          
          if sizeDiff <= 0.18 and not playedPerfectSound then
            playedPerfectSound = true
            local sound = Instance.new("Sound")
            sound.SoundId = "rbxassetid://6026984224"
            sound.Volume = 0.4
            sound.Parent = game.SoundService
            sound:Play()
            game.Debris:AddItem(sound, 2)
          end
          if sizeDiff <= 0.08 and not playedUltraSound then
            playedUltraSound = true
            local sound = Instance.new("Sound")
            sound.SoundId = "rbxassetid://6026984224"
            sound.Volume = 0.4
            sound.PlaybackSpeed = 1.5
            sound.Parent = game.SoundService
            sound:Play()
            game.Debris:AddItem(sound, 2)
          end
          
          -- Let it shrink past bullseye slighly to allow late hits, auto miss at 0 or very far past
          if el >= rhythmDuration * 1.5 or currentScale <= 0 then
            processTap(true)
          end
        end)
      end)
    end
  
    postBtn.MouseButton1Click:Connect(triggerRhythmAction)
  
    local Remotes = ReplicatedStorage:WaitForChild("Remotes")
    local CoinsUpdated = Remotes:WaitForChild("CoinsUpdated")
    local ViewsUpdated = Remotes:WaitForChild("ViewsUpdated")
    local TotalViewsUpdated = Remotes:WaitForChild("TotalViewsUpdated")
    local FollowersUpdated = Remotes:WaitForChild("FollowersUpdated")
  
    local leaderstats = player:WaitForChild("leaderstats")
    if leaderstats then
      local c = leaderstats:WaitForChild("Coins", 2)
      if c then vCoins.Text = fmt(c.Value) end
      local v = leaderstats:WaitForChild("Views", 2)
      if v then vViews.Text = fmt(v.Value) end
      local f = leaderstats:WaitForChild("Followers", 2)
      if f then vFoll.Text = fmt(f.Value) end
    end
  
    local function updateLocks(folls)
    	for _, t in ipairs(typeCards) do
    		if t.lockOverlay then
    			t.lockOverlay.Visible = folls < (t.ct.req or 0)
    		end
    	end
    end
  
    if leaderstats and leaderstats:FindFirstChild("Followers") then
    	updateLocks(leaderstats.Followers.Value)
    end
  
    FollowersUpdated.OnClientEvent:Connect(function(totalFollowers)
      vFoll.Text = fmt(totalFollowers)
    	updateLocks(totalFollowers)
    end)
  
  
    CoinsUpdated.OnClientEvent:Connect(function(totalCoins)
      vCoins.Text = fmt(totalCoins)
    end)
  
    TotalViewsUpdated.OnClientEvent:Connect(function(totalViews)
      vViews.Text = fmt(totalViews)
    end)
  
    ViewsUpdated.OnClientEvent:Connect(function(views, totalViews)
      vViews.Text = fmt(totalViews)
      
      local threshold = 50000000
      local pct = math.clamp(views / threshold, 0, 1)
      tween(progBarFill, TweenInfo.new(0.3), {
        Size = UDim2.new(pct, 0, 1, 0)
      })
      progText.Text = fmt(views) .. " / " .. fmt(threshold) .. " views to go viral"
      
      if pct > 0.9 then
        progBarFill.BackgroundColor3 = Color3.fromRGB(245, 158, 11)
      else
        progBarFill.BackgroundColor3 = Color3.fromRGB(99, 102, 241)
      end
    end)
  
    print("[CloutApp] UI connected to server")
    print("[CloutApp] Content loaded")

return CloutApp
