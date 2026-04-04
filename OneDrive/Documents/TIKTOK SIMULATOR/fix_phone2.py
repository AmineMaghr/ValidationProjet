with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

import re
text = text.replace("local CloutApp = require(script.Parent.PhoneModules.CloutApp)\nlocal SettingsApp = require(script.Parent.PhoneModules.SettingsApp)", "local CloutApp = require(script.Parent.PhoneModules.CloutApp)\nlocal SettingsApp = require(script.Parent.PhoneModules.SettingsApp)\nlocal ShopApp = require(script.Parent.PhoneModules.ShopApp)")

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)
