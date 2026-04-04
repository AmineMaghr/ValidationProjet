import re
import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# 1. Update the slots array to 6
old_slots = '''local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0}
}'''

new_slots = '''local vmSlotsData = {
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0},
    {state = "empty", rank = "", timeLeft = 0, cpm = 0}
}'''
text = text.replace(old_slots, new_slots)

# 2. Fix the header to make avatar be BEFORE name and properly aligned.
# Currently looks like:
'''          local pName = game.Players.LocalPlayer.Name
          local ssTitle = Instance.new("TextLabel")
...
          ssTitle.Parent = slotSelectionScreen
'''

def replace_header(t):
    pattern = r'local pName = game\.Players\.LocalPlayer\.Name[\s\S]*?ssTitle\.Parent = slotSelectionScreen'
    
    new_header = '''local pName = game.Players.LocalPlayer.Name
          local headerContainer = Instance.new("Frame")
          headerContainer.Size = UDim2.new(1, 0, 0, 40)
          headerContainer.Position = UDim2.new(0, 0, 0, 15)
          headerContainer.BackgroundTransparency = 1
          headerContainer.Parent = slotSelectionScreen
          
          local hLayout = Instance.new("UIListLayout")
          hLayout.FillDirection = Enum.FillDirection.Horizontal
          hLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          hLayout.VerticalAlignment = Enum.VerticalAlignment.Center
          hLayout.Padding = UDim.new(0, 10)
          hLayout.SortOrder = Enum.SortOrder.LayoutOrder
          hLayout.Parent = headerContainer

          local pfp = Instance.new("ImageLabel")
          pfp.Size = UDim2.new(0, 40, 0, 40)
          pfp.BackgroundTransparency = 1
          pfp.LayoutOrder = 1
          local userId = game.Players.LocalPlayer.UserId
          pfp.Image = game.Players:GetUserThumbnailAsync(userId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size420x420)
          pfp.ZIndex = 61
          Instance.new("UICorner", pfp).CornerRadius = UDim.new(1, 0)
          pfp.Parent = headerContainer

          local ssTitle = Instance.new("TextLabel")
          ssTitle.Size = UDim2.new(0, 0, 0, 40)
          ssTitle.AutomaticSize = Enum.AutomaticSize.X
          ssTitle.BackgroundTransparency = 1
          ssTitle.Text = pName .. "'s Channel"
          ssTitle.TextColor3 = Color3.fromRGB(255, 255, 255)
          ssTitle.Font = Enum.Font.GothamBold
          ssTitle.TextSize = 36
          ssTitle.LayoutOrder = 2
          ssTitle.ZIndex = 61
          ssTitle.Parent = headerContainer'''
    
    return re.sub(pattern, new_header, t)

text = replace_header(text)

# 3. Increase all for i=1,3 do loops to for i=1,6 do
text = re.sub(r'for i\s*=\s*1\s*,\s*3 do', 'for i = 1, 6 do', text)

# 4. Fix layout for slots to wrap using UIGridLayout
# old slotsLayout: 
'''          local slotsLayout = Instance.new("UIListLayout")
          slotsLayout.FillDirection = Enum.FillDirection.Horizontal        
          slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder
          slotsLayout.Padding = UDim.new(0, 20)
          slotsLayout.Parent = slotsContainer'''

new_layout = '''          local slotsLayout = Instance.new("UIGridLayout")
          slotsLayout.CellSize = UDim2.new(0, 250, 0, 310)
          slotsLayout.CellPadding = UDim2.new(0, 20, 0, 20)
          slotsLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
          slotsLayout.SortOrder = Enum.SortOrder.LayoutOrder
          slotsLayout.Parent = slotsContainer'''

text = re.sub(r'local slotsLayout = Instance\.new\("UIListLayout"\)[\s\S]*?slotsLayout\.Parent = slotsContainer', new_layout, text)


# 5. Make the slot size itself match that new cellsize inside the loop
#    card.Size = UDim2.new(0, 280, 0, 350) -> card.Size = UDim2.new(1, 0, 1, 0)
text = re.sub(
    r'card\.Size = UDim2\.new\(\d+,\s*\d+,\s*\d+,\s*\d+\)',
    r'card.Size = UDim2.new(1, 0, 1, 0)',
    text
)

# 6. Revert the "coins/sec" math to properly represent direct values (because totalIncome adds slot.cpm directly per sec)
text = re.sub(
    r'local cps = math\.floor\(\(slotData\.cpm / 60\).*?\n\s*coins\.Text = tostring\(cps\):gsub\("%\.0\$", ""\) \.\. " coins/sec"',
    r'coins.Text = slotData.cpm .. " coins/sec"',
    text
)

# Replace endgame strings as well
# local cps = math.floor((rtCpm / 60) * 10 + 0.5) / 10\n            rEarn.Text = "Slot earnings: " .. tostring(cps):gsub("%.0$","") .. " coins/sec"
text = re.sub(
    r'local cps = math\.floor\(\(rtCpm / 60\).*?\n\s*rEarn\.Text = "Slot earnings: " \.\. tostring\(cps\):gsub\("%\.0\$",""\) \.\. " coins/sec"',
    r'rEarn.Text = "Slot earnings: " .. rtCpm .. " coins/sec"',
    text
)

text = re.sub(
    r'if earn then local cps = math\.floor\(\(rtCpm / 60\).*?earn\.Text = tostring\(cps\):gsub\("%\.0\$",""\) \.\. " coins/sec" end',
    r'if earn then earn.Text = rtCpm .. " coins/sec" end',
    text
)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print("Done updates!")
