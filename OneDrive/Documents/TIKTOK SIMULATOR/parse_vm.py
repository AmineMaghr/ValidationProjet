import re
with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'lfResetGame' in line or 'Like Farm' in line or 'totalHealth' in line:
        print(f'{i+1}: {line.strip()}')
