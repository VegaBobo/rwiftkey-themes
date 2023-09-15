package rwiftkey.themes;

import rwiftkey.themes.IRemoteServiceCallback;
import rwiftkey.themes.ISelfServiceCallback;
import rwiftkey.themes.ui.screen.home.KeyboardTheme;

interface IRemoteService {
    void ping();

    // remote-side stuff
    void registerRemoteCallback(IRemoteServiceCallback callback);
    void removeRemoteCallback();
    void sendThemesToSelf(in List<KeyboardTheme> themes);
    void onRemoteServiceStarted();
    void onInstallThemeFromUriResult(boolean hasInstalled);

    // self-side stuff
    void registerSelfCallbacks(ISelfServiceCallback callback);
    void removeSelfCallback();
    void requestInstallThemeFromUri(in Uri uri);
}