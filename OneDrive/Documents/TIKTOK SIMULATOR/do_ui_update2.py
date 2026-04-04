import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Update Card Name
text = re.sub(
    r'local card = Instance\.new\("Frame"\)\s*card\.Size = UDim2\.new\(0, 280, 0, 350\)',
    r'local card = Instance.new("Frame")\n                  card.Name = "SlotCard_" .. i\n                  card.Size = UDim2.new(0, 280, 0, 350)',
    text
)

# 2. Add player picture and name
title_pattern = r'local ssTitle = Instance\.new\("TextLabel"\)\s*ssTitle\.Size = UDim2\.new\(1, 0, 0, 40\)\s*ssTitle\.Position = UDim2\.new\(0, 0, 0, 20\)\s*ssTitle\.BackgroundTransparency = 1\s*ssTitle\.Text = "Your Channel"'
    
new_title = '''local pName = game.Players.LocalPlayer.Name
          local ssTitle = Instance.new("TextLabel")
          ssTitle.Size = UDim2.new(1, 0, 0, 40)
          ssTitle.Position = UDim2.new(0, 25, 0, 20)
          ssTitle.BackgroundTransparency = 1
          ssTitle.Text = pName .. "'s Channel"
          
          local pfp = Instance.new("ImageLabel")
          pfp.Size = UDim2.new(0, 40, 0, 40)
          pfp.Position = UDim2.new(0.5, -120, 0, 20)
          pfp.BackgroundTransparency = 1
          local userId = game.Players.LocalPlayer.UserId
          pfp.Image = game.Players:GetUserThumbnailAsync(userId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size420x420)
          pfp.ZIndex = 61
          Instance.new("UICorner", pfp).CornerRadius = UDim.new(1, 0)
          pfp.Parent = slotSelectionScreen'''

text = re.sub(title_pattern, new_title, text)

# 3. Add play button in middle of video frame
thumb_pattern = r'(local thumb = Instance\.new\("Frame"\)[\s\S]*?thumb\.Parent = card[\s\S]*?Instance\.new\("UICorner", thumb\)\.CornerRadius = UDim\.new\(0, 8\))'

new_thumb = r'''\1
                       
                       local playIcon = Instance.new("ImageLabel")
                       playIcon.Size = UDim2.new(0, 60, 0, 60)
                       playIcon.Position = UDim2.new(0.5, -30, 0.5, -30)
                       playIcon.BackgroundTransparency = 1
                       playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                       playIcon.ImageTransparency = 0.3
                       playIcon.ZIndex = 64
                       playIcon.Parent = thumb'''

text = re.sub(thumb_pattern, new_thumb, text)

# 4. Change "cpm" text and live label
# old: coins.Text = slotData.cpm .. " coins/min"
# new: coins.Text = tostring(math.floor((slotData.cpm / 60) * 10 + 0.5) / 10) .. " coins/sec"

text = re.sub(
    r'coins\.Text = slotData\.cpm \.\. " coins/min"',
    r'local cps = math.floor((slotData.cpm / 60) * 10 + 0.5) / 10\n                       coins.Text = tostring(cps):gsub("%.0$", "") .. " coins/sec"\n                       coins.Name = "CoinsLbl"',
    text
)

# old: live.Text = "? Playing"\n                       live.TextColor3 = Color3.fromRGB(255, 60, 60) 
# new: live.Text = string.format("? %d:%02d", math.floor(slotData.timeLeft/60), slotData.timeLeft%60) \n live.Name = "LiveLbl"

text = re.sub(
    r'live\.Text = "\? Playing"\s*live\.TextColor3 = Color3\.fromRGB\(255, 60, 60\)',
    r'live.Name = "LiveLbl"\n                       local m = math.floor(slotData.timeLeft / 60)\n                       local s = math.floor(slotData.timeLeft % 60)\n                       live.Text = string.format("? %d:%02d", m, s)\n                       live.TextColor3 = Color3.fromRGB(255, 60, 60)',
    text
)

# old: timerFill.ZIndex = 64\n                       timerFill.Parent = timerBg
# new: timerFill.Name = "ProgFill"\n timerFill.ZIndex = 64\n                       timerFill.Parent = timerBg

text = re.sub(
    r'timerFill\.ZIndex = 64\s*timerFill\.Parent = timerBg',
    r'timerFill.Name = "ProgFill"\n                       timerFill.ZIndex = 64\n                       timerFill.Parent = timerBg',
    text
)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print("Updated card rendering successfully!")
