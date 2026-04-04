import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Remove the incorrectly scoped variable
text = text.replace('            local minigameStarted = false\n', '')

# Insert it at the correct top level inside Video Manager
target = '          local slotsLayout = Instance.new("UIListLayout")'
replacement = '          local minigameStarted = false\n          local slotsLayout = Instance.new("UIListLayout")'
if target in text:
    text = text.replace(target, replacement)
    print('MOVED minigameStarted')
else:
    print('COULD NOT FIND target')

# Remove the nil check workaround that was protecting the global
old_btn = '''                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          if minigameStarted ~= nil then
                              minigameStarted = true
                          end
                      end)'''
                      
new_btn = '''                          slotSelectionScreen.Visible = false
                          vmMaster.Visible = true
                          minigameStarted = true
                      end)'''

if old_btn in text:
    text = text.replace(old_btn, new_btn)
    print('FIXED btn click')
else:
    print('COULD NOT FIND old_btn')

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("done")
