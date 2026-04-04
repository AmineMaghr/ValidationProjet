import re
import sys

path = 'src/client/PhoneUI.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# Make the labels smaller font, wrap properly, not 70% of screen but 60% with margin
text = text.replace('local lTitle = makeLabel(row, upg.name .. " (Lvl " .. lvl .. ")", 14, C.white, FONT_MAIN, UDim2.new(0.7,0,1,0), UDim2.new(0,0,0,0))\n      lTitle.TextXAlignment = Enum.TextXAlignment.Left', 
'''local lTitle = makeLabel(row, upg.name .. " (Lvl " .. lvl .. ")", 12, C.white, FONT_MAIN, UDim2.new(0.65, -10, 1, 0), UDim2.new(0, 0, 0, 0))
      lTitle.TextXAlignment = Enum.TextXAlignment.Left
      lTitle.TextWrapped = true''')

text = text.replace('btn.Size = UDim2.new(0.3, -5, 1, 0)\n      btn.Position = UDim2.new(0.7, 5, 0, 0)',
'''btn.Size = UDim2.new(0.35, 0, 1, 0)
      btn.Position = UDim2.new(0.65, 0, 0, 0)''')

text = text.replace('row.Size = UDim2.new(1, 0, 0, 40)', 'row.Size = UDim2.new(1, 0, 0, 50)')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
    
print("Updated labels and sizes")
