local SettingsApp = {}

function SettingsApp.create(content, env)
  local player = env.player
  local Remotes = env.Remotes
  local C = env.C
  local makeFrame = env.makeFrame
  local makeLabel = env.makeLabel
  local makeCorner = env.makeCorner

    local lbl = makeLabel(content, "Appearance", 18, C.white, Enum.Font.GothamBold, UDim2.new(1,0,0,40), UDim2.new(0,0,0,20))
    lbl.TextXAlignment = Enum.TextXAlignment.Center
  
    local toggleBg = makeFrame(content, C.card, UDim2.new(0, 240, 0, 50), UDim2.new(0.5, -120, 0, 80), 0)
    makeCorner(toggleBg, 12)
    env.makeStroke(toggleBg, C.border, 2, 0)
    
    local toggleBtn = Instance.new("TextButton")
    env.setGlobalSettingsBtn(toggleBtn)
    toggleBtn.Size = UDim2.new(1, 0, 1, 0)
    toggleBtn.BackgroundTransparency = 1
    toggleBtn.Text = env.getIsDarkMode() and "Switch to Light Mode" or "Switch to Dark Mode"
    toggleBtn.Font = Enum.Font.GothamBold
    toggleBtn.TextSize = 16
    env.addThemedText(toggleBtn, C.indigo)
    toggleBtn.Parent = toggleBg
  
    toggleBtn.MouseButton1Click:Connect(function()
       local nextMode = not env.getIsDarkMode()
       env.applyTheme(nextMode)
       toggleBtn.Text = nextMode and "Switch to Light Mode" or "Switch to Dark Mode"
    end)
  end

return SettingsApp
