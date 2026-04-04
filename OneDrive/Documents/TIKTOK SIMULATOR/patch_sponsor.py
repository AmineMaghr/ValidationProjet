import re
with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace(
    'player.leaderstats.Coins.Value = data.coins\n                            end',
    'player.leaderstats.Coins.Value = data.coins\n                            end\n                            if remotes:FindFirstChild("CoinsUpdated") then\n                                remotes.CoinsUpdated:FireClient(player, data.coins)\n                            end'
)

print("Changed:", "remotes.CoinsUpdated:FireClient(player, data.coins)" in text)
with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
