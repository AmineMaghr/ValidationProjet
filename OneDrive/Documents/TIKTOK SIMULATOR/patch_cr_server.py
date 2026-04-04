import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Add remote
text = text.replace(
    'BuySlot = "RemoteFunction",\n        GetUnlockedSlots = "RemoteFunction",\n        DevAction = "RemoteEvent"',
    'BuySlot = "RemoteFunction",\n        GetUnlockedSlots = "RemoteFunction",\n        DevAction = "RemoteEvent",\n        CoinRushStarted = "RemoteEvent"'
)

# Add player state
text = text.replace(
    'unlockedSlots = 1,',
    'unlockedSlots = 1,\n            coinRushEnds = 0,'
)

# Insert the event listener 
old_dev = """    remotes.DevAction.OnServerEvent:Connect(function(player, action)"""
new_dev = """    remotes.CoinRushStarted.OnServerEvent:Connect(function(player, duration)
        local data = playerData[player.UserId]
        if data then
            data.coinRushEnds = os.time() + (tonumber(duration) or 5)
        end
    end)
    
    remotes.DevAction.OnServerEvent:Connect(function(player, action)"""
text = text.replace(old_dev, new_dev)

# Update the PostContent to apply double coins
old_post = """        local coinsEarned = math.floor(viewsEarned * 0.05)
        data.coins = data.coins + coinsEarned"""
        
new_post = """        local coinsEarned = math.floor(viewsEarned * 0.05)
        
        if os.time() < (data.coinRushEnds or 0) then
            coinsEarned = coinsEarned * 2
        end
        
        data.coins = data.coins + coinsEarned"""
text = text.replace(old_post, new_post)

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Patched GameManager")
