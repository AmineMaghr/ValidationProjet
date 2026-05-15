================================================================================
                    MIDGAR - UNIVERSE & PERSONNAGE FEATURES
                              Developer: Amrou
================================================================================

UNIVERSE MODULE
---------------
- View all universes (list with search and filters)
- Universe detail page (banner, description, themes, YouTube embed)
- Create a universe
- Edit a universe (creator or admin only)
- Delete a universe (creator or admin only)
- Universes linked to the logged-in user who created them
- Create button hidden unless user is logged in

PERSONNAGE MODULE
-----------------
- View all personnages (list with search)
- Personnage detail page (portrait, stats, lore, abilities)
- Create a personnage (with stat sliders and image upload)
- Edit a personnage (creator or admin only)
- Delete a personnage (creator or admin only)
- Re-roll stats "Relancer les Stats" (creator or admin only)
- Save re-rolled stats to database
- Export personnage as JSON to clipboard
- Battle Simulator (pick 2 personnages and simulate a fight)
- Personnages linked to the logged-in user who created them
- Create button hidden unless user is logged in

ADMIN BACKOFFICE
----------------
- Back-Office button in header (visible to admins only)
- Full admin dashboard with tabs for:
    * Users      (block/unblock, promote to admin, delete)
    * Oeuvres    (search, delete)
    * Artefacts  (search, delete)
    * Univers    (list, delete)
    * Personnages (list, delete)
- Non-admins are redirected to home if they try to access admin pages

SESSION & SECURITY
------------------
- Header shows Login + Register when not logged in
- Header shows Profile button with username when logged in
- Header shows Back-Office button only for admins
- Edit, Delete, and Re-roll buttons hidden for non-creators
- Only the creator or an admin can modify or delete their content

NAVIGATION
----------
- All navbar buttons across the entire app correctly route to the right pages
  (fixed for: Oeuvre page, Artefact page, Challenges page, Profile page)

================================================================================
