import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

def replace_buy_btn():
    global text
    old = '''                            buyBtn.MouseButton1Click:Connect(function()
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
                            end)'''

    new = '''                            buyBtn.MouseButton1Click:Connect(function()
                                if BuySlot then
                                    print("[Slots] Attempting to buy slot " .. tostring(i))
                                    print("[Slots] Calling BuySlot RemoteFunction")
                                    local success = BuySlot:InvokeServer(i)
                                    if success then
                                        print("[Slots] Slot " .. tostring(i) .. " unlocked!")
                                        slotData.state = "empty"
                                        renderSlots()
                                    else
                                        print("[Slots] Purchase failed - not enough coins")
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
                            end)'''
    text = text.replace(old, new)

replace_buy_btn()

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Updated VideoManager.lua")
