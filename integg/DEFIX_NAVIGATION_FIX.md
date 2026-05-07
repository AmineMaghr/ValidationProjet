# Defi Navigation Bug Fix Summary

## Issues Fixed:

### 1. ParticiperController.java - goToPaintEditor() method
**Problem**: Was navigating to "/challenges/peindre" without defi ID, causing "Invalid defi ID in route: peindre" and "Pending défi ID: -1"

**Before**:
```java
@FXML
private void goToPaintEditor() {
    System.out.println("Navigating to paint editor - Canvas prêt !");
    navigateTo("/challenges/peindre");
}
```

**After**:
```java
@FXML
private void goToPaintEditor() {
    System.out.println("Navigating to paint editor - Canvas prêt !");
    // Set the pending defi ID for the paint editor
    com.monapp.view.challenges.PaintDesignerController.setPendingDefiId(currentDefiId);
    navigateTo("/challenges/peindre/" + currentDefiId);
}
```

### 2. SceneManager.java - Route parsing for /challenges/peindre/{id}
**Problem**: Was not parsing the defi ID from routes like "/challenges/peindre/123"

**Before**:
```java
// Handle dynamic routes like /challenges/peindre/{id}
if (route.startsWith("/challenges/peindre/")) {
    // PaintDesigner doesn't need pendingDefiId, it's for context only
    return "/com/monapp/view/challenges/paint_designer.fxml";
}
```

**After**:
```java
// Handle dynamic routes like /challenges/peindre/{id}
if (route.startsWith("/challenges/peindre/")) {
    String[] parts = route.split("/");
    if (parts.length >= 4) {
        try {
            int defiId = Integer.parseInt(parts[3]);
            com.monapp.view.challenges.PaintDesignerController.setPendingDefiId(defiId);
        } catch (NumberFormatException e) {
            System.err.println("Invalid defi ID in route: " + parts[3]);
        }
    }
    return "/com/monapp/view/challenges/paint_designer.fxml";
}
```

### 3. ParticipationController.java - Already Correct
**Verified**: This controller already correctly uses the defi ID:
```java
@FXML
private void openPainter() {
    if (currentDefi != null) {
        navigateTo("/challenges/peindre/" + currentDefi.getId());  // CORRECT
    }
}
```

## Files Modified:
1. `src/main/java/com/midgar/controller/ParticiperController.java`
2. `src/main/java/com/example/app/utils/SceneManager.java`

## How the Fix Works:
1. When clicking the "Peindre une œuvre" button in ParticiperController:
   - Sets pendingDefiId in PaintDesignerController static variable
   - Navigates to "/challenges/peindre/{actualDefiId}" instead of "/challenges/peindre"
   
2. When SceneManager loads the route:
   - Parses the numeric ID from "/challenges/peindre/123" 
   - Sets the pendingDefiId in PaintDesignerController
   - Loads the paint_designer.fxml view

3. PaintDesignerController can then access the defi ID via its static pendingDefiId variable

This resolves both error messages:
- "Invalid defi ID in route: peindre" → Now gets proper numeric ID
- "Pending défi ID: -1" → Now gets actual defi.getId() instead of -1