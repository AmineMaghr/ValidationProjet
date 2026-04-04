-- DEV ONLY: DELETE BEFORE LAUNCH
local Players = game:GetService("Players")
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local player = Players.LocalPlayer

local playerGui = player:WaitForChild("PlayerGui")

local screenGui = Instance.new("ScreenGui")
screenGui.Name = "DevMenuGui"
screenGui.ResetOnSpawn = false
screenGui.Parent = playerGui

local devButton = Instance.new("TextButton")
devButton.Name = "DevButton"
devButton.Size = UDim2.new(0, 50, 0, 30)
devButton.Position = UDim2.new(0, 10, 0, 10)
devButton.BackgroundColor3 = Color3.fromRGB(200, 0, 0)
devButton.TextColor3 = Color3.fromRGB(255, 255, 255)
devButton.Font = Enum.Font.GothamBold
devButton.TextSize = 14
devButton.Text = "DEV"
devButton.Parent = screenGui

local devPanel = Instance.new("Frame")
devPanel.Name = "DevPanel"
devPanel.Size = UDim2.new(0, 200, 0, 400)
devPanel.Position = UDim2.new(0, 70, 0, 10)
devPanel.BackgroundColor3 = Color3.fromRGB(30, 30, 30)
devPanel.Visible = false
devPanel.Parent = screenGui

local title = Instance.new("TextLabel")
title.Name = "Title"
title.Size = UDim2.new(1, 0, 0, 30)
title.BackgroundTransparency = 1
title.TextColor3 = Color3.fromRGB(255, 0, 0)
title.Font = Enum.Font.GothamBold
title.TextSize = 16
title.Text = "Developer Menu"
title.Parent = devPanel

local closeButton = Instance.new("TextButton")
closeButton.Name = "CloseButton"
closeButton.Size = UDim2.new(0, 30, 0, 30)
closeButton.Position = UDim2.new(1, -30, 0, 0)
closeButton.BackgroundTransparency = 1
closeButton.TextColor3 = Color3.fromRGB(255, 255, 255)
closeButton.Font = Enum.Font.GothamBold
closeButton.TextSize = 14
closeButton.Text = "X"
closeButton.Parent = devPanel

local scrollFrame = Instance.new("ScrollingFrame")
scrollFrame.Size = UDim2.new(1, 0, 1, -30)
scrollFrame.Position = UDim2.new(0, 0, 0, 30)
scrollFrame.BackgroundTransparency = 1
scrollFrame.CanvasSize = UDim2.new(0, 0, 0, 400)
scrollFrame.ScrollBarThickness = 4
scrollFrame.Parent = devPanel

local listLayout = Instance.new("UIListLayout")
listLayout.Parent = scrollFrame
listLayout.SortOrder = Enum.SortOrder.LayoutOrder
listLayout.Padding = UDim.new(0, 5)

local buttons = {
    { text = "+1,000 Coins", action = "+1000coins" },
    { text = "+10,000 Coins", action = "+10000coins" },
    { text = "+100,000 Coins", action = "+100000coins" },
    { text = "+1,000,000 Coins", action = "+1000000coins" },
    { text = "+100 Followers", action = "+100followers" },
    { text = "+1,000 Followers", action = "+1000followers" },
    { text = "+10,000 Followers", action = "+10000followers" },
    { text = "+100,000 Followers", action = "+100000followers" },
    { text = "+100,000 Views", action = "+100000views" },
    { text = "+1,000,000 Views", action = "+1000000views" },
    { text = "Reset All Stats", action = "reset" }
}

for i, btnInfo in ipairs(buttons) do
    local b = Instance.new("TextButton")
    b.Size = UDim2.new(1, -10, 0, 30)
    b.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
    b.TextColor3 = Color3.fromRGB(255, 255, 255)
    b.Font = Enum.Font.Gotham
    b.TextSize = 14
    b.Text = btnInfo.text
    b.LayoutOrder = i
    b.Parent = scrollFrame
    
    b.MouseButton1Click:Connect(function()
        local remotes = ReplicatedStorage:WaitForChild("Remotes")
        local devAction = remotes:WaitForChild("DevAction")
        devAction:FireServer(btnInfo.action)
    end)
end

devButton.MouseButton1Click:Connect(function()
    devPanel.Visible = not devPanel.Visible
end)

closeButton.MouseButton1Click:Connect(function()
    devPanel.Visible = false
end)
