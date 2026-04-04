import os

with open('src/client/PhoneUI.client.lua', 'r', encoding='utf-8') as f:
    text = f.read()

fmt_func = """local function fmt(n)
    n = math.floor(n or 0)
    if n >= 1e12 then return string.format("%.1fT", n/1e12)
    elseif n >= 1e9 then return string.format("%.1fB", n/1e9)
    elseif n >= 1e6 then return string.format("%.1fM", n/1e6)
    elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
    else return tostring(n) end
end
"""

# remove from original location
text = text.replace(fmt_func, "")

# insert above openApp
text = text.replace('local function openApp(appName)', fmt_func + '\nlocal function openApp(appName)')

with open('src/client/PhoneUI.client.lua', 'w', encoding='utf-8') as f:
    f.write(text)
