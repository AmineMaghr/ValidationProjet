with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()
    
with open("block2.txt", "r", encoding="utf-8") as f:
    block = f.read()

import re
# Insert right before `local function createHeader(parent, appName, appColor)`
new_text = text.replace("local function createHeader(parent, appName, appColor)", block + "\n\nlocal function createHeader(parent, appName, appColor)")

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(new_text)
