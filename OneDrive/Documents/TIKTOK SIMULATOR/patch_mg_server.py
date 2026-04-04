import re

with open("src/server/GameManager.lua", "r", encoding="utf-8") as f:
    text = f.read()

# Change parameter
text = text.replace(
    'remotes.RequestHack.OnServerEvent:Connect(function(player, targetId)',
    'remotes.RequestHack.OnServerEvent:Connect(function(player, targetId, mgSuccess)'
)

# old RNG block
old_rng = '''        local successChance = 0.35 + ((data.upgradeLevels and data.upgradeLevels.EngagementRate or 0) * 0.05) -- Cap around 85% with upgrades 
        if math.random() > successChance then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Hack failed & lost " .. cost .. " coins!") end   
            return
        end'''

# new block using the minigame result
new_rng = '''        -- Minigame Success overrides RNG
        if mgSuccess == false then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Brute force failed & lost " .. cost .. " coins!") end
            return
        end
        -- Extra server validation: if they pass the minigame, they get the steal
        if mgSuccess == nil then
            -- Fallback if old client sends it
            local successChance = 0.35 + ((data.upgradeLevels and data.upgradeLevels.EngagementRate or 0) * 0.05)
            if math.random() > successChance then
                if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Hack failed & lost " .. cost .. " coins!") end
                return
            end
        end'''

text = text.replace(old_rng, new_rng)
print("PATCH SERVER: ", new_rng in text)

with open("src/server/GameManager.lua", "w", encoding="utf-8") as f:
    f.write(text)

