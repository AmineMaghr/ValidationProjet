import sys
import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# remove comments
text = re.sub(r'--\[\[.*?\]\]', '', text, flags=re.DOTALL)
text = re.sub(r'--.*', '', text)
# remove strings
text = re.sub(r'"(?:\\.|[^"\\])*"', '""', text)
text = re.sub(r"'(?:\\.|[^'\\])*'", "''", text)

lines = text.split('\n')

stack = []
for i, line in enumerate(lines):
    words = re.findall(r'\b(if|function|do|end)\b', line)
    for w in words:
        if w in ['if', 'function', 'do']:
            stack.append((w, i + 1))
        elif w == 'end':
            if stack:
                stack.pop()
            else:
                print(f"Excess end at line {i+1}")

print("Remaining open blocks:")
for item in stack:
    print(item)
