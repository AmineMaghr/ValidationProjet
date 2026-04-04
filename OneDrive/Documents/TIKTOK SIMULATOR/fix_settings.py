with open("src/client/PhoneModules/SettingsApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

# We need to remove everything after end) for toggleBtn
# SettingsApp should just be:
# local SettingsApp = {}
# function SettingsApp.create(...) ... end) end return SettingsApp

import re
lines = text.split("\n")
new_lines = []
for line in lines:
    new_lines.append(line)
    if "end)" in line and "toggleBtn.MouseButton1Click:Connect" in text:
        # Let's just find the index
        pass
