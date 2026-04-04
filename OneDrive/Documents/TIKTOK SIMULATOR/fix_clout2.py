with open("src/client/PhoneModules/CloutApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

import re
text = text.replace('ReplicatedStorage:FindFirstChild("Remotes") and ReplicatedStorage.Remotes:FindFirstChild("SetContentType")', 'Remotes:FindFirstChild("SetContentType")')
text = text.replace('ReplicatedStorage:FindFirstChild("Remotes") and ReplicatedStorage.Remotes:FindFirstChild("PostContent")', 'Remotes:FindFirstChild("PostContent")')

with open("src/client/PhoneModules/CloutApp.lua", "w", encoding="utf-8") as f:
    f.write(text)
