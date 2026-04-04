
import re

with open('src/client/PhoneModules/CloutApp.lua', 'r', encoding='utf-8') as f:
    text = f.read()

# Let's just find things that look like variables but are not defined as local

