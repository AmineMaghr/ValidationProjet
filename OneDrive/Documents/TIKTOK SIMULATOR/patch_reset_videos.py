import re

with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_block = r'''        for i = 1, 3 do\n            if sData\[tostring\(i\)\] then\n                vmSlotsData\[i\] = sData\[tostring\(i\)\]\n            elseif sData\[i\] then\n                vmSlotsData\[i\] = sData\[i\]\n            end\n        end'''

new_block = '''        for i = 1, 3 do
            if sData[tostring(i)] then
                vmSlotsData[i] = sData[tostring(i)]
            elseif sData[i] then
                vmSlotsData[i] = sData[i]
            end
            
            -- RESET ACTIVE VIDEOS
            if vmSlotsData[i] and vmSlotsData[i].state == "filled" then
                vmSlotsData[i].state = "empty"
                vmSlotsData[i].timeLeft = 0
                vmSlotsData[i].cpm = 0
                vmSlotsData[i].rank = ""
                pcall(function() Remotes:WaitForChild("SyncSlotData"):FireServer(i, vmSlotsData[i]) end)
            end
        end'''

text = re.sub(old_block, new_block, text)

with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Patched PCOS to reset videos!")
