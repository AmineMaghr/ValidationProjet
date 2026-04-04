import re
with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_code = """local SlotIncome = Remotes:WaitForChild("SlotIncome")
local passiveTimer = 0
local textEffectTimer = 0
local incomeAccumulator = 0"""

new_code = """local SlotIncome = Remotes:WaitForChild("SlotIncome")
local GetUpgrades = Remotes:WaitForChild("GetUpgrades")
local passiveTimer = 0
local textEffectTimer = 0
local incomeAccumulator = 0

local editMultiplier = 1
local seoMultiplier = 1

task.spawn(function()
    while true do
        pcall(function()
            local upg = GetUpgrades:InvokeServer()
            if upg then
                editMultiplier = 1 + ((upg.EditSpeed or 0) * 0.05)
                seoMultiplier = 2 ^ (upg.SEOAlgorithm or 0)
            end
        end)
        task.wait(5)
    end
end)"""

if old_code in text:
    text = text.replace(old_code, new_code)
    
    # Now replace the floating "+ X coins" text
    old_float = 'notif.Text = "+"..tostring(slot.cpm).." coins"'
    new_float = 'notif.Text = "+"..tostring(math.floor(slot.cpm * editMultiplier)).." coins"'
    text = text.replace(old_float, new_float)
    
    with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
        f.write(text)
    print("Patched PCOS successfully!")
else:
    print("Old code not found")
