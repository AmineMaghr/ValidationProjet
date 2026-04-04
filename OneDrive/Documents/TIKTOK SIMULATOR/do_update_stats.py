import re

path = 'src/client/PhoneUI.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

replacement = """  local TotalViewsUpdated = Remotes:WaitForChild("TotalViewsUpdated")
  local FollowersUpdated = Remotes:WaitForChild("FollowersUpdated")

  local function fmt(n)
    n = math.floor(n or 0)
    if n >= 1e12 then return string.format("%.1fT", n/1e12)
    elseif n >= 1e9 then return string.format("%.1fB", n/1e9)
    elseif n >= 1e6 then return string.format("%.1fM", n/1e6)
    elseif n >= 1e3 then return string.format("%.1fK", n/1e3)
    else return tostring(n) end
  end

  local leaderstats = player:WaitForChild("leaderstats")
  if leaderstats then
    local c = leaderstats:WaitForChild("Coins", 2)
    if c then vCoins.Text = fmt(c.Value) end
    local v = leaderstats:WaitForChild("Views", 2)
    if v then vViews.Text = fmt(v.Value) end
    local f = leaderstats:WaitForChild("Followers", 2)
    if f then vFoll.Text = fmt(f.Value) end
  end

  FollowersUpdated.OnClientEvent:Connect(function(totalFollowers)
    vFoll.Text = fmt(totalFollowers)
  end)
"""

pattern = r'  local TotalViewsUpdated = Remotes:WaitForChild\("TotalViewsUpdated"\)[\s\S]*?else return tostring\(n\) end\n  end'

text = re.sub(pattern, replacement, text)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
