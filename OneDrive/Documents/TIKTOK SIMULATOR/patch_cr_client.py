import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace comboDisplay creation
old_meter = """    local comboDisplay = makeLabel(rhythmWindow, "", 14, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0.5, 0, 0, 20))
    comboDisplay.AnchorPoint = Vector2.new(0.5, 0)
    comboDisplay.TextXAlignment = Enum.TextXAlignment.Center
    comboDisplay.ZIndex = 1010
    comboDisplay.Visible = false"""

new_meter = """    local rushMeterBg = makeFrame(rhythmWindow, C.card, UDim2.new(0.8, 0, 0, 20), UDim2.new(0.5, 0, 0, 20), 0)
    rushMeterBg.AnchorPoint = Vector2.new(0.5, 0)
    rushMeterBg.ZIndex = 1010
    rushMeterBg.Visible = false
    makeCorner(rushMeterBg, 10)
    
    local rushMeterFill = makeFrame(rushMeterBg, C.indigo, UDim2.new(0, 0, 1, 0), UDim2.new(0, 0, 0, 0), 0)
    makeCorner(rushMeterFill, 10)
    rushMeterFill.ZIndex = 1011
    
    local comboDisplay = makeLabel(rushMeterBg, "RUSH CHANCE: 0%", 12, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
    comboDisplay.TextXAlignment = Enum.TextXAlignment.Center
    comboDisplay.ZIndex = 1012

    local isCoinRush = false
    local function triggerCoinRush()
        if isCoinRush then return end
        isCoinRush = true
        rushMeterBg.Visible = false
        
        -- Explosion animation
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
        end)
    end"""

text = text.replace(old_meter, new_meter)

# Replace combo update logic
old_combo_logic = """      if multiplier > 0.5 then
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
      end"""

new_combo_logic = """      if multiplier > 0.5 then
        currentCombo = currentCombo + 1
        local chance = currentCombo * 0.05
        local chancePct = math.min(chance, 1)
        
        if isCoinRush then
            rushMeterBg.Visible = false
        else
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
        end
      else
        currentCombo = 0
        rushMeterBg.Visible = false
      end"""

text = text.replace(old_combo_logic, new_combo_logic)

with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Patched CloutApp")
