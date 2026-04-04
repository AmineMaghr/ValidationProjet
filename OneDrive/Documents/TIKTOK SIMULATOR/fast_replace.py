import sys
import re

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Timer Loop
timer_match = re.search(r'while gameRunning and gameTime > 0 do.*?end\) *', text, re.DOTALL)
if timer_match:
    t_new = '''while contentFrame.Parent do
                    task.wait(1)
                    if not minigameStarted then continue end
                    if not gameRunning then break end
                    if gameTime > 0 then
                        gameTime = gameTime - 1
                        timerLbl.Text = string.format("00:%02d", gameTime)
                    end
                    if gameTime <= 0 then
                        endGame()
                        break
                    end
                end
            end)'''
    text = text[:timer_match.start()] + t_new + text[timer_match.end():]
    print("Fixed timer loop")

# 2. Spawner Loop
sp_match = re.search(r'while gameRunning and cmtScroll\.Parent do.*?local spawnDelay = math\.random\(12, 18\) / 10', text, re.DOTALL)
if sp_match:
    sp_new = '''while contentFrame.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    if not gameRunning then break end
                    local spawnDelay = math.random(12, 18) / 10'''
    text = text[:sp_match.start()] + sp_new + text[sp_match.end():]
    print("Fixed spawner loop")

# 3. Btn click
btn_match = re.search(r'btn.MouseButton1Click:Connect\(function\(\)\s*slotSelectionScreen\.Visible = false\s*vmMaster\.Visible = true\s*minigameStarted = true\s*end\)', text, re.DOTALL)
if btn_match:
    btn_new = '''btn.MouseButton1Click:Connect(function()
                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          
                          -- Reset the minigame variables
                          gameRunning = true
                          gameTime = 30
                          totalViews = 0
                          totalHealth = 100
                          
                          local viewsBox = uiArea:FindFirstChild("ViewsBox")
                          if viewsBox and viewsBox:FindFirstChild("TextLabel") then
                              viewsBox.TextLabel.Text = "0"
                          end
                          
                          local timerBox = uiArea:FindFirstChild("TimerBox")
                          if timerBox and timerBox:FindFirstChild("TextLabel") then
                              timerBox.TextLabel.Text = "00:30"
                          end
                          
                          if cmtScroll then
                              for _, c in ipairs(cmtScroll:GetChildren()) do
                                  if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                                      c:Destroy()
                                  end
                              end
                          end
                          
                          minigameStarted = true
                      end)'''
    text = text[:btn_match.start()] + btn_new + text[btn_match.end():]
    print("Fixed btn click")

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
