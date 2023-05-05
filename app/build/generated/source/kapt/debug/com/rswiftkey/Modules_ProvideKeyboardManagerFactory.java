package com.rswiftkey;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;

import com.rswiftkey.core.SKeyboardManager;
import com.rswiftkey.di.Modules;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class Modules_ProvideKeyboardManagerFactory implements Factory<SKeyboardManager> {
  private final Modules module;

  private final Provider<Context> appContextProvider;

  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public Modules_ProvideKeyboardManagerFactory(Modules module, Provider<Context> appContextProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.module = module;
    this.appContextProvider = appContextProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public SKeyboardManager get() {
    return provideKeyboardManager(module, appContextProvider.get(), dataStoreProvider.get());
  }

  public static Modules_ProvideKeyboardManagerFactory create(Modules module,
      Provider<Context> appContextProvider, Provider<DataStore<Preferences>> dataStoreProvider) {
    return new Modules_ProvideKeyboardManagerFactory(module, appContextProvider, dataStoreProvider);
  }

  public static SKeyboardManager provideKeyboardManager(Modules instance, Context appContext,
      DataStore<Preferences> dataStore) {
    return Preconditions.checkNotNullFromProvides(instance.provideKeyboardManager(appContext, dataStore));
  }
}
