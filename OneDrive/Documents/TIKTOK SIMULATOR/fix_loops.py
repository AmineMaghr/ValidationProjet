import sys
import re

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Update the timer UI initialization text
text = text.replace('timerLbl.Text = "?? LIVE: 30s"', 'timerLbl.Text = string.format("00:%02d", gameTime)')

# 2. Update the timer task logic
old_timer_loop = '''            task.spawn(function()
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

new_timer_loop = '''            task.spawn(function()
                while contentFrame.Parent do
                    task.wait(1)
                    if not minigameStarted then continue end
                    if not gameRunning then break end
                    if gameTime > 0 then
                        gameTime = gameTime - 1
                        timerLbl.Text = string.format("00:%02d", gameTime)
                    end
                    if gameTime <= 0 then
                        endGame()
                    end
                end
            end)'''

if old_timer_loop in text:
    text = text.replace(old_timer_loop, new_timer_loop)
    print("FIXED timer loop")
else:
    print("ERR: Could not find timer loop")

# 3. Update the comment spawner loop
old_spawner_loop = '''            task.spawn(function()
                print("[Game] Click system active")
                while gameRunning and cmtScroll.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''

new_spawner_loop = '''            task.spawn(function()
                print("[Game] Click system active")
                while contentFrame.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    if not gameRunning then break end
                    local spawnDelay = math.random(12, 18) / 10 -- 1.2 to 1.8 seconds'''

if old_spawner_loop in text:
    text = text.replace(old_spawner_loop, new_spawner_loop)
    print("FIXED spawner loop")
else:
    print("ERR: Could not find spawner loop")

# 4. Make sure UI resets when an empty slot is clicked
old_btn_click = '''                      btn.MouseButton1Click:Connect(function()
                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          minigameStarted = true
                      end)'''

new_btn_click = '''                      btn.MouseButton1Click:Connect(function()
                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          
                          -- Reset the minigame variables
                          gameRunning = true
                          gameTime = 30
                          totalViews = 0
                          totalHealth = 100
                          
                          local viewsLabel = uiArea:FindFirstChild("ViewsBox")
                          if viewsLabel and viewsLabel:FindFirstChild("TextLabel") then
                              viewsLabel.TextLabel.Text = "0"
                          end
                          
                          local timerBox = uiArea:FindFirstChild("TimerBox")
                          if timerBox and timerBox:FindFirstChild("TextLabel") then
                              timerBox.TextLabel.Text = "00:30"
                          end
                          
                          -- Clear existing comments in case they are left over
                          if cmtScroll then
                              for _, c in ipairs(cmtScroll:GetChildren()) do
                                  if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                                      c:Destroy()
                                  end
                              end
                          end
                          
                          minigameStarted = true
                      end)'''

if old_btn_click in text:
    text = text.replace(old_btn_click, new_btn_click)
    print("FIXED btn click (added resets)")
else:
    print("ERR: Could not find btn click")

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

