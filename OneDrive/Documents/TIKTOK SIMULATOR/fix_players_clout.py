with open("src/client/PhoneModules/CloutApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace('Players:GetUserThumbnailAsync', 'game:GetService("Players"):GetUserThumbnailAsync')

with open("src/client/PhoneModules/CloutApp.lua", "w", encoding="utf-8") as f:
    f.write(text)
