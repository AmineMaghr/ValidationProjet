with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    for line in f:
        if 'totalHealth' in line:
            print(line.strip())
