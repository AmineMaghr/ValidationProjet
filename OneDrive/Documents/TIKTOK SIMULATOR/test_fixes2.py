import sys

with open(r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua', 'r', encoding='utf-8') as f:
    lines = f.readlines()

def print_around(pattern):
    for i, l in enumerate(lines):
        if pattern in l:
            print("FOUND " + pattern + f" AT LINE {i}")
            for j in range(max(0, i-2), min(len(lines), i+3)):
                print(f"{j}: {repr(lines[j])}")
            return
            
print_around('local passiveTimer = 0')
print_around('local gameRunning = true')
print_around('slotSelectionScreen.Visible = false')

