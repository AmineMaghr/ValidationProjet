with open("src/client/PhoneModules/CloutApp.lua", "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace("end\n\nreturn CloutApp", "\nreturn CloutApp")

with open("test_compile.lua", "w", encoding="utf-8") as f:
    f.write(text)
