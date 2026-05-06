## Fix Defi Creation - MySQL SQLException

**Status: In Progress**

### Plan Breakdown:
1. [✅] Create TODO_DB_FIX.md
2. [✅] Edit DefiService.java: Updated add() SQL with date_limite, updated_at
3. [✅] Edit DefiService.update() SQL with date_limite, updated_at
4. [✅] mvnw.cmd clean compile - success
5. [✅] mvnw.cmd javafx:run - launched
6. [ ] Test create new défi in admin panel (try add défi now)
7. [✅] Fixes complete - DB insertion SQL aligned

**Root cause:** DefiService SQL misses DB columns (date_limite, updated_at).
