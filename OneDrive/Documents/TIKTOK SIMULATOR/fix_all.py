import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# --- FIX 3: PASSIVE TIMER ---
pass1 = '''local passiveTimer = 0

RunService.Heartbeat:Connect(function(dt)
    passiveTimer = passiveTimer + dt
    local fireIncome = false
    if passiveTimer >= 60 then
        passiveTimer = 0
        fireIncome = true
    end

    local totalIncome = 0
    for i=1,3 do'''
    
pass2 = '''local passiveTimer = 0
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
    for i=1,3 do'''

# --- FIX 3b: PASSIVE ACCUMULATION ---
pass3 = '''                if fireIncome then
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
                end'''
                
pass4 = '''                local incomePerSec = slot.cpm / 60
                
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
                end'''

# --- FIX 3c: ACCUMULATING FIRE SERVER ---
pass5 = '''    if fireIncome and totalIncome > 0 then
        SlotIncome:FireServer(totalIncome)
    end'''
    
pass6 = '''    if fireIncome and totalIncome > 0 then
        incomeAccumulator = incomeAccumulator + totalIncome
        local fireAmt = math.floor(incomeAccumulator)
        if fireAmt > 0 then
            incomeAccumulator = incomeAccumulator - fireAmt
            SlotIncome:FireServer(fireAmt)
        end
    end'''

# --- FIX 1 & 2: Timer, endGame function, and Loops ---
# First, insert local minigameStarted = false and local function endGame() logic
game1 = '''            local gameRunning = true
            local gameTime = 30'''
            
game2 = '''            local gameRunning = true
            local gameTime = 30
            local minigameStarted = false'''

# Second, replace the timer spawn logic and comment spawner
t1 = '''            task.spawn(function()
                while gameRunning and gameTime > 0 do
                    task.wait(1)
                    if not gameRunning then break end
                    gameTime = gameTime - 1
                    timerLbl.Text = "?? LIVE: " .. gameTime .. "s"
                end
                if gameRunning then
                    gameRunning = false
                end
            end)'''
            
t2 = '''            task.spawn(function()
                while gameRunning and vmMaster.Parent do
                    task.wait(1)
                    if not minigameStarted then continue end
                    if not gameRunning then break end
                    gameTime = gameTime - 1
                    timerLbl.Text = string.format("00:%02d", gameTime)
                    if gameTime <= 0 then
                        endGame()
                        break
                    end
                end
            end)'''
            
c1 = '''            task.spawn(function()
                print("[Game] Click system active")
                while gameRunning and cmtScroll.Parent do
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''
                    
c2 = '''            task.spawn(function()
                print("[Game] Click system active")
                while gameRunning and cmtScroll.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''

# Extract the end game block that sits outside the spawner into the endGame function
# Find where "if cmtScroll.Parent then" begins around line 1472
end_block_start = "                if cmtScroll.Parent then\n                    -- Clear existing comments"
end_block_end = "                    end)\n                end\n            end)"

if pass1 in text:
    text = text.replace(pass1, pass2)
    print("REPLACED pass1")
if pass3 in text:
    text = text.replace(pass3, pass4)
    print("REPLACED pass3")
if pass5 in text:
    text = text.replace(pass5, pass6)
    print("REPLACED pass5")
if game1 in text:
    text = text.replace(game1, game2)
    print("REPLACED game1")
if t1 in text:
    text = text.replace(t1, t2)
    print("REPLACED t1")
if c1 in text:
    text = text.replace(c1, c2)
    print("REPLACED c1")

# Extract endGame manually since its multiline and ends at "end)"
idx1 = text.find('                if cmtScroll.Parent then\n                    -- Clear existing comments')
if idx1 != -1:
    idx2 = text.find('                    end)\n                end\n            end)', idx1)
    if idx2 != -1:
        # Include 'end)\n                end'
        idx2 = idx2 + len('                    end)\n                end')
        
        extracted_end_game = text[idx1:idx2]
        
        # Replace the original with empty string
        text = text[:idx1] + "\n" + text[idx2:]
        
        # Now define it right after minigameStarted = false
        game2_end_func = game2 + "\n\n            local function endGame()\n                gameRunning = false\n" + extracted_end_game + "\n            end\n"
        text = text.replace(game2, game2_end_func)
        print("REPLACED endGame")

# Replace slot screen click to start game
btn1 = '''                            slotSelectionScreen.Visible = false
                            vmMaster.Visible = true
                        end)'''
btn2 = '''                            slotSelectionScreen.Visible = false
                            vmMaster.Visible = true
                            minigameStarted = true
                        end)'''
if btn1 in text:
    text = text.replace(btn1, btn2)
    print("REPLACED btn1")
else:
    print("NOT FOUND: btn1")
    
with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

