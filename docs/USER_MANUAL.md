# AssetTrack Lite User Manual

## Start
Install JDK 17+ and Maven. Run `mvn clean javafx:run` from the repository root on a desktop with a graphical display.

## Use
1. The inventory table opens with three example devices.
2. Type into the search field to filter by tag, manufacturer, model, or device type. Clear the field to show all records.
3. Choose **Add Device**, enter a unique asset tag and the device information, choose Laptop or Desktop, then press Add.
4. Click a row and choose **Edit Device** to change its status to Available, Deployed, In Repair, or Retired.
5. Click a row and choose **Remove Selected** to delete that device.
6. Click a row and choose **Edit Device** to change the asset tag, manufacturer,
   model, assigned user, type, status, and laptop screen size. Press **Save** to
   apply the changes, or **Cancel** to leave the device unchanged. Screen size is
   enabled only for laptops and must be a positive number. Asset tags must remain
   unique, ignoring capitalization. If your edit no longer matches the current
   search, clear the search field to see the updated row.

A duplicate or blank asset tag is rejected. The app stores records **in memory only**. Closing it resets the inventory; it does not save to disk. Laptop screen size defaults to 14 inches and can be changed in the form.

## Demo example
Search for `TAG003`, then clear the search. Add `TAG004` as a Laptop, change it to Deployed, and remove it.
