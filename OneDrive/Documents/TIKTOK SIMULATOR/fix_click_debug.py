import re
import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

text = re.sub(
    r'btn\.MouseButton1Click:Connect\(function\(\)\s*slotSelectionScreen\.Visible = false\s*vmMaster\.Visible = true\s*resetGameUI\(\)\s*end\)',
    '''btn.MouseButton1Click:Connect(function()
                            print("[Game] Clicked Empty Slot!")
                            slotSelectionScreen.Visible = false
                            vmMaster.Visible = true
                            if resetGameUI then resetGameUI() end
                        end)''',
    text
)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print("Injected click debug via regex!")
