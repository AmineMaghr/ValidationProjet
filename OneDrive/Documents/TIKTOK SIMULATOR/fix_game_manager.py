import re

with open("src/server/GameManager.lua", "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace("BuyUpgrade", "PurchaseUpgrade")

new_buy_logic = """    remotes.PurchaseUpgrade.OnServerInvoke = function(player, upgradeName)
        local data = playerData[player.UserId]
        if not data then return false end
        if not data.upgradeLevels then data.upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 } end
        local level = data.upgradeLevels[upgradeName] or 0
        local cost = math.floor(50 * (1.35 ^ level))
        if data.coins >= cost then
            data.coins = data.coins - cost
            data.upgradeLevels[upgradeName] = level + 1
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                player.leaderstats.Coins.Value = data.coins
            end
            if remotes:FindFirstChild("CoinsUpdated") then
                remotes.CoinsUpdated:FireClient(player, data.coins)
            end
            print(player.Name .. " upgraded " .. upgradeName .. " to level " .. data.upgradeLevels[upgradeName])
            return true, data.upgradeLevels[upgradeName], data.coins
        end
        return false
    end"""

text = re.sub(r'remotes\.PurchaseUpgrade\.OnServerInvoke = function\(player, upgradeName\).*?return false\n\s*end', new_buy_logic, text, flags=re.DOTALL)

text = text.replace("data.upgrades", "data.upgradeLevels")
text = text.replace("upgrades =", "upgradeLevels =")

with open("src/server/GameManager.lua", "w", encoding="utf-8") as f:
    f.write(text)
