import re

with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()


# Fix the app name in icons table
text = re.sub(
    r'\{name = "PC Settings",\s*icon = ".*?"\},',
    r'{name = "PC Shop Channel",   icon = "??"},',
    text
)

# Fix the app name in open condition
text = text.replace(
    'elseif appData.name == "PC Settings" then',
    'elseif appData.name == "PC Shop Channel" then'
)

# Fix the title header text
text = text.replace(
    'header.Text = "? PC Upgrades"',
    'header.Text = "?? PC Shop Upgrades"'
)

# Fix the client-side nil addition error
text = text.replace(
    'local newLevel = pcUpgrades[upg.id] + 1',
    'local newLevel = (pcUpgrades[upg.id] or 0) + 1'
)

with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Patched PC Shop Channel and UI crashes.")
