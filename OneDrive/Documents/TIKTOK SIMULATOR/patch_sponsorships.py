import re

with open('src/client/PhoneModules/SponsorshipsApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Make sure we don't duplicate multiple times
if 'updateElements' not in text:
    old_evt = r'''  Remotes:WaitForChild\("FollowersUpdated"\)\.OnClientEvent:Connect\(function\(newF\).*?end\)'''
    new_evt = '''  local updateElements = {}

  local evtConn = nil
  evtConn = Remotes:WaitForChild("FollowersUpdated").OnClientEvent:Connect(function(newF) 
    if not scroll.Parent then 
        if evtConn then evtConn:Disconnect() end
        return 
    end
    followers = newF or 0
    followerLabel.Text = "?? " .. fmt(followers) .. " Followers"
    
    for _, el in ipairs(updateElements) do
        if el.type == "lock" then
            el.inst.Text = fmt(followers) .. " / 500"
            if followers >= 500 then
                 if evtConn then evtConn:Disconnect() end
                 content:ClearAllChildren()
                 SponsorshipsApp.create(content, env)
                 break
            end
        elseif el.type == "btn" then
            if followers >= el.cost and el.btn.Text:find("Need") then
                if evtConn then evtConn:Disconnect() end
                content:ClearAllChildren()
                SponsorshipsApp.create(content, env)
                break
            end
        end
    end
  end)'''
    text = re.sub(old_evt, new_evt, text, flags=re.DOTALL)

    old_lock = r'''    local lockLabel = makeLabel\(lockCard.*?lockProg\.TextXAlignment = Enum\.TextXAlignment\.Center'''
    new_lock = '''    local lockLabel = makeLabel(lockCard, "?? Unlocks at 500 Followers", 15, C.muted, Enum.Font.GothamBold, UDim2.new(1, 0, 0, 24), UDim2.new(0, 0, 0, 10))   
    lockLabel.TextXAlignment = Enum.TextXAlignment.Center
    local lockProg = makeLabel(lockCard, fmt(followers) .. " / 500", 12, Color3.fromRGB(100, 100, 100), Enum.Font.Gotham, UDim2.new(1, 0, 0, 16), UDim2.new(0, 0, 0, 34))
    lockProg.TextXAlignment = Enum.TextXAlignment.Center
    
    table.insert(updateElements, {type = "lock", inst = lockProg})'''
    text = re.sub(old_lock, new_lock, text, flags=re.DOTALL)

    old_btn = r'''        btn\.Text = "Need " \.\. fmt\(offer\.followerCost\) \.\. " followers"\n        btn\.BackgroundColor3 = Color3\.fromRGB\(50, 50, 50\)\n        btn\.TextColor3 = C\.muted\n        btn\.Active = false'''
    new_btn = '''        btn.Text = "Need " .. fmt(offer.followerCost) .. " followers"
        btn.BackgroundColor3 = Color3.fromRGB(50, 50, 50)
        btn.TextColor3 = C.muted
        btn.Active = false
        table.insert(updateElements, {type = "btn", btn = btn, cost = offer.followerCost or 0})'''
    text = re.sub(old_btn, new_btn, text, flags=re.DOTALL)

    with open('src/client/PhoneModules/SponsorshipsApp.lua', 'w', encoding='utf-8') as f:
        f.write(text)
    print("PATCH_SPONSORS: Applied")
else:
    print("PATCH_SPONSORS: Already applied")

