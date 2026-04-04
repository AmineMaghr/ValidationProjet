import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_gen = r'''    local function generateSponsors\(followers\)\s+local offers = \{\}\s+-- Balanced: cost is 15-35%.*?return offers\s+end'''

new_gen = '''    local function generateSponsors(followers)
        local offers = {}
        local maxFollowers = 1000000
        local cappedFollowers = math.max(10, math.min(followers, maxFollowers))

        -- Both cost around 15-20% of followers
        local costA = math.floor(cappedFollowers * (0.15 + math.random() * 0.05))
        local costB = math.floor(cappedFollowers * (0.15 + math.random() * 0.05))

        -- Offer A: "Burst Deal" - High immediate lump sum, low and short passive
        local lumpA = math.floor(cappedFollowers * 0.4 + math.random() * cappedFollowers * 0.1)
        local incomePerSecA = math.floor(cappedFollowers * 0.0005)
        local durA = 2 + math.random(0, 1) -- 2 to 3 minutes

        -- Offer B: "Long Term Contract" - No immediate sum, big massive passive over time
        local incomePerSecB = math.floor(cappedFollowers * 0.0025 + math.random() * cappedFollowers * 0.001)
        local durB = 5 + math.random(1, 2) -- 6 to 7 minutes

        local b1 = BRANDS[math.random(1, #BRANDS)]
        local b2 = BRANDS[math.random(1, #BRANDS)]
        while b2 == b1 do b2 = BRANDS[math.random(1, #BRANDS)] end
        local e1 = EMOJIS[math.random(1, #EMOJIS)]
        local e2 = EMOJIS[math.random(1, #EMOJIS)]

        if incomePerSecA < 1 then incomePerSecA = 1 end
        if incomePerSecB < 1 then incomePerSecB = 1 end
        if lumpA < 50 then lumpA = 50 end

        offers[1] = {
            name = b1 .. " Quick Campaign",
            emoji = e1,
            followerCost = costA,
            coinsPerSec = incomePerSecA,
            duration = durA,
            lumpSum = lumpA,
        }
        offers[2] = {
            name = b2 .. " Exclusive Contract",
            emoji = e2,
            followerCost = costB,
            coinsPerSec = incomePerSecB,
            duration = durB,
            lumpSum = nil,
        }
        return offers
    end'''

new_text = re.sub(old_gen, new_gen, text, flags=re.DOTALL)
print("Changed?", text != new_text)

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(new_text)
