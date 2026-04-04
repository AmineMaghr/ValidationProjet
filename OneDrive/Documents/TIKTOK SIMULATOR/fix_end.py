import sys

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

import re
# remove the end before return CloutApp
text = re.sub(r'print\("\[CloutApp\] Content loaded"\)\nend\n\nreturn CloutApp', 'print("[CloutApp] Content loaded")\n\nreturn CloutApp', text)

with open('src/client/PhoneModules/CloutApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
