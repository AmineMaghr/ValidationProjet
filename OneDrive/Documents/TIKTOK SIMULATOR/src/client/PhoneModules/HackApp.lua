local HackApp = {}

function HackApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner
  local makeStroke = env.makeStroke
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
  layout.Padding = UDim.new(0, 10)
  layout.SortOrder = Enum.SortOrder.LayoutOrder
  layout.Parent = scroll

  layout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    scroll.CanvasSize = UDim2.new(0, 0, 0, layout.AbsoluteContentSize.Y + 40)
  end)

  -- Title
  local title = makeLabel(scroll, "💀 Hack Rivals", 20, C.white, Enum.Font.GothamBlack, UDim2.new(1, 0, 0, 28), nil)
  title.LayoutOrder = 1
  title.TextXAlignment = Enum.TextXAlignment.Center

  local subtitle = makeLabel(scroll, "Select a player and steal their coins", 12, C.muted, Enum.Font.Gotham, UDim2.new(1, 0, 0, 20), nil)
  subtitle.LayoutOrder = 2
  subtitle.TextXAlignment = Enum.TextXAlignment.Center

  -- Status label for feedback
  local statusCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 36), nil, 0)
  statusCard.LayoutOrder = 3
  makeCorner(statusCard, 8)
  local statusLabel = makeLabel(statusCard, "Loading players...", 13, C.muted, Enum.Font.GothamBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
  statusLabel.TextXAlignment = Enum.TextXAlignment.Center
  statusLabel.TextYAlignment = Enum.TextYAlignment.Center

  -- Player list
  local selectedTargetId = nil
  local selectedCard = nil

  local listContainer = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 200), nil, 0)
  listContainer.LayoutOrder = 4
  makeCorner(listContainer, 10)
  listContainer.ClipsDescendants = true

  local listPad = Instance.new("UIPadding")
  listPad.PaddingTop = UDim.new(0, 6)
  listPad.PaddingLeft = UDim.new(0, 8)
  listPad.PaddingRight = UDim.new(0, 8)
  listPad.PaddingBottom = UDim.new(0, 6)
  listPad.Parent = listContainer

  local listLayout = Instance.new("UIListLayout")
  listLayout.Padding = UDim.new(0, 4)
  listLayout.Parent = listContainer

  listLayout:GetPropertyChangedSignal("AbsoluteContentSize"):Connect(function()
    local h = math.min(listLayout.AbsoluteContentSize.Y + 12, 300)
    listContainer.Size = UDim2.new(1, 0, 0, h)
  end)

  local function populateList(players)
    for _, c in ipairs(listContainer:GetChildren()) do
      if c:IsA("TextButton") then c:Destroy() end
    end

    if not players or #players == 0 then
      statusLabel.Text = "No other players online"
      statusLabel.TextColor3 = Color3.fromRGB(255, 180, 50)
      return
    end

    statusLabel.Text = #players .. " players online"
    statusLabel.TextColor3 = Color3.fromRGB(100, 255, 100)

    for _, pinfo in ipairs(players) do
      local row = Instance.new("TextButton")
      row.Size = UDim2.new(1, 0, 0, 36)
      row.BackgroundColor3 = Color3.fromRGB(30, 30, 30)
      row.Text = ""
      row.AutoButtonColor = false
      row.Parent = listContainer
      makeCorner(row, 6)

      local nameLbl = makeLabel(row, tostring(pinfo.name or "Unknown"), 14, C.white, Enum.Font.GothamBold, UDim2.new(0.55, 0, 1, 0), UDim2.new(0, 10, 0, 0))
      nameLbl.TextXAlignment = Enum.TextXAlignment.Left
      nameLbl.TextYAlignment = Enum.TextYAlignment.Center

      local follLbl = makeLabel(row, tostring(pinfo.followers or 0) .. " followers", 12, Color3.fromRGB(37, 244, 238), Enum.Font.Gotham, UDim2.new(0.35, 0, 1, 0), UDim2.new(0.6, 0, 0, 0))
      follLbl.TextXAlignment = Enum.TextXAlignment.Right
      follLbl.TextYAlignment = Enum.TextYAlignment.Center

      local uid = pinfo.userId
      row.MouseButton1Click:Connect(function()
        if selectedCard then
          selectedCard.BackgroundColor3 = Color3.fromRGB(30, 30, 30)
        end
        selectedCard = row
        selectedTargetId = uid
        row.BackgroundColor3 = Color3.fromRGB(60, 40, 40)
      end)
    end
  end

  -- Hack button
  local btnCard = makeFrame(scroll, C.card, UDim2.new(1, 0, 0, 52), nil, 0)
  btnCard.LayoutOrder = 5
  makeCorner(btnCard, 12)

  local hackBtn = Instance.new("TextButton")
  hackBtn.Size = UDim2.new(1, -20, 0, 38)
  hackBtn.Position = UDim2.new(0, 10, 0.5, -19)
  hackBtn.BackgroundColor3 = Color3.fromRGB(239, 68, 68)
  hackBtn.Text = "HACK SELECTED PLAYER"
  hackBtn.Font = Enum.Font.GothamBold
  hackBtn.TextSize = 15
  hackBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
  hackBtn.Parent = btnCard
  makeCorner(hackBtn, 10)
  makeStroke(hackBtn, Color3.fromRGB(120, 30, 30), 2, 0)

  local processing = false

  local function startMinigame(targetId)
    scroll.Visible = false
    local mgFrame = makeFrame(content, C.bg, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0), 1)
    
    local title = makeLabel(mgFrame, "ROUTER BRUTE FORCE", 16, Color3.fromRGB(37, 244, 238), Enum.Font.GothamBlack, UDim2.new(1, 0, 0, 40), UDim2.new(0, 0, 0, 10))
    title.TextXAlignment = Enum.TextXAlignment.Center

    local targetIP = string.format("%02X.%02X", math.random(10, 255), math.random(10, 255))
    local targetLbl = makeLabel(mgFrame, "TARGET IP: " .. targetIP, 20, Color3.fromRGB(239, 68, 68), Enum.Font.GothamBold, UDim2.new(1, 0, 0, 30), UDim2.new(0, 0, 0, 40))
    targetLbl.TextXAlignment = Enum.TextXAlignment.Center
    
    local timeLbl = makeLabel(mgFrame, "TIME: 10.0", 14, C.white, Enum.Font.Gotham, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 70))
    timeLbl.TextXAlignment = Enum.TextXAlignment.Center

    local gridFrame = makeFrame(mgFrame, Color3.fromRGB(20, 20, 20), UDim2.new(1, -20, 0, 200), UDim2.new(0, 10, 0, 100), 0)
    makeCorner(gridFrame, 8)
    
    local gridLayout = Instance.new("UIGridLayout")
    gridLayout.CellSize = UDim2.new(0.48, 0, 0, 40)
    gridLayout.CellPadding = UDim2.new(0.02, 0, 0, 4)
    gridLayout.Parent = gridFrame
    
    local buttons = {}
    local active = true
    
    local function finish(success)
        active = false
        mgFrame:Destroy()
        scroll.Visible = true
        if success then
            statusLabel.Text = "BYPASS SUCCESS!"
            statusLabel.TextColor3 = Color3.fromRGB(100, 255, 100)
            Remotes:WaitForChild("RequestHack"):FireServer(targetId, true)
        else
            statusLabel.Text = "BRUTE FORCE FAILED"
            statusLabel.TextColor3 = Color3.fromRGB(255, 100, 100)
            hackBtn.Text = "HACK SELECTED PLAYER"
            hackBtn.BackgroundColor3 = Color3.fromRGB(239, 68, 68)
            hackBtn.Active = true
            processing = false
            Remotes:WaitForChild("RequestHack"):FireServer(targetId, false)
        end
    end
    
    for i = 1, 10 do
        local btn = Instance.new("TextButton")
        btn.BackgroundColor3 = Color3.fromRGB(40, 40, 40)
        btn.TextColor3 = C.muted
        btn.Font = Enum.Font.Code
        btn.TextSize = 14
        btn.Text = string.format("%02X.%02X", math.random(10, 255), math.random(10, 255))
        btn.Parent = gridFrame
        makeCorner(btn, 6)
        buttons[i] = btn
        
        btn.MouseButton1Click:Connect(function()
            if not active then return end
            if btn.Text == targetIP then
                finish(true)
            else
                btn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
                task.wait(0.2)
                if active then finish(false) end
            end
        end)
    end
    
    task.spawn(function()
        local timeLeft = 10.0
        while active and timeLeft > 0 do
            timeLbl.Text = string.format("TIME: %.1f", timeLeft)
            if math.floor(timeLeft * 10) % 6 == 0 then
                local correctIdx = math.random(1, 10)
                for i = 1, 10 do
                    if i == correctIdx then
                        buttons[i].Text = targetIP
                        buttons[i].BackgroundColor3 = Color3.fromRGB(50, 50, 50)
                        buttons[i].TextColor3 = Color3.fromRGB(255, 255, 255)
                    else
                        buttons[i].Text = string.format("%02X.%02X", math.random(10, 255), math.random(10, 255))
                        buttons[i].BackgroundColor3 = Color3.fromRGB(40, 40, 40)
                        buttons[i].TextColor3 = C.muted
                    end
                end
            end
            task.wait(0.1)
            timeLeft = timeLeft - 0.1
        end
        if active then finish(false) end
    end)
  end

  hackBtn.MouseButton1Click:Connect(function()
    if processing then return end
    if not selectedTargetId then
      statusLabel.Text = "Select a player first!"
      statusLabel.TextColor3 = Color3.fromRGB(255, 100, 100)
      return
    end
    processing = true
    hackBtn.Text = "INITIALIZING MINIGAME..."
    hackBtn.BackgroundColor3 = Color3.fromRGB(100, 30, 30)
    hackBtn.Active = false
    statusLabel.Text = "Preparing brute force attack..."
    statusLabel.TextColor3 = Color3.fromRGB(255, 210, 50)

    task.delay(0.5, function()
      startMinigame(selectedTargetId)
    end)
  end)

  -- Listen for hack result
  Remotes:WaitForChild("HackResult").OnClientEvent:Connect(function(success, info)
    if success then
      local stolen = info and info.amount or 0
      local targetName = info and info.targetName or "rival"
      statusLabel.Text = "Stole " .. stolen .. " coins from " .. targetName .. "!"
      statusLabel.TextColor3 = Color3.fromRGB(100, 255, 100)
    else
      local msg = info or "No valid target"
      statusLabel.Text = "Failed: " .. tostring(msg)
      statusLabel.TextColor3 = Color3.fromRGB(255, 100, 100)
    end
    -- Refresh list
    pcall(function()
      local getList = Remotes:FindFirstChild("GetPlayerList")
      if getList then
        local ok, res = pcall(function() return getList:InvokeServer() end)
        if ok and res then populateList(res) end
      end
    end)
  end)

  -- Initial fetch
  pcall(function()
    local getList = Remotes:FindFirstChild("GetPlayerList")
    if getList then
      local ok, res = pcall(function() return getList:InvokeServer() end)
      if ok and res then
        populateList(res)
      else
        statusLabel.Text = "Could not load players"
      end
    end
  end)
end

return HackApp
