import os

with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

print("Found insert?", 'table.insert(typeCards, {card=card, stroke=stroke, check=check, ct=ct})' in text)
print("Found click?", 'hit.MouseButton1Click:Connect(function()' in text)
