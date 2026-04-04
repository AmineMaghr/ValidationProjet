import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('            local totalViews = 1000\n', '')
text = text.replace('            local totalHealth = 100\n', '')
text = text.replace('            local gameRunning = true\n', '')
text = text.replace('            local gameTime = 30\n', '')

target = '          local minigameStarted = false'
replacement = '''          local minigameStarted = false
          local gameRunning = false
          local gameTime = 30
          local totalViews = 0
          local totalHealth = 100'''

if target in text:
    text = text.replace(target, replacement)
    print('MOVED variables up')

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
