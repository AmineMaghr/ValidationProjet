import os

with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Overlay definition
old_insert = """\t\tlocal hit = Instance.new("TextButton", card)
		hit.Size = UDim2.new(1, 0, 1, 0)
		hit.BackgroundTransparency = 1
		hit.Text = ""

		table.insert(typeCards, {card=card, stroke=stroke, check=check, ct=ct})"""

new_insert = """\t\tlocal hit = Instance.new("TextButton", card)
		hit.Size = UDim2.new(1, 0, 1, 0)
		hit.BackgroundTransparency = 1
		hit.Text = ""

		local lockOverlay = Instance.new("Frame", card)
		lockOverlay.Name = "LockOverlay"
		lockOverlay.Size = UDim2.new(1, 0, 1, 0)
		lockOverlay.BackgroundColor3 = Color3.fromRGB(15, 15, 20)
		lockOverlay.BackgroundTransparency = 0.5
		lockOverlay.BorderSizePixel = 0
		lockOverlay.ZIndex = 5
		lockOverlay.Visible = false
		makeCorner(lockOverlay, 12)
		
		local lockIcon = makeLabel(lockOverlay, "🔒", 24, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
		lockIcon.TextXAlignment = Enum.TextXAlignment.Center
		lockIcon.TextYAlignment = Enum.TextYAlignment.Center
		lockIcon.ZIndex = 6

		table.insert(typeCards, {card=card, stroke=stroke, check=check, ct=ct, lockOverlay=lockOverlay})"""

if old_insert in text:
    text = text.replace(old_insert, new_insert)
else:
    print("WARNING: Could not find old_insert!")


# 2. Click prevention
old_click = """\t\thit.MouseButton1Click:Connect(function()
			activeChoice = ct"""

new_click = """\t\thit.MouseButton1Click:Connect(function()
			local ls = player:FindFirstChild("leaderstats")
			local cFolls = ls and ls:FindFirstChild("Followers") and ls.Followers.Value or 0
			if cFolls < (ct.req or 0) then return end
			
			activeChoice = ct"""

if old_click in text:
    text = text.replace(old_click, new_click)
else:
    print("WARNING: Could not find old_click!")


# 3. Update locks dynamically
old_events = """		local f = leaderstats:WaitForChild("Followers", 2)
		if f then vFoll.Text = fmt(f.Value) end
	end

	FollowersUpdated.OnClientEvent:Connect(function(totalFollowers)
		vFoll.Text = fmt(totalFollowers)
	end)"""

new_events = """		local f = leaderstats:WaitForChild("Followers", 2)
		if f then vFoll.Text = fmt(f.Value) end
	end

	local function updateLocks(folls)
		for _, t in ipairs(typeCards) do
			if t.lockOverlay then
				t.lockOverlay.Visible = folls < (t.ct.req or 0)
			end
		end
	end
	
	if leaderstats and leaderstats:FindFirstChild("Followers") then
		updateLocks(leaderstats.Followers.Value)
	end

	FollowersUpdated.OnClientEvent:Connect(function(totalFollowers)
		vFoll.Text = fmt(totalFollowers)
		updateLocks(totalFollowers)
	end)"""

if old_events in text:
    text = text.replace(old_events, new_events)
else:
    print("WARNING: Could not find old_events!")


with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)
print("Patch applied to PhoneUI.client.lua")
