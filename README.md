# Rwiftkey Themes for SwiftKey
Rwiftkey themes is a application that helps users installing themes made by the community on SwiftKey keyboard app.

## Requirements
- Android device with root access
- SwiftKey keyboard app installed

## Usage
1. Install Rwiftkey Themes
2. Download a theme (themes are commonly .rwift or .zip files)
3. Install theme (you can install by opening .rwift file, or choosing file manually from app's homescreen)
4. Once installation is done, you can tap on "Open themes section", go to "Personalize" tab, and select installed theme
5. Done :)

## Obtaining Themes
There are some amazing themes right there: https://t.me/SwiftkeyThemes

** Use themes that has .rwift or .zip extension, others may fail.

## FAQ
#### Why requires root?
SwiftKey store user themes on its own data folder, so, we need root access to move theme to this folder.
#### It works with SwiftKey Beta?
You're able to select what keyboard to apply themes over preferences, just select the SwiftKey app you want.
#### How i can clean installed themes?
On preferences page, there is a option called "Clear themes", select it, and wait some seconds, installed themes will be cleared.

## Packing a theme
1. Create a SwiftKey theme
2. After that, you should have a folder with a random uuid (contaning all theme resources), and a file called "themelist.json"
3. Rename "themelist.json" to "themelist_custom.json"
4. Select everything (the folder with random uuid, and renamed json), and zip it
5. Optional - once zip file is created, rename file from .zip to .rwift
6. Done, try to install theme using our app
