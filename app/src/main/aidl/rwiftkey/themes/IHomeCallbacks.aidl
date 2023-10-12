package rwiftkey.themes;

import rwiftkey.themes.model.Theme;

oneway interface IHomeCallbacks {
    void onRemoteBoundService();
    void onReceiveThemes(in List<Theme> themes);
    void onInstallThemeResult(boolean hasInstalled);
    void onRemoteRequestRebind();
    void onFinishModifyTheme();
    void onFinishDeleteTheme();
}
