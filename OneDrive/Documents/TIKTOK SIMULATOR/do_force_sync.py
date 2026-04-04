import sys
with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace(
    'local vCoins = makeLabel(s2, "0", 18, C.amber, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))',
    'local vCoins = makeLabel(s2, "0", 18, C.amber, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))\n    vCoins.Name = "vCoinsLabel"'
)

text = text.replace(
    'local vViews = makeLabel(s1, "0", 18, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))',
    'local vViews = makeLabel(s1, "0", 18, C.white, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))\n    vViews.Name = "vViewsLabel"'
)

text = text.replace(
    'local vFoll = makeLabel(s3, "0", 18, C.green, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))',
    'local vFoll = makeLabel(s3, "0", 18, C.green, fontBold, UDim2.new(1, 0, 0, 20), UDim2.new(0, 0, 0, 14))\n    vFoll.Name = "vFollLabel"'
)

# Also let's update openApp to force-sync those
open_app_orig = '''local function openApp(appName)
    local screen = appScreens[appName]
    if not screen then return end

    if currentAppName and currentAppName ~= appName then
        local current = appScreens[currentAppName]
        if current then
            current.Visible = false
            current.Position = UDim2.new(1, 0, 0, STATUS_HEIGHT)
        end
    end'''

open_app_new = '''local function openApp(appName)
    local screen = appScreens[appName]
    if not screen then return end

    if appName == "CloutApp" then
        local leaderstats = player:FindFirstChild("leaderstats")
        if leaderstats then
            local c = leaderstats:FindFirstChild("Coins")
            local v = leaderstats:FindFirstChild("Views")
            local f = leaderstats:FindFirstChild("Followers")
            
            local vCLbl = screen:FindFirstChild("vCoinsLabel", true)
            local vVLbl = screen:FindFirstChild("vViewsLabel", true)
            local vFLbl = screen:FindFirstChild("vFollLabel", true)
            
            if c and vCLbl then vCLbl.Text = fmt(c.Value) end
            if v and vVLbl then vVLbl.Text = fmt(v.Value) end
            if f and vFLbl then vFLbl.Text = fmt(f.Value) end
        end
    end

    if currentAppName and currentAppName ~= appName then
        local current = appScreens[currentAppName]
        if current then
            current.Visible = false
            current.Position = UDim2.new(1, 0, 0, STATUS_HEIGHT)
        end
    end'''

text = text.replace(open_app_orig, open_app_new)

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)
