import re
with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('{name = "PC Shop Channel",   icon = "??"},', '{name = "PC Shop Channel",   icon = "🛒"},')

with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print('Fixed emoji encoding.')
