import re

with open('src/server/GameManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Update passive view generation (SlotIncome)
text = re.sub(
    r'local viewsEarned = amount \* 10',
    r'''local seoMultiplier = 2 ^ (data.upgrades and data.upgrades.SEOAlgorithm or 0)
		local viewsEarned = math.floor(amount * 10 * seoMultiplier)''',
    text
)

# 2. Update active view generation (PostContent)
text = re.sub(
    r'local viewsEarned = math\.floor\(baseViews \* multiplier\)',
    r'''local qualityMultiplier = 2 ^ (data.upgrades and data.upgrades.ContentQuality or 0)
	local viewsEarned = math.floor(baseViews * multiplier * qualityMultiplier)''',
    text
)

# 3. Update all instances of follower calculation (reduce 1000 divisor -> 25)
# EngagementRate upgrade reduces it down to a minimum of 2 views = 1 follower
text = re.sub(
    r'local newFollowers = math\.floor\(data\.totalViews / 1000\)',
    r'''local engRate = data.upgrades and data.upgrades.EngagementRate or 0
		local conversionRate = math.max(2, 25 - (engRate * 2))
		local newFollowers = math.floor(data.totalViews / conversionRate)''',
    text
)

with open('src/server/GameManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("GameManager math updated successfully")
