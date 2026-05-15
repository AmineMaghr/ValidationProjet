========================================================================
             RECOMMENDATION ENGINE & FYP ARCHITECTURE
========================================================================

1. ARCHITECTURE OVERVIEW
------------------------------------------------------------------------
This document outlines the business logic and data flow for the personalized
"For You Page" (FYP) recommendation system. The architecture relies on an
initial profiling step (Admin-curated Quiz) and explicit user preferences,
which are then processed by a ranking algorithm to serve highly relevant
posts and notifications.

Conceptually, the flow operates in four main phases:
 [ Configuration ] -> [ User Onboarding ] -> [ FYP Generation ] -> [ Explainability & Alerts ]

2. COMPONENT BREAKDOWN
------------------------------------------------------------------------

A. The Profiling System (Quiz & Preferences)
   - QuestionController & ReponseController:
     Allow administrators to create onboarding quizzes. Each question and
     associated response option is mapped to specific content tags, metadata,
     or user vectors.
   - ReponseService:
     Stores the user's selected answers during onboarding, forming the
     base of their "Interest Profile".
   - AdvancedPreferencesController & AdvancedPreferencesService:
     Manages explicit user settings (e.g., Dark Fantasy, Epic, Magic, Politics).
     Allows users to input specific custom tags and affinity levels to fine-tune
     their profile beyond the initial quiz.

B. The Recommendation Engine (FYP Algorithm)
   - DiscoverService:
     The orchestration layer. It fetches the user's complete profile (Quiz
     Answers + Advanced Preferences) and retrieves a pool of eligible posts
     from the database.
   - RankedPostService:
     The scoring engine. It compares post metadata (tags, categories) against
     the user's interest matrix. Posts are dynamically ranked using a weighted
     scoring system (e.g., matching a direct custom tag yields more points than
     matching a general response option). The highest-scoring posts are served
     first on the user's FYP.

C. Explainability & AI Transparency (The "Why am I seeing this?" Feature)
   - WhyService & WhyResultService:
     Modern platforms prioritize algorithm transparency. When a user interacts
     with a post and asks "Why is this on my FYP?", this service reverse-engineers
     the RankedPostService logic. It traces back the correlation between the post's
     tags and the specific Quiz Answer or Advanced Preference the user provided.

     AI/NLG Integration: In production-grade apps, this service utilizes
     Generative AI (Natural Language Generation) to translate raw correlation
     metrics into human-readable explanations. Instead of rigid debug text,
     the AI dynamically generates conversational insights (e.g., "We showed you
     this because you told us you like 'Medieval Politics' and recently interacted
     with fantasy lore").

D. Engagement & Retention (Push)
   - PostNotificationService:
     Listens to the creation of new posts. If a newly created post achieves an
     extraordinarily high affinity score for a specific user cluster (based
     on their quiz/preferences), this service dispatches targeted notifications
     to bring users back to the app.

3. DATA FLOW EXAMPLE
------------------------------------------------------------------------
1. Admin creates Question: "What is your favorite setting?" -> Options: [Sci-Fi], [Medieval].
2. User selects [Medieval]. ReponseService maps user ID to the 'Medieval' tag vector.
3. User visits Advanced Preferences and selects 'Magic' and 'War'.
4. User opens the App (Discover page).
5. DiscoverService fetches 50 recent posts.
6. RankedPostService applies a dynamic vector-matching algorithm, computing a high-dimensional relevance score that prioritizes [Medieval, Magic] while decaying unrelated clusters.
7. The FYP feed is dynamically rendered, surfacing the highly correlated [Medieval, Magic] content at the apex of the viewport to maximize engagement probability.
8. PostNotificationService might email the user later if an "Epic Medieval War" post drops.

========================================================================
