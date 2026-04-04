import re

path = 'src/client/PhoneUI.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

replacement = """    local uiPadding = Instance.new("UIPadding")
    uiPadding.PaddingTop = UDim.new(0, 20)
    uiPadding.PaddingLeft = UDim.new(0, 20)
    uiPadding.PaddingRight = UDim.new(0, 20)
    uiPadding.Parent = content

    local listLayout = Instance.new("UIListLayout")
    listLayout.Parent = content
    listLayout.Padding = UDim.new(0, 15)"""

text = text.replace('    local listLayout = Instance.new("UIListLayout")\n    listLayout.Parent = content\n    listLayout.Padding = UDim.new(0, 5)', replacement)

text = text.replace('local lTitle = makeLabel(row, upg.name .. " (Lvl " .. lvl .. ")", 14, C.text, FONT_MAIN, UDim2.new(0.7,0,1,0), UDim2.new(0,0,0,0))',
                    'local lTitle = makeLabel(row, upg.name .. " (Lvl " .. lvl .. ")", 16, C.white, FONT_MAIN, UDim2.new(0.65,0,1,0), UDim2.new(0,0,0,0))\n      lTitle.TextWrapped = true')

# Adjust the button size and position to have more space
text = text.replace('btn.Size = UDim2.new(0.3, -5, 1, 0)\n      btn.Position = UDim2.new(0.7, 5, 0, 0)',
                    'btn.Size = UDim2.new(0.35, 0, 1, 0)\n      btn.Position = UDim2.new(0.65, 0, 0, 0)')

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
