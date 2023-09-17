package rwiftkey.themes;

oneway interface IRemoteServiceCallbacks {
    void onThemesRequest();
    void onInstallThemeRequest(in Uri uri);
    void onRequestCleanup();
    void onRequestModifyTheme(String themeId, in Uri uri);
}
