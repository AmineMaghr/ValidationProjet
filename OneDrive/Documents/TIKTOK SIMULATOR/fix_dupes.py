import sys
import re

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

bad_block = '''          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100
          local gameRunning = false
          local totalViews = 0'''

clean_block = '''          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100'''

if bad_block in text:
    text = text.replace(bad_block, clean_block)
    print("Cleaned up duplicates")

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
