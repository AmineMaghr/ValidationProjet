with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "local dLab = makeLabel(card, ct.hint" in line:
        # replace with slightly smaller font, more width padding (-60), and taller height for wrapping (30)
        lines[i] = '\t\t\tlocal dLab = makeLabel(card, ct.hint, 9, C.muted, fontRegular, UDim2.new(1, -60, 0, 30), UDim2.new(0, 52, 0, 36))\n'
    elif "dLab.TextXAlignment = Enum.TextXAlignment.Left" in line and i > 0 and "makeLabel(card, ct.hint" in lines[i-1]:
        lines[i] = '\t\t\tdLab.TextXAlignment = Enum.TextXAlignment.Left\n\t\t\tdLab.TextYAlignment = Enum.TextYAlignment.Top\n\t\t\tdLab.TextWrapped = true\n'

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.writelines(lines)
    
print("Updated label definition.")