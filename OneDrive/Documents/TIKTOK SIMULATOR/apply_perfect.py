import sys
import re

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Deduplicate 'local minigameStarted = false'
text = text.replace('            local minigameStarted = false\n            local minigameStarted = false', '            local minigameStarted = false')

# 2. Fix the start game button to avoid NOT FOUND
btn_click = '''                      btn.MouseButton1Click:Connect(function()
                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                      end)'''
btn_click_new = '''                      btn.MouseButton1Click:Connect(function()
                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          if minigameStarted ~= nil then
                              minigameStarted = true
                          end
                      end)'''
if btn_click in text:
    text = text.replace(btn_click, btn_click_new)

# 3. Replace timer loop
t_old = '''            task.spawn(function()
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
            
t_new = '''            task.spawn(function()
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

if t_old in text:
    text = text.replace(t_old, t_new)

# 4. Add minigameStarted check in the click spawner loop
sp_old = '''            task.spawn(function()
                print("[Game] Click system active")
                while gameRunning and cmtScroll.Parent do
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''

sp_new = '''            task.spawn(function()
                print("[Game] Click system active")
                while gameRunning and cmtScroll.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''

if sp_old in text:
    text = text.replace(sp_old, sp_new)

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print("Finished!")
