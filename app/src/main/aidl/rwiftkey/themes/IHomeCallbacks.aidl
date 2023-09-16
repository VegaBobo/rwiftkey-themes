package rwiftkey.themes;

import rwiftkey.themes.ui.screen.home.KeyboardTheme;

oneway interface IHomeCallbacks {
    void onRemoteBoundService();
    void onReceiveThemes(in List<KeyboardTheme> themes);
    void onInstallThemeResult(boolean hasInstalled);
    void onRemoteRequestRebind();
}
