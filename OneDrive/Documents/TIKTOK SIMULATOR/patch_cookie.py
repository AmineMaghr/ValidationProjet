import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_ui = r'''              local lfInstrLbl = Instance.new\("TextLabel"\).*?local slotSelectionScreen = Instance.new\("Frame"\)'''
new_ui = '''              local lfInstrLbl = Instance.new("TextLabel")
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

              -- SLOT SELECTION SCREEN'''
text = re.sub(old_ui, new_ui, text, flags=re.DOTALL)


old_logic = r'''    local function lfSpawnLike.*?return VideoManager'''
new_logic = '''    local lfTotalHealth = 0
    
    lfBigLikeBtn.MouseButton1Click:Connect(function()
        if not lfGameRunning then return end
        
        -- Pop animation
        TweenService:Create(lfBigLikeBtn, TweenInfo.new(0.05), {Size = UDim2.new(0, 220, 0, 220), Position = UDim2.new(0.5, -110, 0.5, -70)}):Play()
        task.delay(0.05, function()
            if lfGameRunning then
                TweenService:Create(lfBigLikeBtn, TweenInfo.new(0.1), {Size = UDim2.new(0, 200, 0, 200), Position = UDim2.new(0.5, -100, 0.5, -60)}):Play()
            end
        end)
        
        lfTotalHealth = math.clamp(lfTotalHealth + 6, 0, 100)
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
                lfTotalHealth = math.clamp(lfTotalHealth - 15, 0, 100)
                
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

return VideoManager'''

text = re.sub(old_logic, new_logic, text, flags=re.DOTALL)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print("PATCH_MINIGAME: " + str("return VideoManager" in text))

