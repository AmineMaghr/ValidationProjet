import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('lfBigLikeBtn.Text = "??"', 'lfBigLikeBtn.Text = "??"')
text = text.replace('lfTotalHealth = math.clamp(lfTotalHealth + 6, 0, 100)', 'lfTotalHealth = math.clamp(lfTotalHealth + 3, 0, 100)')
text = text.replace('lfTotalHealth = math.clamp(lfTotalHealth - 15, 0, 100)', 'lfTotalHealth = math.clamp(lfTotalHealth - 20, 0, 100)')

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

