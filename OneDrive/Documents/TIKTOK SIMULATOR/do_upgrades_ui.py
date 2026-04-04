import re

pui = 'src/client/PhoneUI.client.lua'
with open(pui, 'r', encoding='utf-8') as f:
    text = f.read()

# Replace ShopApp "Coming soon" with real ShopApp code
buy_upgrade_code = """
  elseif app.name == "ShopApp" then
    local upgrades = remotes:WaitForChild("GetUpgrades"):InvokeServer()
    if not upgrades then upgrades = { ContentQuality=0, EditSpeed=0, PostFrequency=0, EngagementRate=0, SEOAlgorithm=0 } end

    local listLayout = Instance.new("UIListLayout")
    listLayout.Parent = content
    listLayout.Padding = UDim.new(0, 5)

    local function getCost(level)
      return math.floor(50 * (1.35 ^ level))
    end

    local upgs = {
      { id = "ContentQuality", name = "Content Quality (+0.3 views/click)" },
      { id = "EditSpeed", name = "Edit Speed (+0.05 coins/click)" },
      { id = "PostFrequency", name = "Post Frequency (+1 idle views/sec)" },
      { id = "EngagementRate", name = "Engagement (+2s combo)" },
      { id = "SEOAlgorithm", name = "SEO Algorithm (+1.5% viral)" }
    }

    for i, upg in ipairs(upgs) do
      local lvl = upgrades[upg.id] or 0
      local row = Instance.new("Frame")
      row.Size = UDim2.new(1, 0, 0, 40)
      row.BackgroundTransparency = 1
      row.Parent = content

      local lTitle = makeLabel(row, upg.name .. " (Lvl " .. lvl .. ")", 14, C.text, FONT_MAIN, UDim2.new(0.7,0,1,0), UDim2.new(0,0,0,0))
      lTitle.TextXAlignment = Enum.TextXAlignment.Left

      local btn = makeButton(row, "buyBtn", "Cost: " .. getCost(lvl), C.accent, UDim2.new(0.3,-5,1,0), UDim2.new(0.7,5,0,0))
      
      btn.MouseButton1Click:Connect(function()
        local success = remotes:WaitForChild("BuyUpgrade"):InvokeServer(upg.id)
        if success then
          upgrades[upg.id] = lvl + 1
          lvl = upgrades[upg.id]
          lTitle.Text = upg.name .. " (Lvl " .. lvl .. ")"
          btn.Text = "Cost: " .. getCost(lvl)
        end
      end)
    end
"""

if 'elseif app.name == "ShopApp"' not in text:
    text = text.replace('elseif app.name == "Settings" then\n    createSettingsContent(content)\n  else',
                        'elseif app.name == "Settings" then\n    createSettingsContent(content)\n' + buy_upgrade_code + '\n  else')

with open(pui, 'w', encoding='utf-8') as f:
    f.write(text)
