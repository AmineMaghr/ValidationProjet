with open("src/client/PhoneModules/CloutApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

import re
text = text.replace("local Remotes = ReplicatedStorage:WaitForChild(\"Remotes\")", "-- Uses env.Remotes")

with open("src/client/PhoneModules/CloutApp.lua", "w", encoding="utf-8") as f:
    f.write(text)
