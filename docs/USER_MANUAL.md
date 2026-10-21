# AssetTrack Lite User Manual

## Start
Install JDK 17+ and Maven. Run `mvn clean javafx:run` from the repository root on a desktop with a graphical display.

## Use
1. The inventory table opens with three example devices.
2. Type into the search field to filter by tag, manufacturer, model, or device type. Clear the field to show all records.
3. Choose **Add Device**, enter a unique asset tag and the device information, choose Laptop or Desktop, then press Add.
4. Click a row and choose **Update Status** to select Available, Deployed, In Repair, or Retired.
5. Click a row and choose **Remove Selected** to delete that device.

A duplicate or blank asset tag is rejected. The app stores records **in memory only**. Closing it resets the inventory; it does not save to disk. Laptop screen size is a fixed demo value of 14 inches.

## Demo example
Search for `TAG003`, then clear the search. Add `TAG004` as a Laptop, change it to Deployed, and remove it.
