import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

pattern = r'''([ \t]*)local newFollowers = math\.floor\(data\.totalViews / 1000\)\n([ \t]*)if newFollowers > data\.followers then\n([ \t]*)data\.followers = newFollowers\n([ \t]*)if player:FindFirstChild\("leaderstats"\) and player\.leaderstats:FindFirstChild\("Followers"\) then\n([ \t]*)player\.leaderstats\.Followers\.Value = data\.followers\n([ \t]*)end\n([ \t]*)if remotes:FindFirstChild\("FollowersUpdated"\) then\n([ \t]*)remotes\.FollowersUpdated:FireClient\(player, data\.followers\)\n([ \t]*)end\n([ \t]*)end'''

replacement = r'''\1local newFollowers = math.floor(data.totalViews / 1000)
\2if newFollowers > data.followers then
\3data.followers = newFollowers
\4if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
\5player.leaderstats.Followers.Value = data.followers
\6end
\7if remotes:FindFirstChild("FollowersUpdated") then
\8remotes.FollowersUpdated:FireClient(player, data.followers)
\9end
\9print(player.Name .. " followers: " .. data.followers)
\10end'''

text, count = re.subn(pattern, replacement, text)
print(f"Replaced {count} instances.")

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
