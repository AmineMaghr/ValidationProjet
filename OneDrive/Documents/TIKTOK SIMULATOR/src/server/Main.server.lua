local ReplicatedStorage = game:GetService("ReplicatedStorage")
local DataStoreService = game:GetService("DataStoreService")
local Players = game:GetService("Players")

local GameManager = require(script.Parent:WaitForChild("GameManager"))
GameManager.init()

print("[GoingViral] Server started!")
