import re

with open('src/client/PCModules/VideoManager.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Modify the loop to include the fast background drain
text = re.sub(
    r'(-- Like Farm timer\s+task\.spawn\(function\(\)\s+)(while likeFarmMaster\.Parent do)',
    r'''\g<1>-- Fast smooth loop for meter drain (tug of war)
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

          \g<2>''',
    text
)

# 2. Rip out the old 1-second block
text = re.sub(
    r'(\s+-- Meter drain over time.*?lfCPSLbl\.Text = "Meter: " \.\. math\.floor\(lfTotalHealth\) \.\. "%")',
    '',
    text,
    flags=re.DOTALL
)

# 3. Change click health slightly to balance
text = text.replace('lfTotalHealth + 6, 0, 100', 'lfTotalHealth + 5, 0, 100')

with open('src/client/PCModules/VideoManager.lua', 'w', encoding='utf-8') as f:
    f.write(text)

