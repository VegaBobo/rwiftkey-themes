package com.rswiftkey.ui.screen.home;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<Application> appProvider;

  private final Provider<SKeyboardManager> sKeyboardManagerProvider;

  public HomeViewModel_Factory(Provider<Application> appProvider,
      Provider<SKeyboardManager> sKeyboardManagerProvider) {
    this.appProvider = appProvider;
    this.sKeyboardManagerProvider = sKeyboardManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(appProvider.get(), sKeyboardManagerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<Application> appProvider,
      Provider<SKeyboardManager> sKeyboardManagerProvider) {
    return new HomeViewModel_Factory(appProvider, sKeyboardManagerProvider);
  }

  public static HomeViewModel newInstance(Application app, SKeyboardManager sKeyboardManager) {
    return new HomeViewModel(app, sKeyboardManager);
  }
}
