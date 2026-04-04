import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Balance Prestige Requirements
text = re.sub(
    r'math\.floor\(10000 \* \(1\.5 \^ ',
    r'math.floor(25000 * (1.8 ^ ',
    text
)

# 2. Balance Prestige Multiplier
text = re.sub(
    r'1 \+ \(data\.prestigeCount or 0\) \* 0\.2',
    r'1 + (data.prestigeCount or 0) * 0.1',
    text
)

# 3. Hack Logic
old_hack = r'''    remotes\.RequestHack\.OnServerEvent:Connect\(function\(player, targetId\)\s+local data = playerData\[player\.UserId\]\s+if not data then return end\s+local remotes = ensureRemotes\(\)'''

new_hack = '''    remotes.RequestHack.OnServerEvent:Connect(function(player, targetId)
        local data = playerData[player.UserId]
        if not data then return end
        local remotes = ensureRemotes()'''

text = text.replace(old_hack, new_hack)

hack_body_old = r'''        -- Steal 10% of target's coins \(floor\)
        local stolen = math\.floor\(\(targetData\.coins or 0\) \* 0\.10\)
        if stolen <= 0 then
            if remotes:FindFirstChild\(\"HackResult\"\) then remotes\.HackResult:FireClient\(player, false, \"Target has nothing to steal\"\) end
            return
        end

        targetData\.coins = math\.max\(0, \(targetData\.coins or 0\) - stolen\)
        data\.coins = \(data\.coins or 0\) \+ stolen'''

hack_body_new = '''        -- Hack logic: Cost 15% of your coins (min 1000), 35% success chance
        local cost = math.max(1000, math.floor((data.coins or 0) * 0.15))
        if (data.coins or 0) < cost then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Need " .. cost .. " coins to hack!") end
            return
        end

        data.coins = data.coins - cost

        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end

        local successChance = 0.35 + ((data.upgradeLevels and data.upgradeLevels.EngagementRate or 0) * 0.05) -- Cap around 85% with upgrades
        if math.random() > successChance then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Hack failed & lost " .. cost .. " coins!") end
            return
        end

        -- Steal 10-25% of target's coins (floor)
        local stealPct = 0.10 + (math.random() * 0.15)
        local stolen = math.floor((targetData.coins or 0) * stealPct)
        if stolen <= 0 then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Target has no coins, but you still paid " .. cost .. "!") end
            return
        end

        targetData.coins = math.max(0, (targetData.coins or 0) - stolen)
        data.coins = (data.coins or 0) + stolen'''

text = re.sub(hack_body_old, hack_body_new, text, flags=re.DOTALL)

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("GameManager Balance Applied")
