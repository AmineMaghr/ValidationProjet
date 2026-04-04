with open("src/client/PhoneModules/SettingsApp.lua", "r", encoding="utf-8") as f:
    lines = f.readlines()

new_settings = lines[:33] + ["\nreturn SettingsApp\n"]
with open("src/client/PhoneModules/SettingsApp.lua", "w", encoding="utf-8") as f:
    f.writelines(new_settings)

block = lines[34:106]
with open("block2.txt", "w", encoding="utf-8") as f:
    f.writelines(block)
