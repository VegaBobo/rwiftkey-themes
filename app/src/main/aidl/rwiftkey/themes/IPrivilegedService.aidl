package rwiftkey.themes;

import rwiftkey.themes.model.Theme;

interface IPrivilegedService {
    int getUid();
    void installTheme(String targetKeyboardPackage, String themeFilePath);
    void cleanThemes(String targetKeyboardPackage);
    void forceStopPackage(String packageName);
    List<Theme> getKeyboardThemes(String targetKeyboardPackage);
    void deleteTheme(String targetKeyboardPackage, String themeId);
    void modifyTheme(String targetKeyboardPackage, String themeId, String absZipFileToApply);
}