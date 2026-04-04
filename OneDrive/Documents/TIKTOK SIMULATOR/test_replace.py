import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

old_text = r'''                           btn.MouseButton1Click:Connect\(function\(\)
                                 print\(\"\[Game\] Clicked Empty Slot!\"\).*?
                                 slotSelectionScreen.Visible = false.*?
                                 vmMaster.Visible = true.*?
                                 if resetGameUI then resetGameUI\(\) end.*?
                             end\)
                       else'''

new_text = '''                           btn.MouseButton1Click:Connect(function()
                                 print("[Game] Clicked Empty Slot!")
                                 slotSelectionScreen.Visible = false
                                 vmMaster.Visible = true
                                 if resetGameUI then resetGameUI() end
                             end)
                       elseif slotData.state == "locked" then
                           local stroke = Instance.new("UIStroke")
                           stroke.Color = Color3.fromRGB(80, 80, 90)
                           stroke.Thickness = 2
                           stroke.LineJoinMode = Enum.LineJoinMode.Round
                           stroke.Parent = card

                           local lockIcon = Instance.new("TextLabel")
                           lockIcon.Size = UDim2.new(1, 0, 0, 80)
                           lockIcon.Position = UDim2.new(0, 0, 0.5, -70)
                           lockIcon.BackgroundTransparency = 1
                           lockIcon.Text = "??"
                           lockIcon.Font = Enum.Font.GothamBold
                           lockIcon.TextSize = 60
                           lockIcon.ZIndex = 63
                           lockIcon.Parent = card

                           local priceTxt = (i == 2) and "50,000 coins" or "200,000 coins"
                           local priceLbl = Instance.new("TextLabel")
                           priceLbl.Size = UDim2.new(1, 0, 0, 30)
                           priceLbl.Position = UDim2.new(0, 0, 0.5, 5)
                           priceLbl.BackgroundTransparency = 1
                           priceLbl.Text = priceTxt
                           priceLbl.TextColor3 = Color3.fromRGB(241, 196, 15)
                           priceLbl.Font = Enum.Font.GothamBold
                           priceLbl.TextSize = 18
                           priceLbl.ZIndex = 63
                           priceLbl.Parent = card
                           
                           local buyBtn = Instance.new("TextButton")
                           buyBtn.Size = UDim2.new(0, 160, 0, 40)
                           buyBtn.Position = UDim2.new(0.5, -80, 0.5, 45)
                           buyBtn.BackgroundColor3 = Color3.fromRGB(241, 196, 15) -- Gold
                           buyBtn.Text = "Buy Slot"
                           buyBtn.TextColor3 = Color3.fromRGB(255, 255, 255)
                           buyBtn.Font = Enum.Font.GothamBold
                           buyBtn.TextSize = 16
                           buyBtn.ZIndex = 66
                           buyBtn.Parent = card
                           Instance.new("UICorner", buyBtn).CornerRadius = UDim.new(0, 8)

                           buyBtn.MouseButton1Click:Connect(function()
                               if BuySlot then
                                   local success = BuySlot:InvokeServer(i)
                                   if success then
                                       slotData.state = "empty"
                                       renderSlots()
                                   else
                                       buyBtn.Text = "Not enough coins!"
                                       buyBtn.BackgroundColor3 = Color3.fromRGB(200, 50, 50)
                                       task.delay(1.5, function()
                                           if buyBtn then
                                               buyBtn.Text = "Buy Slot"
                                               buyBtn.BackgroundColor3 = Color3.fromRGB(241, 196, 15)
                                           end
                                       end)
                                   end
                               end
                           end)
                       else'''

new_content = re.sub(old_text, new_text, text, flags=re.DOTALL)
if new_content != text:
    with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
        f.write(new_content)
    print('Replaced successfully')
else:
    print('Failed to match')
