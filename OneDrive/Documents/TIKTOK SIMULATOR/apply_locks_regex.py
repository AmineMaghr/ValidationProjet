import os
import re

with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Overlay definition
text = re.sub(
    r'([ \t]*)table\.insert\(typeCards, \{card=card, stroke=stroke, check=check, ct=ct\}\)',
    r'''\1local lockOverlay = Instance.new("Frame", card)
\1lockOverlay.Name = "LockOverlay"
\1lockOverlay.Size = UDim2.new(1, 0, 1, 0)
\1lockOverlay.BackgroundColor3 = Color3.fromRGB(15, 15, 20)
\1lockOverlay.BackgroundTransparency = 0.5
\1lockOverlay.BorderSizePixel = 0
\1lockOverlay.ZIndex = 5
\1lockOverlay.Visible = false
\1makeCorner(lockOverlay, 12)

\1local lockIcon = makeLabel(lockOverlay, "🔒", 24, C.white, fontBold, UDim2.new(1, 0, 1, 0), UDim2.new(0, 0, 0, 0))
\1lockIcon.TextXAlignment = Enum.TextXAlignment.Center
\1lockIcon.TextYAlignment = Enum.TextYAlignment.Center
\1lockIcon.ZIndex = 6

\1table.insert(typeCards, {card=card, stroke=stroke, check=check, ct=ct, lockOverlay=lockOverlay})''',
    text
)

# 2. Click prevention
text = re.sub(
    r'([ \t]*)hit\.MouseButton1Click:Connect\(function\(\)\n([ \t]*)activeChoice = ct',
    r'''\1hit.MouseButton1Click:Connect(function()
\2local ls = player:FindFirstChild("leaderstats")
\2local cFolls = ls and ls:FindFirstChild("Followers") and ls.Followers.Value or 0
\2if cFolls < (ct.req or 0) then return end
\2activeChoice = ct''',
    text
)

# 3. Update locks dynamically
text = re.sub(
    r'([ \t]*)FollowersUpdated\.OnClientEvent:Connect\(function\(totalFollowers\)\n([ \t]*)vFoll\.Text = fmt\(totalFollowers\)\n([ \t]*)(end\))',
    r'''\1local function updateLocks(folls)
\1\tfor _, t in ipairs(typeCards) do
\1\t\tif t.lockOverlay then
\1\t\t\tt.lockOverlay.Visible = folls < (t.ct.req or 0)
\1\t\tend
\1\tend
\1end

\1if leaderstats and leaderstats:FindFirstChild("Followers") then
\1\tupdateLocks(leaderstats.Followers.Value)
\1end

\1FollowersUpdated.OnClientEvent:Connect(function(totalFollowers)
\2vFoll.Text = fmt(totalFollowers)
\1\tupdateLocks(totalFollowers)
\3\4''',
    text
)

with open('src/client/PhoneUI.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print("Regex patch applied")
