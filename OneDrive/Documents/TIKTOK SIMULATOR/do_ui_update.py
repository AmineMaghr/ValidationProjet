import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# 1. Update Card Name
text = text.replace('local card = Instance.new("Frame")\n                  card.Size = UDim2.new(0, 280, 0, 350)', 
                    'local card = Instance.new("Frame")\n                  card.Name = "SlotCard_" .. i\n                  card.Size = UDim2.new(0, 280, 0, 350)')

# 2. Add player picture and name
old_title_code = '''local ssTitle = Instance.new("TextLabel")
          ssTitle.Size = UDim2.new(1, 0, 0, 40)
          ssTitle.Position = UDim2.new(0, 0, 0, 20)
          ssTitle.BackgroundTransparency = 1
          ssTitle.Text = "Your Channel"'''

new_title_code = '''local pName = game.Players.LocalPlayer.Name
          local ssTitle = Instance.new("TextLabel")
          ssTitle.Size = UDim2.new(1, 0, 0, 40)
          ssTitle.Position = UDim2.new(0, 30, 0, 20)
          ssTitle.BackgroundTransparency = 1
          ssTitle.Text = pName .. "'s Channel"
          
          local pfp = Instance.new("ImageLabel")
          pfp.Size = UDim2.new(0, 40, 0, 40)
          pfp.Position = UDim2.new(0.5, -((ssTitle.TextBounds.X/2) + 120), 0, 20) -- Roughly place it
          pfp.BackgroundTransparency = 1
          -- Will manually adjust layout
          pfp.Image = game.Players:GetUserThumbnailAsync(game.Players.LocalPlayer.UserId, Enum.ThumbnailType.HeadShot, Enum.ThumbnailSize.Size420x420)
          pfp.ZIndex = 61
          pfp.Parent = slotSelectionScreen
          
          -- Fix the centering with a helper
          task.spawn(function()
             task.wait(0.1)
             local tw = ssTitle.TextBounds.X
             pfp.Position = UDim2.new(0.5, -(tw/2) - 50, 0, 20)
          end)
'''
text = text.replace(old_title_code, new_title_code)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print('Applied UI phase 1')
