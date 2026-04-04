import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

text = re.sub(
    r'rEarn\.Text = "Slot earnings: " \.\. rtCpm \.\. " coins/min"',
    r'local cps = math.floor((rtCpm / 60) * 10 + 0.5) / 10\n            rEarn.Text = "Slot earnings: " .. tostring(cps):gsub("%.0$","") .. " coins/sec"',
    text
)

text = re.sub(
    r'if earn then earn\.Text = rtCpm \.\. " coins/min" end',
    r'if earn then local cps = math.floor((rtCpm / 60) * 10 + 0.5) / 10; earn.Text = tostring(cps):gsub("%.0$","") .. " coins/sec" end',
    text
)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("EndGame texts updated")
