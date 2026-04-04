import re

path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Fix the question mark
text = text.replace('live.Text = string.format("? %d:%02d", m, s)', 'live.Text = string.format("▶ %d:%02d", m, s)')

# 2. Update drain loop
old_drain_loop = '''          local function drainLoop()
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
                          end
                      end
                  end
              end
          end'''

new_drain_loop = '''          local function drainLoop()
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
          end'''

# Just to be safe, if that old block is spaced slightly differently:
text = re.sub(r'local function drainLoop\(\).*?end\n.*?end\n.*?end\n.*?end\n.*?end\n.*?end\n.*?end', new_drain_loop, text, flags=re.DOTALL)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print("Updated both the ?? and the drain loop!")
