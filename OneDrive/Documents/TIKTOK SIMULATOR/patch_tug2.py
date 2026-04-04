import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Let's replace by chunk, finding the math.clamp for drain and extracting it
old_drain = r'''                  -- Meter drain over time
                  lfTotalHealth = math.clamp\(lfTotalHealth - 15, 0, 100\)

                  local pct = lfTotalHealth / 100
                  TweenService:Create\(lfMeterFill, TweenInfo.new\(0.2\), \{Size = UDim2.new\(pct, 0, 1, 0\)\}\):Play\(\)
                  if pct > 0.6 then
                      lfMeterFill.BackgroundColor3 = Color3.fromRGB\(80, 220, 80\)
                      lfCPSLbl.TextColor3 = Color3.fromRGB\(80, 220, 80\)
                  elseif pct > 0.3 then
                      lfMeterFill.BackgroundColor3 = Color3.fromRGB\(255, 170, 50\)
                      lfCPSLbl.TextColor3 = Color3.fromRGB\(255, 170, 50\)
                  else
                      lfMeterFill.BackgroundColor3 = Color3.fromRGB\(255, 60, 60\)
                      lfCPSLbl.TextColor3 = Color3.fromRGB\(255, 60, 60\)
                  end
                  lfCPSLbl.Text = "Meter: " .. math.floor\(lfTotalHealth\) .. "%"'''

text = re.sub(old_drain, "", text)

# Now inject our fast loop into the start of the timer
start_target = r'      -- Like Farm timer\n      task.spawn\(function\(\)\n          while likeFarmMaster.Parent do'

new_start = '''      -- Like Farm timer
      task.spawn(function()
          -- Fast smooth loop for meter drain (tug of war)
          task.spawn(function()
              while likeFarmMaster.Parent do
                  task.wait(0.05)
                  if not lfGameRunning then continue end
                  if lfGameTime > 0 then
                      -- Drain ~22.5 health per second (1.125 per tick)
                      lfTotalHealth = math.clamp(lfTotalHealth - 1.125, 0, 100)
                      
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

          while likeFarmMaster.Parent do'''

text = re.sub(start_target, new_start, text)

# Reduce click strength slightly
text = re.sub(
    r'lfTotalHealth = math.clamp\(lfTotalHealth \+ 6, 0, 100\)',
    r'lfTotalHealth = math.clamp(lfTotalHealth + 5, 0, 100)',
    text,
    count=1
)

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

print("Tug of war physics applied.")
