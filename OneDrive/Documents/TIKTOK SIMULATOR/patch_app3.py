import re
import os

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Remove the timer UI definition completely
timer_ui_pattern = r'''\s*local crTimerBg = makeFrame\(sec3.*?\s*task\.wait\(0\.5\)\s*end\s*end\s*end\)'''
text = re.sub(timer_ui_pattern, '', text, flags=re.DOTALL)

# 2. Replace triggerCoinRush
old_trigger = r'''    local function triggerCoinRush\(\).*?        end\)\s*    end'''

new_trigger = r'''    local function triggerCoinRush()
        if isCoinRush then return end
        isCoinRush = true
        rushMeterBg.Visible = false

        -- Explosion animation below speed hint
        local boom = makeLabel(rhythmWindow, "NEXT POST 2X!", 40, Color3.fromRGB(250, 204, 21), fontBold, UDim2.new(1,0,0,50), UDim2.new(0.5,0,0,100))
        boom.AnchorPoint = Vector2.new(0.5, 0)
        boom.ZIndex = 1050
        boom.TextStrokeTransparency = 0
        boom.TextStrokeColor3 = Color3.fromRGB(0,0,0)
        tween(boom, TweenInfo.new(0.4, Enum.EasingStyle.Bounce), {TextSize = 50})
        game.Debris:AddItem(boom, 2)

        local postBtn = sec3:FindFirstChildWhichIsA("TextButton")
        if postBtn then
            tween(postBtn, TweenInfo.new(0.5), {BackgroundColor3 = Color3.fromRGB(250, 204, 21)})
        end

        local crStarted = env.Remotes:FindFirstChild("CoinRushStarted")
        if crStarted then crStarted:FireServer() end
    end'''

match = re.search(old_trigger, text, flags=re.DOTALL)
if match:
    text = text[:match.start()] + new_trigger + text[match.end():]
    print("Replaced triggerCoinRush successfully.")
else:
    print("WARNING: Could not match triggerCoinRush")


# 3. Replace processTap rhythm chance logic
old_tap = r'''      if multiplier > 0\.5 then.*?      else\s*currentCombo = 0\s*rushMeterBg\.Visible = false\s*end'''

new_tap = r'''      local wasCoinRush = isCoinRush
      if isCoinRush then
          isCoinRush = false
          
          -- Animate a "2X COINS!" label
          local usedLab = makeLabel(rhythmWindow, "2X COINS!", 40, Color3.fromRGB(245, 158, 11), fontBold, UDim2.new(1,0,0,50), UDim2.new(0.5,0,0,200))
          usedLab.AnchorPoint = Vector2.new(0.5, 0)
          usedLab.ZIndex = 1100
          usedLab.TextStrokeTransparency = 0
          usedLab.TextStrokeColor3 = C.card
          tween(usedLab, TweenInfo.new(0.4, Enum.EasingStyle.Bounce), {TextSize = 60})
          game.Debris:AddItem(usedLab, 2)

          -- Revert main post button
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
      
      if multiplier > 0.5 then
        currentCombo = currentCombo + 1
        local chance = currentCombo * 0.05
        local chancePct = math.min(chance, 1)

        -- If we aren't currently holding a coin rush buff for the next post, show and roll meter
        if not isCoinRush then
            rushMeterBg.Visible = true
            comboDisplay.Text = "RUSH CHANCE: " .. math.floor(chancePct * 100) .. "%"
            local targetColor = C.indigo:Lerp(Color3.fromRGB(250, 204, 21), chancePct)
            tween(rushMeterFill, TweenInfo.new(0.3), {
                Size = UDim2.new(chancePct, 0, 1, 0),
                BackgroundColor3 = targetColor
            })

            if chancePct > 0 then
                rushMeterBg.Size = UDim2.new(0.85, 0, 0, 24)
                tween(rushMeterBg, TweenInfo.new(0.15), {Size = UDim2.new(0.8, 0, 0, 20)})
            end

            if math.random() < chance then
                currentCombo = 0
                triggerCoinRush()
            end
        else
            rushMeterBg.Visible = false
        end
      else
        currentCombo = 0
        rushMeterBg.Visible = false
      end'''

match2 = re.search(old_tap, text, flags=re.DOTALL)
if match2:
    text = text[:match2.start()] + new_tap + text[match2.end():]
    print("Replaced processTap logic successfully.")
else:
    print("WARNING: Could not match processTap")

with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
