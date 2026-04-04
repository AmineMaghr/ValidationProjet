import re

gm_path = 'src/server/GameManager.lua'
with open(gm_path, 'r', encoding='utf-8') as f:
    text = f.read()

# Add Quality views to baseViews
if 'local qualityBonus = ' not in text:
    text = re.sub(
        r'(local baseViews = BASE_VIEWS\[data\.contentType\] or 100)',
        r'\1\n        local qualityBonus = data.upgrades and (data.upgrades.ContentQuality * 0.3) or 0\n        baseViews = baseViews + qualityBonus',
        text
    )

# Add EditSpeed coins
if 'local editBonus = ' not in text:
    text = re.sub(
        r'(coins\s*=\s*data\.coins + )\d+',
        r'data.coins + math.floor(1 + (data.upgrades and (data.upgrades.EditSpeed * 0.05) or 0))',
        text
    )

# SEOAlgorithm Viral Reduction
if 'local viralThreshold = ' not in text:
    text = re.sub(
        r'(math\.random\(\)\s*<\s*)0\.01',
        r'\1(0.01 + (data.upgrades and (data.upgrades.SEOAlgorithm * 0.015) or 0))',
        text
    )

# PostFrequency Idle Loop
if 'PostFrequency' in text and 'RunService.Heartbeat' not in text:
    # Need to add a loop for idle views
    addon = """
task.spawn(function()
    while true do
        task.wait(1)
        for _, player in pairs(Players:GetPlayers()) do
            local data = playerData[player.UserId]
            if data and data.upgrades then
                local freq = data.upgrades.PostFrequency or 0
                if freq > 0 then
                    data.totalViews = data.totalViews + freq
                    data.followers = math.floor(data.totalViews / 1000)
                    remotes.TotalViewsUpdated:FireClient(player, data.totalViews)
                    remotes.FollowersUpdated:FireClient(player, data.followers)
                end
            end
        end
    end
end)
"""
    if 'while true do' not in text:
        text = text.replace('return remotesFolder\nend', 'return remotesFolder\nend\n' + addon)

with open(gm_path, 'w', encoding='utf-8') as f:
    f.write(text)
