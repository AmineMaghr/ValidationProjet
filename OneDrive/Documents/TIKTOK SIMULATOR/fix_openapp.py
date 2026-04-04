import sys

with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    lines = f.readlines()

new_lines = []
in_openapp = False
for i, line in enumerate(lines):
    if "local function openApp(appName)" in line:
        new_lines.append(line)
        new_lines.extend([
            "    local screen = appScreens[appName]\n",
            "    if not screen then return end\n",
            "\n",
            "    if appName == \"CloutApp\" then\n",
            "        local leaderstats = player:FindFirstChild(\"leaderstats\")\n",
            "        if leaderstats then\n",
            "            local c = leaderstats:FindFirstChild(\"Coins\")\n",
            "            local v = leaderstats:FindFirstChild(\"Views\")\n",
            "            local f = leaderstats:FindFirstChild(\"Followers\")\n",
            "            local vCLbl = screen:FindFirstChild(\"vCoinsLabel\", true)\n",
            "            local vVLbl = screen:FindFirstChild(\"vViewsLabel\", true)\n",
            "            local vFLbl = screen:FindFirstChild(\"vFollLabel\", true)\n",
            "            if c and vCLbl then vCLbl.Text = fmt(c.Value) end\n",
            "            if v and vVLbl then vVLbl.Text = fmt(v.Value) end\n",
            "            if f and vFLbl then vFLbl.Text = fmt(f.Value) end\n",
            "        end\n",
            "    end\n\n"
        ])
        in_openapp = True
        continue
    
    if in_openapp:
        if "if not screen then return end" in line or "local screen" in line:
            pass # remove original
        elif "if currentAppName" in line:
            in_openapp = False # back to normal
            new_lines.append(line)
        continue
        
    new_lines.append(line)

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.writelines(new_lines)

