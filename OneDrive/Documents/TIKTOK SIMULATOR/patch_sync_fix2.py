import re

with open('src/client/PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

new_text = re.sub(
    r'(totalIncome = totalIncome \+ slot\.cpm)',
    r'\g<1>\n                      if math.floor(slot.timeLeft) % 20 == 0 then\n                          pcall(function() Remotes:WaitForChild("SyncSlotData"):FireServer(i, slot) end)\n                      end',
    text
)

with open('src/client/PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(new_text)

print("PCOS.client.lua updated with periodic live syncing.")
