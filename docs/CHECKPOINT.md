# November 2 Progress Checkpoint

## UI design

One window: title, search input, inventory table, and buttons for Add Device, Update Status, and Remove Selected. Take a screenshot of the running JavaFX window before submission. The source layout is in `AssetTrackApp.java`.

## Core class

`Device.java` has documented fields, a constructor, validation, and methods. `Laptop.java` is a documented subclass; `InventoryManager.java` handles inventory operations.

## Three milestones

| Target | Goal |
|---|---|
| November 2 | Document working UI layout and submit commented core class |
| November 16 | Test add/remove/search/status and resolve bugs |
| November 30 | Finish manuals and demo rehearsal |

## Challenges and solutions

| Challenge | Solution |
|---|---|
| Original proposal was too large for a one-person course project | Narrowed to one window, four classes, and in-memory records |
| Earlier Laptop stored a Device rather than inheriting from it | Changed Laptop to extend Device and override getDeviceType |
| GUI dependencies can be hard to configure | Added a Maven JavaFX configuration and run instructions |
| Persistence would increase testing and scope | Clearly documented session-only in-memory storage |

## Before submitting

- Run the app and capture a screenshot of its layout.
- Confirm core class compiles and check the documented examples.
- Include this checkpoint plan and challenge table in the progress submission.
- Optional extra credit: share the progress update and respond to two peers in the discussion forum.
