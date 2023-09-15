package rwiftkey.themes;

import rwiftkey.themes.ui.screen.home.KeyboardTheme;

oneway interface ISelfServiceCallback {
    void onReceiveThemes(in List<KeyboardTheme> themes);
}
