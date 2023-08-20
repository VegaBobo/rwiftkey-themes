package rwiftkey.themes;

import rwiftkey.themes.ui.screen.home.KeyboardTheme;

interface IPrivilegedService {
    int getUid();
    void installTheme(String targetKeyboardPackage, String themeFilePath);
    void cleanThemes(String targetKeyboardPackage);
    void forceStopPackage(String packageName);
    List<KeyboardTheme> getKeyboardThemes(String targetKeyboardPackage);
    void deleteTheme(String targetKeyboardPackage, String themeName);
    void modifyTheme(String targetKeyboardPackage, String themeId, String absZipFileToApply);
}