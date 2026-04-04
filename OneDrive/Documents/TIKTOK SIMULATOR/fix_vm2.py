import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Find the exact string using regex to handle the weird line breaks and backticks
res = re.sub(r'-- SLOT SELECTION SCREEN.*?slotSelectionScreen\.Name = "SlotSelectionScreen"', 
'''-- SLOT SELECTION SCREEN
              local slotSelectionScreen = Instance.new("Frame")
              slotSelectionScreen.Name = "SlotSelectionScreen"''', 
text, flags=re.DOTALL)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(res)
