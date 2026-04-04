import re

path = 'src/server/GameManager.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# Fix BuyUpgrade
buy_upgrade_old = """        if data.coins >= cost then
            data.coins = data.coins - cost
            data.upgrades[upgradeName] = level + 1
            remotes.CoinsUpdated:FireClient(player, data.coins)
            return true"""
buy_upgrade_new = """        if data.coins >= cost then
            data.coins = data.coins - cost
            data.upgrades[upgradeName] = level + 1
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                player.leaderstats.Coins.Value = data.coins
            end
            if remotes:FindFirstChild("CoinsUpdated") then
                remotes.CoinsUpdated:FireClient(player, data.coins)
            end
            return true"""
text = text.replace(buy_upgrade_old, buy_upgrade_new)

# Fix PostContent
post_content_old = """        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Views") then
            player.leaderstats.Views.Value = data.totalViews
        end

        remotes.ViewsUpdated:FireClient(player, data.views, data.totalViews)
        if remotes:FindFirstChild("TotalViewsUpdated") then
            remotes.TotalViewsUpdated:FireClient(player, data.totalViews)
        end"""

post_content_new = """        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Views") then
            player.leaderstats.Views.Value = data.totalViews
        end

        remotes.ViewsUpdated:FireClient(player, data.views, data.totalViews)
        if remotes:FindFirstChild("TotalViewsUpdated") then
            remotes.TotalViewsUpdated:FireClient(player, data.totalViews)
        end"""
text = text.replace(post_content_old, post_content_new)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print('Fixed GameManager')
