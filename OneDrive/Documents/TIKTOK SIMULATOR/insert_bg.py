with open('src/client/PhoneModules/ShopApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

bg_code = """  local makeCorner = env.makeCorner

  -- Enhanced Background
  local bgFrame = Instance.new("Frame")
  bgFrame.Name = "ShopBackground"
  bgFrame.Size = UDim2.new(1, 0, 1, 0)
  bgFrame.BackgroundColor3 = Color3.new(1, 1, 1)
  bgFrame.BorderSizePixel = 0
  bgFrame.ZIndex = -1
  bgFrame.Parent = content
  
  local bgGradient = Instance.new("UIGradient")
  bgGradient.Color = ColorSequence.new({
      ColorSequenceKeypoint.new(0, Color3.fromRGB(55, 25, 85)), 
      ColorSequenceKeypoint.new(0.5, Color3.fromRGB(25, 18, 45)),
      ColorSequenceKeypoint.new(1, Color3.fromRGB(10, 8, 15))  
  })
  bgGradient.Rotation = 60
  bgGradient.Parent = bgFrame
  
  makeCorner(bgFrame, 12)
"""

text = text.replace('  local makeCorner = env.makeCorner', bg_code)

with open('src/client/PhoneModules/ShopApp.lua', 'w', encoding='utf-8') as f:
    f.write(text)
print("done")
