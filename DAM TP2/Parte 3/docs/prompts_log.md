Read all documentation inside /docs and follow agents.md rules.

Start implementing Step 1 and Step 2 from the implementation plan.

Create:
- Android project setup (Kotlin + XML Views)
- Add INTERNET permission in AndroidManifest

Do not generate large files.
Explain what you are doing.

Continue following the implementation plan.

Implement Step 3:
Create the data model ImageItem based on docs/04_data_model.md.

Keep the code simple and clean.

Continue following the implementation plan.

Implement Step 3:
Create the data model ImageItem based on docs/04_data_model.md.

Keep the code simple and clean.

Continue following the implementation plan.

Implement Step 3:
Create the data model ImageItem based on docs/04_data_model.md.

Keep the code simple and clean.

Continue following the implementation plan.

Implement Step 4:
Create an API service to fetch images from Unsplash.

Use:
- HTTP request
- Gson or similar for parsing

Do not implement UI yet.

Implement Step 5:

Create a Repository class that:
- Calls the API service
- Returns image data

Follow MVVM architecture strictly.

Implement Step 6:

Create a ViewModel that:
- Uses LiveData
- Calls the Repository
- Exposes image list to the UI

Keep logic inside ViewModel (not Activity).


Implement Step 7:

Create activity_main.xml with:
- RecyclerView (full screen)
- SwipeRefreshLayout
- ProgressBar

Each item should occupy full screen height.




Implement Step 8:

Create RecyclerView Adapter:
- Full screen image per item
- Use ImageView
- Load images from URL

Keep it simple and readable.
Implement Step 9:

Connect ViewModel to MainActivity:
- Observe LiveData
- Update RecyclerView

Follow MVVM correctly (no business logic in Activity).

Implement Step 10:

Make RecyclerView behave like TikTok:
- Vertical scrolling
- One image per screen
- Snap to each item

Use appropriate LayoutManager or SnapHelper.

Implement Step 11:

Add swipe-to-refresh:
- Pull down loads new images
- Connect to ViewModel


Implement Step 12 and Step 13:

- Show ProgressBar while loading 
- Handle API errors gracefully

Do not crash the app.