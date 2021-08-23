# google-map-note

Current Progress
1. user can create note whether at the current or selected location by the below methods: -
    - double click (single click to see note title)
    - click the upper left plus button
2. Actions in create note activity
    - save note -> store to the global array list & add the marker on Google Map
    - cancel -> the info of the marker is not stored & the users still have chance to change the selected location
2. For now, all the info (lat;lng) of markers are stored in global class (for future use in database references)
    - don't use them as ID (since the user can drag the marker to adjust the marker position)

===        
        
⚠️⚠️⚠️ ️Remaining Task ⚠️⚠️⚠️
1. CRUD for note at particular position in Google Maps
2. show all note in NotesActivity
3. design for 1st page (LoginActivity) and CreateNoteActivity

***

👍👍👍 Good Tutorial: 👍👍👍
1. change menu item text color // override options menu style
    - youtu.be/tugFulvCYIM
2. include drawer for all activity
    - stackoverflow.com/questions/28678151/android-navigation-drawer-on-all-activities
3. radius profile picture
    - youtu.be/PGijKuscSkk
        - library: github.com/hdodenhof/CircleImageView