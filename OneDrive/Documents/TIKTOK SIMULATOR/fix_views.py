import sys

file_path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('updateViewsUI(15)', 'updateViewsUI(80)')
content = content.replace('floatText("+15"', 'floatText("+80"')

content = content.replace('updateViewsUI(-10)', 'updateViewsUI(-50)')
content = content.replace('floatText("-10"', 'floatText("-50"')

content = content.replace('updateViewsUI(40)', 'updateViewsUI(150)')
content = content.replace('floatText("+40"', 'floatText("+150"')

content = content.replace('updateViewsUI(-20)', 'updateViewsUI(-60)')
content = content.replace('floatText("-20"', 'floatText("-60"')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Updated view payouts successfully!')
