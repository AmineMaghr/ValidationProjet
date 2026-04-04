import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Add forward declaration of resetGameUI
target_vars = '''          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100'''

repl_vars = '''          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100
          local resetGameUI = function() end'''

if target_vars in text:
    text = text.replace(target_vars, repl_vars)
    print("Added resetGameUI declaration")

# Replace btn click with a callback
old_click = '''                       btn.MouseButton1Click:Connect(function()
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

new_click = '''                       btn.MouseButton1Click:Connect(function()
                           slotSelectionScreen.Visible = false
                           vmMaster.Visible = true
                           resetGameUI()
                       end)'''

if old_click in text:
    text = text.replace(old_click, new_click)
    print("Replaced btn click logic")
else:
    print("Couldn't find old click")

# Add the actual resetGameUI definition later at the bottom of the set
target_timer = 'timerLbl.Parent = topBar'

repl_timer = '''timerLbl.Parent = topBar

             resetGameUI = function()
                 gameRunning = true
                 gameTime = 30
                 totalViews = 0
                 totalHealth = 100
                 
                 if vidViews then vidViews.Text = "??  0 views" end
                 if timerLbl then timerLbl.Text = string.format("00:%02d", gameTime) end
                 
                 if cmtScroll then
                     for _, c in ipairs(cmtScroll:GetChildren()) do
                         if c:IsA("Frame") and string.sub(c.Name, 1, 8) == "Comment_" then
                             c:Destroy()
                         end
                     end
                 end
                 
                 minigameStarted = true
             end'''

if target_timer in text:
    text = text.replace(target_timer, repl_timer)
    print("Added resetGameUI implementation")
else:
    print("Couldn't find timerLbl.Parent")

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
