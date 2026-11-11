# AssetTrack Lite Implementation Manual

## Design

- `Device`: shared fields (asset tag, manufacturer, model, status, assigned user), validation, getters, and status update.
- `Laptop extends Device`: adds screen size and overrides `getDeviceType()`. The GUI calls this method through a `Device` reference, demonstrating runtime polymorphism.
- `InventoryManager`: owns an `ArrayList<Device>`; adds and removes objects, rejects duplicate asset tags, loops to filter search results, and uses recursion to find an exact tag.
- `AssetTrackApp`: one JavaFX window with table, search field, and add/update/remove buttons. It keeps business logic in InventoryManager.

The authoritative Mermaid UML is in the root README. The original seven-class model was scaled down to four classes for achievable semester scope.

## Algorithm walkthrough

`findByTag(tag, index)` stops when the index reaches the list length (base case). It returns the matching device when found; otherwise it calls itself using `index + 1`. Recursive search meets the recursion objective, though a loop would be more efficient for large inventories.

`search(query)` normalizes text to lowercase, loops across devices, and checks tag, manufacturer, model, and type. It returns a new list rather than exposing the internal collection.

## Build and test

Use Java 17+ and Maven:

```bash
mvn clean compile
mvn javafx:run
```

Manual checks:
- Search for `TAG003` and confirm only the ThinkPad row appears.
- Search for `laptop` and confirm the laptop type is matched.
- Add a unique tag and confirm the row appears.
- Try to add the same tag twice and confirm the warning.
- Select a device, choose Edit Device, change its status, save, and confirm the new value.
- Remove a selected device and confirm it disappears.
- Clear search and confirm all remaining devices return.
- Reopen app and confirm sample inventory resets.

These steps are a **testing checklist**, not a claim they have already been executed in every environment.

Automated model checks are in `tests/InventoryTest.java`; run the commands in
the README with `-ea` to enable assertions. They cover duplicate tags, recursive
lookup, search (including a Turkish default locale), defensive list copies,
status validation, laptop screen sizes, and removal. `DeviceDemo.java` is an
optional console entry point and is not an additional application model class.

Invalid or duplicate tags keep the Add dialog open so entered fields are retained.
Laptop sizes reject non-finite values, and search uses `Locale.ROOT` so matching
does not depend on the computer's language settings.

## Intentional limitations

There is no persistence, authentication, maintenance history, or database. No desktop-specific subclass is needed for the small demo.

## Editing devices

The Add and Edit actions share `showDeviceForm(Device)`. Edit pre-fills every field,
including laptop screen size. Validation keeps the dialog open on invalid input.
Cancel makes no changes. Save creates a replacement `Device` or `Laptop`, allowing
device type changes while keeping identity fields immutable on each object.
`InventoryManager.updateDevice(originalTag, replacement)` checks for duplicate tags
before replacing the original at the same list position. The table refreshes and
reapplies the active search after saving. The Screen Size column shows laptop sizes.

Regression checks cover tag renaming, duplicate collisions with no partial update,
missing originals, null replacements, and conversion between device types.

## Presentation

Explain the problem, show the four-class Mermaid diagram, demonstrate the core buttons, and identify an example each of inheritance, polymorphism, conditions, loops, recursion, strings, and arrays. Describe the scope change as a design decision, not unfinished functionality.
