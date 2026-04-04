with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

import re
text = text.replace("getIsDarkMode()", "isDarkMode")

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)
