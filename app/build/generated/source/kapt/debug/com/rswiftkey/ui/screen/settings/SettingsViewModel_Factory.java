package com.rswiftkey.ui.screen.settings;

import android.app.Application;
import com.rswiftkey.core.SKeyboardManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Application> appProvider;

  private final Provider<SKeyboardManager> sKeyboardManagerProvider;

  public SettingsViewModel_Factory(Provider<Application> appProvider,
      Provider<SKeyboardManager> sKeyboardManagerProvider) {
    this.appProvider = appProvider;
    this.sKeyboardManagerProvider = sKeyboardManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(appProvider.get(), sKeyboardManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Application> appProvider,
      Provider<SKeyboardManager> sKeyboardManagerProvider) {
    return new SettingsViewModel_Factory(appProvider, sKeyboardManagerProvider);
  }

  public static SettingsViewModel newInstance(Application app, SKeyboardManager sKeyboardManager) {
    return new SettingsViewModel(app, sKeyboardManager);
  }
}
