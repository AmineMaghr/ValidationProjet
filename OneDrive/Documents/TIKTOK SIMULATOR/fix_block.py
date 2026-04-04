with open("block2.txt", "r", encoding="utf-8") as f:
    text = f.read()

import re
text = re.sub(r'env\.', '', text)
text = re.sub(r'^\s\s', '', text, flags=re.MULTILINE) # remove 2 space indent

with open("block2.txt", "w", encoding="utf-8") as f:
    f.write(text)
