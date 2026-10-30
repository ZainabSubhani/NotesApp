# Product Requirements Document — NotesApp (Notes · Checklists · Reminders)

**Author:** Zainab
**Version:** 1.0
**Status:** In development — portfolio and learning project
**Last updated:** October 2026

> **How this document came to be:** planning began with a single-module OCR notes app. Before development started, and after reviewing reference UI, scope was deliberately expanded to a 3-module app (Notes, Checklists, Reminders) with shared categories. This was a conscious, weighed decision — not scope drift — documented with trade-offs throughout. Every feature considered and rejected along the way is recorded in Section 9, so the reasoning survives even when it isn't rebuilt.

---

## 1. Overview

### 1.1 Purpose
A local-first Android productivity app — Notes (with on-device OCR capture), Checklists, and Reminders — built as a solo junior-developer portfolio and learning project. OCR-scan-to-note is the headline differentiator; Checklists and Reminders are supporting modules that make the app a complete personal organizer without diluting that differentiator.

### 1.2 Goals
- Build a working, polished app and present it through a public GitHub repository (source, tests, PRD and design reference).
- Demonstrate CameraX + ML Kit integration, clean MVVM architecture across multiple related data modules, and deliberate scope control — all defensible in a junior interview.
- Keep OCR as the first, most visually prominent thing about the app, not one feature among four.
- Treat the build as a learning exercise: each step is understood, tested and committed before the next begins.

### 1.3 Non-goals (explicitly out of scope)
See Section 9 for the full list with reasoning. Headline exclusions: cloud sync/backend, a unified Home dashboard, a calendar/month view, true WYSIWYG rich text, image/photo attachments, Hilt, Jetpack Compose.

---

## 2. Information architecture

```
Bottom navigation, 4 tabs — no Home/dashboard tab:
[Notes]  [Checklists]  [Reminders]  [Settings]
```

Each tab queries only its own data type. No screen merges Notes + Checklists + Reminders into one mixed feed — rejected early for both cost (multi-viewtype adapter, three-way merge-sort by date) and clarity (mixed feeds are harder to scan, not easier).

**Screens (12 total):**
1. Notes List (start destination)
2. Note Detail (read-only, rendered Markdown)
3. Add/Edit Note (raw Markdown editor + formatting toolbar)
4. Scan (CameraX + gallery entry to OCR)
5. Extracted text preview (confirm or retake before the text reaches the editor)
6. Checklist List
7. Checklist Detail (interactive — check items inline, same screen serves edit)
8. Reminders List (grouped: Overdue / Today / Upcoming)
9. Add Reminder (title, date, time)
10. Settings
11. Add Category (bottom sheet, triggered from the "+ Add" chip on the Notes/Checklist chip rows)
12. Manage Categories (in Settings — list of all categories; rename and delete are available only for custom categories)

---

## 3. Core user flows

### 3.1 Create a note (typed)
List → tap "+" FAB → Add/Edit (empty) → type title + content (Markdown) → Save → Room insert → back to List.

### 3.2 Create a note via OCR scan
Notes List → tap scan FAB (secondary FAB, stacked above "+") → Scan screen → **Take Photo (CameraX)** or **Choose from Gallery** — both feed the same ML Kit pipeline → text extracted → **preview screen** ("Retake" / "Use this text") → Add/Edit, content pre-filled with extracted text, title empty, amber "Scanned — review before saving" banner shown → user edits/saves.

**The image itself is never stored.** OCR is a pure text-extraction step — processed, then discarded. `Note.content` is always a plain string (Markdown), never a blob or file reference.

Failure paths: camera permission denied → rationale + retry; no text detected → message + retake; no camera hardware → hide scan entry.

### 3.3 View and edit a note
List → tap note → **Detail** (read-only, Markwon-rendered — this is where formatting is actually visible) → tap pencil icon → **Add/Edit** (raw Markdown, editable) → Save → back to Detail, re-rendered.

Tapping a note does **not** open the raw editor directly — Detail is the default view. This was a deliberate fix: without it, formatting would be invisible in normal use.

### 3.4 Create/use a checklist
Checklist List → tap "+" FAB → Checklist Detail (empty) → type title, add items via "+ Add item" → tap items to toggle checked (strikethrough + progress bar updates live) → no separate save step — this screen doubles as both detail and edit, since interacting with items IS the primary use case (unlike Notes, no rendered/raw split needed here).

### 3.5 Create/view a reminder
Reminders List → tap "+" FAB → Add Reminder (title, date, time) → Save → WorkManager schedules a `OneTimeWorkRequest` → notification fires around the scheduled time (see Section 6.2 for why "around," not exact).

### 3.6 Add a category
The app ships with 5 default categories. To create more, tap the "+ Add" chip on any chip row (Notes or Checklists) → **bottom sheet** (not a dialog — see Section 6.6 for why) → name field (auto-focused keyboard) + 6-swatch color picker from the **custom-category palette** (distinct from the 5 default-category colors, so no visual collision) → validation (non-empty, non-duplicate, max ~20 chars) → "Add category" → sheet dismisses → new chip appended to the row (before "+ Add") → **no auto-filter to the new category** (it's empty; jumping there would just show an empty state).

Custom categories can later be renamed or deleted from Manage Categories. Default categories can be neither renamed nor deleted.

---

## 4. Functional requirements

| ID | Requirement | Priority |
|----|---|---|
| FR-1 | Create a note by typing | Must |
| FR-2 | Create a note via OCR (camera or gallery source) | Must |
| FR-3 | View notes list | Must |
| FR-4 | Edit an existing note | Must |
| FR-5 | Delete a note, with undo | Must |
| FR-6 | OCR runs fully on-device, no network required | Must |
| FR-7 | Graceful handling of camera permission denial | Must |
| FR-8 | Empty state when no notes exist | Should |
| FR-9 | Loading state while fetching | Should |
| FR-10 | List updates reactively via LiveData | Must |
| FR-11 | Assign a category to a note or checklist | Must |
| FR-12 | Create a custom category via the "+" chip | Must |
| FR-13 | Filter list by category, including "All" | Must |
| FR-14 | Deleting a custom category does not delete its notes or checklists (they fall back to "Uncategorized") | Must |
| FR-14a | The 5 seeded default categories can be neither deleted nor renamed | Must |
| FR-14b | Custom categories can be renamed and deleted | Must |
| FR-15 | Apply Markdown formatting (H1, H2, Bold, Bullet, Numbered) via toolbar buttons that insert/wrap syntax at the cursor | Must |
| FR-16 | Note content renders as formatted Markdown in Detail (view) mode | Must |
| FR-17 | Note content shows raw Markdown syntax while actively editing (accepted trade-off) | Must |
| FR-18 | Create a checklist with a title and category | Must |
| FR-19 | Add/remove items on a checklist | Must |
| FR-20 | Toggle item checked state; checklist shows live progress ("x of y done") | Must |
| FR-21 | Create a reminder with title, date, time | Must |
| FR-22 | Reminders grouped into Overdue / Today / Upcoming based on `dueAt` vs. current time | Must |
| FR-23 | Notification fires via WorkManager around the scheduled time | Must |
| FR-24 | Reminder notifications survive device reboot (WorkManager's own persistence — no custom boot-receiver code needed) | Must |
| FR-25 | Theme setting: System / Light / Dark, controlled from Settings, persisted, applied via `AppCompatDelegate.setDefaultNightMode()` | Must |
| FR-26 | Notes list is list-view only — no grid view, no view-mode toggle | Won't (v1) |

---

## 5. Non-functional requirements

- **Offline-first:** all core functionality (CRUD across all 3 modules, OCR) works with no network connection. Reminder notification *timing* may vary slightly due to WorkManager's battery-aware scheduling, but scheduling itself requires no network.
- **Local persistence only:** Room, no backend, no data leaves the device.
- **Accessibility:** category identity is never conveyed by color alone — every category tag also shows as readable text, so colorblind/low-vision users aren't dependent on hue to distinguish categories.
- **Privacy:** camera used only for on-device text extraction; nothing transmitted externally.
- **Compatibility:** minSdk 24, compileSdk/targetSdk 36 (ML Kit Text Recognition v2 and CameraX both support this range; confirm CameraX minimum during setup).
- **Testing:** every DAO and every non-trivial query is covered by an instrumented test before UI is built on top of it.

---

## 6. Technical scope

### 6.1 Stack
- **Language:** Kotlin
- **UI:** XML layouts + View Binding
- **Architecture:** MVVM, Single Activity + Fragments, Navigation Component + Safe Args, `BottomNavigationView` for the 4-tab structure
- **Data:** Room, Coroutines, LiveData
- **Lists:** RecyclerView + ListAdapter + DiffUtil
- **Camera/OCR:** CameraX, ML Kit Text Recognition v2 (on-device/bundled model, no Firebase project)
- **Text rendering:** Markwon (renders Markdown in Detail screens)
- **Background work:** WorkManager (reminder notification scheduling)
- **Theming:** `AppCompatDelegate` day/night mode, DataStore/SharedPreferences for the persisted theme preference
- **Tooling:** Git, GitHub, Android Studio

### 6.2 Explicitly excluded (with reasoning)
- **Retrofit, Firebase** — no network feature exists; unused dependencies would be pure resume-padding.
- **Hilt/Dagger** — one abstraction layer too many stacked on top of Fragments + Navigation + Room + LiveData + CameraX + ML Kit for a first Android project. Manual DI via `by viewModels()` is sufficient.
- **Jetpack Compose** — deliberate sequencing choice: XML forces learning of Android view/lifecycle fundamentals Compose abstracts away, appropriate for a return-to-Android project after a gap. Acknowledged portfolio trade-off; a small Compose screen/feature is a legitimate later addition to close the gap.
- **True WYSIWYG rich text (Spannable-based editor)** — no built-in numbered-list span in Android; heading/alignment/cursor-state tracking and HTML/span serialization made this comparable in effort to the entire OCR feature. Resolved via Markdown instead (see FR-15–17).
- **Alignment and highlight formatting** — not part of standard Markdown; would need non-standard Markwon extensions. Trimmed from the toolbar; revisit only if genuinely needed later.
- **Grid/masonry note layout** — `StaggeredGridLayoutManager` is meaningfully harder than a linear list for no functional benefit. **Also revisited later and confirmed: list-view-only, no grid/list toggle at all** — a toggle solves a density problem this app doesn't have, given notes (especially OCR'd ones) benefit more from reading room than from card density.
- **Home/Overview dashboard** — pure aggregation screen with zero unique function once the three tab screens exist individually; also the single most technically expensive screen to build correctly (multi-type merge feed).
- **Calendar/month-grid view, and later a date-strip week navigator on Reminders** — both considered and both cut. A calendar grid is a large, separate rendering problem; a date-strip reintroduces a smaller version of the same cost (chevron paging, month-header logic, per-day dot indicators, scroll-position tracking) for a secondary tab. A plain grouped list (Overdue/Today/Upcoming) covers the real need at a fraction of the cost.
- **AlarmManager (exact alarms) for Reminders** — considered against WorkManager; rejected due to the `SCHEDULE_EXACT_ALARM` manual-settings permission flow (Android 13+) and the need for custom `BOOT_COMPLETED` re-scheduling logic. WorkManager's slight timing imprecision (minutes, not seconds) is an acceptable trade-off for a personal reminder app; automatic reboot persistence and a standard runtime-permission flow are worth more than exact-second timing here.
- **Image/photo attachments as a note type** — OCR extracts text only; the source image is never persisted. A true "attach a photo to a note" feature is a different, unstarted feature (local image storage) — not built, not designed.
- **Reminder categories** — cut (see Section 9). Reminders are organized by time (Overdue/Today/Upcoming), not by category.

### 6.3 Data models

```kotlin
enum class NoteSource { TYPED, SCANNED }

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,       // Markdown syntax, not rendered HTML
    val createdAt: Long,
    val updatedAt: Long,
    val sourceType: NoteSource, // TYPED or SCANNED
    val categoryId: Int? = null
)

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val isDefault: Boolean = false  // true for the 5 seeded categories — locked: cannot be renamed or deleted
)

@Entity
data class Checklist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val categoryId: Int? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val checklistId: Int,
    val text: String,
    val isChecked: Boolean = false,
    val position: Int
)

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dueAt: Long,           // epoch millis, date + time combined
    val isCompleted: Boolean = false,
    val createdAt: Long
)
```

**Relationships and delete rules:**

| Relationship | Rule | Why |
|---|---|---|
| `Note.categoryId` → `Category.id` | `SET_NULL` | A note without a category is still a valid note ("Uncategorized") |
| `Checklist.categoryId` → `Category.id` | `SET_NULL` | Same reasoning |
| `ChecklistItem.checklistId` → `Checklist.id` | `CASCADE` | An item with no checklist is meaningless; deleting a checklist deletes its items |
| `Reminder` | no relationships | Reminders are organized by time, not category |

Default categories are protected at the query level: the DAO's delete and rename queries both include `AND isDefault = 0`, so a default category can be neither deleted nor renamed regardless of what the UI does.

`Overdue`/`Today`/`Upcoming` grouping on the Reminders tab is computed at query/bind time from `dueAt` vs. current time — not a stored field.

Checklist tab progress ("3 of 5 done") is produced by a `LEFT JOIN` + `COUNT`/`SUM` aggregate query that returns a non-entity result class (`ChecklistWithProgress`). The `LEFT JOIN` ensures empty checklists still appear as "0 of 0". This is covered by instrumented tests.

### 6.4 Category color system

Two fixed, non-overlapping palettes:

**Default categories (5, seeded once on first Room database creation via a `RoomDatabase.Callback`; locked — cannot be renamed or deleted):**
| Category | Color |
|---|---|
| Work | `#6366F1` (indigo) |
| Personal | `#FB7185` (rose) |
| Ideas | `#A78BFA` (violet) |
| Health | `#34D399` (emerald) |
| Study | `#38BDF8` (sky) |

**Custom category palette (offered in the "Add Category" bottom sheet only):**
`#2DD4BF` (teal), `#E879F9` (fuchsia), `#A3E635` (lime), `#22D3EE` (cyan), `#FB923C` (orange), `#64748B` (slate)

Color is a property of the **category**, sourced once at creation and never re-picked per note — every note and checklist in a category shows that category's one fixed color via a left-border accent + tag pill. This must be looked up from `Category.colorHex`, never hardcoded or randomized per list item. Reminders do not use category colors; their list rows use status colors (overdue / today) instead.

### 6.5 Theming

Semantic color tokens (not hardcoded hex) defined in `res/values/colors.xml` (light) and `res/values-night/colors.xml` (dark) so the system resolves the right set automatically:

| Token | Light | Dark |
|---|---|---|
| Background | `#F7F7F5` | `#16171B` |
| Surface | `#FFFFFF` | `#1D1E23` |
| Text primary | `#16171B` | `#EDEDEF` |
| Text secondary | `#6E6F78` | `#8A8B93` |
| Accent | `#0D9488` | `#5EEAD4` |
| Danger | `#DC2626` | `#F87171` |
| Border | `#ECECE9` | `#26272D` |

Theme mode (System/Light/Dark) is user-controlled from Settings (FR-25), persisted, not just system-inherited — this was a deliberate choice over the simpler "auto-follow system only" option, in exchange for a small amount of extra code (stored preference + apply-on-launch) and a genuine Settings feature.

Visual language: flat cards/rows with a colored left-border accent + small tag pill for category identity — not full pastel card-fill backgrounds (the original reference-inspired direction). Bold sans-serif type (no serif), consistent across all screens.

### 6.6 UI component choices worth remembering the reasoning for

- **Notes tab dual FAB:** a small outlined "scan" FAB stacked above the solid primary "+" FAB — not a bottom-nav item (breaks nav-as-destination semantics) and not folded into a speed-dial menu (only two actions, doesn't need branching menu logic). Elevates OCR's visual prominence to match its role as the app's differentiator.
- **Add Category as a bottom sheet, not a centered dialog:** better keyboard behavior (anchored to the edge the keyboard extends from, no repositioning), better thumb reachability, consistent with current mobile design patterns, and — importantly — nearly the same implementation cost as a dialog (`BottomSheetDialogFragment` vs `AlertDialog`), so the improvement was close to free.
- **Note Detail vs. Add/Edit split, but no equivalent split for Checklists:** deliberate, not inconsistent. Notes need a read-only rendered view because Markdown formatting is invisible while raw-editing. Checklists have no such problem — checking off an item *is* the primary interaction, so Detail and Edit are correctly the same screen.

---

## 7. Build sequence (each step runnable before the next)

1. Room: all 5 entities + DAOs, each verified by instrumented tests in isolation. **(done)**
2. RecyclerView (ListAdapter + DiffUtil) with fake data, one module at a time.
3. Wire Room → RecyclerView directly, confirm real data flow.
4. ViewModel + LiveData + sealed UI-state classes per module.
5. Fragments + Navigation Component + Safe Args + `BottomNavigationView` wiring across all 4 tabs.
6. Category system (seeding, two-tier palette, bottom sheet, filter chips) — shared infrastructure, build once, reuse across Notes and Checklists.
7. CameraX — built and tested in an isolated throwaway project first.
8. ML Kit Text Recognition — added to the same throwaway project, tested independently.
9. Merge CameraX + ML Kit into main app: Scan screen → preview/confirm → Add/Edit handoff.
10. Markwon integration — Detail screen rendering + Add/Edit toolbar syntax insertion.
11. WorkManager reminder scheduling.
12. Theming — semantic tokens, day/night resource sets, Settings toggle.
13. Polish: empty/loading/error states across all modules, permission-denial handling.
14. Portfolio readiness: README with screenshots, clean repository, PRD and design reference in `docs/`.

**Guiding principle:** CameraX/ML Kit are validated in isolation before integration, to avoid debugging multiple unfamiliar systems simultaneously.

---

## 8. Portfolio readiness checklist

- [ ] Public GitHub repository with a clean, meaningful commit history
- [ ] `README.md`: description, tech stack, current status, screenshots
- [ ] `docs/` contains this PRD and the design reference
- [ ] `.gitignore` excludes build output, IDE state and cache folders (`.kotlin/`, `local.properties`)
- [ ] All instrumented tests pass
- [ ] App builds and runs from a fresh clone

---

## 9. Full scope decision log — everything considered and its outcome

| Feature | Outcome | Reasoning (see relevant section for detail) |
|---|---|---|
| Categories | **In** | Genuinely useful, low-moderate cost, orthogonal to the OCR differentiator |
| Default categories: renamable or locked | **Locked** | 5 seeded defaults can be neither renamed nor deleted; users add their own via the "+" chip. Keeps the built-in set stable and the delete/rename rules simple (one `isDefault` check in the DAO). Custom categories remain fully editable |
| Rich text (true WYSIWYG spans) | **Rejected** → Markdown instead | Cost comparable to entire OCR feature; no built-in numbered-list span in Android |
| Grid/masonry note layout | **Rejected**, then grid-vs-list toggle also rejected | Staggered layout complexity not justified; later decided list-only serves OCR content better than density |
| Home/Overview dashboard | **Cut** | Zero unique function once tab screens exist; most expensive screen to build correctly |
| Calendar/month view | **Cut** | Large separate rendering problem; grouped list covers the real need |
| Date-strip week navigator (Reminders) | **Cut** | Same cost category as the calendar view, just smaller scale; not worth it for a secondary tab |
| AlarmManager (exact alarms) | **Rejected** → WorkManager | Manual settings-permission flow + custom boot-receiver code vs. WorkManager's simpler, self-persisting model |
| Image/photo attachments | **Not built** | OCR is text-extraction only; image is never persisted |
| Alignment / highlight Markdown | **Trimmed** | Non-standard Markdown, needs extensions; kept toolbar to H1/H2/Bold/Bullet/Numbered |
| Checklists + Reminders modules | **In** (full scope reset) | Deliberate, weighed decision — accepted a 3-5x timeline increase in exchange for a stronger, more complete portfolio piece |
| Speed-dial FAB (3-way branching) | **Rejected** → dual FAB | Only 2 creation actions on Notes tab; branching menu unnecessary |
| Grid/list view toggle | **Rejected** | Solves a density problem this app doesn't have; notes benefit more from reading room |
| Single shared color per note (random) | **Rejected** → category-derived color | Color must be a property of the category, not picked per-note, or it stops meaning anything |
| Reminder category | **Cut** | The design's Reminders list never surfaced it (rows use status colors, no tag or filter row). Dropped rather than adding filter/tag UI to a screen deliberately kept simple; reminders are transient and already organized by time |
| Room schema export | **Disabled** | Room 2.8.5 crashes the KSP build with a kotlinx-serialization `AbstractMethodError` when exporting schemas. With no user data and no earlier database versions, there is nothing to migrate, so export is off (`exportSchema = false`). Revisit if the schema ever needs migrations |
| Temporary seed data in screens | **Temporary** | `seedIfEmpty()` helpers exist only to show real Room rows before the Add screens are built; each is deleted when the matching create flow exists |
