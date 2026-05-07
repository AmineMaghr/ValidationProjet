# JavaFX Project - Technical Features Summary

## Overview
This JavaFX e-commerce project implements three advanced features: an AI-powered chatbot, automated shop event management, and stock prediction with risk analysis. These features are integrated into a shop backend interface for administrative management.

---

## 1. CHATBOT FEATURE (ChatbotService)

### Location
- **Service**: [com/example/app/services/ChatbotService.java](src/main/java/com/example/app/services/ChatbotService.java)
- **Controller**: [HeaderController.java](src/main/java/com/example/app/controllers/HeaderController.java)
- **ShopBackendController**: Uses chatbot for customer support

### What It Does
- Provides an interactive AI-powered customer support assistant integrated into the application
- Responds to customer inquiries about products, orders, shipping, and returns
- Falls back to rule-based local support when API is unavailable
- Runs asynchronously to prevent UI blocking

### Technical Implementation

#### AI Backend: Google Gemini API
```
Endpoint: https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent
Authentication: Environment variable GEMINI_API_KEY
Model: gemini-2.5-flash (latest, fast version)
```

#### Key Technologies
- **HTTP Client**: Java 11+ `HttpClient` with 20-second connection timeout
- **Concurrency**: JavaFX `Task<String>` for background execution
- **Text Parsing**: Regex pattern matching to extract JSON responses
- **JSON Processing**: Manual JSON building and parsing

#### Core Methods

1. **`chat(String message, Map<String, String> customerInfo): String`**
   - Main method for getting chatbot responses
   - Takes customer context (name, order number, issue type)
   - Returns AI-generated or locally-generated response
   - 30-second HTTP timeout with fallback
   - Exception handling for network/API failures

2. **`buildPrompt(String message, Map<String, String> customerInfo): String`**
   - Constructs context-aware prompt for Gemini
   - Includes: system instructions in French, customer context, product catalog (top 5 products), user message
   - Loads products from database to provide real product context
   - Optimized for short, useful responses

3. **`buildLocalSupportReply(String message, Map<String, String> customerInfo): String`**
   - Rule-based support when API unavailable
   - Pattern matching on keywords: commande, livraison, retour, stock, prix
   - Returns relevant help message in French
   - Acts as fallback for robustness

4. **`isConfigured(): boolean`**
   - Checks if GEMINI_API_KEY environment variable is set
   - Used for availability detection

5. **`healthCheck(): String`**
   - Returns status message: "Gemini chatbot prêt" or "Mode support local prêt"

#### JSON Processing
- **Escape function**: Handles JSON special characters for safe transmission
- **Unescape function**: Reverses JSON escaping for readability
- **Text Extraction**: Uses regex pattern `"text"\s*:\s*"(.*?)"` to extract response text from Gemini JSON

#### UI Integration (HeaderController)
- Opens as modal dialog with conversation history
- TextField for user input
- TextArea for conversation display
- Asynchronous message sending via Task/Thread
- Error handling with UI alerts

#### Configuration
- **Temperature**: 0.7 (balanced creativity vs determinism)
- **Max Output Tokens**: 512 (concise responses)
- **Request Timeout**: 30 seconds
- **Connection Timeout**: 20 seconds

---

## 2. AUTOMATION FEATURES (ShopAutomationEventService)

### Location
- **Service**: [com/example/app/services/ShopAutomationEventService.java](src/main/java/com/example/app/services/ShopAutomationEventService.java)
- **Entity**: [ShopAutomationEvent.java](src/main/java/com/example/app/entities/ShopAutomationEvent.java)
- **DAO**: [ShopAutomationEventDAO.java](src/main/java/com/example/app/dao/ShopAutomationEventDAO.java)
- **Controller**: [ShopBackendController.java](src/main/java/com/example/app/controllers/ShopBackendController.java) (lines 100, 363-410, 680-720)

### What It Does
- Creates automated alerts when inventory reaches critical levels
- Integrates with stock prediction service to recommend restock actions
- Logs all automation events to database for audit trail
- Provides historical view of automation actions

### Technical Implementation

#### Event Types
- `STOCK_ALERT`: Triggered when product stock falls below threshold
- `RESTOCK`: Recommended restocking action based on predictions
- `INFO`: Informational events (no predictions available, etc.)

#### Core Methods

1. **`generateStockAlerts(int threshold): List<ShopAutomationEvent>`**
   - Primary automation trigger
   - Two-phase alert generation:
     
     **Phase 1: Low Stock Alerts**
     ```java
     List<Produit> lowStockProducts = analyticsService.getLowStockProducts(threshold)
     ```
     - Retrieves products below stock threshold (default: 10)
     - Creates STOCK_ALERT for each with current stock info
     
     **Phase 2: Prediction-Based Restock Recommendations**
     ```java
     List<StockPrediction> criticalPredictions = stockPredictionService.findCriticalProducts(20)
     ```
     - Gets predictions where recommended_stock > current_stock
     - Creates RESTOCK events with specific recommendations
     - Format: "Réapprovisionnement recommandé pour [Product] (actuel: X, recommandé: Y)"

2. **`createEvent(String eventType, String description, String status): void`**
   - Persists automation event to database
   - Takes event type, human-readable description, and status (ACTIVE/INACTIVE)
   - Timestamps automatically set

3. **`findLatest(int limit): List<ShopAutomationEvent>`**
   - Retrieves most recent automation events
   - Ordered by creation timestamp (descending)
   - Default limit: 20 events

#### Data Persistence

**Database Schema:**
```sql
CREATE TABLE shop_automation_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50),           -- STOCK_ALERT, RESTOCK, INFO
    description VARCHAR(500),         -- Human-readable event description
    status VARCHAR(20),               -- ACTIVE, INACTIVE
    created_at TIMESTAMP              -- Auto-generated timestamp
)
```

#### Integration Flow

In ShopBackendController:
1. User clicks "Generate Automation Alerts" button
2. Calls `automationEventService.generateStockAlerts(10)`
3. Service analyzes:
   - Current stock vs threshold
   - Prediction recommendations vs current stock
4. Creates events and persists to database
5. UI ListView updated with latest 20 events
6. Display format: `[Date] EVENT_TYPE - Description (Status)`

#### UI Components (ShopBackendController)
- **ListView<ShopAutomationEvent>**: Displays automation events
- **Refresh mechanism**: `reloadAutomationEvents()` loads latest 20
- **Fallback**: If no events exist, generates alerts and displays results

---

## 3. STOCK PREDICTION & AI RISK ANALYSIS

### Location
- **Service**: [com/example/app/services/StockPredictionService.java](src/main/java/com/example/app/services/StockPredictionService.java)
- **Analytics**: [ShopAnalyticsService.java](src/main/java/com/example/app/services/ShopAnalyticsService.java)
- **Entity**: [StockPrediction.java](src/main/java/com/example/app/entities/StockPrediction.java)
- **DAO**: [StockPredictionDAO.java](src/main/java/com/example/app/dao/StockPredictionDAO.java)

### What It Does
- Predicts next day's product demand using linear regression
- Calculates optimal stock levels for 14-day supply
- Assigns confidence scores based on data volatility
- Identifies critical stock situations (Critique/Faible/Sain)
- Provides trend analysis and risk metrics

### Technical Implementation

#### Prediction Algorithm: Linear Regression

**Data Collection Phase:**
```java
List<Integer> dailyDemand = loadDailyDemand(productId, 30)
```
- Loads last 30 days of sales from `commande` table
- Groups by date: `DATE(date_commande)`
- Sums quantities per day: `SUM(quantite)`
- Returns day-by-day demand as integer list

**Linear Regression Calculation:**

```
For historical data: y = mx + b
Where:
  x = day index (1 to n)
  y = demand on that day
  
Formula:
  m (slope) = (n*Σ(xy) - Σx*Σy) / (n*Σ(x²) - (Σx)²)
  b (intercept) = (Σy - m*Σx) / n
  
Predicted next day = m*(n+1) + b
```

**Code Implementation:**
```java
double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
double intercept = (sumY - (slope * sumX)) / n;
double nextDayValue = (slope * (n + 1d)) + intercept;
return Math.max(0d, nextDayValue);
```

#### Stock Level Recommendation

```java
int recommendedStock = (int) Math.ceil(Math.max(predictedDailyDemand, 0d) * 14d)
```
- 14-day supply buffer
- Based on predicted daily demand
- Ceiling rounding to ensure minimum coverage

#### Confidence Scoring Algorithm

**Components:**
1. **Volatility Penalty**: Based on variance in demand
   ```java
   double variance = Σ(value - average)² / count
   double volatilityPenalty = Math.min(40d, Math.sqrt(variance) * 5d)
   ```
   - Cap: 40% penalty max
   - Factor: 5x standard deviation

2. **Data Quantity Penalty**: When insufficient data points
   ```java
   double dataPenalty = (count of days with sales < 5) ? 25d : 0d
   ```
   - 25% penalty if fewer than 5 sales days

3. **Low Demand Penalty**: When predicted demand ≤ 0
   ```java
   if (predictedDailyDemand <= 0d) {
       confidence = Math.max(15d, confidence - 20d)
   }
   ```
   - Additional 20% penalty for no-demand items
   - Minimum 15% confidence floor

4. **Final Calculation:**
   ```java
   double confidence = 100d - volatilityPenalty - dataPenalty
   return Math.max(5d, Math.min(100d, confidence))  // Clamp: 5-100%
   ```

#### Key Methods

1. **`predictProductStock(Produit product): StockPrediction`**
   - Core prediction for single product
   - Returns StockPrediction entity with all metrics

2. **`predictAllProducts(): List<StockPrediction>`**
   - Predicts for all products in catalog
   - Persists predictions to database
   - Handles null/missing products gracefully

3. **`findLatestPredictions(int limit): List<StockPrediction>`**
   - Retrieves most recent predictions
   - Default: 20 predictions

4. **`findCriticalProducts(int limit): List<StockPrediction>`**
   - Returns products needing restock
   - Filters: `recommended_stock > current_stock`
   - Used by automation to trigger alerts

5. **`refreshPredictions(): List<StockPrediction>`**
   - Recalculates all predictions
   - Falls back to demo data if no sales data

6. **`cleanupOldPredictions(int daysToKeep)`**
   - Maintenance: removes predictions older than N days

#### Data Persistence

**Database Schema:**
```sql
CREATE TABLE stock_prediction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    predicted_demand DOUBLE NOT NULL,         -- Linear regression forecast
    current_stock INT NOT NULL,               -- Actual stock now
    recommended_stock INT NOT NULL,           -- 14-day supply target
    confidence DOUBLE NOT NULL,               -- 5-100% confidence score
    created_at TIMESTAMP NOT NULL             -- Prediction timestamp
)
```

### Risk Analysis Integration (ShopAnalyticsService)

#### Stock Risk Classification

```java
if (currentStock <= 3)
    stockRisk = "Critique"      // Red: Immediate restocking needed
else if (currentStock <= 10)
    stockRisk = "Faible"        // Yellow: Low stock warning
else
    stockRisk = "Sain"          // Green: Healthy stock level
```

#### ProductPerformanceRow Data Structure
Combines multiple analytics:
- **Product demand**: Total units sold in period
- **Estimated margin**: 25% of revenue (assumed markup)
- **Stock risk**: Classification from above
- **Current stock**: Actual inventory

#### Analytics Metrics
1. **Sales Last 30 Days**: Sum of `prix_total` from orders
2. **Order Count**: Number of transactions
3. **Growth Percentage**: (Sales_now - Sales_past) / Sales_past
4. **Average Basket Value**: Total sales / order count
5. **Daily Sales Last 7 Days**: Time-series data for trends
6. **Customer Segments**: VIP (≥5 orders), Regular (≥2), Occasional

#### Low Stock Products Query
```sql
SELECT * FROM produit WHERE quantite_disponible <= threshold
ORDER BY quantite_disponible ASC
```

---

## 4. UI/CONTROLLER INTEGRATION

### ShopBackendController (Main Integration Point)

**Initialization (line 120):**
```java
reloadPredictions()      // Loads latest 20 predictions
reloadAutomationEvents() // Loads latest 20 events
```

**User Actions:**
1. **"Refresh Insights"** → Calls all reload methods
2. **"Generate Predictions"** → Triggers `stockPredictionService.refreshPredictions()`
3. **"Generate Automation Alerts"** → Triggers `automationEventService.generateStockAlerts(10)`

**Data Display:**
- **Predictions ListView**: Displays StockPrediction with custom cell factory
  - Format: "{ProductName}: Demande=X, Stock=Y, Recommandé=Z (C% confiance)"
- **Automation Events ListView**: Displays ShopAutomationEvent in chronological order
- **Performance Table**: Shows ProductPerformanceRow with demand/margin/risk/stock columns

**Error Handling:**
- SQLException caught and displayed via `showError()` dialog
- Fallback demo data generated if no real data available

---

## 5. ARCHITECTURE PATTERNS

### Service Layer Pattern
```
Entity ← DAO ← Service ← Controller ← UI
```
- Separation of concerns
- Database abstraction via DAO
- Business logic in services
- UI coordination in controllers

### Data Flow: Prediction & Automation
```
[Product Sales Data] 
    ↓
[StockPredictionService.loadDailyDemand()]
    ↓
[Linear Regression Analysis]
    ↓
[StockPrediction Entity + Confidence Score]
    ↓
[ShopAutomationEventService.generateStockAlerts()]
    ↓
[ShopAutomationEvent Created]
    ↓
[Database Persisted + UI Updated]
```

### Fallback Strategies
1. **Chatbot**: Gemini API → Local rule-based support
2. **Predictions**: Calculated predictions → Demo data
3. **Automation**: Real data → "No alerts" informational event
4. **Database**: Active connection → Exception handling

---

## 6. TECHNOLOGIES & DEPENDENCIES

### External APIs
- **Google Gemini 2.5 Flash API**: AI chat responses

### Java Libraries
- **JavaFX**: UI framework (TableView, ListView, Dialog, Task)
- **Java 11+ HttpClient**: HTTP requests with timeouts
- **JDBC**: MySQL database connectivity
- **Java Streams**: Data filtering/sorting

### Database
- **MySQL**: Persistence for predictions, events, orders, products
- **SQL Timestamps**: Automatic UTC handling

### Concurrency
- **JavaFX Task**: Background computation
- **Thread**: Daemon threads for async chatbot

---

## 7. KEY PERFORMANCE CONSIDERATIONS

### Linear Regression Complexity
- **Time**: O(n) where n=30 (daily demand lookback)
- **Space**: O(n) for demand array
- **Per product**: ~5-10ms

### Confidence Score Calculation
- Volatility computation: O(n) single pass with variance
- Overall: O(n) for all products

### Database Queries
- **Critical optimizations:**
  - Indexes on: `produit_id`, `date_commande` in commande table
  - Date grouping with `DATE()` function
  - Aggregate functions: `SUM()`, `COUNT()`, `COALESCE()`

### Scaling Considerations
- Prediction refresh: Scales with product count (~100 products = 1s)
- Storage: 1 prediction per product per refresh
- Cleanup: Removes old predictions to maintain performance

---

## 8. RISK & LIMITATIONS

### Chatbot
- **API Dependency**: Requires GEMINI_API_KEY and internet connectivity
- **Cost**: Gemini API has usage limits/costs
- **Latency**: 30-second timeout could block user briefly
- **Context Loss**: Each message independent (no conversation history stored)

### Stock Prediction
- **Historical Data Assumption**: Assumes sales pattern continues
- **Seasonality Not Considered**: No adjustment for seasonal demand
- **Zero-Demand Issue**: Assumes minimum 5 days sales data
- **Linear Model Limitation**: Cannot handle exponential growth/decline
- **External Factors**: Ignores marketing campaigns, supply disruptions

### Automation
- **Threshold Hardcoded**: Stock alert threshold=10, automation limit=20
- **No Scheduling**: Events generated on-demand, not periodic
- **No Actions**: Events logged but no automatic actions taken (no email, SMS)
- **False Positives**: May alert before actual stock-out

---

## 9. DEMO MODE

When no real data available:
```java
List<StockPrediction> buildDemoPredictions() {
    // Creates demo predictions if DB empty
    demo.add(createDemoPrediction(0, "Produit A", 3.2, 24, 42.5));
    demo.add(createDemoPrediction(0, "Produit B", 1.4, 8, 58.0));
    demo.add(createDemoPrediction(0, "Produit C", 0.6, 2, 71.0));
}
```
- Enables feature testing without historical sales data
- Demo data has realistic variance in predictions

---

## 10. SUMMARY TABLE

| Feature | Algorithm | Input | Output | Storage |
|---------|-----------|-------|--------|---------|
| **Chatbot** | LLM (Gemini) + Regex | User query + Context | Text response | None (logs events) |
| **Stock Prediction** | Linear Regression | 30-day sales history | Demand forecast + Confidence | `stock_prediction` table |
| **Automation** | Threshold + Comparison | Current stock + Predictions | Alert events | `shop_automation_event` table |
| **Risk Analysis** | Thresholds | Product inventory | Risk classification | None (computed) |

---

## 11. ENTRY POINTS FOR EXTENSION

1. **Better ML**: Replace linear regression with ARIMA, Prophet, or ML models
2. **Historical Chatbot**: Store conversation history for context awareness
3. **Scheduled Automation**: Add cron/timer for automatic alerts
4. **Predictive Actions**: Auto-generate purchase orders from predictions
5. **Advanced Risk**: Add seasonality, trend, anomaly detection
6. **Multi-language**: Make chatbot language-agnostic
7. **Custom Thresholds**: Admin-configurable alert thresholds
