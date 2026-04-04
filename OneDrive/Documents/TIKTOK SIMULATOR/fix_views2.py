import sys

file_path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('updateViewsUI(80)', 'updateViewsUI(60)')
content = content.replace('floatText("+80"', 'floatText("+60"')

content = content.replace('updateViewsUI(-50)', 'updateViewsUI(-40)')
content = content.replace('floatText("-50"', 'floatText("-40"')

content = content.replace('updateViewsUI(150)', 'updateViewsUI(100)')
content = content.replace('floatText("+150"', 'floatText("+100"')

content = content.replace('updateViewsUI(-60)', 'updateViewsUI(-50)')
content = content.replace('floatText("-60"', 'floatText("-50"')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Updated view payouts successfully!')
