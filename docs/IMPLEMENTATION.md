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
- Select a device, update status, and confirm the new value.
- Remove a selected device and confirm it disappears.
- Clear search and confirm all remaining devices return.
- Reopen app and confirm sample inventory resets.

These steps are a **testing checklist**, not a claim they have already been executed in every environment.

## Intentional limitations

There is no persistence, authentication, maintenance history, or database. The screen size for new laptops is set to 14 inches to avoid adding more forms and complexity. No desktop-specific subclass is needed for the small demo.

## Presentation

Explain the problem, show the four-class Mermaid diagram, demonstrate the core buttons, and identify an example each of inheritance, polymorphism, conditions, loops, recursion, strings, and arrays. Describe the scope change as a design decision, not unfinished functionality.
