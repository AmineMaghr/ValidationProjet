import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_unlocked = r'''    remotes\.GetUnlockedSlots\.OnServerInvoke = function\(player\)
        local data = playerData\[player\.UserId\]
        return data and data\.unlockedSlots or 1
    end'''
new_unlocked = '''    remotes.GetUnlockedSlots.OnServerInvoke = function(player)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]
        return data and data.unlockedSlots or 1
    end'''
text = re.sub(old_unlocked, new_unlocked, text, flags=re.DOTALL)

old_slotdata = r'''    remotes\.GetSlotData\.OnServerInvoke = function\(player\)
        local data = playerData\[player\.UserId\]
        if not data then return nil end'''
new_slotdata = '''    remotes.GetSlotData.OnServerInvoke = function(player)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]'''
text = re.sub(old_slotdata, new_slotdata, text, flags=re.DOTALL)

old_buyslot = r'''    remotes\.BuySlot\.OnServerInvoke = function\(player, slotNumber\)
        local data = playerData\[player\.UserId\]
        if not data then return false end'''
new_buyslot = '''    remotes.BuySlot.OnServerInvoke = function(player, slotNumber)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]'''
text = re.sub(old_buyslot, new_buyslot, text, flags=re.DOTALL)

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
