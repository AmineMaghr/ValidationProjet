import sys
import re

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Fix the Spawner Loop
sp_old = '''                while contentFrame.Parent do
                    if not minigameStarted then task.wait(0.5); continue end
                    if not gameRunning then break end
                    local spawnDelay = math.random(12, 18) / 10'''

sp_new = '''                while contentFrame.Parent do
                    if not minigameStarted or not gameRunning then 
                        task.wait(0.5)
                        continue 
                    end
                    local spawnDelay = math.random(12, 18) / 10'''

if sp_old in text:
    text = text.replace(sp_old, sp_new)
    print("Fixed Spawner loops")

# 2. Fix the Timer Loop
t_old = '''                while contentFrame.Parent do
                    task.wait(1)
                    if not minigameStarted then continue end
                    if not gameRunning then break end
                    if gameTime > 0 then'''

t_new = '''                while contentFrame.Parent do
                    task.wait(1)
                    if not minigameStarted or not gameRunning then continue end
                    if gameTime > 0 then'''

if t_old in text:
    text = text.replace(t_old, t_new)
    print("Fixed Timer loops")

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
