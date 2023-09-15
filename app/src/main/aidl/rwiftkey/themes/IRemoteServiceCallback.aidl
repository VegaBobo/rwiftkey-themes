package rwiftkey.themes;

oneway interface IRemoteServiceCallback {
    void onThemesRequest();
    void onInstallThemeRequest(in Uri uri);
}
