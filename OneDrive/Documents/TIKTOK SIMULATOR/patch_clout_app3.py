import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

trigger_re = r'        -- Explosion animation(.*?)task\.delay\(([0-9]+),\s*function\(\)\s*isCoinRush\s*=\s*false(.*?)end\)'

match = re.search(trigger_re, text, re.DOTALL)
if match:
    print("Found trigger regex match")
    # Instead of replacing everything, let's just make it simpler
    new_text = """        -- Explosion animation below speed hint
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
        end)"""
    text = text[:match.start()] + new_text + text[match.end():]
    print("Replaced Trigger")
else:
    print("Could not find trigger")

with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)

