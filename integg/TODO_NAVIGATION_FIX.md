# Navigation Fix - JavaFX Standard (No Routes)

## Information Gathered
- DefiController : createDefiCard() → navigateTo("/challenges/" + defi.getId()) 
- SceneManager : Parse route → pendingDefiId → ParticiperController.initialize()
- User veut : **FXMLLoader.getController() + setDefi(Defi object)**

## Plan
1. **DefiController** : New method `openParticiper(Defi defi)`
2. **ParticiperController** : `private Defi currentDefi; public void setDefi(Defi defi)`
3. **Supprimer** : pendingDefiId, route parsing, SceneManager ID logic
4. **goToPaintEditor** : Direct `openPaintEditor(currentDefi)`

## Files
| File | Type | Changes |
|------|------|---------|
| DefiController.java | Edit | Add `openParticiper(Defi)` → loader.getController().setDefi()
| ParticiperController.java | Edit | Add `setDefi(Defi)` → remove pendingDefiId
| SceneManager.java | Edit | Remove defi ID parsing 
| PaintDesignerController.java | Edit | Add `setDefi(Defi)`

## Followup
1. Edits → mvn clean compile
2. Test : Défis → Participer → Peindre

<ask_followup_question>Plan OK ? Proceed ?</ask_followup_question>

