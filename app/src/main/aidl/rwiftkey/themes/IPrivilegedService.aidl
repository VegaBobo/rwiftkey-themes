package rwiftkey.themes;

interface IPrivilegedService {
    int getUid();
    void installTheme(String targetKeyboardPackage, String themeFilePath);
    void cleanThemes(String targetKeyboardPackage);
    void forceStopPackage(String packageName);
}