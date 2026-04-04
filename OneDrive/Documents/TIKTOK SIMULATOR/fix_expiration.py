import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

old_loop = """          local function drainLoop()
              while slotsContainer.Parent do
                  task.wait(0.2)
                  for i = 1, 3 do
                      local st = vmSlotsData[i]
                      if st.state == "filled" then
                          local card = slotsContainer:FindFirstChild("SlotCard_" .. i)
                          if card then
                              local tBg = card:FindFirstChild("TimerBg")
                              if tBg then
                                  local pFill = tBg:FindFirstChild("ProgFill")
                                  if pFill then
                                      local pct = math.clamp(st.timeLeft / 180, 0, 1)
                                      pFill.Size = UDim2.new(pct, 0, 1, 0)
                                  end
                              end
                              local liveLbl = card:FindFirstChild("LiveLbl")
                              if liveLbl then
                                  local m = math.floor(st.timeLeft / 60)
                                  local s = math.floor(st.timeLeft % 60)
                                  liveLbl.Text = string.format("▶ %d:%02d", m, s)
                              end
                          end
                      end
                  end
              end
          end"""

new_loop = """          local function drainLoop()
              while slotsContainer.Parent do
                  task.wait(0.2)
                  local needsRender = false
                  for i = 1, 3 do
                      local st = vmSlotsData[i]
                      local card = slotsContainer:FindFirstChild("SlotCard_" .. i)
                      if st.state == "filled" then
                          if card then
                              local tBg = card:FindFirstChild("TimerBg")
                              if tBg then
                                  local pFill = tBg:FindFirstChild("ProgFill")
                                  if pFill then
                                      local pct = math.clamp(st.timeLeft / 180, 0, 1)
                                      pFill.Size = UDim2.new(pct, 0, 1, 0)
                                  end
                              end
                              local liveLbl = card:FindFirstChild("LiveLbl")
                              if liveLbl then
                                  local m = math.floor(st.timeLeft / 60)
                                  local s = math.floor(st.timeLeft % 60)
                                  liveLbl.Text = string.format("▶ %d:%02d", m, s)
                              end
                          end
                      else
                          if card and card:FindFirstChild("LiveLbl") then
                              needsRender = true
                          end
                      end
                  end
                  if needsRender then
                      renderSlots()
                  end
              end
          end"""

if old_loop in text:
    text = text.replace(old_loop, new_loop)
    print("Replaced exactly!")
else:
    # Use regex if spacing varies
    pattern = r'local function drainLoop\(\).*?end\n.*?end\n.*?end\n.*?end\n.*?end\n.*?end\n.*?end'
    text = re.sub(pattern, new_loop, text, flags=re.DOTALL)
    print("Replaced via regex!")

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
