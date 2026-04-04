import sys
with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

bad_str = '''            local gameRunning = true
            local gameTime = 30
            local minigameStarted = false
            local minigameStarted = false

            local function endGame()
                gameRunning = false
                if cmtScroll.Parent then'''
            
good_str = '''            local gameRunning = true
            local gameTime = 30
            
            -- Timer UI'''

if bad_str in text:
    print('FOUND BAD STRING. Attempting to fix')
else:
    print('COULD NOT FIND')
