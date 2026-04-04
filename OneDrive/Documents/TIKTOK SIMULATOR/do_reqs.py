with open("src/client/PhoneUI.client.lua", "r", encoding="utf-8") as f:
    text = f.read()

r0 = '{ name = "Short Videos",  color = C.indigo, emoji = "📱", hint = "Starter content", rhythm = "Tap once on the beat", req = 0 }'
r1 = '{ name = "Vlogs",         color = C.amber,  emoji = "🎥", hint = "Unlock at 1K followers", rhythm = "Tap 3 times: fast → fast → slow", req = 1000 }'
r2 = '{ name = "Podcasts",      color = C.green,  emoji = "🎙️", hint = "Unlock at 5K followers", rhythm = "Hold the button for 2 seconds", req = 5000 }'
r3 = '{ name = "Livestreams",   color = C.red,    emoji = "📡", hint = "Unlock at 10K followers", rhythm = "Tap 5 times rapidly", req = 10000 }'
r4 = '{ name = "Movies",        color = C.pink,   emoji = "🎬", hint = "Unlock at 50K followers", rhythm = "4 beats with a pause in the middle", req = 50000 }'
r5 = '{ name = "Stadium Tours", color = C.cyan,   emoji = "🏟️", hint = "Unlock at 100K followers", rhythm = "6-beat pattern — changes every post", req = 100000 }'

text = text.replace('{ name = "Short Videos",  color = C.indigo, emoji = "📱", hint = "Starter content", rhythm = "Tap once on the beat" }', r0)
text = text.replace('{ name = "Vlogs",         color = C.amber,  emoji = "🎥", hint = "Unlock at 1K followers", rhythm = "Tap 3 times: fast → fast → slow" }', r1)
text = text.replace('{ name = "Podcasts",      color = C.green,  emoji = "🎙️", hint = "Unlock at 5K followers", rhythm = "Hold the button for 2 seconds" }', r2)
text = text.replace('{ name = "Livestreams",   color = C.red,    emoji = "📡", hint = "Unlock at 10K followers", rhythm = "Tap 5 times rapidly" }', r3)
text = text.replace('{ name = "Movies",        color = C.pink,   emoji = "🎬", hint = "Unlock at 50K followers", rhythm = "4 beats with a pause in the middle" }', r4)
text = text.replace('{ name = "Stadium Tours", color = C.cyan,   emoji = "🏟️", hint = "Unlock at 100K followers", rhythm = "6-beat pattern — changes every post" }', r5)

with open("src/client/PhoneUI.client.lua", "w", encoding="utf-8") as f:
    f.write(text)

print("Updated contentTypes with requirements")