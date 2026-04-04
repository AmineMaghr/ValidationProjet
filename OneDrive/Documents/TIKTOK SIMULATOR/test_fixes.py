import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    content = f.read()

old_passive = '''local passiveTimer = 0

RunService.Heartbeat:Connect(function(dt)
    passiveTimer = passiveTimer + dt
    local fireIncome = false
    if passiveTimer >= 60 then
        passiveTimer = 0
        fireIncome = true
    end

    local totalIncome = 0
    for i=1,3 do
        local slot = vmSlotsData[i]
        if slot.state == "filled" then
            slot.timeLeft = slot.timeLeft - dt
            if slot.timeLeft <= 0 then
                slot.state = "empty"
                slot.timeLeft = 0
                slot.rank = ""
                slot.cpm = 0
                if slotGuis[i] then
                    local exp = Instance.new("TextLabel")
                    exp.Size = UDim2.new(1,0,1,0)
                    exp.BackgroundTransparency = 1
                    exp.Text = "EXPIRED"
                    exp.TextColor3 = Color3.new(1,0,0)
                    exp.TextSize = 24
                    exp.Font = Enum.Font.GothamBold
                    exp.ZIndex = 110
                    exp.Parent = slotGuis[i]
                    task.delay(1.5, function() if exp then exp:Destroy() end end)

                    for _, c in ipairs(slotGuis[i]:GetChildren()) do
                        if c.Name == "FilledUI" then c.Visible = false end
                        if c.Name == "EmptyUI" then c.Visible = true end
                    end
                end
            else
                if fireIncome then
                    totalIncome = totalIncome + slot.cpm
                    if slotGuis[i] then
                        local notif = Instance.new("TextLabel")
                        notif.Size = UDim2.new(1,0,0,30)
                        notif.Position = UDim2.new(0,0,0, -20)
                        notif.BackgroundTransparency = 1
                        notif.Text = "+"..tostring(slot.cpm).." coins"
                        notif.TextColor3 = Color3.fromRGB(0,255,0)
                        notif.TextStrokeTransparency = 0
                        notif.TextSize = 16
                        notif.Font = Enum.Font.GothamBold
                        notif.ZIndex = 110
                        notif.Parent = slotGuis[i]

                        local tw = TweenService:Create(notif, TweenInfo.new(2), {Position = UDim2.new(0,0,0,-60), TextTransparency = 1, TextStrokeTransparency = 1})
                        tw:Play()
                        game.Debris:AddItem(notif, 2)
                    end
                end
            end
        end
    end

    if fireIncome and totalIncome > 0 then
        SlotIncome:FireServer(totalIncome)
    end'''

new_passive = '''local passiveTimer = 0
local textEffectTimer = 0
local incomeAccumulator = 0

RunService.Heartbeat:Connect(function(dt)
    passiveTimer = passiveTimer + dt
    textEffectTimer = textEffectTimer + dt
    local fireIncome = false
    local textEffect = false
    
    if passiveTimer >= 1 then
        passiveTimer = passiveTimer - 1
        fireIncome = true
    end
    if textEffectTimer >= 5 then
        textEffectTimer = textEffectTimer - 5
        textEffect = true
    end

    local totalIncome = 0
    for i=1,3 do
        local slot = vmSlotsData[i]
        if slot.state == "filled" then
            slot.timeLeft = slot.timeLeft - dt
            if slot.timeLeft <= 0 then
                slot.state = "empty"
                slot.timeLeft = 0
                slot.rank = ""
                slot.cpm = 0
                if slotGuis[i] then
                    local exp = Instance.new("TextLabel")
                    exp.Size = UDim2.new(1,0,1,0)
                    exp.BackgroundTransparency = 1
                    exp.Text = "EXPIRED"
                    exp.TextColor3 = Color3.new(1,0,0)
                    exp.TextSize = 24
                    exp.Font = Enum.Font.GothamBold
                    exp.ZIndex = 110
                    exp.Parent = slotGuis[i]
                    task.delay(1.5, function() if exp then exp:Destroy() end end)

                    for _, c in ipairs(slotGuis[i]:GetChildren()) do
                        if c.Name == "FilledUI" then c.Visible = false end
                        if c.Name == "EmptyUI" then c.Visible = true end
                    end
                end
            else
                local incomePerSec = slot.cpm / 60
                
                if fireIncome then
                    totalIncome = totalIncome + incomePerSec
                end
                
                if textEffect then
                    if slotGuis[i] then
                        local notif = Instance.new("TextLabel")
                        notif.Size = UDim2.new(1,0,0,30)
                        notif.Position = UDim2.new(0,0,0, -20)
                        notif.BackgroundTransparency = 1
                        local amt = math.floor(incomePerSec * 5)
                        if amt <= 0 then amt = 1 end
                        notif.Text = "+"..tostring(amt).." coins"
                        notif.TextColor3 = Color3.fromRGB(0,255,0)
                        notif.TextStrokeTransparency = 0
                        notif.TextSize = 16
                        notif.Font = Enum.Font.GothamBold
                        notif.ZIndex = 110
                        notif.Parent = slotGuis[i]

                        local tw = TweenService:Create(notif, TweenInfo.new(2), {Position = UDim2.new(0,0,0,-60), TextTransparency = 1, TextStrokeTransparency = 1})
                        tw:Play()
                        game.Debris:AddItem(notif, 2)
                    end
                end
            end
        end
    end

    if fireIncome and totalIncome > 0 then
        incomeAccumulator = incomeAccumulator + totalIncome
        local fireAmt = math.floor(incomeAccumulator)
        if fireAmt > 0 then
            incomeAccumulator = incomeAccumulator - fireAmt
            SlotIncome:FireServer(fireAmt)
        end
    end'''

old_game_loop = '''                  local gameRunning = true
                  local spawnerControl = true
                  local cmtScroll = uiArea:FindFirstChild("ScrollingFrame")
                  
                  local viewsLabel = uiArea:FindFirstChild("ViewsBox"):FindFirstChild("TextLabel")
                  local timerLabel = uiArea:FindFirstChild("TimerBox"):FindFirstChild("TextLabel")
                  local totalViews = 0
                  local gameTime = 30
                  
                  task.spawn(function()
                      while gameRunning and uiArea.Parent do
                          local delayTime = math.random(8, 20)/10
                          task.wait(delayTime)
                          if not gameRunning or not spawnerControl then break end
                          if cmtScroll.Parent then
                              local txts = {"Cool video!", "Haha lol", "Awesome!", "Wow.", "Nice edits.", "Follow me", "XD", "First"}
                              local cGui = Instance.new("Frame")
                              cGui.Size = UDim2.new(0.9,0,0,40)
                              cGui.BackgroundColor3 = Color3.fromRGB(240,240,240)
                                
                              local txt = Instance.new("TextLabel")
                              txt.Size = UDim2.new(1,-10,1,0)
                              txt.Position = UDim2.new(0,10,0,0)
                              txt.BackgroundTransparency = 1
                              txt.Text = txts[math.random(1, #txts)]
                              txt.TextSize = 14
                              txt.Font = Enum.Font.Gotham
                              txt.TextXAlignment = Enum.TextXAlignment.Left
                              txt.Parent = cGui
                                
                              local btn = Instance.new("TextButton")
                              btn.Size = UDim2.new(0,80,0,24)
                              btn.Position = UDim2.new(1,-90, 0.5, -12)
                              btn.BackgroundColor3 = Color3.fromRGB(100,200,100)
                              btn.Text = "Approve"
                              btn.TextColor3 = Color3.new(1,1,1)
                              btn.Font = Enum.Font.GothamBold
                              btn.Parent = cGui
                              
                              btn.MouseButton1Click:Connect(function()
                                  totalViews = totalViews + math.random(20, 80)
                                  viewsLabel.Text = tostring(totalViews)
                                  
                                  local float = Instance.new("TextLabel")
                                  float.Size = UDim2.new(0,50,0,20)
                                  float.Position = UDim2.new(0,math.random(10,50),0,math.random(10,30))
                                  float.BackgroundTransparency = 1
                                  float.Text = "+views"
                                  float.TextColor3 = Color3.new(0,1,0)
                                  float.TextScaled = true
                                  float.Font = Enum.Font.GothamBold
                                  float.Parent = btn
                                  local tw = TweenService:Create(float, TweenInfo.new(1), {Position = UDim2.new(0,math.random(10,50),0,-20), TextTransparency=1})
                                  tw:Play()
                                  
                                  task.wait(0.1)
                                  cGui:Destroy()
                              end)
                                
                              cGui.Parent = cmtScroll
                          end
                      end
                  end)
                  
                  task.spawn(function()
                      while gameRunning and uiArea.Parent do
                          task.wait(1)
                          if not gameRunning then break end
                          gameTime = gameTime - 1
                          if timerLabel then timerLabel.Text = tostring(gameTime).."s" end
                          if gameTime <= 0 then
                              gameRunning = false
                              break
                          end
                      end
                      
                      if cmtScroll.Parent then
                          local rank = "F"
                          local rtColor = Color3.fromRGB(255, 50, 50)
                          local rtCpm = 20
                          if totalViews >= 1600 then
                              rank = "S"; rtColor = Color3.fromRGB(255, 215, 0); rtCpm = 180
                          elseif totalViews >= 1400 then
                              rank = "A"; rtColor = Color3.fromRGB(50, 255, 50); rtCpm = 120
                          elseif totalViews >= 1200 then
                              rank = "B"; rtColor = Color3.fromRGB(50, 150, 255); rtCpm = 80
                          elseif totalViews >= 900 then
                              rank = "C"; rtColor = Color3.fromRGB(150, 150, 150); rtCpm = 50
                          end
                            
                          uiArea:ClearAllChildren()
                            
                          local resFrame = Instance.new("Frame")
                          resFrame.Size = UDim2.new(0.8,0,0.8,0)
                          resFrame.Position = UDim2.new(0.1,0,0.1,0)
                          resFrame.BackgroundColor3 = Color3.new(1,1,1)
                          resFrame.Parent = uiArea
                            
                          local rTitle = Instance.new("TextLabel")
                          rTitle.Size = UDim2.new(1,0,0,40)
                          rTitle.BackgroundTransparency = 1
                          rTitle.Text = "VIDEO FINISHED"
                          rTitle.Font = Enum.Font.GothamBold
                          rTitle.TextSize = 24
                          rTitle.Parent = resFrame
                            
                          local rRank = Instance.new("TextLabel")
                          rRank.Size = UDim2.new(1,0,0,60)
                          rRank.Position = UDim2.new(0,0,0,50)
                          rRank.BackgroundTransparency = 1
                          rRank.Text = "Rank: "..rank
                          rRank.TextColor3 = rtColor
                          rRank.Font = Enum.Font.GothamBlack
                          rRank.TextSize = 48
                          rRank.Parent = resFrame
                            
                          local rCpm = Instance.new("TextLabel")
                          rCpm.Size = UDim2.new(1,0,0,30)
                          rCpm.Position = UDim2.new(0,0,0,120)
                          rCpm.BackgroundTransparency = 1
                          rCpm.Text = "Est. Coins / min: "..tostring(rtCpm)
                          rCpm.Font = Enum.Font.Gotham
                          rCpm.TextSize = 18
                          rCpm.Parent = resFrame
                            
                          local finBtn = Instance.new("TextButton")
                          finBtn.Size = UDim2.new(0.6,0,0,40)
                          finBtn.Position = UDim2.new(0.2,0,1,-60)
                          finBtn.BackgroundColor3 = Color3.fromRGB(50,150,255)
                          finBtn.Text = "PUBLISH VIDEO"
                          finBtn.TextColor3 = Color3.new(1,1,1)
                          finBtn.Font = Enum.Font.GothamBold
                          finBtn.TextSize = 18
                          finBtn.Parent = resFrame
                            
                          finBtn.MouseButton1Click:Connect(function()
                              vmSlotsData[activeSlotIndex] = {state = "filled", rank = rank, timeLeft = 180, cpm = rtCpm}
                              uiArea.Parent = nil
                              renderSlots()
                              slotSelectionScreen.Visible = true
                          end)
                      end
                  end)'''

new_game_loop = '''                  local minigameStarted = false
                  local gameRunning = true
                  local spawnerControl = true
                  local cmtScroll = uiArea:FindFirstChild("ScrollingFrame")
                  
                  local viewsLabel = uiArea:FindFirstChild("ViewsBox"):FindFirstChild("TextLabel")
                  local timerLabel = uiArea:FindFirstChild("TimerBox"):FindFirstChild("TextLabel")
                  local totalViews = 0
                  local timeLeft = 30
                  
                  local function endGame()
                      gameRunning = false
                      if cmtScroll.Parent then
                          local rank = "F"
                          local rtColor = Color3.fromRGB(255, 50, 50)
                          local rtCpm = 20
                          if totalViews >= 1600 then
                              rank = "S"; rtColor = Color3.fromRGB(255, 215, 0); rtCpm = 180
                          elseif totalViews >= 1400 then
                              rank = "A"; rtColor = Color3.fromRGB(50, 255, 50); rtCpm = 120
                          elseif totalViews >= 1200 then
                              rank = "B"; rtColor = Color3.fromRGB(50, 150, 255); rtCpm = 80
                          elseif totalViews >= 900 then
                              rank = "C"; rtColor = Color3.fromRGB(150, 150, 150); rtCpm = 50
                          end
                            
                          uiArea:ClearAllChildren()
                            
                          local resFrame = Instance.new("Frame")
                          resFrame.Size = UDim2.new(0.8,0,0.8,0)
                          resFrame.Position = UDim2.new(0.1,0,0.1,0)
                          resFrame.BackgroundColor3 = Color3.new(1,1,1)
                          resFrame.Parent = uiArea
                            
                          local rTitle = Instance.new("TextLabel")
                          rTitle.Size = UDim2.new(1,0,0,40)
                          rTitle.BackgroundTransparency = 1
                          rTitle.Text = "VIDEO FINISHED"
                          rTitle.Font = Enum.Font.GothamBold
                          rTitle.TextSize = 24
                          rTitle.Parent = resFrame
                            
                          local rRank = Instance.new("TextLabel")
                          rRank.Size = UDim2.new(1,0,0,60)
                          rRank.Position = UDim2.new(0,0,0,50)
                          rRank.BackgroundTransparency = 1
                          rRank.Text = "Rank: "..rank
                          rRank.TextColor3 = rtColor
                          rRank.Font = Enum.Font.GothamBlack
                          rRank.TextSize = 48
                          rRank.Parent = resFrame
                            
                          local rCpm = Instance.new("TextLabel")
                          rCpm.Size = UDim2.new(1,0,0,30)
                          rCpm.Position = UDim2.new(0,0,0,120)
                          rCpm.BackgroundTransparency = 1
                          rCpm.Text = "Est. Coins / min: "..tostring(rtCpm)
                          rCpm.Font = Enum.Font.Gotham
                          rCpm.TextSize = 18
                          rCpm.Parent = resFrame
                            
                          local finBtn = Instance.new("TextButton")
                          finBtn.Size = UDim2.new(0.6,0,0,40)
                          finBtn.Position = UDim2.new(0.2,0,1,-60)
                          finBtn.BackgroundColor3 = Color3.fromRGB(50,150,255)
                          finBtn.Text = "PUBLISH VIDEO"
                          finBtn.TextColor3 = Color3.new(1,1,1)
                          finBtn.Font = Enum.Font.GothamBold
                          finBtn.TextSize = 18
                          finBtn.Parent = resFrame
                            
                          finBtn.MouseButton1Click:Connect(function()
                              vmSlotsData[activeSlotIndex] = {state = "filled", rank = rank, timeLeft = 180, cpm = rtCpm}
                              uiArea.Parent = nil
                              renderSlots()
                              slotSelectionScreen.Visible = true
                          end)
                      end
                  end

                  task.spawn(function()
                      while gameRunning and uiArea.Parent do
                          local delayTime = math.random(8, 20)/10
                          task.wait(delayTime)
                          if not minigameStarted then continue end
                          if not gameRunning or not spawnerControl then break end
                          if cmtScroll.Parent then
                              local txts = {"Cool video!", "Haha lol", "Awesome!", "Wow.", "Nice edits.", "Follow me", "XD", "First"}
                              local cGui = Instance.new("Frame")
                              cGui.Size = UDim2.new(0.9,0,0,40)
                              cGui.BackgroundColor3 = Color3.fromRGB(240,240,240)
                                
                              local txt = Instance.new("TextLabel")
                              txt.Size = UDim2.new(1,-10,1,0)
                              txt.Position = UDim2.new(0,10,0,0)
                              txt.BackgroundTransparency = 1
                              txt.Text = txts[math.random(1, #txts)]
                              txt.TextSize = 14
                              txt.Font = Enum.Font.Gotham
                              txt.TextXAlignment = Enum.TextXAlignment.Left
                              txt.Parent = cGui
                                
                              local btn = Instance.new("TextButton")
                              btn.Size = UDim2.new(0,80,0,24)
                              btn.Position = UDim2.new(1,-90, 0.5, -12)
                              btn.BackgroundColor3 = Color3.fromRGB(100,200,100)
                              btn.Text = "Approve"
                              btn.TextColor3 = Color3.new(1,1,1)
                              btn.Font = Enum.Font.GothamBold
                              btn.Parent = cGui
                              
                              btn.MouseButton1Click:Connect(function()
                                  totalViews = totalViews + math.random(20, 80)
                                  viewsLabel.Text = tostring(totalViews)
                                  
                                  local float = Instance.new("TextLabel")
                                  float.Size = UDim2.new(0,50,0,20)
                                  float.Position = UDim2.new(0,math.random(10,50),0,math.random(10,30))
                                  float.BackgroundTransparency = 1
                                  float.Text = "+views"
                                  float.TextColor3 = Color3.new(0,1,0)
                                  float.TextScaled = true
                                  float.Font = Enum.Font.GothamBold
                                  float.Parent = btn
                                  local tw = TweenService:Create(float, TweenInfo.new(1), {Position = UDim2.new(0,math.random(10,50),0,-20), TextTransparency=1})
                                  tw:Play()
                                  
                                  task.wait(0.1)
                                  cGui:Destroy()
                              end)
                                
                              cGui.Parent = cmtScroll
                          end
                      end
                  end)
                  
                  task.spawn(function()
                      while gameRunning and uiArea.Parent do
                          task.wait(1)
                          if not minigameStarted then continue end
                          if not gameRunning then break end
                          timeLeft = timeLeft - 1
                          if timerLabel then timerLabel.Text = string.format("00:%02d", timeLeft) end
                          if timeLeft <= 0 then
                              endGame()
                              break
                          end
                      end
                  end)'''

old_btn_click = '''                      btn.MouseButton1Click:Connect(function()
                          activeSlotIndex = i
                          slotSelectionScreen.Visible = false
                          uiArea.Visible = true
                      end)'''

new_btn_click = '''                      btn.MouseButton1Click:Connect(function()
                          activeSlotIndex = i
                          slotSelectionScreen.Visible = false
                          uiArea.Visible = true
                          if minigameStarted ~= nil then
                              minigameStarted = true
                          end
                      end)'''

if old_passive in content:
    content = content.replace(old_passive, new_passive)
    print('PASSIVE REPLACED')
else:
    print('PASSIVE NOT FOUND')

if old_game_loop in content:
    content = content.replace(old_game_loop, new_game_loop)
    print('GAMELOOP REPLACED')
else:
    print('GAMELOOP NOT FOUND')

if old_btn_click in content:
    content = content.replace(old_btn_click, new_btn_click)
    print('BTN REPLACED')
else:
    print('BTN NOT FOUND')
    
with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(content)
