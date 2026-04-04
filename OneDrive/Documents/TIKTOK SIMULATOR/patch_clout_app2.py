import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

trigger_old = """        -- Explosion animation
        local boom = makeLabel(rhythmWindow, "COIN RUSH!", 40, Color3.fromRGB(250, 204, 21), fontBold, UDim2.new(1,0,1,0), UDim2.new(0,0,0,0))
        boom.ZIndex = 1050
        boom.TextStrokeTransparency = 0
        boom.TextStrokeColor3 = Color3.fromRGB(0,0,0)
        tween(boom, TweenInfo.new(0.5, Enum.EasingStyle.Bounce), {TextSize = 60})
        game.Debris:AddItem(boom, 2)

        -- Screen edges UI effect
        tween(rhythmWindow, TweenInfo.new(0.5), {BackgroundColor3 = Color3.fromRGB(80, 70, 20)})

        local postBtn = sec3:FindFirstChildWhichIsA("TextButton")
        if postBtn then
            tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = Color3.fromRGB(250, 204, 21)})
        end

        local crStarted = env.Remotes:FindFirstChild("CoinRushStarted")
        if crStarted then crStarted:FireServer(5) end

        task.delay(5, function()
            isCoinRush = false
            tween(rhythmWindow, TweenInfo.new(0.5), {BackgroundColor3 = C.card})
            if postBtn then
                tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = C.indigo})
            end
        end)"""

trigger_new = """        -- Explosion animation below speed hint
        local boom = makeLabel(rhythmWindow, "COIN RUSH!", 40, Color3.fromRGB(250, 204, 21), fontBold, UDim2.new(1,0,0,50), UDim2.new(0,0,0,100))
        boom.ZIndex = 1050
        boom.TextStrokeTransparency = 0
        boom.TextStrokeColor3 = Color3.fromRGB(0,0,0)
        tween(boom, TweenInfo.new(0.5, Enum.EasingStyle.Bounce), {TextSize = 50})
        game.Debris:AddItem(boom, 2)

        -- Screen edges UI effect
        tween(rhythmWindow, TweenInfo.new(0.5), {BackgroundColor3 = Color3.fromRGB(80, 70, 20)})

        local postBtn = sec3:FindFirstChildWhichIsA("TextButton")
        if postBtn then
            tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = Color3.fromRGB(250, 204, 21)})
        end

        local crStarted = env.Remotes:FindFirstChild("CoinRushStarted")
        local dur = 5
        if crStarted then crStarted:FireServer(dur) end

        crTimerBg.Visible = true
        crTimerText.Text = "🔥 COIN RUSH " .. dur .. "s"
        
        task.spawn(function()
            for i = dur, 1, -1 do
                if not isCoinRush then break end
                crTimerText.Text = "🔥 COIN RUSH " .. i .. "s"
                task.wait(1)
            end
            if isCoinRush then
                isCoinRush = false
                crTimerBg.Visible = false
                tween(rhythmWindow, TweenInfo.new(0.5), {BackgroundColor3 = C.card})
                if postBtn then
                    local targetColor = C.indigo
                    -- simplified reverting
                    tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = targetColor})
                end
            end
        end)"""

if trigger_old in text:
    text = text.replace(trigger_old, trigger_new)
    print("Replaced Trigger")
else:
    print("Could not find trigger")


speed_shake_old = """      if hintText ~= "" then
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
      end"""

speed_shake_new = """      if hintText ~= "" then
        speedHint.Text = hintText
        speedHint.TextColor3 = hintColor
        speedHint.Visible = true
        speedHint.TextTransparency = 0
        speedStroke.Transparency = 0
        speedHint.Position = UDim2.new(0.5, 0, 0, 40)
        if doShake then
          task.spawn(function()
            for i = 1, 16 do
              if not isWaitingToStart and not isRhythmActive then break end
              speedHint.Position = UDim2.new(0.5, math.random(-6, 6), 0, 40 + math.random(-6, 6))
              task.wait(0.05)
            end
            speedHint.Position = UDim2.new(0.5, 0, 0, 40)
          end)
        end
        tween(speedHint, TweenInfo.new(0.5, Enum.EasingStyle.Linear, Enum.EasingDirection.Out, 0, false, 0.3), {TextTransparency = 1})
        tween(speedStroke, TweenInfo.new(0.5, Enum.EasingStyle.Linear, Enum.EasingDirection.Out, 0, false, 0.3), {Transparency = 1})
      end"""

if speed_shake_old in text:
    text = text.replace(speed_shake_old, speed_shake_new)
    print("Replaced speed_shake")
else:
    print("Could not find speed_shake")

with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
