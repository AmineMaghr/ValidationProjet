import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# 1. Force 3 columns using FillDirectionMaxCells
text = text.replace(
    'slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center\n          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder',
    'slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center\n          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder\n          slotsLayout.FillDirectionMaxCells = 3'
)

# 2. Make absolutely sure cards and buttons are sized correctly.
# The button might be unclickable if it's placed behind the card stroke, though UIStroke doesn't block.
# Let's ensure ZIndex logic is foolproof.
text = text.replace(
    'local btn = Instance.new("TextButton")\n                       btn.Size = UDim2.new(1, 0, 1, 0)\n                       btn.BackgroundTransparency = 1\n                       btn.Text = ""\n                       btn.ZIndex = 65\n                       btn.Parent = card',
    'local btn = Instance.new("TextButton")\n                       btn.Size = UDim2.new(1, 0, 1, 0)\n                       btn.BackgroundTransparency = 1\n                       btn.Text = ""\n                       btn.ZIndex = 100 -- Maximum layer to ensure clickability\n                       btn.Parent = card'
)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print("Forced 3-column stacking and boosted button ZIndex!")
