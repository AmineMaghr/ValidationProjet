import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# remove block comments
text = re.sub(r'--\[\[.*?\]\]', '', text, flags=re.DOTALL)
lines = text.split('\n')

indent = 0
for i, line in enumerate(lines):
    # remove strings
    line = re.sub(r'"(?:\\.|[^"\\])*"', '""', line)
    line = re.sub(r"'(?:\\.|[^'\\])*'", "''", line)
    line = re.sub(r'--.*', '', line)
    line = line.strip()
    
    if not line: continue
    
    opens = len(re.findall(r'\b(if|function|do)\b', line))
    # single line if ... then ... end
    if re.search(r'\bif\b.*\bthen\b.*\bend\b', line):
        opens -= 1
        ends = 0
    else:
        ends = len(re.findall(r'\bend\b', line))
    
    if ends > 0:
        indent -= ends
        
    print(f"{i+1:4} | {indent:2} | {line[:60]}")
    
    if opens > 0:
        indent += opens

