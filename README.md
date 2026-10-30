# Notely

An Android productivity app (Notes, Checklists, Reminders) built with Kotlin, Room and MVVM.
Planned headline feature: on-device OCR scan-to-note (CameraX + ML Kit).

**Status: in development.** This is a portfolio and learning project.

## What works so far
- Room database with 5 entities (Category, Note, Checklist, ChecklistItem, Reminder)
- Foreign-key delete rules: SET_NULL (notes and checklists keep existing when a category is deleted) and CASCADE (deleting a checklist deletes its items)
- Aggregate query for checklist progress ("x of y done") using LEFT JOIN
- 12 instrumented tests covering all DAOs and the progress query
- Notes list backed by real Room data through a ViewModel and LiveData (RecyclerView + ListAdapter + DiffUtil)

## In progress / planned
- Bottom navigation with Notes, Checklists, Reminders and Settings tabs
- Add/Edit/Detail note screens with Markdown formatting
- OCR scan-to-note (CameraX + ML Kit Text Recognition, fully on-device)
- Reminders with WorkManager notifications
- Categories with default and custom colors; light/dark theme

## Tech
Kotlin, XML + ViewBinding, Room, ViewModel, LiveData, Coroutines, RecyclerView

## Documentation
The product requirements and design reference are in [`docs/`](docs/), including a decision log of every feature considered and cut.