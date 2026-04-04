import re

with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_spawn = r'''task\.spawn\(function\(\)\n    local unlockedCount = GetUnlockedSlots:InvokeServer\(\) or 1\n    for i = 1, 3 do\n        if i <= unlockedCount then\n            if vmSlotsData\[i\]\.state == "locked" then\n                vmSlotsData\[i\]\.state = "empty"\n            end\n        end\n    end\nend\)'''

new_spawn = '''task.spawn(function()
    -- Ask server for actual slot info, which already accounts for unlocked slots AND timers
    local GetSlotData = Remotes:WaitForChild("GetSlotData")
    local ok, sData = pcall(function() return GetSlotData:InvokeServer() end)
    if ok and sData then
        for i = 1, 3 do
            if sData[tostring(i)] then
                vmSlotsData[i] = sData[tostring(i)]
            elseif sData[i] then
                vmSlotsData[i] = sData[i]
            end
        end
    else
        -- Fallback
        local unlockedCount = GetUnlockedSlots:InvokeServer() or 1
        for i = 1, 3 do
            if i <= unlockedCount then
                if vmSlotsData[i].state == "locked" then
                    vmSlotsData[i].state = "empty"
                end
            end
        end
    end
end)'''

text = re.sub(old_spawn, new_spawn, text, flags=re.DOTALL)

with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

