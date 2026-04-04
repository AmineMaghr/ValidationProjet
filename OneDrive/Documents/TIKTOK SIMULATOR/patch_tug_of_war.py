import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace the click health gain (currently 6) to 5 for a tighter balance
text = re.sub(
    r'lfTotalHealth = math\.clamp\(lfTotalHealth \+ 6, 0, 100\)',
    r'lfTotalHealth = math.clamp(lfTotalHealth + 5, 0, 100)',
    text,
    count=1
)

# Extract old 1-second logic. We will replace it with a combination
# of a fast RenderStepped/0.05s loop for smooth draining and a 1s loop for the seconds & rewards.

old_timer_loop = r'''      -- Like Farm timer\n      task\.spawn\(function\(\)\n          while likeFarmMaster\.Parent do\n              task\.wait\(1\)\n              if not lfGameRunning then continue end\n                if lfGameTime > 0 and lfGameRunning then\n                  lfGameTime = lfGameTime - 1\n                  lfTimerLbl\.Text = lfGameTime \.\. "s"\n                  if lfGameTime <= 5 then\n                      lfTimerLbl\.TextColor3 = Color3\.fromRGB\(255, 50, 50\)\n                  elseif lfGameTime <= 10 then\n                      lfTimerLbl\.TextColor3 = Color3\.fromRGB\(255, 180, 50\)\n                  end\n\n                  -- Meter drain over time\n                  lfTotalHealth = math\.clamp\(lfTotalHealth - 15, 0, 100\)\n\n                  local pct = lfTotalHealth / 100\n                  TweenService:Create\(lfMeterFill, TweenInfo\.new\(0\.2\), \{Size = UDim2\.new\(pct, 0, 1, 0\)\}\):Play\(\)\n                  if pct > 0\.6 then\n                      lfMeterFill\.BackgroundColor3 = Color3\.fromRGB\(80, 220, 80\)\n                      lfCPSLbl\.TextColor3 = Color3\.fromRGB\(80, 220, 80\)\n                  elseif pct > 0\.3 then\n                      lfMeterFill\.BackgroundColor3 = Color3\.fromRGB\(255, 170, 50\)\n                      lfCPSLbl\.TextColor3 = Color3\.fromRGB\(255, 170, 50\)\n                  else\n                      lfMeterFill\.BackgroundColor3 = Color3\.fromRGB\(255, 60, 60\)\n                      lfCPSLbl\.TextColor3 = Color3\.fromRGB\(255, 60, 60\)\n                  end\n                  lfCPSLbl\.Text = "Meter: " \.\. math\.floor\(lfTotalHealth\) \.\. "%"\n\n                  -- Passive views gain based on meter\n                  if lfTotalHealth > 0 then\n                      local baseEarn = 30\n                      local mult = \(lfTotalHealth / 100\) \* 4 -- max x4 multiplier\n                      local viewsGained = math\.floor\(baseEarn \+ \(baseEarn \* mult\)\)\n                      lfTotalViews = lfTotalViews \+ viewsGained\n                      lfViewsLbl\.Text = lfFmt\(lfTotalViews\) \.\. " views"\n                  end\n\n              elseif lfGameTime <= 0 and lfGameRunning then\n                  lfEndGame\(\)\n              end\n          end\n      end\)'''

new_timer_loop = '''      -- Like Farm timer
      task.spawn(function()
          -- Fast smooth loop for meter drain (tug of war)
          task.spawn(function()
              while likeFarmMaster.Parent do
                  task.wait(0.05)
                  if not lfGameRunning then continue end
                  if lfGameTime > 0 then
                      -- Drain ~25 health per second (1.25 per tick)
                      lfTotalHealth = math.clamp(lfTotalHealth - 1.25, 0, 100)
                      
                      local pct = lfTotalHealth / 100
                      lfMeterFill.Size = UDim2.new(pct, 0, 1, 0)
                      
                      if pct > 0.6 then
                          lfMeterFill.BackgroundColor3 = Color3.fromRGB(80, 220, 80)
                          lfCPSLbl.TextColor3 = Color3.fromRGB(80, 220, 80)
                      elseif pct > 0.3 then
                          lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 170, 50)
                          lfCPSLbl.TextColor3 = Color3.fromRGB(255, 170, 50)
                      else
                          lfMeterFill.BackgroundColor3 = Color3.fromRGB(255, 60, 60)
                          lfCPSLbl.TextColor3 = Color3.fromRGB(255, 60, 60)
                      end
                      lfCPSLbl.Text = "Meter: " .. math.floor(lfTotalHealth) .. "%"
                  end
              end
          end)

          while likeFarmMaster.Parent do
              task.wait(1)
              if not lfGameRunning then continue end
              if lfGameTime > 0 and lfGameRunning then
                  lfGameTime = lfGameTime - 1
                  lfTimerLbl.Text = lfGameTime .. "s"
                  if lfGameTime <= 5 then
                      lfTimerLbl.TextColor3 = Color3.fromRGB(255, 50, 50)
                  elseif lfGameTime <= 10 then
                      lfTimerLbl.TextColor3 = Color3.fromRGB(255, 180, 50)
                  end

                  -- Passive views gain based on meter (once a second)
                  if lfTotalHealth > 0 then
                      local baseEarn = 30
                      local mult = (lfTotalHealth / 100) * 4 -- max x4 multiplier
                      local viewsGained = math.floor(baseEarn + (baseEarn * mult))
                      lfTotalViews = lfTotalViews + viewsGained
                      lfViewsLbl.Text = lfFmt(lfTotalViews) .. " views"
                  end

              elseif lfGameTime <= 0 and lfGameRunning then
                  lfEndGame()
              end
          end
      end)'''

text = re.sub(old_timer_loop, new_timer_loop, text, flags=re.MULTILINE)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Tug of war physics applied.")
