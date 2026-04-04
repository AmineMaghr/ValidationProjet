import os

with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

# Make sure we only replace within the contentTypes block if there are multiple occurrences.
# Looking at the codebase, it's very likely these phrases only occur here.

text = text.replace('hint = "Easy to learn"', 'hint = "Starter content"')
text = text.replace('hint = "3-beat rhythm"', 'hint = "Unlock at 1K followers"')
text = text.replace('hint = "Hold to record"', 'hint = "Unlock at 5K followers"')
text = text.replace('hint = "Rapid tapping"', 'hint = "Unlock at 10K followers"')
text = text.replace('hint = "Cinematic timing"', 'hint = "Unlock at 50K followers"')
text = text.replace('hint = "Master level"', 'hint = "Unlock at 100K followers"')

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)
print("Updated successfully.")
