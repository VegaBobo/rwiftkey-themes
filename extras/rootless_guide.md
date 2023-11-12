## Rootless guide
Current rootless implementation uses Xposed, there are various rootless implementations of Xposed, all of them should work with Rwiftkey.

In this guide, i'll be using LSPatch, but as i written before, it should work with other rootless Xposed implementations, keep in mind that in most cases, you will need to patch desired keyboard, which will require an application reinstall, app data will be gone after reinstallation, this step is required only once.

Steps for rootless operation mode:
 1. Install Rwiftkey themes app
 2. Install and setup Shizuku (Shizuku must be running).
 3. Install LSPatch, open app and check if there a is red card with text related to Shizuku, if so, tap on it, and grant Shizuku permissions.
 4. In LSPatch app, open the "Manage" tab, tap on "+" (plus icon).
 5. If you're setting LSPatch by the first time, it should prompt you to "Select storage directory", if so, tap "OK"
 6. Now you must select a folder which LSPatch will store data, if you have no clue what to do, just tap on the three lines showing in top left corner, pick your device, after that, in toolbar there is a button to create a new folder, tap on it, and create a new folder called "LSPatch", open newly created folder, and tap "Use this folder" and "Grant".
 7. Now, you must select the desired keyboard app, keep in mind that Rwiftkey only supports Swiftkey Stable and Beta, since most of us has the keyboard app already installed in device, tap "Select an installed app".
 8. Search for desired keyboard app.
 9. Some options to create a new patched app will be shown, just tap "Start Patch", you don't need to customize options, just use the recommended ones.
 10. Install new app (old app will be uninstalled, and all data related to it will be gone, after that, new app generated be installed).
 11. After installation, the desired keyboard will be shown in LSPatch manage list, tap on it, and select "Module Scope", and enable "Rwiftkey Themes", and tap on confirm (the right/correct icon)
 12. Open newly installed keyboard app, and setup it, set it as your own keyboard, and is highly recommend that you visit the themes section.
 13. After that, force keyboard app close, open Rwiftkey and use it.

For future usage, everything you need to do is to open Rwiftkey and use as you want to, while Rwiftkey is enabled in module scope, rootless operation mode should work, when device reboot, you will need to start Shizuku again.

If rootless operation mode is not working, check if Rwiftkey is enabled in module scope, if it is enabled and still not working, try force close keyboard app. 
