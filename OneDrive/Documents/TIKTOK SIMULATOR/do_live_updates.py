import codecs

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with codecs.open(path, 'r', 'utf-8') as f:
    text = f.read()

# Make sure timerBg has a name
text = text.replace(
    'local timerBg = Instance.new("Frame")',
    'local timerBg = Instance.new("Frame")\n                       timerBg.Name = "TimerBg"'
)

# Insert the live loop
insertion = '''renderSlots()
           
           task.spawn(function()
               while slotSelectionScreen do
                   if slotSelectionScreen.Visible then
                       for i = 1, 3 do
                           local slotData = vmSlotsData[i]
                           if slotData.state == "filled" then
                               local card = slotsContainer:FindFirstChild("SlotCard_" .. i)
                               if card then
                                   local liveLbl = card:FindFirstChild("LiveLbl")
                                   if liveLbl then
                                       local tl = math.max(0, slotData.timeLeft)
                                       local m = math.floor(tl / 60)
                                       local s = math.floor(tl % 60)
                                       liveLbl.Text = string.format("? %d:%02d", m, s)
                                   end
                                   local tbg = card:FindFirstChild("TimerBg")
                                   if tbg then
                                       local fill = tbg:FindFirstChild("ProgFill")
                                       if fill then
                                           local pct = math.clamp(slotData.timeLeft / 180, 0, 1)
                                           fill.Size = UDim2.new(pct, 0, 1, 0)
                                       end
                                   end
                               end
                           end
                       end
                   end
                   task.wait(1)
               end
           end)
           
           print("[SlotSelection] Loaded")'''

text = text.replace('renderSlots()\n           print("[SlotSelection] Loaded")', insertion)

with codecs.open(path, 'w', 'utf-8') as f:
    f.write(text)

print('Live loop added!')
