import re
import os

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

text = text.replace('coinRushEnds = 0,', 'coinRushReady = false,')

# Find CoinRushStarted handler
old_handler = r'''remotes.CoinRushStarted.OnServerEvent:Connect(function(player, duration)
        local data = playerData[player.UserId]
        if data then
            data.coinRushEnds = os.time() + (tonumber(duration) or 5)
        end
    end)'''

new_handler = r'''remotes.CoinRushStarted.OnServerEvent:Connect(function(player)
        local data = playerData[player.UserId]
        if data then
            data.coinRushReady = true
        end
    end)'''

if old_handler in text:
    text = text.replace(old_handler, new_handler)
    print("Replaced CoinRushStarted handler successfully")
else:
    print("Warning: Could not find CoinRushStarted handler")


old_math = r'''if os.time() < (data.coinRushEnds or 0) then
            coinsEarned = coinsEarned * 2
        end'''

new_math = r'''if data.coinRushReady then
            coinsEarned = coinsEarned * 2
            data.coinRushReady = false
        end'''

if old_math in text:
    text = text.replace(old_math, new_math)
    print("Replaced math logic successfully")
else:
    print("Warning: Could not find math logic")

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
