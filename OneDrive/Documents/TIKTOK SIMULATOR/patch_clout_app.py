import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Timer Injection
timer_inject_old = 'local postStroke = makeStroke(postBtn, C.indigo, 3, 0.5)'
timer_inject_new = '''local postStroke = makeStroke(postBtn, C.indigo, 3, 0.5)

    local crTimerBg = makeFrame(sec3, Color3.fromRGB(250, 204, 21), UDim2.new(0, 150, 0, 30), UDim2.new(0.5, 0, 0, 30), 0)
    crTimerBg.AnchorPoint = Vector2.new(0.5, 0.5)
    makeCorner(crTimerBg, 15)
    crTimerBg.Visible = false
    crTimerBg.ZIndex = 100
    local crTimerText = makeLabel(crTimerBg, "🔥 COIN RUSH 5s", 14, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    crTimerText.TextXAlignment = Enum.TextXAlignment.Center
    crTimerText.ZIndex = 101

    task.spawn(function()
        while true do
            if crTimerBg.Visible then
                tween(crTimerBg, TweenInfo.new(0.5, Enum.EasingStyle.Sine, Enum.EasingDirection.InOut), {BackgroundTransparency = 0.3})
                task.wait(0.5)
                tween(crTimerBg, TweenInfo.new(0.5, Enum.EasingStyle.Sine, Enum.EasingDirection.InOut), {BackgroundTransparency = 0})
                task.wait(0.5)
            else
                task.wait(0.5)
            end
        end
    end)'''

if timer_inject_old in text:
    text = text.replace(timer_inject_old, timer_inject_new)
    print('Injected Timer UI')

# 2. Trigger Logic to Handle Timer and Boom repositioning
trigger_old = '''        -- Explosion animation
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
        end)'''

trigger_new = '''        -- Explosion animation below speed hint
        local boom = makeLabel(rhythmWindow, "COIN RUSH!", 40, Color3.fromRGB(250, 204, 21), fontBold, UDim2.new(1,0,0,50), UDim2.new(0.5,0,0,100))
        boom.AnchorPoint = Vector2.new(0.5, 0)
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
                    -- Revert to original color based on selection
                    local targetColor = C.indigo
                    for _, child in ipairs(sec1:GetChildren()) do
                        if child:IsA("Frame") and child:FindFirstChild("ContentChoice") then
                            if child.BackgroundColor3 ~= C.card then
                                targetColor = child.BackgroundColor3
                                break
                            end
                        end
                    end
                    tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = targetColor})
                end
            end
        end)'''

if trigger_old in text:
    text = text.replace(trigger_old, trigger_new)
    print("Injected trigger Logic")
else:
    print("WARNING: Trigger old logic not found!")

# 3. Speed Hint Reposition and Fade
old_speed_hint = '''    local speedHint = makeLabel(rhythmWindow, "", 36, C.white, fontBold, UDim2.new(1, 0, 0, 50), UDim2.new(0.5, 0, 0.5, -140))
    speedHint.AnchorPoint = Vector2.new(0.5, 0.5)
    speedHint.TextXAlignment = Enum.TextXAlignment.Center
    speedHint.ZIndex = 1010
    local speedStroke = Instance.new("UIStroke", speedHint)
    speedStroke.Color = Color3.fromRGB(0, 0, 0)
    speedStroke.Thickness = 3
    speedHint.Visible = false'''

new_speed_hint = '''    local speedHint = makeLabel(rhythmWindow, "", 36, C.white, fontBold, UDim2.new(1, 0, 0, 50), UDim2.new(0.5, 0, 0, 40))
    speedHint.AnchorPoint = Vector2.new(0.5, 0)
    speedHint.TextXAlignment = Enum.TextXAlignment.Center
    speedHint.ZIndex = 1010
    local speedStroke = Instance.new("UIStroke", speedHint)
    speedStroke.Color = Color3.fromRGB(0, 0, 0)
    speedStroke.Thickness = 3
    speedHint.Visible = false
    speedHint.TextTransparency = 0
    speedStroke.Transparency = 0'''

if old_speed_hint in text:
    text = text.replace(old_speed_hint, new_speed_hint)
    print("Replaced speed hint creation")
else:
    print("WARNING: Speed hint creation not found")

old_speed_shake = '''        if hintText ~= "" then
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
        task.delay(0.8, function()
          isWaitingToStart = false
          isRhythmActive = true
          speedHint.Visible = false'''

new_speed_shake = '''        if hintText ~= "" then
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
          -- Fade out halfway through delay so it disappears before shrinking
          tween(speedHint, TweenInfo.new(0.4, Enum.EasingStyle.Linear, Enum.EasingDirection.Out, 0, false, 0.4), {TextTransparency = 1})
          tween(speedStroke, TweenInfo.new(0.4, Enum.EasingStyle.Linear, Enum.EasingDirection.Out, 0, false, 0.4), {Transparency = 1})
        end
        task.delay(0.8, function()
          isWaitingToStart = false
          isRhythmActive = true
          speedHint.Visible = false'''

if old_speed_shake in text:
    text = text.replace(old_speed_shake, new_speed_shake)
    print("Replaced speed shake logic")
else:
    print("WARNING: Speed shake logic not found")


with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
