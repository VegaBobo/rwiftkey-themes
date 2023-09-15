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

    // self-side stuff
    void registerSelfCallbacks(ISelfServiceCallback callback);
    void removeSelfCallback();
}