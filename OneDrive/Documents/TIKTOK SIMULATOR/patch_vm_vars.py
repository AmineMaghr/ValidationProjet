import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Try a regex approach
pattern = r'local BuySlot = env\.BuySlot\s+print\(\"\[VideoManager\] Layout loaded\"\)'

new_code = """local BuySlot = env.BuySlot
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
    end)"""

if re.search(pattern, text):
    text = re.sub(pattern, new_code, text)
    with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
        f.write(text)
    print("Patched variables successfully!")
else:
    print("Could not find the insertion point.")
