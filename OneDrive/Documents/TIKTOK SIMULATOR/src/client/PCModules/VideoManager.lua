local VideoManager = {}

function VideoManager.create(winFrame, contentFrame, env)
    local Remotes = env.Remotes
    local player = env.player
    local formatViews = env.formatViews
    local playShakeAnimation = env.playShakeAnimation
    local maxWindowZIndex = env.maxWindowZIndex
    local fontBold = env.fontBold
    local fontReg = env.fontReg
    local floatText = env.floatText
    local spawnStarBurst = env.spawnStarBurst
    local vmSlotsData = env.vmSlotsData
    local slotGuis = env.slotGuis
    local TweenService = env.TweenService
    local HasPC = env.HasPC
    local updateViewsUI = env.updateViewsUI
    local BuyPC = env.BuyPC
    local SlotIncome = env.SlotIncome
    local BuySlot = env.BuySlot
    local lockedSlotData = env.lockedSlotData or nil
    local prestigeUnlockedSlots = env.prestigeUnlockedSlots or 0
    local GetUpgrades = Remotes:WaitForChild("GetUpgrades")

    print("[VideoManager] Layout loaded")
    
    local editMultiplier = 1
    local seoMultiplier = 1
    task.spawn(function()
        while true do
            pcall(function()
                local upg = GetUpgrades:InvokeServer()
                if upg then
                    editMultiplier = 1 + ((upg.EditSpeed or 0) * 0.03)
                    seoMultiplier = 1 + ((upg.SEOAlgorithm or 0) * 0.25)
                end
            end)
            task.wait(5)
        end
    end)
    
              -- =========================================================================
              -- ? GORGEOUS VIDEO APP LAYOUT ?
              -- =========================================================================
    
              -- 1) MAIN CONTAINER (Sleek deep purple/dark aesthetic)
              local vmMaster = Instance.new("Frame")
              vmMaster.Name = "VideoAppMaster"
              vmMaster.Size = UDim2.new(1, 0, 1, 0)
              vmMaster.BackgroundColor3 = Color3.fromRGB(18, 16, 26) -- Rich dark indigo
              vmMaster.BorderSizePixel = 0
              vmMaster.ZIndex = 50
              vmMaster.Parent = contentFrame
              vmMaster.Visible = false
     
              -- LIKE FARM MASTER (Slot 2 minigame)
              local likeFarmMaster = Instance.new("Frame")
              likeFarmMaster.Name = "LikeFarmMaster"
              likeFarmMaster.Size = UDim2.new(1, 0, 1, 0)
              likeFarmMaster.BackgroundColor3 = Color3.fromRGB(15, 10, 25)
              likeFarmMaster.BorderSizePixel = 0
              likeFarmMaster.ZIndex = 50
              likeFarmMaster.Parent = contentFrame
              likeFarmMaster.Visible = false
     
              local lfTopBar = Instance.new("Frame")
              lfTopBar.Size = UDim2.new(1, 0, 0, 55)
              lfTopBar.BackgroundColor3 = Color3.fromRGB(25, 15, 40)
              lfTopBar.BorderSizePixel = 0
              lfTopBar.ZIndex = 51
              lfTopBar.Parent = likeFarmMaster
     
              local lfLogoLbl = Instance.new("TextLabel")
              lfLogoLbl.Size = UDim2.new(1, -20, 1, 0)
              lfLogoLbl.Position = UDim2.new(0, 10, 0, 0)
              lfLogoLbl.BackgroundTransparency = 1
              lfLogoLbl.Text = "❤️ Like Farm — Slot 2"
              lfLogoLbl.TextColor3 = Color3.fromRGB(255, 80, 120)
              lfLogoLbl.Font = Enum.Font.GothamBold
              lfLogoLbl.TextSize = 20
              lfLogoLbl.TextXAlignment = Enum.TextXAlignment.Left
              lfLogoLbl.ZIndex = 52
              lfLogoLbl.Parent = lfTopBar
     
              local lfTimerLbl = Instance.new("TextLabel")
              lfTimerLbl.Size = UDim2.new(0, 120, 1, 0)
              lfTimerLbl.Position = UDim2.new(1, -130, 0, 0)
              lfTimerLbl.BackgroundTransparency = 1
              lfTimerLbl.Text = "30s"
              lfTimerLbl.TextColor3 = Color3.fromRGB(255, 100, 100)
              lfTimerLbl.Font = Enum.Font.GothamBold
              lfTimerLbl.TextSize = 22
              lfTimerLbl.TextXAlignment = Enum.TextXAlignment.Right
              lfTimerLbl.ZIndex = 52
              lfTimerLbl.Parent = lfTopBar
     
              local lfViewsLbl = Instance.new("TextLabel")
              lfViewsLbl.Size = UDim2.new(0, 150, 1, 0)
              lfViewsLbl.Position = UDim2.new(1, -290, 0, 0)
              lfViewsLbl.BackgroundTransparency = 1
              lfViewsLbl.Text = "0 views"
              lfViewsLbl.TextColor3 = Color3.fromRGB(80, 220, 80)
              lfViewsLbl.Font = Enum.Font.GothamBold
              lfViewsLbl.TextSize = 18
              lfViewsLbl.TextXAlignment = Enum.TextXAlignment.Right
              lfViewsLbl.ZIndex = 52
              lfViewsLbl.Parent = lfTopBar
     
              local lfMainArea = Instance.new("Frame")
              lfMainArea.Size = UDim2.new(1, -20, 1, -65)
              lfMainArea.Position = UDim2.new(0, 10, 0, 60)
              lfMainArea.BackgroundTransparency = 1
              lfMainArea.ZIndex = 51
              lfMainArea.Parent = likeFarmMaster
     
              local lfInstrLbl = Instance.new("TextLabel")
              lfInstrLbl.Size = UDim2.new(1, 0, 0, 30)
              lfInstrLbl.BackgroundTransparency = 1
              lfInstrLbl.Text = "Spam click the Like Button! Keep the meter high to earn more views!"
              lfInstrLbl.TextColor3 = Color3.fromRGB(200, 180, 220)
              lfInstrLbl.Font = Enum.Font.Gotham
              lfInstrLbl.TextSize = 14
              lfInstrLbl.ZIndex = 52
              lfInstrLbl.Parent = lfMainArea

              local lfCPSLbl = Instance.new("TextLabel")
              lfCPSLbl.Size = UDim2.new(1, 0, 0, 28)
              lfCPSLbl.Position = UDim2.new(0, 0, 0, 28)
              lfCPSLbl.BackgroundTransparency = 1
              lfCPSLbl.Text = "Meter: 0%"
              lfCPSLbl.TextColor3 = Color3.fromRGB(255, 215, 0)
              lfCPSLbl.Font = Enum.Font.GothamBold
              lfCPSLbl.TextSize = 22
              lfCPSLbl.ZIndex = 52
              lfCPSLbl.Parent = lfMainArea

              local lfMeterBg = Instance.new("Frame")
              lfMeterBg.Size = UDim2.new(0.6, 0, 0, 30)
              lfMeterBg.Position = UDim2.new(0.2, 0, 0, 65)
              lfMeterBg.BackgroundColor3 = Color3.fromRGB(40, 20, 30)
              lfMeterBg.ZIndex = 52
              lfMeterBg.Parent = lfMainArea
              Instance.new("UICorner", lfMeterBg).CornerRadius = UDim.new(0, 8)

              local lfMeterFill = Instance.new("Frame")
              lfMeterFill.Size = UDim2.new(0, 0, 1, 0)
              lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
              lfMeterFill.ZIndex = 53
              lfMeterFill.Parent = lfMeterBg
              Instance.new("UICorner", lfMeterFill).CornerRadius = UDim.new(0, 8)

              local lfBigLikeBtn = Instance.new("TextButton")
              lfBigLikeBtn.Size = UDim2.new(0, 200, 0, 200)
              lfBigLikeBtn.Position = UDim2.new(0.5, -100, 0.5, -60)
              lfBigLikeBtn.BackgroundColor3 = Color3.fromRGB(255, 80, 120)
              lfBigLikeBtn.Text = "??"
              lfBigLikeBtn.TextColor3 = Color3.new(1, 1, 1)
              lfBigLikeBtn.Font = Enum.Font.GothamBlack
              lfBigLikeBtn.TextSize = 100
              lfBigLikeBtn.ZIndex = 55
              lfBigLikeBtn.Parent = lfMainArea
              Instance.new("UICorner", lfBigLikeBtn).CornerRadius = UDim.new(1, 0)
              
              local uiStroke = Instance.new("UIStroke")
              uiStroke.Color = Color3.fromRGB(200, 40, 80)
              uiStroke.Thickness = 6
              uiStroke.Parent = lfBigLikeBtn

              -- SLOT SELECTION SCREEN
              local slotSelectionScreen = Instance.new("Frame")
              slotSelectionScreen.Name = "SlotSelectionScreen"
              slotSelectionScreen.Size = UDim2.new(1, 0, 1, 0)
              slotSelectionScreen.BackgroundColor3 = Color3.fromRGB(15, 15, 20)
              slotSelectionScreen.ZIndex = 60
              slotSelectionScreen.Parent = contentFrame
              slotSelectionScreen.Visible = true
              
              local pName = player.Name
              local headerContainer = Instance.new("Frame")
              headerContainer.Size = UDim2.new(1, 0, 0, 40)
              headerContainer.Position = UDim2.new(0, 0, 0, 15)
              headerContainer.BackgroundTransparency = 1
              headerContainer.Parent = slotSelectionScreen
              
              local hLayout = Instance.new("UIListLayout")
              hLayout.FillDirection = Enum.FillDirection.Horizontal
              hLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
              hLayout.VerticalAlignment = Enum.VerticalAlignment.Center
              hLayout.Padding = UDim.new(0, 10)
              hLayout.SortOrder = Enum.SortOrder.LayoutOrder
              hLayout.Parent = headerContainer
    
              local pfp = Instance.new("ImageLabel")
              pfp.Size = UDim2.new(0, 40, 0, 40)
              pfp.BackgroundTransparency = 1
              pfp.LayoutOrder = 1
              local userId = player.UserId
              pfp.Image = game.Players:GetUserThumbnailAsync(userId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size420x420)
              pfp.ZIndex = 61
              Instance.new("UICorner", pfp).CornerRadius = UDim.new(1, 0)
              pfp.Parent = headerContainer
    
              local ssTitle = Instance.new("TextLabel")
              ssTitle.Size = UDim2.new(0, 0, 0, 40)
              ssTitle.AutomaticSize = Enum.AutomaticSize.X
              ssTitle.BackgroundTransparency = 1
              ssTitle.Text = pName .. "'s Channel"
              ssTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
              ssTitle.Font = Enum.Font.GothamBold
              ssTitle.TextSize = 36
              ssTitle.LayoutOrder = 2
              ssTitle.ZIndex = 61
              ssTitle.Parent = headerContainer
              
              local ssSub = Instance.new("TextLabel")
              ssSub.Size = UDim2.new(1, 0, 0, 20)
              ssSub.Position = UDim2.new(0, 0, 0, 65)
              ssSub.BackgroundTransparency = 1
              ssSub.Text = "Select a slot to upload a video"
              ssSub.TextColor3 = Color3.fromRGB(180, 180, 180)
              ssSub.Font = Enum.Font.Gotham
              ssSub.TextSize = 18
              ssSub.ZIndex = 61
              ssSub.Parent = slotSelectionScreen
              
              local slotsContainer = Instance.new("Frame")
              slotsContainer.Size = UDim2.new(1, 0, 1, -60)
              slotsContainer.Position = UDim2.new(0, 0, 0, 70)
              slotsContainer.BackgroundTransparency = 1
              slotsContainer.ZIndex = 61
              slotsContainer.Parent = slotSelectionScreen
              
              local minigameStarted = false
              local gameRunning = false
              local gameTime = 30
              local totalViews = 0
              local totalHealth = 100
              local resetGameUI = function() end
                        local slotsLayout = Instance.new("UIListLayout")
              slotsLayout.FillDirection = Enum.FillDirection.Horizontal
              slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
              slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder
              slotsLayout.Padding = UDim.new(0, 20)
              slotsLayout.Parent = slotsContainer
              
              local function renderSlots()
                  for _, c in ipairs(slotsContainer:GetChildren()) do
                      if c:IsA("Frame") then c:Destroy() end
                  end
                  for i = 1, 3 do
                      local slotData = vmSlotsData[i]
                      local card = Instance.new("Frame")
                      card.Name = "SlotCard_" .. i
                      card.Size = UDim2.new(0, 280, 0, 350)
                      card.BackgroundColor3 = Color3.fromRGB(25, 25, 30)
                      card.ZIndex = 62
                      card.Parent = slotsContainer
                       slotGuis[i] = card
                      Instance.new("UICorner", card).CornerRadius = UDim.new(0, 12)
                      
                      local btn = Instance.new("TextButton")
                      btn.Size = UDim2.new(1, 0, 1, 0)
                      btn.BackgroundTransparency = 1
                      btn.Text = ""
                      btn.ZIndex = 65
                      btn.Parent = card
                      
                      if slotData.state == "empty" then
                          local stroke = Instance.new("UIStroke")
                          stroke.Color = Color3.fromRGB(80, 80, 90)
                          stroke.Thickness = 2
                          stroke.LineJoinMode = Enum.LineJoinMode.Round
                          stroke.Parent = card
                          
                          local plus = Instance.new("TextLabel")
                          plus.Size = UDim2.new(1, 0, 0, 80)
                          plus.Position = UDim2.new(0, 0, 0.5, -50)
                          plus.BackgroundTransparency = 1
                          plus.Text = "+"
                          plus.TextColor3 = Color3.fromRGB(150, 150, 160)
                          plus.Font = Enum.Font.GothamBold
                          plus.TextSize = 80
                          plus.ZIndex = 63
                          plus.Parent = card
                          
                          local lbl = Instance.new("TextLabel")
                          lbl.Size = UDim2.new(1, 0, 0, 30)
                          lbl.Position = UDim2.new(0, 0, 0.5, 15)
                          lbl.BackgroundTransparency = 1
                          lbl.Text = "Empty  Click to Upload"
                          lbl.TextColor3 = Color3.fromRGB(150, 150, 160)
                          lbl.Font = Enum.Font.Gotham
                          lbl.TextSize = 16
                          lbl.ZIndex = 63
                          lbl.Parent = card
                          
                       btn.MouseButton1Click:Connect(function()
                                 print("[Game] Clicked Empty Slot!")
                                 if i == 2 then
                                     -- Slot 2 = Like Farm
                                     slotSelectionScreen.Visible = false
                                     likeFarmMaster.Visible = true
                                     if lfResetGame then lfResetGame() end
                                 else
                                     -- Slots 1 & 3 = Video minigame
                                     slotSelectionScreen.Visible = false
                                     vmMaster.Visible = true
                                     if resetGameUI then resetGameUI() end
                                 end
                             end)
                       elseif slotData.state == "locked" then
                           local stroke = Instance.new("UIStroke")
                           stroke.Color = Color3.fromRGB(80, 80, 90)
                           stroke.Thickness = 2
                           stroke.LineJoinMode = Enum.LineJoinMode.Round
                           stroke.Parent = card

                           local lockIcon = Instance.new("TextLabel")
                           lockIcon.Size = UDim2.new(1, 0, 0, 80)
                           lockIcon.Position = UDim2.new(0, 0, 0.5, -70)
                           lockIcon.BackgroundTransparency = 1
                           lockIcon.Text = "🔒"
                           lockIcon.Font = Enum.Font.GothamBold
                           lockIcon.TextSize = 60
                           lockIcon.ZIndex = 63
                           lockIcon.Parent = card

                            local priceTxt = (i == 2) and "8,000 coins" or "25,000 coins"
                           local priceLbl = Instance.new("TextLabel")
                           priceLbl.Size = UDim2.new(1, 0, 0, 30)
                           priceLbl.Position = UDim2.new(0, 0, 0.5, 5)
                           priceLbl.BackgroundTransparency = 1
                           priceLbl.Text = priceTxt
                           priceLbl.TextColor3 = Color3.fromRGB(241, 196, 15)
                           priceLbl.Font = Enum.Font.GothamBold
                           priceLbl.TextSize = 18
                           priceLbl.ZIndex = 63
                           priceLbl.Parent = card
                           
                           local buyBtn = Instance.new("TextButton")
                           buyBtn.Size = UDim2.new(0, 160, 0, 40)
                           buyBtn.Position = UDim2.new(0.5, -80, 0.5, 45)
                           buyBtn.BackgroundColor3 = Color3.fromRGB(241, 196, 15) -- Gold
                           buyBtn.Text = "Buy Slot"
                           buyBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
                           buyBtn.Font = Enum.Font.GothamBold
                           buyBtn.TextSize = 16
                           buyBtn.ZIndex = 66
                           buyBtn.Parent = card
                           Instance.new("UICorner", buyBtn).CornerRadius = UDim.new(0, 8)

                            buyBtn.MouseButton1Click:Connect(function()
                                if BuySlot then
                                    local success = BuySlot:InvokeServer(i)
                                    if success then
                                        slotData.state = "empty"
                                        renderSlots()
                                    else
                                        buyBtn.Text = "Not enough coins!"
                                        buyBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
                                        task.delay(1.5, function()
                                            if buyBtn then
                                                buyBtn.Text = "Buy Slot"
                                                buyBtn.BackgroundColor3 = Color3.fromRGB(241, 196, 15)
                                            end
                                        end)
                                    end
                                end
                            end)
                        elseif slotData.state == "lockedPrestige" then
                            local stroke = Instance.new("UIStroke")
                            stroke.Color = Color3.fromRGB(180, 80, 255)
                            stroke.Thickness = 2
                            stroke.LineJoinMode = Enum.LineJoinMode.Round
                            stroke.Parent = card

                            local lockIcon = Instance.new("TextLabel")
                            lockIcon.Size = UDim2.new(1, 0, 0, 80)
                            lockIcon.Position = UDim2.new(0, 0, 0.5, -70)
                            lockIcon.BackgroundTransparency = 1
                            lockIcon.Text = "👑"
                            lockIcon.Font = Enum.Font.GothamBold
                            lockIcon.TextSize = 60
                            lockIcon.ZIndex = 63
                            lockIcon.Parent = card

                            local lockLabel = Instance.new("TextLabel")
                            lockLabel.Size = UDim2.new(1, 0, 0, 30)
                            lockLabel.Position = UDim2.new(0, 0, 0.5, 5)
                            lockLabel.BackgroundTransparency = 1
                            lockLabel.Text = "Locked from Prestige"
                            lockLabel.TextColor3 = Color3.fromRGB(180, 80, 255)
                            lockLabel.Font = Enum.Font.GothamBold
                            lockLabel.TextSize = 16
                            lockLabel.ZIndex = 63
                            lockLabel.Parent = card
                            
                            local infoLabel = Instance.new("TextLabel")
                            infoLabel.Size = UDim2.new(1, 0, 0, 20)
                            infoLabel.Position = UDim2.new(0, 0, 0.5, 35)
                            infoLabel.BackgroundTransparency = 1
                            infoLabel.Text = "Click to restore this slot"
                            infoLabel.TextColor3 = Color3.fromRGB(150, 150, 150)
                            infoLabel.Font = Enum.Font.Gotham
                            infoLabel.TextSize = 12
                            infoLabel.ZIndex = 63
                            infoLabel.Parent = card
                            
                            local restoreBtn = Instance.new("TextButton")
                            restoreBtn.Size = UDim2.new(0, 160, 0, 40)
                            restoreBtn.Position = UDim2.new(0.5, -80, 0.5, 70)
                            restoreBtn.BackgroundColor3 = Color3.fromRGB(180, 80, 255)
                            restoreBtn.Text = "Restore Slot"
                            restoreBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
                            restoreBtn.Font = Enum.Font.GothamBold
                            restoreBtn.TextSize = 16
                            restoreBtn.ZIndex = 66
                            restoreBtn.Parent = card
                            Instance.new("UICorner", restoreBtn).CornerRadius = UDim.new(0, 8)

                            restoreBtn.MouseButton1Click:Connect(function()
                                slotData.state = "empty"
                                renderSlots()
                            end)
                        else
                          local thumb = Instance.new("Frame")
                          thumb.Size = UDim2.new(1, -20, 0, 115)
                          thumb.Position = UDim2.new(0, 10, 0, 10)
                          thumb.BackgroundColor3 = Color3.fromRGB(40, 30, 60)
                          thumb.ZIndex = 63
                          thumb.Parent = card
                          Instance.new("UICorner", thumb).CornerRadius = UDim.new(0, 8)
                           
                           local playIcon = Instance.new("ImageLabel")
                           playIcon.Size = UDim2.new(0, 40, 0, 40)
                           playIcon.Position = UDim2.new(0.5, -20, 0.5, -20)
                           playIcon.BackgroundTransparency = 1
                           playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                           playIcon.ImageTransparency = 0.3
                           playIcon.ZIndex = 64
                           playIcon.Parent = thumb
                           
                           local playIcon = Instance.new("ImageLabel")
                           playIcon.Size = UDim2.new(0, 40, 0, 40)
                           playIcon.Position = UDim2.new(0.5, -20, 0.5, -20)
                           playIcon.BackgroundTransparency = 1
                           playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                           playIcon.ImageTransparency = 0.3
                           playIcon.ZIndex = 64
                           playIcon.Parent = thumb
                          
                          local title = Instance.new("TextLabel")
                          title.Size = UDim2.new(1, -20, 0, 25)
                          title.Position = UDim2.new(0, 10, 0, 130)
                          title.BackgroundTransparency = 1
                          title.Text = "Video #" .. i
                          title.TextColor3 = Color3.fromRGB(255, 255, 255)
                          title.Font = Enum.Font.GothamBold
                          title.TextSize = 16
                          title.TextXAlignment = Enum.TextXAlignment.Left
                          title.ZIndex = 63
                          title.Parent = card
                          
                          local rankBadge = Instance.new("TextLabel")
                          rankBadge.Size = UDim2.new(0, 24, 0, 24)
                          rankBadge.Position = UDim2.new(1, -34, 0, 130)
                          if slotData.rank == "S" then rankBadge.BackgroundColor3 = Color3.fromRGB(255,215,0)
                          elseif slotData.rank == "A" or slotData.rank == "B" then rankBadge.BackgroundColor3 = Color3.fromRGB(100,255,100)
                          else rankBadge.BackgroundColor3 = Color3.fromRGB(255,100,100) end
                          rankBadge.Text = slotData.rank
                          rankBadge.TextColor3 = Color3.fromRGB(0,0,0)
                          rankBadge.Font = Enum.Font.GothamBold
                          rankBadge.TextSize = 14
                          rankBadge.ZIndex = 63
                          rankBadge.Parent = card
                          Instance.new("UICorner", rankBadge).CornerRadius = UDim.new(1,0)
                          
                          local coins = Instance.new("TextLabel")
                          coins.Size = UDim2.new(1, -20, 0, 20)
                          coins.Position = UDim2.new(0, 10, 0, 155)
                          coins.BackgroundTransparency = 1
                          coins.Text = math.floor(math.floor(slotData.cpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec"
                           coins.Name = "CoinsLbl"
                          coins.TextColor3 = Color3.fromRGB(80, 220, 80)
                          coins.Font = Enum.Font.Gotham
                          coins.TextSize = 14
                          coins.TextXAlignment = Enum.TextXAlignment.Left
                          coins.ZIndex = 63
                          coins.Parent = card
                          
                          local live = Instance.new("TextLabel")
                          live.Size = UDim2.new(0, 80, 0, 20)
                          live.Position = UDim2.new(0, 10, 1, -25)
                          live.BackgroundTransparency = 1
                          live.Name = "LiveLbl"
                           local m = math.floor(slotData.timeLeft / 60)
                           local s = math.floor(slotData.timeLeft % 60)
                           live.Text = string.format("▶ %d:%02d", m, s)
                           live.TextColor3 = Color3.fromRGB(255, 60, 60)
                          live.Font = Enum.Font.GothamBold
                          live.TextSize = 12
                          live.TextXAlignment = Enum.TextXAlignment.Left
                          live.ZIndex = 63
                          live.Parent = card
                          
                          local timerBg = Instance.new("Frame")
                           timerBg.Name = "TimerBg"
                           timerBg.Name = "TimerBg"
                          timerBg.Size = UDim2.new(1, -20, 0, 6)
                          timerBg.Position = UDim2.new(0, 10, 1, -8)
                          timerBg.BackgroundColor3 = Color3.fromRGB(40, 40, 40)
                          timerBg.ZIndex = 63
                          timerBg.Parent = card
                          Instance.new("UICorner", timerBg).CornerRadius = UDim.new(1,0)
                          
                          local timerFill = Instance.new("Frame")
                          local pct = math.clamp(slotData.timeLeft / 180, 0, 1)
                          timerFill.Size = UDim2.new(pct, 0, 1, 0)
                          timerFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                          timerFill.Name = "ProgFill"
                           timerFill.Name = "ProgFill"
                           timerFill.ZIndex = 64
                           timerFill.Parent = timerBg
                          Instance.new("UICorner", timerFill).CornerRadius = UDim.new(1,0)
                      end
                  end
              end
              renderSlots()
              print("[SlotSelection] Loaded")
    
              -- Custom loop to drain those slot wait-timers!
                        local function drainLoop()
                  while slotsContainer.Parent do
                      task.wait(0.2)
                      local needsRender = false
                      for i = 1, 3 do
                          local st = vmSlotsData[i]
                          local card = slotsContainer:FindFirstChild("SlotCard_" .. i)
                          if st.state == "filled" then
                              if card then
                                  local tBg = card:FindFirstChild("TimerBg")
                                  if tBg then
                                      local pFill = tBg:FindFirstChild("ProgFill")
                                      if pFill then
                                          local pct = math.clamp(st.timeLeft / 180, 0, 1)
                                          pFill.Size = UDim2.new(pct, 0, 1, 0)
                                      end
                                  end
                                  local liveLbl = card:FindFirstChild("LiveLbl")
                                  if liveLbl then
                                      local m = math.floor(st.timeLeft / 60)
                                      local s = math.floor(st.timeLeft % 60)
                                      liveLbl.Text = string.format("▶ %d:%02d", m, s)
                                  end
                              end
                          else
                              if card and card:FindFirstChild("LiveLbl") then
                                  needsRender = true
                              end
                          end
                      end
                      if needsRender then
                          renderSlots()
                      end
                  end
              end
              task.spawn(drainLoop)
    
              
    
              -- 2) TOP NAVBAR (Glassmorphism feel)
              local topBar = Instance.new("Frame")
              topBar.Name = "TopNav"
              topBar.Size = UDim2.new(1, 0, 0, 60)
              topBar.Position = UDim2.new(0, 0, 0, 0)
              topBar.BackgroundColor3 = Color3.fromRGB(28, 25, 40)
              topBar.BackgroundTransparency = 0.2
              topBar.BorderSizePixel = 0
              topBar.ZIndex = 51
              topBar.Parent = vmMaster
              
              local topBarStroke = Instance.new("UIStroke")
              topBarStroke.Color = Color3.fromRGB(50, 45, 70)
              topBarStroke.Thickness = 1
              topBarStroke.Parent = topBar
    
              -- App Logo (Gradient Text)
              local logoWrapper = Instance.new("Frame")
              logoWrapper.Size = UDim2.new(0, 200, 1, 0)
              logoWrapper.BackgroundTransparency = 1
              logoWrapper.Position = UDim2.new(0, 20, 0, 0)
              logoWrapper.ZIndex = 52
              logoWrapper.Parent = topBar
    
              local ytLogo = Instance.new("TextLabel")
              ytLogo.Name = "LogoText"
              ytLogo.Size = UDim2.new(1, 0, 1, 0)
              ytLogo.BackgroundTransparency = 1
              ytLogo.Text = "▶ GoingViral Pro"
              ytLogo.TextColor3 = Color3.fromRGB(255, 255, 255)
              ytLogo.Font = Enum.Font.GothamBlack
              ytLogo.TextSize = 22
              ytLogo.TextXAlignment = Enum.TextXAlignment.Left
              ytLogo.ZIndex = 53
              ytLogo.Parent = logoWrapper
    
              local logoGradient = Instance.new("UIGradient")
              logoGradient.Color = ColorSequence.new({
                ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 60, 100)),   -- Vibrant pink
                ColorSequenceKeypoint.new(1, Color3.fromRGB(255, 170, 50))    -- Bright orange
              })
              logoGradient.Parent = ytLogo
    
              -- Top Right Icons
              local rightIcons = Instance.new("TextLabel")
              rightIcons.Size = UDim2.new(0, 120, 1, 0)
              rightIcons.Position = UDim2.new(1, -140, 0, 0)
              rightIcons.BackgroundTransparency = 1
              rightIcons.Text = "🔔   ✨   👤"
              rightIcons.TextColor3 = Color3.fromRGB(230, 230, 255)
              rightIcons.Font = Enum.Font.Gotham
              rightIcons.TextSize = 18
              rightIcons.TextXAlignment = Enum.TextXAlignment.Right
              rightIcons.ZIndex = 52
              rightIcons.Parent = topBar
    
              -- 3) MAIN CONTENT LAYOUT
              local mainBody = Instance.new("Frame")
              mainBody.Name = "MainBody"
              mainBody.Size = UDim2.new(1, 0, 1, -60)
              mainBody.Position = UDim2.new(0, 0, 0, 60)
              mainBody.BackgroundTransparency = 1
              mainBody.ZIndex = 51
              mainBody.Parent = vmMaster
    
              -- ================= LEFT SIDE (VIDEO PLAYER) =================
              local leftSide = Instance.new("Frame")
              leftSide.Name = "LeftSideVideo"
              leftSide.Size = UDim2.new(0.65, 0, 1, 0)
              leftSide.Position = UDim2.new(0, 0, 0, 0)
              leftSide.BackgroundTransparency = 1
              leftSide.ZIndex = 52
              leftSide.Parent = mainBody
    
              -- Beautiful Video Player Canvas
              local vidPlayer = Instance.new("Frame")
              vidPlayer.Name = "VideoPlayerRect"
              vidPlayer.Size = UDim2.new(1, -40, 0, 320)
              vidPlayer.Position = UDim2.new(0, 20, 0, 20)
              vidPlayer.BackgroundColor3 = Color3.fromRGB(10, 9, 15)
              vidPlayer.ZIndex = 53
              vidPlayer.Parent = leftSide
              
              local vidCorner = Instance.new("UICorner")
              vidCorner.CornerRadius = UDim.new(0, 16)
              vidCorner.Parent = vidPlayer
              
              local vidStroke = Instance.new("UIStroke")
              vidStroke.Color = Color3.fromRGB(40, 35, 60)
              vidStroke.Thickness = 2
              vidStroke.Parent = vidPlayer
    
              -- Vibrant Thumbnail Gradient
              local thumbnail = Instance.new("Frame")
              thumbnail.Size = UDim2.new(1, 0, 1, 0)
              thumbnail.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
              thumbnail.ZIndex = 54
              thumbnail.Parent = vidPlayer
              local thumbCorner = Instance.new("UICorner")
              thumbCorner.CornerRadius = UDim.new(0, 16)
              thumbCorner.Parent = thumbnail
              local thumbGrad = Instance.new("UIGradient")
              thumbGrad.Color = ColorSequence.new({
                ColorSequenceKeypoint.new(0, Color3.fromRGB(20, 10, 30)),
                ColorSequenceKeypoint.new(1, Color3.fromRGB(45, 15, 60))
              })
              thumbGrad.Rotation = 45
              thumbGrad.Parent = thumbnail
    
              -- Big Play Button (Neon glowing effect)
              local playBtnOuter = Instance.new("Frame")
              playBtnOuter.Size = UDim2.new(0, 80, 0, 80)
              playBtnOuter.Position = UDim2.new(0.5, -40, 0.5, -40)
              playBtnOuter.BackgroundColor3 = Color3.fromRGB(255, 40, 80)
              playBtnOuter.ZIndex = 55
              playBtnOuter.Parent = vidPlayer
              local pbCorner = Instance.new("UICorner")
              pbCorner.CornerRadius = UDim.new(1, 0)
              pbCorner.Parent = playBtnOuter
              
              local playLabel = Instance.new("TextLabel")
              playLabel.Size = UDim2.new(1, 0, 1, 0)
              playLabel.BackgroundTransparency = 1
              playLabel.Text = "?"
              playLabel.TextColor3 = Color3.fromRGB(255, 255, 255)
              playLabel.Font = Enum.Font.GothamBlack
              playLabel.TextSize = 36
              playLabel.ZIndex = 56
              playLabel.Parent = playBtnOuter
    
              -- Video Title
              local vidTitle = Instance.new("TextLabel")
              vidTitle.Name = "VideoTitle"
              vidTitle.Size = UDim2.new(1, -40, 0, 35)
              vidTitle.Position = UDim2.new(0, 25, 0, 350)
              vidTitle.BackgroundTransparency = 1
              vidTitle.Text = "My Awesome Video! ✨"
              vidTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
              vidTitle.Font = Enum.Font.GothamBlack
              vidTitle.TextSize = 26
              vidTitle.TextXAlignment = Enum.TextXAlignment.Left
              vidTitle.ZIndex = 53
              vidTitle.Parent = leftSide
    
              -- Views Counter (with a cool badge look)
              local viewsBadge = Instance.new("Frame")
              viewsBadge.Size = UDim2.new(0, 110, 0, 26)
              viewsBadge.Position = UDim2.new(0, 25, 0, 390)
              viewsBadge.BackgroundColor3 = Color3.fromRGB(40, 210, 130)
              viewsBadge.ZIndex = 53
              viewsBadge.Parent = leftSide
              local vbCorner = Instance.new("UICorner")
              vbCorner.CornerRadius = UDim.new(0, 6)
              vbCorner.Parent = viewsBadge
    
              local vidViews = Instance.new("TextLabel")
              vidViews.Size = UDim2.new(1, 0, 1, 0)
              vidViews.BackgroundTransparency = 1
              vidViews.Text = "👀  1,000 views"
              vidViews.TextColor3 = Color3.fromRGB(15, 40, 25)
              vidViews.Font = Enum.Font.GothamBold
              vidViews.TextSize = 14
              vidViews.ZIndex = 54
              vidViews.Parent = viewsBadge
    
              -- Video Health/Hype Meter
              local hpTitle = Instance.new("TextLabel")
              hpTitle.Size = UDim2.new(0, 100, 0, 20)
              hpTitle.Position = UDim2.new(0, 150, 0, 393)
              hpTitle.BackgroundTransparency = 1
              hpTitle.Text = "HYPE METER 🔥"
              hpTitle.TextColor3 = Color3.fromRGB(200, 190, 220)
              hpTitle.Font = Enum.Font.GothamBold
              hpTitle.TextSize = 14
              hpTitle.TextXAlignment = Enum.TextXAlignment.Left
              hpTitle.ZIndex = 53
              hpTitle.Parent = leftSide
    
              local hpBarBG = Instance.new("Frame")
              hpBarBG.Name = "HealthBarBG"
              hpBarBG.Size = UDim2.new(1, -280, 0, 14)
              hpBarBG.Position = UDim2.new(0, 270, 0, 396)
              hpBarBG.BackgroundColor3 = Color3.fromRGB(25, 20, 35)
              hpBarBG.ZIndex = 53
              hpBarBG.Parent = leftSide
              local hpBgCorner = Instance.new("UICorner")
              hpBgCorner.CornerRadius = UDim.new(1, 0)
              hpBgCorner.Parent = hpBarBG
              local hpStroke = Instance.new("UIStroke")
              hpStroke.Color = Color3.fromRGB(50, 40, 70)
              hpStroke.Parent = hpBarBG
    
              local hpBarFill = Instance.new("Frame")
              hpBarFill.Name = "HealthBarFill"
              hpBarFill.Size = UDim2.new(1, 0, 1, 0)
              hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
              hpBarFill.ZIndex = 54
              hpBarFill.Parent = hpBarBG
              local hpFillCorner = Instance.new("UICorner")
              hpFillCorner.CornerRadius = UDim.new(1, 0)
              hpFillCorner.Parent = hpBarFill
              
              local hpGradient = Instance.new("UIGradient")
              hpGradient.Color = ColorSequence.new({
                ColorSequenceKeypoint.new(0, Color3.fromRGB(255, 200, 0)),    -- Gold
                ColorSequenceKeypoint.new(0.5, Color3.fromRGB(255, 0, 100)),  -- Hot Pink
                ColorSequenceKeypoint.new(1, Color3.fromRGB(150, 0, 255))     -- Purple
              })
              hpGradient.Parent = hpBarFill
    
              -- ================= RIGHT SIDE (COMMENTS) =================
              local rightSide = Instance.new("Frame")
              rightSide.Name = "RightSideComments"
              rightSide.Size = UDim2.new(0.35, -20, 1, -20)
              rightSide.Position = UDim2.new(0.65, 0, 0, 10)
              rightSide.BackgroundColor3 = Color3.fromRGB(26, 24, 38)
              rightSide.ZIndex = 52
              rightSide.Parent = mainBody
    
              local rsCorner = Instance.new("UICorner")
              rsCorner.CornerRadius = UDim.new(0, 16)
              rsCorner.Parent = rightSide
              
              local rsStroke = Instance.new("UIStroke")
              rsStroke.Color = Color3.fromRGB(45, 40, 65)
              rsStroke.Thickness = 2
              rsStroke.Parent = rightSide
    
              -- Comment Section Title (Cute badge design)
              local titleArea = Instance.new("Frame")
              titleArea.Size = UDim2.new(1, 0, 0, 60)
              titleArea.BackgroundTransparency = 1
              titleArea.ZIndex = 53
              titleArea.Parent = rightSide
              
              local cmtsTitle = Instance.new("TextLabel")
              cmtsTitle.Name = "CommentsTitle"
              cmtsTitle.Size = UDim2.new(1, -40, 1, 0)
              cmtsTitle.Position = UDim2.new(0, 20, 0, 0)
              cmtsTitle.BackgroundTransparency = 1
              cmtsTitle.Text = "💬 Live Comments"
              cmtsTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
              cmtsTitle.Font = Enum.Font.GothamBlack
              cmtsTitle.TextSize = 20
              cmtsTitle.TextXAlignment = Enum.TextXAlignment.Left
              cmtsTitle.ZIndex = 54
              cmtsTitle.Parent = titleArea
              
              local divider = Instance.new("Frame")
              divider.Size = UDim2.new(1, 0, 0, 2)
              divider.Position = UDim2.new(0, 0, 1, -2)
              divider.BackgroundColor3 = Color3.fromRGB(40, 35, 55)
              divider.BorderSizePixel = 0
              divider.ZIndex = 54
              divider.Parent = titleArea
    
              -- Comment Area ScrollFrame
              local cmtScroll = Instance.new("ScrollingFrame")
              cmtScroll.Name = "CommentsContainer"
              cmtScroll.Size = UDim2.new(1, -20, 1, -70)
              cmtScroll.Position = UDim2.new(0, 10, 0, 75)
              cmtScroll.BackgroundTransparency = 1
              cmtScroll.CanvasSize = UDim2.new(0, 0, 0, 0)
              cmtScroll.ScrollBarThickness = 6
              cmtScroll.ScrollBarImageColor3 = Color3.fromRGB(80, 70, 110)
              cmtScroll.ZIndex = 53
              cmtScroll.ClipsDescendants = true -- Prevents half-cutoff bottom elements from falling out of frame
              cmtScroll.Parent = rightSide
                local cmtPadding = Instance.new("UIPadding")
                cmtPadding.PaddingTop = UDim.new(0, 10)
                cmtPadding.PaddingBottom = UDim.new(0, 10) -- Added bottom padding so the last comment isn't flat against the wall
                cmtPadding.Parent = cmtScroll
    
    
    
              local cmtListLayout = Instance.new("UIListLayout")
              cmtListLayout.SortOrder = Enum.SortOrder.LayoutOrder
              cmtListLayout.Padding = UDim.new(0, 12)
              cmtListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
                cmtListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
                cmtListLayout.Parent = cmtScroll
    
                -- Comment Spawning Logic
                local goodComments = {
                    "W video!",
                    "this goes hard 🔥",
                    "instant subscribe",
                    "bro ate fr",
                    "algorithm brought me here",
                    "this slaps"
                }
                local badComments = {
                    "this is trash 💀",
                    "ratio + L",
                    "who asked",
                    "mid content",
                    "fell off bro",
                    "unsubscribed",
                    "worst video ever",
                    "delete your channel"
                }
    
                local commentCount = 0
                local lastShiftTime = 0
                
                local function formatViews(v)
                    -- Helper to add commas
                    local formatted = tostring(v)
                    while true do
                        local str, k = string.gsub(formatted, "^(-?%d+)(%d%d%d)", '%1,%2')
                        formatted = str
                        if k == 0 then break end
                    end
                    return string.format("👀  %s views", formatted)
                end
    
                local function playShakeAnimation()
                    if not thumbnail then return end
                    task.spawn(function()
                        for i=1, 6 do
                            local offset = (i%2 == 0) and 3 or -3
                            TweenService:Create(thumbnail, TweenInfo.new(0.05), {Position = UDim2.new(0, offset, 0, 0)}):Play()
                            task.wait(0.05)
                        end
                        TweenService:Create(thumbnail, TweenInfo.new(0.05), {Position = UDim2.new(0, 0, 0, 0)}):Play()
                    end)
                end
    
                local function floatText(textStr, color, parentFrame)
                    local lbl = Instance.new("TextLabel")
                    lbl.Text = textStr
                    lbl.TextColor3 = color
                    lbl.BackgroundTransparency = 1
                    lbl.Font = Enum.Font.GothamBlack
                    lbl.TextSize = 25
                    lbl.Size = UDim2.new(0, 100, 0, 40)
                    -- Start exactly in the middle of current parent, slightly offset
                    lbl.AnchorPoint = Vector2.new(0.5, 0.5)
                    lbl.Position = UDim2.new(0.5, 0, 0.5, 0)
                    lbl.ZIndex = 99
                    lbl.Parent = parentFrame
    
                    TweenService:Create(lbl, TweenInfo.new(1, Enum.EasingStyle.Quad, Enum.EasingDirection.Out), {
                        Position = UDim2.new(0.5, 0, 0, -40),
                        TextTransparency = 1
                    }):Play()
    
                    game.Debris:AddItem(lbl, 1)
                end
    
                local function spawnStarBurst(parentFrame)
                    for i=1, 5 do
                        local star = Instance.new("TextLabel")
                        star.Text = "⭐"
                        star.BackgroundTransparency = 1
                        star.TextSize = 20
                        star.Size = UDim2.new(0, 30, 0, 30)
                        star.AnchorPoint = Vector2.new(0.5, 0.5)
                        star.Position = UDim2.new(0.5, 0, 0.5, 0)
                        star.ZIndex = 99
                        star.Parent = parentFrame
    
                        local angle = math.rad((360 / 5) * i)
                        local dist = 60
                        local tox = math.cos(angle) * dist
                        local toy = math.sin(angle) * dist
    
                        TweenService:Create(star, TweenInfo.new(0.6, Enum.EasingStyle.Back, Enum.EasingDirection.Out), {
                            Position = UDim2.new(0.5, tox, 0.5, toy),
                            TextTransparency = 1,
                            Rotation = math.random(-90, 90)
                        }):Play()
    
                        game.Debris:AddItem(star, 0.6)
                    end
                end
    
                local function updateViewsUI(change)
                    totalViews = math.max(0, totalViews + change)
                    vidViews.Text = formatViews(totalViews)
                    
                    if totalViews < 500 then
                        vidViews.TextColor3 = Color3.fromRGB(255, 60, 60)
                    elseif totalViews > 1200 then
                        vidViews.TextColor3 = Color3.fromRGB(255, 215, 0)
                    else
                        vidViews.TextColor3 = Color3.fromRGB(255, 255, 255)
                    end
                end
    
                local function updateHealthUI(change)
                    totalHealth = math.clamp(totalHealth + change, 0, 100)
                    local pct = totalHealth / 100
    
                    TweenService:Create(hpBarFill, TweenInfo.new(0.3), {
                        Size = UDim2.new(pct, 0, 1, 0)
                    }):Play()
    
                    -- Force override gradient by destroying it
                    for _, child in ipairs(hpBarFill:GetChildren()) do
                        if child:IsA("UIGradient") then
                            child:Destroy()
                        end
                    end
    
                    if pct > 0.6 then
                        hpBarFill.BackgroundColor3 = Color3.fromRGB(80, 220, 80)
                    elseif pct > 0.3 then
                        hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 170, 50)
                    else
                        hpBarFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                    end
                end
    
    
                local function endGame()
                    gameRunning = false
                    if cmtScroll.Parent then
                        -- Clear existing comments
                        for _, c in ipairs(cmtScroll:GetChildren()) do
                            if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                                c:Destroy()
                            end
                        end
                        
                        local rank = "F"
                        local rtColor = Color3.fromRGB(255, 50, 50)
                        local rtCpm = 0.3
                        if totalViews >= 1600 then
                            rank = "S"; rtColor = Color3.fromRGB(255, 215, 0); rtCpm = 2.5
                        elseif totalViews >= 1400 then
                            rank = "A"; rtColor = Color3.fromRGB(50, 255, 50); rtCpm = 1.6
                        elseif totalViews >= 1200 then
                            rank = "B"; rtColor = Color3.fromRGB(50, 150, 255); rtCpm = 1.0
                        elseif totalViews >= 900 then
                            rank = "C"; rtColor = Color3.fromRGB(150, 150, 150); rtCpm = 0.6
                        end
                        print("[Game] Ended with views: " .. totalViews)
                        local MinigameReward = Remotes:FindFirstChild("MinigameReward")
                        if MinigameReward then
                            MinigameReward:FireServer(totalViews)
                        end
                        
                        -- Screen
                        local resScreen = Instance.new("Frame")
                        resScreen.Size = UDim2.new(1, 0, 1, 0)
                        resScreen.BackgroundColor3 = Color3.fromRGB(18, 16, 26)
                        resScreen.ZIndex = 100
                        resScreen.Parent = contentFrame
                        
                        local ytTop = Instance.new("Frame")
                        ytTop.Size = UDim2.new(1, 0, 0, 60)
                        ytTop.BackgroundColor3 = Color3.fromRGB(28, 25, 40)
                        ytTop.BorderSizePixel = 0
                        ytTop.ZIndex = 101
                        ytTop.Parent = resScreen
                        
                        local rTitle = Instance.new("TextLabel")
                        rTitle.Size = UDim2.new(1, 0, 1, 0)
                        rTitle.BackgroundTransparency = 1
                        rTitle.Text = "Video Performance"
                        rTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
                        rTitle.Font = Enum.Font.GothamBlack
                        rTitle.TextSize = 24
                        rTitle.ZIndex = 102
                        rTitle.Parent = ytTop
    
                        local rViews = Instance.new("TextLabel")
                        rViews.Size = UDim2.new(1, 0, 0, 60)
                        rViews.Position = UDim2.new(0, 0, 0, 100)
                        rViews.BackgroundTransparency = 1
                        rViews.Text = totalViews .. " Views"
                        rViews.TextColor3 = rtColor
                        rViews.Font = Enum.Font.GothamBlack
                        rViews.TextSize = 64
                        rViews.ZIndex = 102
                        rViews.Parent = resScreen
                        
                        local chartBg = Instance.new("Frame")
                        chartBg.Size = UDim2.new(0, 400, 0, 150)
                        chartBg.Position = UDim2.new(0.5, -200, 0, 180)
                        chartBg.BackgroundTransparency = 1
                        chartBg.ZIndex = 101
                        chartBg.Parent = resScreen
                        
                        local barWidth = 400 / 10
                        for i=1, 10 do
                            local barHeight = math.random(30, 130)
                            if i == 10 then barHeight = math.clamp(totalViews / 15, 30, 150) end
                            local bar = Instance.new("Frame")
                            bar.Size = UDim2.new(0, barWidth - 4, 0, barHeight)
                            bar.Position = UDim2.new(0, (i-1)*barWidth + 2, 1, -barHeight)
                            bar.BackgroundColor3 = rtColor
                            bar.ZIndex = 102
                            bar.Parent = chartBg
                            Instance.new("UICorner", bar).CornerRadius = UDim.new(0, 4)
                        end
                        
                        local rRankBadge = Instance.new("TextLabel")
                        rRankBadge.Size = UDim2.new(0, 100, 0, 100)
                        rRankBadge.Position = UDim2.new(0.5, -50, 0, 360)
                        rRankBadge.BackgroundColor3 = rtColor
                        rRankBadge.Text = rank
                        rRankBadge.TextColor3 = Color3.fromRGB(20, 20, 20)
                        rRankBadge.Font = Enum.Font.GothamBlack
                        rRankBadge.TextSize = 60
                        rRankBadge.ZIndex = 102
                        rRankBadge.Parent = resScreen
                        local rc = Instance.new("UICorner")
                        rc.CornerRadius = UDim.new(1,0)
                        rc.Parent = rRankBadge
    
                        local rEarn = Instance.new("TextLabel")
                        rEarn.Size = UDim2.new(1, 0, 0, 30)
                        rEarn.Position = UDim2.new(0, 0, 0, 480)
                        rEarn.BackgroundTransparency = 1
                        rEarn.Text = "Slot earnings: " .. math.floor(math.floor(rtCpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec"
                        rEarn.TextColor3 = Color3.fromRGB(200, 200, 200)
                        rEarn.Font = Enum.Font.GothamMedium
                        rEarn.TextSize = 20
                        rEarn.ZIndex = 102
                        rEarn.Parent = resScreen
    
                        task.delay(3, function()
                            local filledSlotIndex = nil
                            for i = 1, 3 do
                                if vmSlotsData[i] and vmSlotsData[i].state == "empty" then
                                    vmSlotsData[i].state = "filled"
                                    vmSlotsData[i].rank = rank
                                    vmSlotsData[i].cpm = rtCpm
                                    vmSlotsData[i].timeLeft = 180
                                    filledSlotIndex = i

                                    if slotGuis[i] then
                                        for _, c in ipairs(slotGuis[i]:GetChildren()) do
                                            if c.Name == "EmptyUI" then c.Visible = false end
                                            if c.Name == "FilledUI" then c.Visible = true end
                                        end

                                        local fUI = slotGuis[i]:FindFirstChild("FilledUI")
                                        if fUI then
                                            local title = fUI:FindFirstChild("Title")
                                            if title then title.Text = "Upload: " .. rank .. " Rank" end
                                            local earn = fUI:FindFirstChild("Earnings")
                                            if earn then earn.Text = math.floor(math.floor(rtCpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec" end
                                        end
                                    end
                                    break
                                end
                            end
                            if filledSlotIndex then
                                pcall(function()
                                    local syncRemote = Remotes:FindFirstChild("SyncSlotData")
                                    if syncRemote then
                                        syncRemote:FireServer(filledSlotIndex, {
                                            state = "filled",
                                            rank = rank,
                                            timeLeft = 180,
                                            cpm = rtCpm,
                                        })
                                    end
                                end)
                            end
                            resScreen:Destroy()
                            vmMaster.Visible = false
                            slotSelectionScreen.Visible = true
                            renderSlots()
                        end)
                    end
                end
    
                
                -- Timer UI
                local timerLbl = Instance.new("TextLabel")
                timerLbl.Size = UDim2.new(0, 100, 1, 0)
                timerLbl.Position = UDim2.new(0.5, -50, 0, 0)
                timerLbl.BackgroundTransparency = 1
                timerLbl.Text = "🔴 LIVE: 30s"
                timerLbl.TextColor3 = Color3.fromRGB(255, 60, 60)
                timerLbl.Font = Enum.Font.GothamBlack
                timerLbl.TextSize = 20
                timerLbl.ZIndex = 56
                timerLbl.Parent = topBar
    
                 resetGameUI = function()
                     gameRunning = true
                     gameTime = 30
                     totalViews = 0
                     totalHealth = 100
                     
                     if vidViews then vidViews.Text = "??  0 views" end
                     if timerLbl then timerLbl.Text = string.format("00:%02d", gameTime) end
                     
                     if cmtScroll then
                         for _, c in ipairs(cmtScroll:GetChildren()) do
                             if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                                 c:Destroy()
                             end
                         end
                     end
                     
                     minigameStarted = true
                 end
    
                 
                
                task.spawn(function()
                    while contentFrame.Parent do
                        task.wait(1)
                        if not minigameStarted or not gameRunning then end
                        if gameTime > 0 then
                            gameTime = gameTime - 1
                            timerLbl.Text = string.format("00:%02d", gameTime)
                        end
                        if gameTime <= 0 then
                            endGame()
                        end
                    end
                end)
    
                task.spawn(function()
                    print("[Game] Click system active")
                    while contentFrame.Parent do
                        if not minigameStarted or not gameRunning then 
                            task.wait(0.5)
                        end
                        local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds 
                        if gameTime <= 10 then
                            spawnDelay = math.random(7, 10) / 10 -- 0.7 to 1.0 seconds (Dialed back the chaos for the finale)
                        elseif gameTime <= 20 then
                            spawnDelay = math.random(9, 14) / 10 -- 0.9 to 1.4 seconds
                        end
                        task.wait(spawnDelay)
                        if not gameRunning then end
    
                        local isGood = math.random() > 0.6
                        local list = isGood and goodComments or badComments
                        local txt = list[math.random(1, #list)]
    
                        commentCount = commentCount + 1
    
                        local cmtCard = Instance.new("Frame")
                        cmtCard.Name = "Comment_" .. commentCount
                        cmtCard.Size = UDim2.new(1, -24, 0, 80)
                        cmtCard.BackgroundColor3 = isGood and Color3.fromRGB(20, 50, 25) or Color3.fromRGB(60, 20, 20)
                        cmtCard.ZIndex = 54
                        cmtCard.LayoutOrder = commentCount
    
                        local cmtCorner = Instance.new("UICorner")
                        cmtCorner.CornerRadius = UDim.new(0, 10)
                        cmtCorner.Parent = cmtCard
    
                        local cmtStroke = Instance.new("UIStroke")
                        cmtStroke.Color = isGood and Color3.fromRGB(40, 100, 50) or Color3.fromRGB(120, 40, 40)
                        cmtStroke.Thickness = 1
                        cmtStroke.Parent = cmtCard
    
                        local avatar = Instance.new("Frame")
                        avatar.Size = UDim2.new(0, 40, 0, 40)
                        avatar.Position = UDim2.new(0, 10, 0.5, -20)
                        avatar.BackgroundColor3 = Color3.fromRGB(math.random(100,255), math.random(100,255), math.random(100,255))
                        avatar.ZIndex = 55
                        avatar.Parent = cmtCard
    
                        local avCorner = Instance.new("UICorner")
                        avCorner.CornerRadius = UDim.new(1, 0)
                        avCorner.Parent = avatar
    
                        local userLbl = Instance.new("TextLabel")
                        userLbl.Size = UDim2.new(1, -70, 0, 20)
                        userLbl.Position = UDim2.new(0, 60, 0, 15)
                        userLbl.BackgroundTransparency = 1
                        userLbl.Text = "@User" .. math.random(1000, 9999)
                        userLbl.TextColor3 = Color3.fromRGB(180, 180, 190)
                        userLbl.Font = Enum.Font.GothamMedium
                        userLbl.TextSize = 13
                        userLbl.TextXAlignment = Enum.TextXAlignment.Left
                        userLbl.ZIndex = 55
                        userLbl.Parent = cmtCard
    
                        local txtLbl = Instance.new("TextLabel")
                        txtLbl.Size = UDim2.new(1, -110, 0, 30)
                        txtLbl.Position = UDim2.new(0, 60, 0, 35)
                        txtLbl.BackgroundTransparency = 1
                        txtLbl.Text = txt
                        txtLbl.TextColor3 = Color3.fromRGB(255, 255, 255)
                        txtLbl.Font = Enum.Font.GothamBold
                        txtLbl.TextSize = 14
                        txtLbl.TextXAlignment = Enum.TextXAlignment.Left
                        txtLbl.ZIndex = 55
                        txtLbl.Parent = cmtCard
    
                        local thumbIcon = Instance.new("TextLabel")
                        thumbIcon.Size = UDim2.new(0, 40, 0, 40)
                        thumbIcon.Position = UDim2.new(1, -45, 0.5, -20)
                        thumbIcon.BackgroundTransparency = 1
                        thumbIcon.Text = "👍 " .. math.random(1, 99)
                        thumbIcon.TextColor3 = Color3.fromRGB(200, 200, 200)
                        thumbIcon.Font = Enum.Font.GothamMedium
                        thumbIcon.TextSize = 14
                        thumbIcon.TextXAlignment = Enum.TextXAlignment.Right
                        thumbIcon.ZIndex = 55
                        thumbIcon.Parent = cmtCard
    
                        -- The Click Button
                        local clickBtn = Instance.new("TextButton")
                        clickBtn.Size = UDim2.new(1, 0, 1, 0)
                        clickBtn.BackgroundTransparency = 1
                        clickBtn.Text = ""
                        clickBtn.ZIndex = 56
                        clickBtn.Parent = cmtCard
    
                        cmtCard.Parent = cmtScroll
    
                        local clicked = false
    
                        clickBtn.MouseButton1Click:Connect(function()
                            if clicked or not gameRunning then return end
                            if os.clock() - lastShiftTime < 0.2 then return end -- Grace period for layout shifts!
                            clicked = true
    
                            if isGood then
                                -- Good Comment Clicked (MISTAKE)
                                updateViewsUI(-40)
                                floatText("-40", Color3.fromRGB(255, 50, 50), cmtCard)
                                floatText("DON'T DELETE GOOD COMMENTS!", Color3.fromRGB(255, 50, 50), cmtCard.Parent.Parent)
                                
                                -- Card shake then disappear
                                task.spawn(function()
                                    local startPos = cmtCard.Position
                                    for i=1, 4 do
                                        local offset = (i%2 == 0) and 5 or -5
                                        TweenService:Create(cmtCard, TweenInfo.new(0.05), {Position = UDim2.new(startPos.X.Scale, startPos.X.Offset + offset, startPos.Y.Scale, startPos.Y.Offset)}):Play()
                                        task.wait(0.05)
                                        TweenService:Create(cmtCard, TweenInfo.new(0.05), {Position = startPos}):Play()
                                        task.wait(0.05)
                                    end
                                    
                                    TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                                    for _, desc in ipairs(cmtCard:GetDescendants()) do
                                        if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                            pcall(function()
                                                TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                            end)
                                        end
                                    end
                                    task.delay(0.3, function() 
                                        lastShiftTime = os.clock()
                                        if cmtCard then cmtCard:Destroy() end 
                                    end)
                                end)
                            else
                                -- Bad Comment Clicked (SUCCESS)
                                updateViewsUI(60)
                                floatText("+60", Color3.fromRGB(80, 255, 80), cmtCard)
                                
                                TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                                for _, desc in ipairs(cmtCard:GetDescendants()) do
                                    if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                        pcall(function()
                                            TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                        end)
                                    end
                                end
                                task.delay(0.3, function() 
                                    lastShiftTime = os.clock()
                                    if cmtCard then cmtCard:Destroy() end 
                                end)
                            end
                        end)
    
                        -- Timer logic for expiration based on difficulty
                        local expTime = 5
                        if gameTime <= 10 then expTime = 3 elseif gameTime <= 20 then expTime = 4 end
                        
                        -- Good comments despawn a bit faster so they don't clog up the screen
                        if isGood then
                            expTime = expTime * 0.7 
                        end
                        
                        task.delay(expTime, function()
                            if not clicked and cmtCard.Parent == cmtScroll then
                                clicked = true
                                if isGood and gameRunning then
                                    -- Good comment reached end (Bonus!)
                                    updateViewsUI(100)
                                    floatText("+100", Color3.fromRGB(255, 215, 0), cmtCard.Parent.Parent)
                                elseif not isGood and gameRunning then
                                    -- Bad comment unclicked (Penalty)
                                    playShakeAnimation()
                                    updateViewsUI(-40)
                                    floatText("-40", Color3.fromRGB(255, 50, 50), cmtCard.Parent.Parent) -- Float on rightSide container
                                    updateHealthUI(-10)
    
                                    -- Red flash on health bar container
                                    local bgOrig = hpBarBG.BackgroundColor3
                                    hpBarBG.BackgroundColor3 = Color3.fromRGB(255, 50, 50)
                                    TweenService:Create(hpBarBG, TweenInfo.new(0.5), {BackgroundColor3 = bgOrig}):Play()
                                end
                                TweenService:Create(cmtCard, TweenInfo.new(0.3), {BackgroundTransparency = 1}):Play()
                                for _, desc in ipairs(cmtCard:GetDescendants()) do
                                    if desc:IsA("GuiObject") and desc.Name ~= "UICorner" then
                                        pcall(function()
                                            TweenService:Create(desc, TweenInfo.new(0.3), {BackgroundTransparency = 1, TextTransparency = 1}):Play()
                                        end)
                                    end
                                end
                                task.delay(0.3, function() 
                                    lastShiftTime = os.clock()
                                    if cmtCard then cmtCard:Destroy() end 
                                end)
                            end
                        end)
    
                        local frames = {}
                        for _, child in ipairs(cmtScroll:GetChildren()) do
                            if child:IsA("Frame") and string.sub(child.Name, 1, 8) == "Comment_" then
                                table.insert(frames, child)
                            end
                        end
                        table.sort(frames, function(a,b) return a.LayoutOrder < b.LayoutOrder end)
                        
                        while #frames > 6 do
                            local oldest = table.remove(frames, 1)
                            if oldest then 
                                lastShiftTime = os.clock()
                                oldest:Destroy() 
                            end
                        end
                        
                        cmtScroll.CanvasSize = UDim2.new(0, 0, 0, #frames * 92)
                        cmtScroll.CanvasPosition = Vector2.new(0, cmtScroll.CanvasSize.Y.Offset)
                    end
                    
    
    
                end)
    
                -- Minor float animation
                task.spawn(function()
                    local elapsed = 0
                    local rs = game:GetService("RunService")
                    while playBtnOuter and playBtnOuter.Parent do
                        local dt = rs.RenderStepped:Wait()
                        elapsed = elapsed + dt
                        local scale = 1 + math.sin(elapsed * 4) * 0.05
                        playBtnOuter.Size = UDim2.new(0, 80 * scale, 0, 80 * scale)
                        playBtnOuter.Position = UDim2.new(0.5, -(40 * scale), 0.5, -(40 * scale))
                    end
                end)
     
    -- ================================================================
    -- LIKE FARM MINIGAME (Slot 2)
    -- ================================================================
    local lfGameRunning = false
    local lfGameTime = 30
    local lfTotalViews = 0
    local lfCombo = 0

    local lfGoodMessages = {"this is fire 🔥", "instant follow", "W video!!", "best thing ive seen", "subscribe NOW", "goes so hard", "not me crying", "im literally screaming"}
    local lfBadMessages = {"delete this", "who asked 💀", "this is garbage", "ratio + L", "mid", "worst post ever", "unsubscribed", "cringe", "your fault", "go touch grass"}

    local function lfFmt(n)
        n = math.floor(n or 0)
        if n >= 1e6 then return string.format("%.1fM", n/1e6)
        elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
        else return tostring(n) end
    end

    local lfTotalHealth = 0
    
    lfBigLikeBtn.MouseButton1Click:Connect(function()
        if not lfGameRunning then return end
        
        -- Pop animation
        TweenService:Create(lfBigLikeBtn, TweenInfo.new(0.05), {Size = UDim2.new(0, 220, 0, 220), Position = UDim2.new(0.5, -110, 0.5, -70)}):Play()
        task.delay(0.05, function()
            if lfGameRunning then
                TweenService:Create(lfBigLikeBtn, TweenInfo.new(0.1), {Size = UDim2.new(0, 200, 0, 200), Position = UDim2.new(0.5, -100, 0.5, -60)}):Play()
            end
        end)
        
        lfTotalHealth = math.clamp(lfTotalHealth + 3, 0, 100)
        lfTotalViews = lfTotalViews + 10 -- plus 10 views per click directly
        lfViewsLbl.Text = lfFmt(lfTotalViews) .. " views"
        
        local pct = lfTotalHealth / 100
        TweenService:Create(lfMeterFill, TweenInfo.new(0.1), {Size = UDim2.new(pct, 0, 1, 0)}):Play()
        if pct > 0.6 then
            lfMeterFill.BackgroundColor3 = Color3.fromRGB(80, 220, 80)
            lfCPSLbl.TextColor3 = Color3.fromRGB(80, 220, 80)
        elseif pct > 0.3 then
            lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 170, 50)
            lfCPSLbl.TextColor3 = Color3.fromRGB(255, 170, 50)
        else
            lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
            lfCPSLbl.TextColor3 = Color3.fromRGB(255, 60, 60)
        end
        lfCPSLbl.Text = "Meter: " .. math.floor(lfTotalHealth) .. "%"
    end)

    local function lfEndGame()
        lfGameRunning = false
        lfViewsLbl.Text = lfFmt(lfTotalViews) .. " views"
        lfViewsLbl.TextColor3 = Color3.fromRGB(255, 215, 0)

        local MinigameReward = Remotes:FindFirstChild("MinigameReward")
        if MinigameReward then
            MinigameReward:FireServer(lfTotalViews)
        end

        local rank = "F"
        local rtColor = Color3.fromRGB(255, 50, 50)
        local rtCpm = 0.3
        if lfTotalViews >= 1600 then
            rank = "S"; rtColor = Color3.fromRGB(255, 215, 0); rtCpm = 3.5
        elseif lfTotalViews >= 1200 then
            rank = "A"; rtColor = Color3.fromRGB(50, 255, 50); rtCpm = 2.4
        elseif lfTotalViews >= 800 then
            rank = "B"; rtColor = Color3.fromRGB(50, 150, 255); rtCpm = 1.5
        elseif lfTotalViews >= 400 then
            rank = "C"; rtColor = Color3.fromRGB(150, 150, 150); rtCpm = 0.8
        end

        local resScreen = Instance.new("Frame")
        resScreen.Size = UDim2.new(1, 0, 1, 0)
        resScreen.BackgroundColor3 = Color3.fromRGB(18, 12, 30)
        resScreen.ZIndex = 100
        resScreen.Parent = likeFarmMaster

        local rTitle = Instance.new("TextLabel")
        rTitle.Size = UDim2.new(1, 0, 0, 40)
        rTitle.Position = UDim2.new(0, 0, 0, 40)
        rTitle.BackgroundTransparency = 1
        rTitle.Text = "Round Complete!"
        rTitle.TextColor3 = Color3.fromRGB(255, 100, 150)
        rTitle.Font = Enum.Font.GothamBlack
        rTitle.TextSize = 28
        rTitle.ZIndex = 101
        rTitle.TextXAlignment = Enum.TextXAlignment.Center
        rTitle.Parent = resScreen

        local rViews = Instance.new("TextLabel")
        rViews.Size = UDim2.new(1, 0, 0, 60)
        rViews.Position = UDim2.new(0, 0, 0, 100)
        rViews.BackgroundTransparency = 1
        rViews.Text = lfFmt(lfTotalViews)
        rViews.TextColor3 = Color3.fromRGB(80, 255, 80)
        rViews.Font = Enum.Font.GothamBlack
        rViews.TextSize = 52
        rViews.ZIndex = 101
        rViews.TextXAlignment = Enum.TextXAlignment.Center
        rViews.Parent = resScreen

        local rSub = Instance.new("TextLabel")
        rSub.Size = UDim2.new(1, 0, 0, 30)
        rSub.Position = UDim2.new(0, 0, 0, 165)
        rSub.BackgroundTransparency = 1
        rSub.Text = "views earned | Rank: " .. rank
        rSub.TextColor3 = Color3.fromRGB(180, 180, 200)
        rSub.Font = Enum.Font.Gotham
        rSub.TextSize = 18
        rSub.ZIndex = 101
        rSub.TextXAlignment = Enum.TextXAlignment.Center
        rSub.Parent = resScreen

        local continueBtn = Instance.new("TextButton")
        continueBtn.Size = UDim2.new(0, 200, 0, 44)
        continueBtn.Position = UDim2.new(0.5, -100, 0, 230)
        continueBtn.BackgroundColor3 = Color3.fromRGB(255, 80, 120)
        continueBtn.Text = "CONTINUE"
        continueBtn.TextColor3 = Color3.new(1, 1, 1)
        continueBtn.Font = Enum.Font.GothamBold
        continueBtn.TextSize = 16
        continueBtn.ZIndex = 101
        continueBtn.Parent = resScreen
        Instance.new("UICorner", continueBtn).CornerRadius = UDim.new(0, 10)

        continueBtn.MouseButton1Click:Connect(function()
            -- Fill slot 2
            local filledSlotIndex = 2
            if vmSlotsData[2] then
                vmSlotsData[2].state = "filled"
                vmSlotsData[2].rank = rank
                vmSlotsData[2].cpm = rtCpm
                vmSlotsData[2].timeLeft = 180
            end
            pcall(function()
                local syncRemote = Remotes:FindFirstChild("SyncSlotData")
                if syncRemote then
                    syncRemote:FireServer(2, {
                        state = "filled",
                        rank = rank,
                        timeLeft = 180,
                        cpm = rtCpm,
                    })
                end
            end)
            resScreen:Destroy()
            likeFarmMaster.Visible = false
            slotSelectionScreen.Visible = true
            renderSlots()
        end)
    end

    lfResetGame = function()
        lfGameRunning = true
        lfGameTime = 30
        lfTotalViews = 0
        lfTotalHealth = 0
        lfViewsLbl.Text = "0 views"
        lfViewsLbl.TextColor3 = Color3.fromRGB(80, 220, 80)
        lfTimerLbl.Text = "30s"
        lfTimerLbl.TextColor3 = Color3.fromRGB(255, 100, 100)
        lfCPSLbl.Text = "Meter: 0%"
        lfMeterFill.Size = UDim2.new(0, 0, 1, 0)
        lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
        lfBigLikeBtn.Size = UDim2.new(0, 200, 0, 200)
        lfBigLikeBtn.Position = UDim2.new(0.5, -100, 0.5, -60)
    end

    -- Like Farm timer
    task.spawn(function()
        while likeFarmMaster.Parent do
            task.wait(1)
            if not lfGameRunning then end
            if lfGameTime > 0 and lfGameRunning then
                lfGameTime = lfGameTime - 1
                lfTimerLbl.Text = lfGameTime .. "s"
                if lfGameTime <= 5 then
                    lfTimerLbl.TextColor3 = Color3.fromRGB(255, 50, 50)
                elseif lfGameTime <= 10 then
                    lfTimerLbl.TextColor3 = Color3.fromRGB(255, 180, 50)
                end
                
                -- Meter drain over time
                lfTotalHealth = math.clamp(lfTotalHealth - 20, 0, 100)
                
                local pct = lfTotalHealth / 100
                TweenService:Create(lfMeterFill, TweenInfo.new(0.2), {Size = UDim2.new(pct, 0, 1, 0)}):Play()
                if pct > 0.6 then
                    lfMeterFill.BackgroundColor3 = Color3.fromRGB(80, 220, 80)
                    lfCPSLbl.TextColor3 = Color3.fromRGB(80, 220, 80)
                elseif pct > 0.3 then
                    lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 170, 50)
                    lfCPSLbl.TextColor3 = Color3.fromRGB(255, 170, 50)
                else
                    lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                    lfCPSLbl.TextColor3 = Color3.fromRGB(255, 60, 60)
                end
                lfCPSLbl.Text = "Meter: " .. math.floor(lfTotalHealth) .. "%"
                
                -- Passive views gain based on meter
                if lfTotalHealth > 0 then
                    local baseEarn = 30
                    local mult = (lfTotalHealth / 100) * 4 -- max x4 multiplier
                    local viewsGained = math.floor(baseEarn + (baseEarn * mult))
                    lfTotalViews = lfTotalViews + viewsGained
                    lfViewsLbl.Text = lfFmt(lfTotalViews) .. " views"
                end
                
            elseif lfGameTime <= 0 and lfGameRunning then
                lfEndGame()
            end
        end
    end)
end

return VideoManager

