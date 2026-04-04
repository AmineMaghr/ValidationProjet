import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Back to 3 slots
old_slots = '''local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0}
}'''

new_slots = '''local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0}
}'''

if old_slots in text:
    text = text.replace(old_slots, new_slots)
else:
    text = re.sub(r'local vmSlotsData = \{.*?\n\s*\}', new_slots, text, flags=re.DOTALL)

# 2. Revert to UIListLayout
list_layout = '''local slotsLayout = Instance.new("UIListLayout")
          slotsLayout.FillDirection = Enum.FillDirection.Horizontal
          slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder
          slotsLayout.Padding = UDim.new(0, 20)
          slotsLayout.Parent = slotsContainer'''

text = re.sub(r'local slotsLayout = Instance\.new\("UIGridLayout"\).*?slotsLayout\.Parent = slotsContainer', list_layout, text, flags=re.DOTALL)

# 3. Loops
text = text.replace('for i = 1, 6 do', 'for i = 1, 3 do')

# 4. Card sizing
text = text.replace('card.Size = UDim2.new(1, 0, 1, 0)', 'card.Size = UDim2.new(0, 280, 0, 350)')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print('Reverted everything!')
