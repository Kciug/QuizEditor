# Migration System — Business Logic Specification

## Table of Contents

1. [Overview](#overview)
2. [Environments](#environments)
3. [Migration Modes](#migration-modes)
4. [Data Models](#data-models)
5. [Migration Flows](#migration-flows)
6. [Migration History](#migration-history)
7. [Production Transfer Tracking](#production-transfer-tracking)
8. [Access Control](#access-control)
9. [User Interface Specification](#user-interface-specification)
10. [Database Collections](#database-collections)
11. [Localization](#localization)
12. [Error Handling](#error-handling)

---

## Overview

The Migration System enables transferring content data between three isolated environments: **Development**, **Test**, and **Production**. The application operates in four independent modes, each with its own data structure and migration strategy.

Migration is a **one-way copy operation** — data is duplicated from a source environment to a target environment. The source data is never deleted or modified (except for adding a production transfer timestamp when migrating to Production).

Every migration operation is recorded in a shared audit log visible across all devices.

---

## Environments

The application supports three environments (also called "collections"):

| Environment | Purpose |
|-------------|---------|
| **Development** | Primary workspace for content creation and editing |
| **Test** | Staging area for QA and review |
| **Production** | Live data consumed by end-user applications |

**Rules:**
- Migration source and target must always be different environments.
- Any environment can be a source or target (e.g., Development → Production, Test → Development, Production → Test).
- Migration does not delete data from the source — it copies.

---

## Migration Modes

| Mode | Migration Type | What Is Migrated | Trigger |
|------|---------------|------------------|---------|
| **Main Mode** | Per category | One selected category + all its questions | User selects a specific category |
| **Swipe Mode** | Bulk (all) | All swipe questions | User initiates bulk transfer |
| **Translations Mode** | Bulk (all) | All translations | User initiates bulk transfer |
| **CEM Mode** | Per category (recursive) | One selected category + all subcategories (recursive) + all unique questions | User selects a specific root/parent category |

---

## Data Models

### Main Mode

**Category**
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier |
| title | String | Category name |
| questionIDs | Array of Integer | References to questions belonging to this category |
| status | Enum | One of: `done`, `inProgress`, `draft`, `needsRework` |
| color | String | Display color identifier |
| isFree | Boolean | Whether content is available for free |
| dateCreated | Timestamp | Creation date |
| dateModified | Timestamp | Last modification date |
| productionTransferDate | Timestamp (nullable) | When this item was last migrated to Production |

**Question**
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier |
| text | String | Question text |
| answers | Array of Answer | List of answer options |
| status | Enum | One of: `done`, `inProgress`, `draft`, `needsRework` |
| dateCreated | Timestamp | Creation date |
| dateModified | Timestamp | Last modification date |

### Swipe Mode

**SwipeQuestion**
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier |
| text | String | Question/statement text |
| isCorrect | Boolean | Whether the statement is true |
| dateCreated | Timestamp | Creation date |
| dateModified | Timestamp | Last modification date |
| productionTransferDate | Timestamp (nullable) | When this item was last migrated to Production |

### Translations Mode

**Translation**
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier |
| phrase | String | Source phrase to translate |
| translations | Array of String | List of accepted translations |
| dateCreated | Timestamp | Creation date |
| dateModified | Timestamp | Last modification date |
| productionTransferDate | Timestamp (nullable) | When this item was last migrated to Production |

### CEM Mode

**CEMCategory**
| Field | Type | Description |
|-------|------|-------------|
| id | Integer | Unique identifier |
| title | String | Category name |
| subtitle | String | Short description |
| questionIDs | Array of Integer | References to questions in this category |
| subcategoryIDs | Array of Integer | References to child categories |
| parentCategoryID | Integer (nullable) | Reference to parent category (`null` = root) |
| status | Enum | One of: `done`, `inProgress`, `draft`, `needsRework` |
| color | String | Display color identifier |
| isFree | Boolean | Whether content is available for free |
| dateCreated | Timestamp | Creation date |
| dateModified | Timestamp | Last modification date |
| productionTransferDate | Timestamp (nullable) | When this item was last migrated to Production |

**CEMQuestion** — same structure as Main Mode Question (shared model).

**Category Tree Structure:**
```
Root Category (parentCategoryID = null)
├── Subcategory A (parentCategoryID = Root.id)
│   ├── Subcategory A1 (parentCategoryID = A.id)
│   └── Subcategory A2 (parentCategoryID = A.id)
└── Subcategory B (parentCategoryID = Root.id)
    └── Subcategory B1 (parentCategoryID = B.id)
```

Each category at any level can have its own `questionIDs` referencing questions.

---

## Migration Flows

### Main Mode Flow

```
1. User selects a category to migrate
2. System validates that the category exists
3. System reads the category and all questions referenced by category.questionIDs
4. System writes the category document to the target environment
5. System writes all question documents to the target environment (concurrently)
6. If target = Production → mark source category with productionTransferDate = now
7. System saves a MigrationRecord to the audit log
```

**Item details format:** `["Category Name (N questions)"]`

### Swipe Mode Flow

```
1. User initiates bulk migration
2. System reads ALL swipe questions from the current environment
3. System writes all questions to the target environment (concurrently)
4. If target = Production → for each source question:
   a. Set productionTransferDate = now
   b. Save the updated question back to the source environment
5. System saves a MigrationRecord to the audit log
```

**Item details format:** `["Question text 1", "Question text 2", ...]`

### Translations Mode Flow

```
1. User initiates bulk migration
2. System reads ALL translations from the current environment
3. System writes all translations to the target environment (concurrently)
4. If target = Production → for each source translation:
   a. Set productionTransferDate = now
   b. Save the updated translation back to the source environment
5. System saves a MigrationRecord to the audit log
```

**Item details format:** `["Phrase 1", "Phrase 2", ...]`

### CEM Mode Flow

```
1. User selects a category to migrate
2. System validates that the category exists
3. System recursively collects the FULL category tree:
   a. Start with the selected category
   b. For each subcategoryID, load the subcategory
   c. Repeat recursively for all descendants
   d. Result: flat list of all categories in the tree
4. System collects ALL questions from the tree:
   a. For each category in the flat list, read all questionIDs
   b. Load each question
   c. Deduplicate by question ID (same question may be referenced by multiple categories)
   d. Result: list of unique questions
5. System writes all categories to the target environment (concurrently)
6. System writes all unique questions to the target environment (concurrently)
7. If target = Production → for each source category in the tree:
   a. Set productionTransferDate = now
   b. Save the updated category back to the source environment
8. System saves a MigrationRecord to the audit log
```

**Item details format:**
```
[
  "Category Name (X subcategories, Y questions)",
  "Subcategory A (X subcategories, Y questions)",
  ...
  "Total: Z questions"
]
```

### Write Strategy

All document writes to the target environment use a **full overwrite** strategy (`merge: false`). If a document with the same ID already exists in the target, it is completely replaced.

Writes are performed **concurrently** (not sequentially) for performance optimization.

---

## Migration History

### MigrationRecord

Every migration creates a single record in a shared audit collection:

| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Unique record identifier, also used as document ID |
| mode | String | `"main"`, `"swipe"`, `"translations"`, or `"cem"` |
| sourceCollection | String | Source environment name (e.g., `"Development"`) |
| targetCollection | String | Target environment name (e.g., `"Production"`) |
| itemCount | Integer | Total number of items migrated |
| itemDetails | Array of String | Human-readable descriptions of migrated items |
| performedBy | String | Name of the user who performed the migration |
| date | Timestamp | Date and time of the migration |

### Item Count Calculation

| Mode | itemCount formula |
|------|-------------------|
| Main | 1 (category) + N (questions) |
| Swipe | N (all questions) |
| Translations | N (all translations) |
| CEM | N (all categories in tree) + M (all unique questions) |

### History Retrieval

- Records are fetched **filtered by mode** — each mode screen shows only its own history.
- Records are **sorted by date descending** (newest first).
- History is stored in a **global collection** shared across all environments and modes.
- History is visible on **all devices** (persisted in Firestore, not local storage).

---

## Production Transfer Tracking

### Purpose

When items are migrated **to the Production environment**, the system marks each source item with a `productionTransferDate` timestamp. This enables two key features:

1. **Visual indication** that an item has been deployed to Production.
2. **"Needs update" detection** — the system can determine if an item was modified after its last production migration.

### "Needs Update" Logic

```
needsUpdate = (dateModified > productionTransferDate)
```

- If `productionTransferDate` is null → item was never migrated to Production.
- If `dateModified > productionTransferDate` → item was edited since last production migration.
- If `dateModified <= productionTransferDate` → item is up to date on Production.

**This computed flag applies to:**
- Main Mode: `Category`
- CEM Mode: `CEMCategory`

**Swipe Mode and Translations Mode** store `productionTransferDate` but do not compute `needsUpdate` (since they use bulk migration of all items).

### What Gets Marked

| Mode | Marked items | Not marked |
|------|-------------|------------|
| Main | Source category | Source questions |
| Swipe | All source questions | — |
| Translations | All source translations | — |
| CEM | All source categories in tree | Source questions |

### Marking Behavior

- `productionTransferDate` is set to the current timestamp at the moment of migration.
- The field is only updated when migrating **to Production**. Migrations to Development or Test do not set this field.
- The field is updated on the **source** item, not the target copy.
- The `productionTransferDate` field is **optional** — documents without this field remain backward compatible.

---

## Access Control

| User Role | Can Trigger Migration? | Can View Migration History? |
|-----------|----------------------|---------------------------|
| **Admin** | Yes | Yes |
| **Non-Admin** | No | No (UI elements hidden or blocked) |

**Behavior:**
- For **Main Mode** and **CEM Mode**: the migrate action is available via swipe gesture on the category row. For non-admin users, tapping it shows a "Migration Declined" alert.
- For **Swipe Mode** and **Translations Mode**: the migrate toolbar button is only visible to admin users (completely hidden for non-admins).

---

## User Interface Specification

### Swipe Mode & Translations Mode — Bulk Migration Sheet

**Entry point:** Toolbar button (icon: bidirectional arrows, color: orange). Visible to admin only.

**Sheet contents:**

1. **Section: "Migration Details"**
   - Source environment (read-only, shows current environment)
   - Target environment (picker, excludes current environment)
   - Item count to migrate (read-only)

2. **Section: Actions**
   - "Migrate All" button (destructive style, disabled when item count = 0)
   - "Cancel" button

3. **Section: "Migration History"**
   - List of past migration records for this mode
   - Each row is expandable (see History Row below)
   - Shows loading indicator while fetching
   - Shows "No migration history" when empty

### CEM Mode — Category Migration Sheet

**Entry point:** Leading swipe action on category row (orange button). Admin → opens sheet; non-admin → shows "Migration Declined" alert.

**Sheet contents:**

1. **Section: "Selected Category"**
   - Category card with badge and status indicator

2. **Section: Category Details**
   - Total subcategories count (recursive — all levels)
   - Total questions count (deduplicated across entire tree)

3. **Section: Migration**
   - Source environment (read-only)
   - Target environment (picker)

4. **Section: Actions**
   - "Migrate" button (destructive style)
   - "Cancel" button

5. **Section: "Migration History"**
   - Same shared history component, filtered to CEM mode

### Main Mode — Category Migration Sheet (existing, extended)

The existing migration sheet now includes a **"Migration History" section** at the bottom, showing past records filtered to Main Mode.

### Migration History Row (shared component)

Each history record is displayed as an expandable row:

**Collapsed state (header):**
- Direction: `Source → Target` (e.g., "Development → Production")
- Date of migration
- Item count

**Expanded state (details):**
- List of `itemDetails` strings
- "Performed by: [user name]"

---

## Database Collections

### Data Collections Per Mode

| Mode | Environment | Collection Name(s) |
|------|------------|---------------------|
| Main | Development | `development_categories`, `development_questions` |
| Main | Test | `test_categories`, `test_questions` |
| Main | Production | `production_categories`, `production_questions` |
| Swipe | Development | `development_swipe_questions` |
| Swipe | Test | `test_swipe_questions` |
| Swipe | Production | `production_swipe_questions` |
| Translations | Development | `development_translations` |
| Translations | Test | `test_translations` |
| Translations | Production | `production_translations` |
| CEM | Development | `development_cem_categories`, `development_cem_questions` |
| CEM | Test | `test_cem_categories`, `test_cem_questions` |
| CEM | Production | `production_cem_categories`, `production_cem_questions` |

### Migration History Collection

| Property | Value |
|----------|-------|
| Collection name | `migration_history` |
| Scope | Global (shared by all modes and environments) |
| Document ID | UUID string from `MigrationRecord.id` |
| Querying | Filter by `mode` field, sort by `date` descending |

---

## Localization

All user-visible strings must be localized. Required keys:

| Key | English | Polish |
|-----|---------|--------|
| `section_migration_history` | Migration History | Historia Migracji |
| `text_no_migration_history` | No migration history | Brak historii migracji |
| `text_performed_by` | Performed by: | Wykonane przez: |
| `text_items_count` | Items count: | Liczba elementów: |
| `text_questions_count` | Questions: | Pytania: |
| `text_subcategories_count` | Subcategories: | Subkategorie: |
| `button_migrate_all` | Migrate All | Przenieś wszystko |
| `guidance_failed_to_migrate_swipe` | Swipe questions migration failed. Try again. | Migracja pytań Swipe nie powiodła się. Spróbuj ponownie. |
| `guidance_failed_to_migrate_translations` | Translations migration failed. Try again. | Migracja tłumaczeń nie powiodła się. Spróbuj ponownie. |
| `guidance_failed_to_migrate_cem_category` | CEM category migration failed. Try again. | Migracja kategorii CEM nie powiodła się. Spróbuj ponownie. |

**Previously existing keys reused:** `title_migration`, `section_migration_details`, `text_migrate_from`, `picker_migrate_to`, `button_migrate`, `button_cancel`, `section_selected_category`, `alert_title_migration_declined`, `message_migration_declined`, `button_ok`.

---

## Error Handling

### Error Scenarios

| Scenario | Behavior |
|----------|----------|
| Category not found (Main/CEM) | Throw error, show message, abort migration |
| Network failure during write | Throw error, show mode-specific guidance message |
| Zero items to migrate | "Migrate" / "Migrate All" button disabled (prevent action) |
| Non-admin triggers migration | Show "Migration Declined" alert or hide UI element |

### Error Messages Per Mode

- **Main Mode:** `"guidance_failed_to_migrate_category"` (existing)
- **Swipe Mode:** `"guidance_failed_to_migrate_swipe"`
- **Translations Mode:** `"guidance_failed_to_migrate_translations"`
- **CEM Mode:** `"guidance_failed_to_migrate_cem_category"`

### Atomicity Note

Migration is **not atomic** — if a failure occurs mid-migration, some documents may have already been written to the target environment. A retry will overwrite them (since writes use the same document IDs). There is no rollback mechanism. The migration history record is only saved **after all writes succeed**.
