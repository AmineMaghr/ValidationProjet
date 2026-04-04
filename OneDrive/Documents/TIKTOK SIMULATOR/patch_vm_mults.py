import re
with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# First, add GetUpgrades
old_req = """    local TweenService = env.TweenService
    local HasPC = env.HasPC
    local updateViewsUI = env.updateViewsUI
    local BuyPC = env.BuyPC
    local SlotIncome = env.SlotIncome
    local BuySlot = env.BuySlot

    print("[VideoManager] Layout loaded")"""

new_req = """    local TweenService = env.TweenService
    local HasPC = env.HasPC
    local updateViewsUI = env.updateViewsUI
    local BuyPC = env.BuyPC
    local SlotIncome = env.SlotIncome
    local BuySlot = env.BuySlot
    local GetUpgrades = Remotes:WaitForChild("GetUpgrades")

    print("[VideoManager] Layout loaded")
    
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
    end)
"""
text = text.replace(old_req, new_req)

text = text.replace(
    'coins.Text = (slotData.cpm * 10) .. " views/sec"', 
    'coins.Text = math.floor(math.floor(slotData.cpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec"'
)

text = text.replace(
    'rEarn.Text = "Slot earnings: " .. (rtCpm * 10) .. " views/sec"',
    'rEarn.Text = "Slot earnings: " .. math.floor(math.floor(rtCpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec"'
)

text = text.replace(
    'if earn then earn.Text = (rtCpm * 10) .. " views/sec" end',
    'if earn then earn.Text = math.floor(math.floor(rtCpm * editMultiplier) * 10 * seoMultiplier) .. " views/sec" end'
)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Patched VideoManager successfully!")
