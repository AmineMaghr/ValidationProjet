local Players = game:GetService("Players")
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local RunService = game:GetService("RunService")
local DataStoreService = nil
local playerStore = nil
local _devStore = {}

-- Try to initialize DataStoreService; if unavailable we'll gracefully fall back.
local ok_ds, ds = pcall(function() return game:GetService("DataStoreService") end)
if ok_ds and ds then
    DataStoreService = ds
    local ok_store, store = pcall(function() return DataStoreService:GetDataStore("GoingViralPlayerData") end)
    if ok_store and store then
        playerStore = store
    end
end

local AUTO_SAVE_INTERVAL = 60

local MILESTONES = {
    -- Post count milestones
    { type = "posts",  count = 5,    reward = 100,   label = "First 5 Posts!" },
    { type = "posts",  count = 15,   reward = 300,   label = "15 Posts!" },
    { type = "posts",  count = 50,   reward = 1000,  label = "50 Posts!" },
    { type = "posts",  count = 100,  reward = 3000,  label = "100 Posts!" },
    { type = "posts",  count = 250,  reward = 8000,  label = "250 Posts!" },
    { type = "posts",  count = 500,  reward = 20000, label = "500 Posts!" },
    { type = "posts",  count = 1000, reward = 50000, label = "1000 Posts!" },
    -- Follower milestones
    { type = "followers", count = 100,    reward = 200,   label = "100 Followers!" },
    { type = "followers", count = 1000,   reward = 1000,  label = "1K Followers!" },
    { type = "followers", count = 5000,   reward = 5000,  label = "5K Followers!" },
    { type = "followers", count = 10000,  reward = 15000, label = "10K Followers!" },
    { type = "followers", count = 50000,  reward = 50000, label = "50K Followers!" },
    { type = "followers", count = 100000, reward = 150000, label = "100K Followers!" },
}

local function checkMilestones(player, data, remotes)
    if not data.achievedMilestones then data.achievedMilestones = {} end
    for i, m in ipairs(MILESTONES) do
        if not data.achievedMilestones[i] then
            local current = (m.type == "posts" and data.postCount or data.followers) or 0
            if current >= m.count then
                data.achievedMilestones[i] = true
                data.coins = data.coins + m.reward
                if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                    player.leaderstats.Coins.Value = data.coins
                end
                local milestoneEvent = remotes:FindFirstChild("MilestoneHit")
                if milestoneEvent then
                    milestoneEvent:FireClient(player, m.label, m.reward)
                end
            end
        end
    end
end

local function dsGet(key)
    if playerStore then
        local ok, res = pcall(function() return playerStore:GetAsync(key) end)
        if ok then
            print("[DataStore] GetAsync OK for key=" .. tostring(key))
            return res
        end
        warn("[DataStore] GetAsync failed for key=" .. tostring(key) .. ": " .. tostring(res))
    end
    return _devStore[key]
end

local function dsSet(key, value)
    _devStore[key] = value
    if playerStore then
        local ok, err = pcall(function() playerStore:SetAsync(key, value) end)
        if ok then
            print("[DataStore] SetAsync OK for key=" .. tostring(key))
        else
            warn("[DataStore] SetAsync failed for key=" .. tostring(key) .. ": " .. tostring(err))
        end
    end
end

local GameManager = {}
local playerData = {}

local function savePlayerData(userId)
    local data = playerData[userId]
    if not data then return end
    local key = "pv_" .. tostring(userId)
    dsSet(key, data)
end

local BASE_VIEWS = {
    ["Short Videos"]  = 100,
    ["Vlogs"]         = 280,
    ["Podcasts"]      = 500,
    ["Livestreams"]   = 180,
    ["Movies"]        = 1200,
    ["Stadium Tours"] = 3000,
}

local function ensureRemotes()
    local remotesFolder = ReplicatedStorage:FindFirstChild("Remotes")
    if not remotesFolder then
        remotesFolder = Instance.new("Folder")
        remotesFolder.Name = "Remotes"
        remotesFolder.Parent = ReplicatedStorage
    end

    local requiredRemotes = {
        PostContent = "RemoteEvent",
        SetContentType = "RemoteEvent",
            RequestHack = "RemoteEvent",
            HackResult = "RemoteEvent",
            GetBestRival = "RemoteFunction",
            GetPlayerList = "RemoteFunction",
            ExportPlayerData = "RemoteFunction",
            ImportPlayerData = "RemoteEvent",
        CoinsUpdated = "RemoteEvent",
        ViewsUpdated = "RemoteEvent",
        TotalViewsUpdated = "RemoteEvent",
        FollowersUpdated = "RemoteEvent",
        OpenPC = "RemoteEvent",
        HasPC = "RemoteFunction",
        BuyPC = "RemoteFunction",
        SlotIncome = "RemoteEvent",
        MinigameReward = "RemoteEvent",
        PurchaseUpgrade = "RemoteFunction",
        GetUpgrades = "RemoteFunction",
        BuySlot = "RemoteFunction",
        GetUnlockedSlots = "RemoteFunction",
        GetSlotData = "RemoteFunction",
        SyncSlotData = "RemoteEvent",
        DevAction = "RemoteEvent",
        MilestoneHit = "RemoteEvent",
        Prestige = "RemoteEvent",
        PrestigeResult = "RemoteEvent",
        GetDailyBonus = "RemoteFunction",
        GetPrestigeInfo = "RemoteFunction",
        GetStats = "RemoteFunction",
        GetHomeData = "RemoteFunction",
        ClaimHome = "RemoteFunction",
        UpgradeHome = "RemoteFunction",
        GetSponsorships = "RemoteFunction",
        AcceptSponsor = "RemoteFunction",
        GetLockedContentType = "RemoteFunction",
        GetLockedSlotData = "RemoteFunction",
        UpgradePC = "RemoteFunction",
        GetPCInfo = "RemoteFunction",
        GetPCUpgrades = "RemoteFunction",
        PurchasePCUpgrade = "RemoteFunction",
        GetChallenges = "RemoteFunction",
        ClaimChallenge = "RemoteFunction",
        GetDailyQuests = "RemoteFunction",
        ClaimDailyQuest = "RemoteFunction",
        GetLeaderboard = "RemoteFunction",
        GoingViral = "RemoteEvent",
        ClaimGoingViral = "RemoteFunction",
    }

    for name, class in pairs(requiredRemotes) do
        local remote = remotesFolder:FindFirstChild(name)
        if not remote then
            remote = Instance.new(class)
            remote.Name = name
            remote.Parent = remotesFolder
        end
    end
    
    
    return remotesFolder
end

-- Ensure a top-level remotes reference so background code can use it
local remotes = ensureRemotes()

task.spawn(function()
    local remotes = ensureRemotes()
    local passiveTick = 0
    while true do
        task.wait(1)
        passiveTick = passiveTick + 1
        if passiveTick >= 3 then
            passiveTick = 0
            for _, player in pairs(Players:GetPlayers()) do
                local data = playerData[player.UserId]
                if data and data.upgradeLevels then
                    local freq = data.upgradeLevels.PostFrequency or 0
                    if freq > 0 then
                        local idleViews = freq
                        data.totalViews = (data.totalViews or 0) + idleViews
                        local engRate = data.upgradeLevels.EngagementRate or 0
                        local conversionRate = math.max(2, 25 - (engRate * 2))
                        local oldFollowers = data.followers or 0
                        data.followers = math.floor(data.totalViews / conversionRate)
                        if remotes:FindFirstChild("TotalViewsUpdated") then remotes.TotalViewsUpdated:FireClient(player, data.totalViews) end
                        if data.followers > oldFollowers then
                            if remotes:FindFirstChild("FollowersUpdated") then remotes.FollowersUpdated:FireClient(player, data.followers) end
                            checkMilestones(player, data, remotes)
                        end
                    end
                end
            end
        end
    end
end)

-- ================================================================
-- CHALLENGES & QUESTS DEFINITIONS
-- ================================================================
local CHALLENGES = {
    { id = "first_post",     name = "First Steps",         emoji = "📱", type = "once",  desc = "Post your first video",                          target = 1,      stat = "postCount",    rewardCoins = 100,  rewardViews = 500   },
    { id = "post_10",        name = "Content Creator",       emoji = "🎬", type = "once",  desc = "Post 10 videos",                                target = 10,     stat = "postCount",    rewardCoins = 500,  rewardViews = 2000  },
    { id = "post_50",        name = "Prolific Poster",      emoji = "🔥", type = "once",  desc = "Post 50 videos",                                target = 50,     stat = "postCount",    rewardCoins = 2500, rewardViews = 10000 },
    { id = "post_100",       name = "Content Machine",      emoji = "⭐", type = "once",  desc = "Post 100 videos",                               target = 100,    stat = "postCount",    rewardCoins = 8000, rewardViews = 50000 },
    { id = "followers_100",  name = "Rising Star",          emoji = "🌟", type = "once",  desc = "Reach 100 followers",                           target = 100,    stat = "followers",    rewardCoins = 200,  rewardViews = 1000  },
    { id = "followers_1k",   name = "Influencer",          emoji = "✨", type = "once",  desc = "Reach 1,000 followers",                         target = 1000,   stat = "followers",    rewardCoins = 1000, rewardViews = 5000  },
    { id = "followers_10k",  name = "Viral Sensation",      emoji = "💥", type = "once",  desc = "Reach 10,000 followers",                       target = 10000,  stat = "followers",    rewardCoins = 5000, rewardViews = 25000 },
    { id = "views_10k",      name = "Getting Noticed",      emoji = "👀", type = "once",  desc = "Reach 10,000 total views",                     target = 10000,  stat = "totalViews",   rewardCoins = 300,  rewardViews = 0     },
    { id = "views_100k",     name = "Trending",             emoji = "📈", type = "once",  desc = "Reach 100,000 total views",                    target = 100000, stat = "totalViews",   rewardCoins = 1500, rewardViews = 0     },
    { id = "views_1m",       name = "Going Global",         emoji = "🌍", type = "once",  desc = "Reach 1,000,000 total views",                  target = 1000000,stat = "totalViews",   rewardCoins = 10000,rewardViews = 0     },
    { id = "first_prestige", name = "Reborn",               emoji = "👑", type = "once",  desc = "Prestige for the first time",                  target = 1,      stat = "prestigeCount",rewardCoins = 1000, rewardViews = 5000  },
    { id = "buy_pc",         name = "Tech Upgrade",         emoji = "💻", type = "once",  desc = "Buy your first PC",                            target = 1,      stat = "_hasPC",       rewardCoins = 500,  rewardViews = 1000  },
    { id = "unlock_slot2",   name = "Multi-Platform",       emoji = "📺", type = "once",  desc = "Unlock PC video slot 2",                       target = 2,      stat = "_unlockedSlots",rewardCoins=800, rewardViews = 2000 },
    { id = "buy_home",       name = "Real Estate Mogul",    emoji = "🏠", type = "once",  desc = "Own your first home",                          target = 1,      stat = "_homeTier",    rewardCoins = 500,  rewardViews = 1500  },
    { id = "first_hack",     name = "Digital Rebel",        emoji = "💀", type = "once",  desc = "Hack another player",                           target = 1,      stat = "_hackCount",   rewardCoins = 300,  rewardViews = 500   },
}

local DAILY_QUESTS = {
    { id = "daily_posts",    name = "Daily Grind",         emoji = "📱", desc = "Post 3 videos today",          target = 3,  stat = "_todayPosts", rewardCoins = 150 },
    { id = "daily_views",    name = "Reach the Masses",    emoji = "👀", desc = "Get 5,000 views today",        target = 5000,stat = "_todayViews", rewardCoins = 300 },
    { id = "daily_followers",name = "Build Your Audience",  emoji = "👥", desc = "Gain 50 followers today",     target = 50, stat = "_todayFollowers", rewardCoins = 200 },
    { id = "daily_coins",    name = "Coin Collector",       emoji = "💰", desc = "Earn 1,000 coins today",      target = 1000,stat = "_todayCoins", rewardCoins = 250 },
    { id = "daily_slot",     name = "Slot Master",           emoji = "🎰", desc = "Fill 1 PC video slot",         target = 1,  stat = "_todaySlotsFilled", rewardCoins = 100 },
}

local VIRAL_THRESHOLD = 50000000

function GameManager.init()
    local remotes = ensureRemotes()
        remotes.PurchaseUpgrade.OnServerInvoke = function(player, upgradeName)
        local data = playerData[player.UserId]
        if not data then return false end
        if not data.upgradeLevels then data.upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 } end
        local level = data.upgradeLevels[upgradeName] or 0
        local maxLevel = 10 + math.min((data.prestigeCount or 0), 10) * 10
        if level >= maxLevel then
            return false, level, data.coins, maxLevel
        end
        local cost = math.floor(100 * (1.4 ^ level))
        if (data.coins or 0) >= cost then
            data.coins = (data.coins or 0) - cost
            data.upgradeLevels[upgradeName] = level + 1
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                player.leaderstats.Coins.Value = data.coins
            end
            if remotes:FindFirstChild("CoinsUpdated") then
                remotes.CoinsUpdated:FireClient(player, data.coins)
            end
            print(player.Name .. " upgraded " .. upgradeName .. " to level " .. data.upgradeLevels[upgradeName])
            return true, data.upgradeLevels[upgradeName], data.coins, maxLevel
        end
        return false
    end

    remotes.GetUpgrades.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        if not data.upgradeLevels then data.upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 } end
        return data.upgradeLevels
    end

    remotes.GetPrestigeInfo.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        local prestigeCount = data.prestigeCount or 0
        local capped = math.min(prestigeCount, 10)
        local maxLevel = 10 + capped * 10
        local isMaxed = prestigeCount >= 10
        return {
            prestigeCount = prestigeCount,
            maxLevel = maxLevel,
            followers = data.followers or 0,
            canPrestige = (data.followers or 0) >= 10000 and not isMaxed,
            nextMaxLevel = isMaxed and 110 or (10 + math.min(prestigeCount + 1, 10) * 10),
            bonusPercent = capped * 20,
            isMaxed = isMaxed,
        }
    end

    
    Players.PlayerAdded:Connect(function(player)
        -- Attempt to load saved data
        local key = "pv_" .. tostring(player.UserId)
        local loaded = nil
        local res = dsGet(key)
        if type(res) == "table" then
            loaded = res
        end

        if loaded then
            playerData[player.UserId] = loaded
            if loaded.views == nil then loaded.views = 0 end
            if loaded.totalViews == nil then loaded.totalViews = 0 end
            if loaded.coins == nil then loaded.coins = 0 end
            if loaded.followers == nil then loaded.followers = 0 end
            if loaded.contentType == nil then loaded.contentType = "Short Videos" end
            if loaded.prestigeCount == nil then loaded.prestigeCount = 0 end
            if loaded.hasPC == nil then loaded.hasPC = false end
            if loaded.pcLevel == nil then loaded.pcLevel = 1 end
            if loaded.unlockedSlots == nil then loaded.unlockedSlots = 1 end
            if loaded.name == nil then loaded.name = player.Name end
            if not loaded.upgradeLevels then
                loaded.upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 }
            end
            if not loaded.slotData then
                loaded.slotData = {}
                for i = 1, 3 do
                    loaded.slotData[i] = { state = "empty", rank = "", timeLeft = 0, cpm = 0 }
                end
            end
            if loaded.lastPostTime == nil then loaded.lastPostTime = 0 end
            if loaded.postCount == nil then loaded.postCount = 0 end
            if loaded.achievedMilestones == nil then loaded.achievedMilestones = {} end
            if loaded.lastLoginDate == nil then loaded.lastLoginDate = "" end
            if loaded.loginStreak == nil then loaded.loginStreak = 0 end
            if loaded.homeSlot == nil then loaded.homeSlot = 0 end
            if loaded.homeTier == nil then loaded.homeTier = 0 end
            if loaded.activeSponsor == nil then loaded.activeSponsor = nil end
            if loaded.lockedContentType == nil then loaded.lockedContentType = nil end
            if loaded.lockedSlotData == nil then loaded.lockedSlotData = nil end
            if loaded.prestigeUnlockedSlots == nil then loaded.prestigeUnlockedSlots = 0 end
            if not loaded.pcUpgrades then loaded.pcUpgrades = {} end
            if not loaded.challengeProgress then loaded.challengeProgress = {} end
            if not loaded.completedChallenges then loaded.completedChallenges = {} end
            if not loaded.dailyQuestDate then loaded.dailyQuestDate = "" end
            if not loaded.dailyQuestProgress then loaded.dailyQuestProgress = {} end
            if not loaded.completedDailyQuests then loaded.completedDailyQuests = {} end
            if loaded.hasGoneViral == nil then loaded.hasGoneViral = false end
            print("[GameManager] Loaded data for " .. player.Name .. " from DataStore")
        else
            playerData[player.UserId] = {
                views = 0,
                totalViews = 0,
                coins = 300,
                followers = 0,
                contentType = "Short Videos",
                prestigeCount = 0,
                hasPC = false,
                pcLevel = 1,
                unlockedSlots = 1,
                name = player.Name,
                lastPostTime = 0,
                postCount = 0,
                achievedMilestones = {},
                lastLoginDate = "",
                loginStreak = 0,
                homeSlot = 0,
                homeTier = 0,
                activeSponsor = nil,
                lockedContentType = nil,
                lockedSlotData = nil,
                prestigeUnlockedSlots = 0,
                pcUpgrades = {},
                challengeProgress = {},
                completedChallenges = {},
                dailyQuestDate = "",
                dailyQuestProgress = {},
                completedDailyQuests = {},
                hasGoneViral = false,
                upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 },
                slotData = {
                    { state = "empty", rank = "", timeLeft = 0, cpm = 0 },
                    { state = "locked", rank = "", timeLeft = 0, cpm = 0 },
                    { state = "locked", rank = "", timeLeft = 0, cpm = 0 },
                },
            }
            print("[GameManager] No saved data for " .. player.Name .. ", using defaults")
        end

        local leaderstats = Instance.new("Folder")
        leaderstats.Name = "leaderstats"
        leaderstats.Parent = player

        local coinsStat = Instance.new("IntValue")
        coinsStat.Name = "Coins"
        coinsStat.Value = playerData[player.UserId].coins or 0
        coinsStat.Parent = leaderstats

        local followersStat = Instance.new("IntValue")
        followersStat.Name = "Followers"
        followersStat.Value = playerData[player.UserId].followers or 0
        followersStat.Parent = leaderstats

        local totalViews = Instance.new("IntValue")
        totalViews.Name = "Views"
        totalViews.Value = playerData[player.UserId].totalViews or 0
        totalViews.Parent = leaderstats

        local prestigeStat = Instance.new("IntValue")
        prestigeStat.Name = "Prestige"
        prestigeStat.Value = playerData[player.UserId].prestigeCount or 0
        prestigeStat.Parent = leaderstats
    end)

    Players.PlayerRemoving:Connect(function(player)
        local data = playerData[player.UserId]
        if data then
            savePlayerData(player.UserId)
            print("[GameManager] Saved data for " .. player.Name)
            playerData[player.UserId] = nil
        end
    end)

    game:BindToClose(function()
        print("[GameManager] Server shutting down — saving all player data")
        for userId, _ in pairs(playerData) do
            savePlayerData(userId)
        end
        task.wait(3)
    end)

    task.spawn(function()
        while true do
            task.wait(AUTO_SAVE_INTERVAL)
            for userId, _ in pairs(playerData) do
                savePlayerData(userId)
            end
            print("[GameManager] Auto-saved all player data")
        end
    end)
    
    remotes.DevAction.OnServerEvent:Connect(function(player, action)
        local data = playerData[player.UserId]
        if not data then return end
        
        print("[DEV] Action: " .. action .. " by " .. player.Name)
        
        if action == "+1000coins" then data.coins = data.coins + 1000
        elseif action == "+10000coins" then data.coins = data.coins + 10000
        elseif action == "+100000coins" then data.coins = data.coins + 100000
        elseif action == "+1000000coins" then data.coins = data.coins + 1000000
        elseif action == "+100followers" then data.followers = data.followers + 100
        elseif action == "+1000followers" then data.followers = data.followers + 1000
        elseif action == "+10000followers" then data.followers = data.followers + 10000
        elseif action == "+100000followers" then data.followers = data.followers + 100000
        elseif action == "+100000views" then data.totalViews = data.totalViews + 100000
        elseif action == "+1000000views" then data.totalViews = data.totalViews + 1000000
        elseif action == "reset" then
            data.coins = 0
            data.followers = 0
            data.totalViews = 0
        elseif string.match(action, "^seedPlayers%s*(%d+)") then
            local n = tonumber(string.match(action, "^seedPlayers%s*(%d+)") ) or 0
            for i = 1, n do
                local uid = 9000000 + i
                playerData[uid] = {
                    name = "Bot" .. tostring(i),
                    followers = math.random(0, 500),
                    totalViews = math.random(0, 10000),
                    coins = math.random(0, 1000),
                    views = 0,
                    contentType = "Short Videos",
                    prestigeCount = 0,
                    hasPC = false,
                    pcLevel = 1,
                    unlockedSlots = 1,
                    upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 }
                }
            end
            print("[DEV] Seeded " .. tostring(n) .. " fake players")
        end
        
        -- Update leaderstats immediately
        if player:FindFirstChild("leaderstats") then
            if player.leaderstats:FindFirstChild("Coins") then player.leaderstats.Coins.Value = data.coins end
            if player.leaderstats:FindFirstChild("Followers") then player.leaderstats.Followers.Value = data.followers end
            if player.leaderstats:FindFirstChild("Views") then player.leaderstats.Views.Value = data.totalViews end
        end
        
        -- Fire Remotes
        if remotes:FindFirstChild("CoinsUpdated") then remotes.CoinsUpdated:FireClient(player, data.coins) end
        if remotes:FindFirstChild("FollowersUpdated") then remotes.FollowersUpdated:FireClient(player, data.followers) end
        if remotes:FindFirstChild("TotalViewsUpdated") then remotes.TotalViewsUpdated:FireClient(player, data.totalViews) end
    end)
    
    remotes.SetContentType.OnServerEvent:Connect(function(player, contentType)
        local data = playerData[player.UserId]
        if data then
            data.contentType = contentType
            print("[GameManager] " .. player.Name .. " chose " .. tostring(contentType))
        end
    end)

    -- Dev export of in-memory player data (for Studio testing)
    remotes.ExportPlayerData.OnServerInvoke = function(player)
        -- Only allow for devs (players with DevAction access already implied)
        return playerData
    end

    remotes.ImportPlayerData.OnServerEvent:Connect(function(player, dataTable)
        if type(dataTable) ~= "table" then return end
        for uid, info in pairs(dataTable) do
            playerData[tonumber(uid) or uid] = info
        end
        print("[DEV] Imported playerData table")
    end)

    remotes.RequestHack.OnServerEvent:Connect(function(player, targetId, mgSuccess)
        local data = playerData[player.UserId]
        if not data then return end

        local remotes = ensureRemotes()

        local chosenId = nil
        -- If client supplied a targetId and it's valid, use it
        if targetId and playerData[targetId] then
            chosenId = targetId
        else
            -- Find best rival: minimize difference in followers + views
            local bestId = nil
            local bestScore = nil
            for uid, other in pairs(playerData) do
                if uid ~= player.UserId then
                    local diffViews = math.abs((other.totalViews or 0) - (data.totalViews or 0))
                    local diffFollowers = math.abs((other.followers or 0) - (data.followers or 0))
                    local score = diffViews + (diffFollowers * 1000)
                    if not bestScore or score < bestScore then
                        bestScore = score
                        bestId = uid
                    end
                end
            end
            chosenId = bestId
        end

        if not chosenId then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "No valid rivals online") end
            return
        end

        local targetPlayer = nil
        for _, p in ipairs(Players:GetPlayers()) do
            if p.UserId == chosenId then targetPlayer = p break end
        end

        if not targetPlayer then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Target not found") end
            return
        end

        local targetData = playerData[chosenId]
        if not targetData then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Target data missing") end
            return
        end

        -- Hack logic: Cost 15% of your coins (min 1000), 35% success chance
        local cost = math.max(1000, math.floor((data.coins or 0) * 0.15))
        if (data.coins or 0) < cost then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Need " .. cost .. " coins to hack!") end
            return
        end

        data.coins = data.coins - cost

        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end

        local successChance = 0.35 + ((data.upgradeLevels and data.upgradeLevels.EngagementRate or 0) * 0.05) -- Cap around 85% with upgrades
        if math.random() > successChance then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Hack failed & lost " .. cost .. " coins!") end
            return
        end

        -- Steal 10-25% of target's coins (floor)
        local stealPct = 0.10 + (math.random() * 0.15)
        local stolen = math.floor((targetData.coins or 0) * stealPct)
        if stolen <= 0 then
            if remotes:FindFirstChild("HackResult") then remotes.HackResult:FireClient(player, false, "Target has no coins, but you still paid " .. cost .. "!") end
            return
        end

        targetData.coins = math.max(0, (targetData.coins or 0) - stolen)
        data.coins = (data.coins or 0) + stolen
        data.hackCount = (data.hackCount or 0) + 1
        data._todayCoins = (data._todayCoins or 0) + stolen

        -- Update leaderstats and notify both players
        for _, p in ipairs(Players:GetPlayers()) do
            local d = playerData[p.UserId]
            if d then
                if p:FindFirstChild("leaderstats") and p.leaderstats:FindFirstChild("Coins") then
                    p.leaderstats.Coins.Value = d.coins
                end
                if remotes:FindFirstChild("CoinsUpdated") and p.UserId == player.UserId then
                    remotes.CoinsUpdated:FireClient(p, d.coins)
                end
                if remotes:FindFirstChild("CoinsUpdated") and p.UserId == chosenId then
                    remotes.CoinsUpdated:FireClient(p, d.coins)
                end
            end
        end

        if remotes:FindFirstChild("HackResult") then
            remotes.HackResult:FireClient(player, true, { amount = stolen, targetName = targetPlayer.Name })
        end
    end)

    -- Server function to return best rival info to clients
    remotes.GetBestRival.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end

        -- Find best rival by minimizing difference in followers + views
        local bestId = nil
        local bestScore = nil
        for uid, other in pairs(playerData) do
            if uid ~= player.UserId then
                local diffViews = math.abs((other.totalViews or 0) - (data.totalViews or 0))
                local diffFollowers = math.abs((other.followers or 0) - (data.followers or 0))
                local score = diffViews + (diffFollowers * 1000)
                if not bestScore or score < bestScore then
                    bestScore = score
                    bestId = uid
                end
            end
        end

        if not bestId then return nil end

        -- Build a lightweight info table about the rival
        local info = playerData[bestId]
        if not info then return nil end
        return {
            userId = bestId,
            targetName = info.name or "Unknown",
            followers = info.followers or 0,
            views = info.totalViews or 0,
            coins = info.coins or 0,
        }
    end

    -- Return a list of players (excluding the caller) so clients can present a selection
    remotes.GetPlayerList.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return {} end
        local list = {}
        for uid, info in pairs(playerData) do
            if uid ~= player.UserId then
                table.insert(list, {
                    userId = uid,
                    name = info.name or "Unknown",
                    followers = info.followers or 0,
                    views = info.totalViews or 0,
                    coins = info.coins or 0,
                })
            end
        end
        return list
    end
    
    remotes.BuyPC.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return false end
        
        if (data.coins or 0) >= 1000 and not data.hasPC then
            data.coins = (data.coins or 0) - 1000
            data.hasPC = true
            data.pcLevel = 1
            
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                player.leaderstats.Coins.Value = data.coins
            end
            if remotes:FindFirstChild("CoinsUpdated") then
                remotes.CoinsUpdated:FireClient(player, data.coins)
            end
            
            print("[GameManager] " .. player.Name .. " bought a PC!")
            return true
        end
        return false
    end

    remotes.UpgradePC.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return false end
        if not data.hasPC then return false end
        
        local maxLevel = 10
        local currentLevel = data.pcLevel or 1
        if currentLevel >= maxLevel then return false end
        
        local cost = math.floor(500 * (1.5 ^ currentLevel))
        if (data.coins or 0) < cost then return false end
        
        data.coins = (data.coins or 0) - cost
        data.pcLevel = currentLevel + 1
        
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        print("[GameManager] " .. player.Name .. " upgraded PC to level " .. data.pcLevel)
        return true, data.pcLevel, cost
    end

    remotes.GetPCInfo.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        return {
            hasPC = data.hasPC or false,
            pcLevel = data.pcLevel or 1,
        }
    end

    remotes.BuySlot.OnServerInvoke = function(player, slotNumber)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]
        
        local costs = {[2] = 8000, [3] = 25000}
        local cost = costs[slotNumber]
        
        print("[Slots] BuySlot called by " .. player.Name .. " for slot " .. tostring(slotNumber))
        print("[Slots] Player coins: " .. tostring(data.coins))
        print("[Slots] Required: " .. tostring(cost))
        
        if not cost then return false end
        if (data.coins or 0) < cost then return false end
        
        data.coins = (data.coins or 0) - cost
        data.unlockedSlots = slotNumber
        
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        return true
    end

    remotes.GetUnlockedSlots.OnServerInvoke = function(player)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]
        return data and data.unlockedSlots or 1
    end

    remotes.GetSlotData.OnServerInvoke = function(player)
        while not playerData[player.UserId] do task.wait(0.2) end
        local data = playerData[player.UserId]
        if not data.slotData then
            data.slotData = {}
            for i = 1, 3 do
                local state = (i <= (data.unlockedSlots or 1)) and "empty" or "locked"
                data.slotData[i] = { state = state, rank = "", timeLeft = 0, cpm = 0 }
            end
        end
        return data.slotData
    end

    remotes.SyncSlotData.OnServerEvent:Connect(function(player, slotIndex, slotInfo)
        local data = playerData[player.UserId]
        if not data then return end
        if type(slotIndex) ~= "number" or slotIndex < 1 or slotIndex > 3 then return end
        if type(slotInfo) ~= "table" then return end
        if not data.slotData then
            data.slotData = {}
            for i = 1, 3 do data.slotData[i] = { state = "empty", rank = "", timeLeft = 0, cpm = 0 } end
        end
        data.slotData[slotIndex] = {
            state = slotInfo.state or "empty",
            rank = slotInfo.rank or "",
            timeLeft = tonumber(slotInfo.timeLeft) or 0,
            cpm = tonumber(slotInfo.cpm) or 0,
        }
    end)

    remotes.SlotIncome.OnServerEvent:Connect(function(player, amount)
        local data = playerData[player.UserId]
        if not data then return end
        
        amount = math.floor(tonumber(amount) or 0)
        if amount <= 0 then return end
        
        local level = data.upgradeLevels and data.upgradeLevels.EditSpeed or 0
        local editMultiplier = 1 + (level * 0.03)
        local pcLevelMult = 1 + ((data.pcLevel or 1) - 1) * 0.1
        local uploadSpeedBonus = 1 + ((data.pcUpgrades and data.pcUpgrades.uploadSpeed or 0) * 0.1)
        local prestigeMult = 1 + (data.prestigeCount or 0) * 0.1
        local finalIncome = math.floor(amount * editMultiplier * pcLevelMult * uploadSpeedBonus * prestigeMult)
        
        print("[PassiveIncome] EditSpeed Lv." .. level .. " = " .. editMultiplier .. "x, PC Lv." .. (data.pcLevel or 1) .. " = " .. pcLevelMult .. "x")
        
        data.coins = (data.coins or 0) + finalIncome
        local seoLevel = data.upgradeLevels and data.upgradeLevels.SEOAlgorithm or 0
        local pcSeoBonus = 1 + ((data.pcUpgrades and data.pcUpgrades.seoBoost or 0) * 0.15)
        local seoMultiplier = (1 + (seoLevel * 0.25)) * pcSeoBonus
        local viewsEarned = math.floor(finalIncome * 10 * seoMultiplier)
        data.totalViews = (data.totalViews or 0) + viewsEarned

        local engRate = data.upgradeLevels and data.upgradeLevels.EngagementRate or 0
        local conversionRate = math.max(2, 25 - (engRate * 2))
        local newFollowers = math.floor(data.totalViews / conversionRate)
        if newFollowers > (data.followers or 0) then
            data.followers = newFollowers
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
                player.leaderstats.Followers.Value = data.followers
            end
            if remotes:FindFirstChild("FollowersUpdated") then
                remotes.FollowersUpdated:FireClient(player, data.followers)
            end
            checkMilestones(player, data, remotes)
            print(player.Name .. " followers: " .. data.followers)
        end

        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Views") then
            player.leaderstats.Views.Value = data.totalViews
        end

        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        if remotes:FindFirstChild("TotalViewsUpdated") then
            remotes.TotalViewsUpdated:FireClient(player, data.totalViews)
        end
        print(player.Name .. " passive income: +" .. amount .. " coins, +" .. viewsEarned .. " views")
    end)

    remotes.MinigameReward.OnServerEvent:Connect(function(player, viewsEarned)
        local data = playerData[player.UserId]
        if not data then return end
        
        viewsEarned = math.clamp(math.floor(tonumber(viewsEarned) or 0), 0, 1000000)
        if viewsEarned <= 0 then return end
        
        data.views = (data.views or 0) + viewsEarned
        data.totalViews = (data.totalViews or 0) + viewsEarned
        data._todayViews = (data._todayViews or 0) + viewsEarned
        data._todaySlotsFilled = (data._todaySlotsFilled or 0) + 1
        if not data.hasGoneViral and (data.totalViews or 0) >= VIRAL_THRESHOLD then
            task.spawn(function()
                task.wait(0.1)
                if remotes:FindFirstChild("GoingViral") then remotes.GoingViral:FireClient(player, player.Name, data.totalViews) end
            end)
        end

        local engRate = data.upgradeLevels and data.upgradeLevels.EngagementRate or 0
        local conversionRate = math.max(2, 25 - (engRate * 2))
        local newFollowers = math.floor(data.totalViews / conversionRate)
        if newFollowers > (data.followers or 0) then
            data.followers = newFollowers
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
                player.leaderstats.Followers.Value = data.followers
            end
            if remotes:FindFirstChild("FollowersUpdated") then
                remotes.FollowersUpdated:FireClient(player, data.followers)
            end
            checkMilestones(player, data, remotes)
            print(player.Name .. " followers: " .. data.followers)
        end

        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Views") then
            player.leaderstats.Views.Value = data.totalViews
        end

        remotes.ViewsUpdated:FireClient(player, data.views or 0, data.totalViews or 0)
        if remotes:FindFirstChild("TotalViewsUpdated") then
            remotes.TotalViewsUpdated:FireClient(player, data.totalViews or 0)
        end
    end)

    remotes.HasPC.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        return data and data.hasPC or false
    end

    remotes.PostContent.OnServerEvent:Connect(function(player, multiplier)
        local data = playerData[player.UserId]
        if not data then return end

        local now = os.time()
        if now - (data.lastPostTime or 0) < 2 then return end

        multiplier = math.clamp(tonumber(multiplier) or 1.0, 0.5, 3.0)
        
        local cType = data.contentType or "Short Videos"
        local baseViews = BASE_VIEWS[cType] or 100
        local qualityMultiplier = 1.3 ^ (data.upgradeLevels and data.upgradeLevels.ContentQuality or 0)
        local viewsEarned = math.floor(baseViews * multiplier * qualityMultiplier)
        data.totalViews = (data.totalViews or 0) + viewsEarned

        local engRate = data.upgradeLevels and data.upgradeLevels.EngagementRate or 0
        local conversionRate = math.max(2, 25 - (engRate * 2))
        local newFollowers = math.floor(data.totalViews / conversionRate)
        if newFollowers > (data.followers or 0) then
            data.followers = newFollowers
            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Followers") then
                player.leaderstats.Followers.Value = data.followers
            end
            if remotes:FindFirstChild("FollowersUpdated") then
                remotes.FollowersUpdated:FireClient(player, data.followers)
            end
            checkMilestones(player, data, remotes)
            print(player.Name .. " followers: " .. data.followers)
        end

        local prestigeMult = 1 + (data.prestigeCount or 0) * 0.1
        local retentionBonus = 1 + ((data.pcUpgrades and data.pcUpgrades.retention or 0) * 0.30)
        local adRevenueBonus = 1 + ((data.pcUpgrades and data.pcUpgrades.adRevenue or 0) * 0.20)
        viewsEarned = math.floor(viewsEarned * retentionBonus)
        local coinsEarned = math.floor(viewsEarned * 0.08 * prestigeMult * adRevenueBonus)
        data.coins = (data.coins or 0) + coinsEarned
        data._todayCoins = (data._todayCoins or 0) + coinsEarned
        if not data.hasGoneViral and (data.totalViews or 0) >= VIRAL_THRESHOLD then
            task.spawn(function()
                task.wait(0.1)
                if remotes:FindFirstChild("GoingViral") then remotes.GoingViral:FireClient(player, player.Name, data.totalViews) end
            end)
        end

        data.lastPostTime = os.time()
        data.postCount = (data.postCount or 0) + 1
        data._todayPosts = (data._todayPosts or 0) + 1
        data._todayViews = (data._todayViews or 0) + viewsEarned
        data._todayFollowers = (data._todayFollowers or 0) + math.max(0, newFollowers - (data.followers - math.max(0, math.floor(viewsEarned / conversionRate))))
        checkMilestones(player, data, remotes)

        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Views") then
            player.leaderstats.Views.Value = data.totalViews
        end

        remotes.ViewsUpdated:FireClient(player, data.views or 0, data.totalViews or 0)
        if remotes:FindFirstChild("TotalViewsUpdated") then
            remotes.TotalViewsUpdated:FireClient(player, data.totalViews or 0)
        end
    end)

    -- Home System
    remotes.GetHomeData.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        return {
            ownedSlot = data.homeSlot or 0,
            homeTier = data.homeTier or 0,
            slotCount = 6,
        }
    end

    remotes.ClaimHome.OnServerInvoke = function(player, slotNumber)
        local data = playerData[player.UserId]
        if not data then return false end
        if data.homeSlot and data.homeSlot > 0 then return false end
        if type(slotNumber) ~= "number" or slotNumber < 1 or slotNumber > 6 then return false end
        data.homeSlot = slotNumber
        data.homeTier = 1
        return true
    end

    -- PC Upgrades available in PCOS
    local PC_UPGRADES = {
        { id = "seoBoost", name = "SEO Algorithm", desc = "+15% views from passive income", baseCost = 2000, costMult = 1.8, effect = 0.15 },
        { id = "adRevenue", name = "Ad Revenue", desc = "+20% coins from videos", baseCost = 3000, costMult = 2.0, effect = 0.20 },
        { id = "brandDeal", name = "Brand Deals", desc = "+25% sponsor income", baseCost = 5000, costMult = 2.2, effect = 0.25 },
        { id = "uploadSpeed", name = "Upload Speed", desc = "Slots generate income 10% faster", baseCost = 2500, costMult = 1.9, effect = 0.10 },
        { id = "retention", name = "Audience Retention", desc = "+30% video views", baseCost = 4000, costMult = 2.1, effect = 0.30 },
    }

    remotes.GetPCUpgrades.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return {} end
        if not data.pcUpgrades then data.pcUpgrades = {} end
        return data.pcUpgrades
    end

    remotes.PurchasePCUpgrade.OnServerInvoke = function(player, upgradeId)
        local data = playerData[player.UserId]
        if not data then return false, "No data" end
        if not data.hasPC then return false, "No PC" end
        if not data.pcUpgrades then data.pcUpgrades = {} end
        
        local upgrade = nil
        for _, u in ipairs(PC_UPGRADES) do
            if u.id == upgradeId then upgrade = u break end
        end
        if not upgrade then return false, "Invalid upgrade" end
        
        local prestigeCount = data.prestigeCount or 0
        local currentLevel = data.pcUpgrades[upgradeId] or 0
        local maxLevel = 5 + prestigeCount
        if currentLevel >= maxLevel then return false, "Max level" end
        
        local prestigeBonus = 1 + prestigeCount * 0.15
        local cost = math.floor((upgrade.baseCost * prestigeBonus) * (upgrade.costMult ^ currentLevel))
        if (data.coins or 0) < cost then return false, "Not enough coins" end
        
        data.coins = (data.coins or 0) - cost
        data.pcUpgrades[upgradeId] = currentLevel + 1
        
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        return true, currentLevel + 1, cost
    end

    local HOME_TIERS = {
        { name = "Shack",      cost = 0,      income = 1,      reqFollowers = 0 },
        { name = "Studio",     cost = 2500,   income = 3,      reqFollowers = 100 },
        { name = "Apartment",  cost = 15000,  income = 8,      reqFollowers = 500 },
        { name = "House",      cost = 75000,  income = 25,     reqFollowers = 2000 },
        { name = "Mansion",    cost = 400000, income = 100,    reqFollowers = 10000 },
        { name = "Penthouse",  cost = 2500000, income = 500,   reqFollowers = 50000 },
    }

    remotes.UpgradeHome.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return false end
        if (data.homeSlot or 0) <= 0 then return false end
        local nextTier = (data.homeTier or 0) + 1
        if nextTier > #HOME_TIERS then return false end
        local tier = HOME_TIERS[nextTier]
        if (data.coins or 0) < tier.cost then return false end
        data.coins = (data.coins or 0) - tier.cost
        data.homeTier = nextTier
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        return true
    end

    -- Home passive income loop
    task.spawn(function()
        while true do
            task.wait(60)
            for _, player in pairs(Players:GetPlayers()) do
                local data = playerData[player.UserId]
                if data and (data.homeSlot or 0) > 0 and (data.homeTier or 0) >= 1 then
                    local tier = HOME_TIERS[data.homeTier]
                    if tier then
                        local prestigeMult = 1 + (data.prestigeCount or 0) * 0.1
                        local pcLevelMult = 1 + ((data.pcLevel or 1) - 1) * 0.1
                        local income = math.floor(tier.income * prestigeMult * pcLevelMult)
                        data.coins = data.coins + income
                        data._todayCoins = (data._todayCoins or 0) + income
                        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                            player.leaderstats.Coins.Value = data.coins
                        end
                    end
                end
            end
        end
    end)

    -- Stats
    remotes.GetStats.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        return {
            totalViews = data.totalViews or 0,
            followers = data.followers or 0,
            coins = data.coins or 0,
            postCount = data.postCount or 0,
            prestigeCount = data.prestigeCount or 0,
            contentType = data.contentType or "Short Videos",
            hasPC = data.hasPC or false,
            unlockedSlots = data.unlockedSlots or 1,
            homeTier = data.homeTier or 0,
            pcLevel = data.pcLevel or 1,
        }
    end

    -- Sponsorships (randomized, scaled to follower count)
    local BRANDS = {"ZapTech", "NovaWear", "FrostByte", "LunaBites", "PulseGear",
        "VibeCandy", "NeonDrinks", "ByteSnacks", "CosmoFit", "HyperSole",
        "TrendTok", "ClipMaster", "WaveSync", "BlazeVR", "ChillPill"}
    local EMOJIS = {"🥤", "🎧", "🎮", "👟", "⌚", "🍕", "💻", "🏋️", "📱", "🕹️"}

    local function generateSponsors(followers)
        local offers = {}
        local maxFollowers = 1000000
        local cappedFollowers = math.max(10, math.min(followers, maxFollowers))

        -- Both cost around 15-20% of followers
        local costA = math.floor(cappedFollowers * (0.15 + math.random() * 0.05))
        local costB = math.floor(cappedFollowers * (0.15 + math.random() * 0.05))

        -- Offer A: "Burst Deal" - High immediate lump sum, low and short passive
        local lumpA = math.floor(cappedFollowers * 0.4 + math.random() * cappedFollowers * 0.1)
        local incomePerSecA = math.floor(cappedFollowers * 0.0005)
        local durA = 2 + math.random(0, 1) -- 2 to 3 minutes

        -- Offer B: "Long Term Contract" - No immediate sum, big massive passive over time
        local incomePerSecB = math.floor(cappedFollowers * 0.0025 + math.random() * cappedFollowers * 0.001)
        local durB = 5 + math.random(1, 2) -- 6 to 7 minutes

        local b1 = BRANDS[math.random(1, #BRANDS)]
        local b2 = BRANDS[math.random(1, #BRANDS)]
        while b2 == b1 do b2 = BRANDS[math.random(1, #BRANDS)] end
        local e1 = EMOJIS[math.random(1, #EMOJIS)]
        local e2 = EMOJIS[math.random(1, #EMOJIS)]

        if incomePerSecA < 1 then incomePerSecA = 1 end
        if incomePerSecB < 1 then incomePerSecB = 1 end
        if lumpA < 50 then lumpA = 50 end

        offers[1] = {
            name = b1 .. " Quick Campaign",
            emoji = e1,
            followerCost = costA,
            coinsPerSec = incomePerSecA,
            duration = durA,
            lumpSum = lumpA,
        }
        offers[2] = {
            name = b2 .. " Exclusive Contract",
            emoji = e2,
            followerCost = costB,
            coinsPerSec = incomePerSecB,
            duration = durB,
            lumpSum = nil,
        }
        return offers
    end

    remotes.GetSponsorships.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        local active = data.activeSponsor
        local remaining = 0
        if active then
            if active.endTime and os.time() >= active.endTime then
                data.activeSponsor = nil
                active = nil
            else
                remaining = math.max(0, math.ceil((active.endTime - os.time())))
            end
        end
        local offers = {}
        if (data.followers or 0) >= 500 and not active then
            offers = generateSponsors(data.followers or 0)
            data.pendingOffers = offers
        end
        return {
            followers = data.followers or 0,
            activeSponsor = active,
            remaining = remaining,
            available = offers,
        }
    end

    remotes.GetLockedContentType.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        return data.lockedContentType or nil
    end

    remotes.GetLockedSlotData.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return nil end
        return {
            slotData = data.lockedSlotData or nil,
            prestigeUnlockedSlots = data.prestigeUnlockedSlots or 0,
        }
    end

    remotes.AcceptSponsor.OnServerInvoke = function(player, offerIndex)
        local data = playerData[player.UserId]
        if not data then return false end
        if data.activeSponsor then return false end
        local offers = data.pendingOffers or {}
        local sponsor = offers[offerIndex]
        if not sponsor then return false end
        if (data.followers or 0) < sponsor.followerCost then return false end
        data.followers = (data.followers or 0) - sponsor.followerCost
        if sponsor.lumpSum then
            data.coins = (data.coins or 0) + sponsor.lumpSum
        end
        if player:FindFirstChild("leaderstats") then
            if player.leaderstats:FindFirstChild("Followers") then
                player.leaderstats.Followers.Value = data.followers
            end
            if player.leaderstats:FindFirstChild("Coins") then
                player.leaderstats.Coins.Value = data.coins
            end
        end
        
        if remotes:FindFirstChild("FollowersUpdated") then
            remotes.FollowersUpdated:FireClient(player, data.followers)
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        
        data.activeSponsor = {
            name = sponsor.name,
            emoji = sponsor.emoji,
            coinsPerSec = sponsor.coinsPerSec,
            endTime = os.time() + (sponsor.duration * 60),
        }
        data.pendingOffers = nil
        return true
    end

    -- Sponsor income loop (every 1 second)
    task.spawn(function()
        while true do
            task.wait(1)
            for _, player in pairs(Players:GetPlayers()) do
                local data = playerData[player.UserId]
                if data and data.activeSponsor then
                    if os.time() >= (data.activeSponsor.endTime or 0) then
                        data.activeSponsor = nil
                    else
                        local prestigeMult = 1 + (data.prestigeCount or 0) * 0.1
                        local brandDealBonus = 1 + ((data.pcUpgrades and data.pcUpgrades.brandDeal or 0) * 0.25)
                        local income = math.floor((data.activeSponsor.coinsPerSec or 0) * prestigeMult * brandDealBonus)
                        if income > 0 then
                            data.coins = (data.coins or 0) + income
                            data._todayCoins = (data._todayCoins or 0) + income
                            if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
                                player.leaderstats.Coins.Value = data.coins
                            end
                            if remotes:FindFirstChild("CoinsUpdated") then
                                remotes.CoinsUpdated:FireClient(player, data.coins)
                            end
                            if remotes:FindFirstChild("CoinsUpdated") then
                                remotes.CoinsUpdated:FireClient(player, data.coins)
                            end
                        end
                    end
                end
            end
        end
    end)

    remotes.Prestige.OnServerEvent:Connect(function(player)
        local data = playerData[player.UserId]
        if not data then return end
        if (data.prestigeCount or 0) >= 10 then return end
        
        -- Scale required followers: 10K, 15K, 22.5K, 33.75K... (+50% each prestige)
        local requiredFollowers = math.floor(25000 * (1.8 ^ (data.prestigeCount or 0)))
        if (data.followers or 0) < requiredFollowers then
            local prestigeEvent = remotes:FindFirstChild("PrestigeResult")
            if prestigeEvent then
                prestigeEvent:FireClient(player, data.prestigeCount or 0, data.coins or 0)
            end
            return
        end

        local prestigeBonus = 1 + ((data.prestigeCount or 0) * 0.2)
        data.prestigeCount = (data.prestigeCount or 0) + 1
        data.views = 0
        data.totalViews = 0
        data.coins = math.floor(500 * prestigeBonus)
        data.followers = 0
        -- Keep PC and upgrades on prestige, just lock content type
        data.lockedContentType = data.contentType
        data.contentType = "Short Videos"
        data.postCount = 0
        data.achievedMilestones = {}
        data.activeSponsor = nil
        -- Reset phone upgrades but keep PC upgrades
        data.upgradeLevels = { ContentQuality = 0, EditSpeed = 0, PostFrequency = 0, EngagementRate = 0, SEOAlgorithm = 0 }
        -- Reset home (not PC)
        data.homeSlot = 0
        data.homeTier = 0
        if player:FindFirstChild("leaderstats") then
            local ls = player.leaderstats
            if ls:FindFirstChild("Coins") then ls.Coins.Value = data.coins end
            if ls:FindFirstChild("Followers") then ls.Followers.Value = 0 end
            if ls:FindFirstChild("Views") then ls.Views.Value = 0 end
            if ls:FindFirstChild("Prestige") then ls.Prestige.Value = data.prestigeCount end
        end
        local prestigeEvent = remotes:FindFirstChild("PrestigeResult")
        if prestigeEvent then
            prestigeEvent:FireClient(player, data.prestigeCount, data.coins)
        end
        if remotes:FindFirstChild("FollowersUpdated") then
            remotes.FollowersUpdated:FireClient(player, 0)
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        print("[Prestige] " .. player.Name .. " prestiged! Count: " .. data.prestigeCount)
    end)

    remotes.GetDailyBonus.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return false, 0 end
        local today = os.date("%Y-%m-%d")
        if data.lastLoginDate == today then
            return false, 0
        end
        data.lastLoginDate = today
        local dayStreak = (data.loginStreak or 0) + 1
        if dayStreak > 7 then dayStreak = 1 end
        data.loginStreak = dayStreak
        local bonus = 50 + (dayStreak * 50)
        data.coins = data.coins + bonus
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        return true, bonus, dayStreak
    end

    remotes.GetChallenges.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return {} end
        local result = {}
        for _, ch in ipairs(CHALLENGES) do
            local completed = data.completedChallenges and data.completedChallenges[ch.id] or false
            local progress = 0
            if ch.stat == "_hasPC" then
                progress = data.hasPC and 1 or 0
            elseif ch.stat == "_unlockedSlots" then
                progress = data.unlockedSlots or 1
            elseif ch.stat == "_homeTier" then
                progress = data.homeTier and data.homeTier > 0 and 1 or 0
            elseif ch.stat == "_hackCount" then
                progress = data.hackCount or 0
            elseif ch.stat == "prestigeCount" then
                progress = data.prestigeCount or 0
            else
                progress = data[ch.stat] or 0
            end
            table.insert(result, {
                id = ch.id,
                name = ch.name,
                emoji = ch.emoji,
                desc = ch.desc,
                target = ch.target,
                progress = math.min(progress, ch.target),
                rewardCoins = ch.rewardCoins,
                rewardViews = ch.rewardViews,
                completed = completed,
                claimable = progress >= ch.target and not completed,
            })
        end
        return result
    end

    remotes.ClaimChallenge.OnServerInvoke = function(player, challengeId)
        local data = playerData[player.UserId]
        if not data then return false, "No data" end
        if not data.completedChallenges then data.completedChallenges = {} end
        if data.completedChallenges[challengeId] then return false, "Already claimed" end
        local ch = nil
        for _, c in ipairs(CHALLENGES) do
            if c.id == challengeId then ch = c break end
        end
        if not ch then return false, "Invalid challenge" end
        local progress = 0
        if ch.stat == "_hasPC" then
            progress = data.hasPC and 1 or 0
        elseif ch.stat == "_unlockedSlots" then
            progress = data.unlockedSlots or 1
        elseif ch.stat == "_homeTier" then
            progress = data.homeTier and data.homeTier > 0 and 1 or 0
        elseif ch.stat == "_hackCount" then
            progress = data.hackCount or 0
        elseif ch.stat == "prestigeCount" then
            progress = data.prestigeCount or 0
        else
            progress = data[ch.stat] or 0
        end
        if progress < ch.target then return false, "Not completed" end
        data.completedChallenges[challengeId] = true
        data.coins = (data.coins or 0) + ch.rewardCoins
        data.totalViews = (data.totalViews or 0) + ch.rewardViews
        if player:FindFirstChild("leaderstats") then
            if player.leaderstats:FindFirstChild("Coins") then player.leaderstats.Coins.Value = data.coins end
            if player.leaderstats:FindFirstChild("Views") then player.leaderstats.Views.Value = data.totalViews end
        end
        if remotes:FindFirstChild("CoinsUpdated") then remotes.CoinsUpdated:FireClient(player, data.coins) end
        if remotes:FindFirstChild("TotalViewsUpdated") then remotes.TotalViewsUpdated:FireClient(player, data.totalViews) end
        return true, ch.rewardCoins, ch.rewardViews
    end

    local function getDailyQuestsForData(data)
        local today = os.date("%Y-%m-%d")
        if (data.dailyQuestDate or "") ~= today then
            data.dailyQuestDate = today
            data.dailyQuestProgress = {}
            data.completedDailyQuests = {}
            data._todayPosts = 0
            data._todayViews = 0
            data._todayFollowers = 0
            data._todayCoins = 0
            data._todaySlotsFilled = 0
        end
        local result = {}
        for _, q in ipairs(DAILY_QUESTS) do
            local completed = data.completedDailyQuests and data.completedDailyQuests[q.id] or false
            local progress = 0
            if q.stat == "_todayPosts" then progress = data._todayPosts or 0
            elseif q.stat == "_todayViews" then progress = data._todayViews or 0
            elseif q.stat == "_todayFollowers" then progress = data._todayFollowers or 0
            elseif q.stat == "_todayCoins" then progress = data._todayCoins or 0
            elseif q.stat == "_todaySlotsFilled" then progress = data._todaySlotsFilled or 0
            end
            table.insert(result, {
                id = q.id,
                name = q.name,
                emoji = q.emoji,
                desc = q.desc,
                target = q.target,
                progress = math.min(progress, q.target),
                rewardCoins = q.rewardCoins,
                completed = completed,
                claimable = progress >= q.target and not completed,
            })
        end
        return result
    end

    remotes.GetDailyQuests.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return {} end
        return getDailyQuestsForData(data)
    end

    remotes.ClaimDailyQuest.OnServerInvoke = function(player, questId)
        local data = playerData[player.UserId]
        if not data then return false end
        local today = os.date("%Y-%m-%d")
        if (data.dailyQuestDate or "") ~= today then
            data.dailyQuestDate = today
            data.dailyQuestProgress = {}
            data.completedDailyQuests = {}
        end
        if not data.completedDailyQuests then data.completedDailyQuests = {} end
        if data.completedDailyQuests[questId] then return false end
        local q = nil
        for _, dq in ipairs(DAILY_QUESTS) do
            if dq.id == questId then q = dq break end
        end
        if not q then return false end
        local progress = 0
        if q.stat == "_todayPosts" then progress = data._todayPosts or 0
        elseif q.stat == "_todayViews" then progress = data._todayViews or 0
        elseif q.stat == "_todayFollowers" then progress = data._todayFollowers or 0
        elseif q.stat == "_todayCoins" then progress = data._todayCoins or 0
        elseif q.stat == "_todaySlotsFilled" then progress = data._todaySlotsFilled or 0
        end
        if progress < q.target then return false end
        data.completedDailyQuests[q.id] = true
        data.coins = (data.coins or 0) + q.rewardCoins
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then remotes.CoinsUpdated:FireClient(player, data.coins) end
        return true, q.rewardCoins
    end

    remotes.GetLeaderboard.OnServerInvoke = function(player)
        local list = {}
        for uid, info in pairs(playerData) do
            table.insert(list, {
                name = info.name or "Unknown",
                totalViews = info.totalViews or 0,
                followers = info.followers or 0,
                prestige = info.prestigeCount or 0,
                coins = info.coins or 0,
            })
        end
        table.sort(list, function(a, b) return a.totalViews > b.totalViews end)
        local top10 = {}
        for i = 1, math.min(10, #list) do
            top10[i] = list[i]
        end
        local myRank = 0
        local myData = playerData[player.UserId]
        if myData then
            for i, entry in ipairs(list) do
                if entry.name == (myData.name or "Unknown") and entry.totalViews == (myData.totalViews or 0) then
                    myRank = i
                    break
                end
            end
        end
        return { top = top10, myRank = myRank }
    end

    remotes.GoingViral.OnServerEvent:Connect(function(player)
        local data = playerData[player.UserId]
        if not data then return end
        if data.hasGoneViral then return end
        if (data.totalViews or 0) < VIRAL_THRESHOLD then return end
        data.hasGoneViral = true
        local bonus = 50000
        data.coins = (data.coins or 0) + bonus
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then
            remotes.CoinsUpdated:FireClient(player, data.coins)
        end
        for _, p in ipairs(Players:GetPlayers()) do
            if remotes:FindFirstChild("GoingViral") then
                remotes.GoingViral:FireClient(p, player.Name, data.totalViews or 0)
            end
        end
    end)

    remotes.ClaimGoingViral.OnServerInvoke = function(player)
        local data = playerData[player.UserId]
        if not data then return false end
        if not data.hasGoneViral then return false end
        local bonus = 50000
        data.coins = (data.coins or 0) + bonus
        if player:FindFirstChild("leaderstats") and player.leaderstats:FindFirstChild("Coins") then
            player.leaderstats.Coins.Value = data.coins
        end
        if remotes:FindFirstChild("CoinsUpdated") then remotes.CoinsUpdated:FireClient(player, data.coins) end
        return true, bonus
    end

    print("[GameManager] Online")
end

return GameManager
