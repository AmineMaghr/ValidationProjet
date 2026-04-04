with open("src/client/PhoneModules/CloutApp.lua", "r", encoding="utf-8") as f:
    text = f.read()
import re
text = re.sub(r"return CloutApp", "end\n\nreturn CloutApp", text)
with open("src/client/PhoneModules/CloutApp.lua", "w", encoding="utf-8") as f:
    f.write(text)
