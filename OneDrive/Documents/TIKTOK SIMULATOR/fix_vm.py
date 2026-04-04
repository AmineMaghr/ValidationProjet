import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('-- SLOT SELECTION SCREEN
              local slotSelectionScreen = Instance.new("Frame")
              slotSelectionScreen.Name = "SlotSelectionScreen"', 
'''-- SLOT SELECTION SCREEN
              local slotSelectionScreen = Instance.new("Frame")
              slotSelectionScreen.Name = "SlotSelectionScreen"''')

# Just in case:
text = text.replace('-- SLOT SELECTION SCREEN\n              slotSelectionScreen.Name = "SlotSelectionScreen"', 
'''-- SLOT SELECTION SCREEN
              local slotSelectionScreen = Instance.new("Frame")
              slotSelectionScreen.Name = "SlotSelectionScreen"''')

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

