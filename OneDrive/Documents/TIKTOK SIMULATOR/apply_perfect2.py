import re
import traceback

try:
    path = r'c:\Users\feral\OneDrive\Documents\TIKTOK SIMULATOR\src\client\PCOS.client.lua'
    with open(path, 'r', encoding='utf-8') as f:
        text = f.read()

    # 1. Clean duplicated resetGameUI just in case (the whole function block)
    matches = list(re.finditer(r'(resetGameUI = function\(\).*?minigameStarted = true\n\s*end)', text, re.DOTALL))
    if len(matches) > 1:
        # keep the first one, delete the second one
        text = text[:matches[1].start()] + text[matches[1].end():]
        print("Removed duplicate resetGameUI block.")

    # 2. Add TimerBg name to timer background so we can find it
    if 'timerBg.Name = "TimerBg"' not in text:
        text = text.replace('local timerBg = Instance.new("Frame")', 'local timerBg = Instance.new("Frame")\n                       timerBg.Name = "TimerBg"')

    # 3. Add dynamic loop to update the slots progress bar so it drains!
    render_slots_call = 'renderSlots()\n          print("[SlotSelection] Loaded")'
    if render_slots_call in text and 'local function drainLoop' not in text:
        drain_loop = '''renderSlots()
          print("[SlotSelection] Loaded")

          -- Custom loop to drain those slot wait-timers!
          local function drainLoop()
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
          end
          task.spawn(drainLoop)
'''
        text = text.replace(render_slots_call, drain_loop)

        print("Added new progress bar drain physics!")

    with open(path, 'w', encoding='utf-8') as f:
        f.write(text)
    print("Fixes applied successfully.")

except Exception as e:
    print("Error doing replaces:", e)
    traceback.print_exc()
