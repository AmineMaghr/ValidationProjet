import re

gm_path = 'src/server/GameManager.lua'
with open(gm_path, 'r', encoding='utf-8') as f:
    gm_text = f.read()

# Add upgrades to playerData
if 'upgrades =' not in gm_text:
    gm_text = re.sub(
        r'pcLevel = 1\s*}', 
        r'pcLevel = 1,\n            upgrades = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 }\n        }', 
        gm_text
    )

# Add BuyUpgrade RemoteFunction
if 'BuyUpgrade =' not in gm_text:
    gm_text = re.sub(
        r'MinigameReward = "RemoteEvent".*?\n\s*}',
        r'MinigameReward = "RemoteEvent",\n        BuyUpgrade = "RemoteFunction",\n        GetUpgrades = "RemoteFunction"\n    }',
        gm_text,
        flags=re.DOTALL
    )

# Add the handling functions
addon = """
    remotes.BuyUpgrade.OnServerInvoke = function(player, upgradeName)
        local data = playerData[player.UserId]
        if not data then return false end
        if not data.upgrades then data.upgrades = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 } end
        local level = data.upgrades[upgradeName] or 0
        local cost = math.floor(50 * (1.35 ^ level))
        if data.coins >= cost then
            data.coins = data.coins - cost
            data.upgrades[upgradeName] = level + 1
            remotes.CoinsUpdated:FireClient(player, data.coins)
            return true
        end
        return false
    end

    remotes.GetUpgrades.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        if not data.upgrades then data.upgrades = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 } end
        return data.upgrades
    end
"""
if 'BuyUpgrade.OnServerInvoke' not in gm_text:
    gm_text = gm_text.replace('return remotesFolder', addon + '\n    return remotesFolder')

with open(gm_path, 'w', encoding='utf-8') as f:
    f.write(gm_text)
