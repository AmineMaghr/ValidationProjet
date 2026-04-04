import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace all occurrences of the if newFollowers > data.followers then logic
old_block = """	local newFollowers = math.floor(data.totalViews / 1000)
	if newFollowers > data.followers then
		data.followers = newFollowers
		if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
			player.leaderstats.Followers.Value = data.followers
		end
		if remotes:FindFirstChild("FollowersUpdated") then
			remotes.FollowersUpdated:FireClient(player, data.followers)
		end
	end"""

new_block = """	local newFollowers = math.floor(data.totalViews / 1000)
	if newFollowers > data.followers then
		data.followers = newFollowers
		if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
			player.leaderstats.Followers.Value = data.followers
		end
		if remotes:FindFirstChild("FollowersUpdated") then
			remotes.FollowersUpdated:FireClient(player, data.followers)
		end
		print(player.Name .. " followers: " .. data.followers)
	end"""

if old_block in text:
    text = text.replace(old_block, new_block)
    print("Replaced standard blocks")
else:
    print("Standard block not found")

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)
