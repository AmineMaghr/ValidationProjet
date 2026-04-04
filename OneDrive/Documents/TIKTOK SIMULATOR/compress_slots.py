import re
import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# 1. Update Grid Layout
text = text.replace(
    'slotsLayout.CellSize = UDim2.new(0, 250, 0, 310)',
    'slotsLayout.CellSize = UDim2.new(0, 220, 0, 210)'
)
text = text.replace(
    'slotsLayout.CellPadding = UDim2.new(0, 20, 0, 20)',
    'slotsLayout.CellPadding = UDim2.new(0, 15, 0, 15)'
)

# 2. Update Empty States
text = text.replace(
    'plus.Size = UDim2.new(1, 0, 0, 100)',
    'plus.Size = UDim2.new(1, 0, 0, 80)'
)
text = text.replace(
    'plus.Position = UDim2.new(0, 0, 0.5, -70)',
    'plus.Position = UDim2.new(0, 0, 0.5, -50)'
)
text = text.replace(
    'lbl.Position = UDim2.new(0, 0, 0.5, 30)',
    'lbl.Position = UDim2.new(0, 0, 0.5, 15)'
)

# 3. Update Filled States (Thumb, Title, Badge, Coins, Live, Timer)
text = text.replace(
    'thumb.Size = UDim2.new(1, -20, 0, 150)',
    'thumb.Size = UDim2.new(1, -20, 0, 115)'
)
text = text.replace(
    'title.Position = UDim2.new(0, 10, 0, 170)',
    'title.Position = UDim2.new(0, 10, 0, 130)'
)
text = text.replace(
    'title.TextSize = 20',
    'title.TextSize = 16'
)

text = re.sub(
    r'rankBadge\.Size = UDim2\.new\(0, 30, 0, 30\)',
    'rankBadge.Size = UDim2.new(0, 24, 0, 24)',
    text
)
text = re.sub(
    r'rankBadge\.Position = UDim2\.new\(1, -40, 0, 170\)',
    'rankBadge.Position = UDim2.new(1, -34, 0, 130)',
    text
)
text = re.sub(
    r'rankBadge\.TextSize = \d+',
    'rankBadge.TextSize = 14',
    text
)

text = text.replace(
    'coins.Position = UDim2.new(0, 10, 0, 205)',
    'coins.Position = UDim2.new(0, 10, 0, 155)'
)
text = text.replace(
    'coins.TextSize = 16',
    'coins.TextSize = 14'
)

text = text.replace(
    'live.Position = UDim2.new(0, 10, 1, -40)',
    'live.Position = UDim2.new(0, 10, 1, -25)'
)
text = text.replace(
    'live.TextSize = 14',
    'live.TextSize = 12'
)

text = text.replace(
    'timerBg.Position = UDim2.new(0, 10, 1, -15)',
    'timerBg.Position = UDim2.new(0, 10, 1, -8)'
)

# 4. Remove duplicate play icon that got added by accident
text = text.replace('''
                       local playIcon = Instance.new("ImageLabel")
                       playIcon.Size = UDim2.new(0, 60, 0, 60)
                       playIcon.Position = UDim2.new(0.5, -30, 0.5, -30)
                       playIcon.BackgroundTransparency = 1
                       playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                       playIcon.ImageTransparency = 0.3
                       playIcon.ZIndex = 64
                       playIcon.Parent = thumb''', '', 1)

text = text.replace(
    'playIcon.Size = UDim2.new(0, 60, 0, 60)',
    'playIcon.Size = UDim2.new(0, 40, 0, 40)'
)
text = text.replace(
    'playIcon.Position = UDim2.new(0.5, -30, 0.5, -30)',
    'playIcon.Position = UDim2.new(0.5, -20, 0.5, -20)'
)

# 5. Fix card container from clipping the top UI
text = text.replace(
    'slotsContainer.Size = UDim2.new(1, -40, 1, -120)',
    'slotsContainer.Size = UDim2.new(1, -40, 1, -60)'
)
text = text.replace(
    'slotsContainer.Position = UDim2.new(0, 20, 0, 100)',
    'slotsContainer.Position = UDim2.new(0, 20, 0, 70)'
)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print('Shrunk and optimized slots layout!')
