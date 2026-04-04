import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = re.sub(
    r'lfTotalHealth = math\.clamp\(lfTotalHealth \+ 3, 0, 100\)',
    r'lfTotalHealth = math.clamp(lfTotalHealth + 6, 0, 100)',
    text,
    count=1
)

text = re.sub(
    r'lfTotalHealth = math\.clamp\(lfTotalHealth - 20, 0, 100\)',
    r'lfTotalHealth = math.clamp(lfTotalHealth - 15, 0, 100)',
    text,
    count=1
)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Difficulty reduced.")
