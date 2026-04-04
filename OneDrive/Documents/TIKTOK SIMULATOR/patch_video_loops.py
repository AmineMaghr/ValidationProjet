import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Fix Minigame 1 loops
text = re.sub(
    r'(if not minigameStarted or not gameRunning then) end\s*(if gameTime > 0 then)',
    r'\g<1> continue end\n                          \g<2>',
    text,
    count=1
)

text = re.sub(
    r'(if not minigameStarted or not gameRunning then\s+task\.wait\(0\.5\)\s+)end',
    r'\g<1>continue\n                          end',
    text,
    count=1
)

# Fix Minigame 2 loops
text = re.sub(
    r'(if not lfGameRunning then) end\s*(if lfGameTime > 0 and lfGameRunning then)',
    r'\g<1> continue end\n              \g<2>',
    text,
    count=1
)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Loops patched")
