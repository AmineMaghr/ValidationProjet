import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

pattern = r'''                        local playIcon = Instance.new\("ImageLabel"\)
                        playIcon.Size = UDim2.new\(0, 40, 0, 40\)
                        playIcon.Position = UDim2.new\(0.5, -20, 0.5, -20\)   
                        playIcon.BackgroundTransparency = 1
                        playIcon.Image = "rbxassetid://16405230910" -- Standard UI play icon
                        playIcon.ImageTransparency = 0.3
                        playIcon.ZIndex = 64
                        playIcon.Parent = thumb

                        local playIcon = Instance.new\("ImageLabel"\)'''

replacement = '''                        local playIcon = Instance.new("ImageLabel")'''

text = re.sub(pattern, replacement, text)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("Duplicates cleaned!")
