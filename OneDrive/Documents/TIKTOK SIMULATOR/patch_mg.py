import re
with open("src/client/PhoneModules/HackApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

minigame_code = """
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
"""

old_hack_btn = """  hackBtn.MouseButton1Click:Connect(function()
    if processing then return end
    if not selectedTargetId then
      statusLabel.Text = "Select a player first!"
      statusLabel.TextColor3 = Color3.fromRGB(255, 100, 100)
      return
    end
    processing = true
    hackBtn.Text = "HACKING..."
    hackBtn.BackgroundColor3 = Color3.fromRGB(100, 30, 30)
    hackBtn.Active = false

    pcall(function()
      Remotes:WaitForChild("RequestHack"):FireServer(selectedTargetId)
    end)
    task.delay(1.5, function()
      processing = false
      hackBtn.Text = "HACK SELECTED PLAYER"
      hackBtn.BackgroundColor3 = Color3.fromRGB(239, 68, 68)
      hackBtn.Active = true
    end)
  end)"""

new_hack_btn = minigame_code + "\n" + """  hackBtn.MouseButton1Click:Connect(function()
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
  end)"""

text = text.replace(old_hack_btn, new_hack_btn)
print("PATCHING HACKAPP: ", old_hack_btn in text or new_hack_btn in text)
with open("src/client/PhoneModules/HackApp.lua", "w", encoding="utf-8") as f:
    f.write(text)

