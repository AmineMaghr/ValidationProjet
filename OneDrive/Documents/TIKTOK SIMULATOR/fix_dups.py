import re
import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# 1. Clean up duplicate resetGameUI definitions
text = text.replace('local resetGameUI = function() end\n           local resetGameUI = function() end', 'local resetGameUI = function() end')
text = text.replace('local resetGameUI = function() end\nlocal resetGameUI = function() end', 'local resetGameUI = function() end')

dup_block = '''resetGameUI = function()
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

count = text.count(dup_block)
if count > 1:
    text = text.replace(dup_block, '', count - 1)

# 2. Fix the layout to look stacked 2x3 visually. We can do this by forcing a tighter grid and making slotsContainer bigger.
# Instead of cell size 220, let's make it more perfectly suited for PC size.
text = text.replace(
    'slotsLayout.CellSize = UDim2.new(0, 220, 0, 210)',
    'slotsLayout.CellSize = UDim2.new(0, 200, 0, 200)'
)
text = text.replace(
    'slotsLayout.CellPadding = UDim2.new(0, 15, 0, 15)',
    'slotsLayout.CellPadding = UDim2.new(0, 10, 0, 10)'
)
# Ensure the container is huge so it doesn't clip
text = text.replace(
    'slotsContainer.Size = UDim2.new(1, -40, 1, -60)',
    'slotsContainer.Size = UDim2.new(1, 0, 1, -60)'
)
text = text.replace(
    'slotsContainer.Position = UDim2.new(0, 20, 0, 70)',
    'slotsContainer.Position = UDim2.new(0, 0, 0, 70)'
)

# 3. Ensure the active status is properly resetting to true. If minigameStarted wasn't working, let's ensure the variables aren't shadowed anywhere.

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print("Duplicates cleaned and sizes tweaked!")
