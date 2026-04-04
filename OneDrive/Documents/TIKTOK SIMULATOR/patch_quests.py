with open('src/client/PhoneModules/QuestsApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace(
    'if c:IsA("Frame") or c:IsA("TextButton") or c:IsA("TextLabel") then     \\nc:Destroy() end',
    'if not c:IsA("UIListLayout") and not c:IsA("UIPadding") then c:Destroy() end'
).replace(
    'if c:IsA("Frame") or c:IsA("TextButton") or c:IsA("TextLabel") then c:Destroy() end',
    'if not c:IsA("UIListLayout") and not c:IsA("UIPadding") then c:Destroy() end'
)

with open('src/client/PhoneModules/QuestsApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print("done")
