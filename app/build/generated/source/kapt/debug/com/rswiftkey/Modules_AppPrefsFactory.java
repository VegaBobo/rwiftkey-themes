package com.rswiftkey;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;

import com.rswiftkey.core.AppPreferences;
import com.rswiftkey.di.Modules;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class Modules_AppPrefsFactory implements Factory<AppPreferences> {
  private final Modules module;

  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public Modules_AppPrefsFactory(Modules module,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.module = module;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public AppPreferences get() {
    return appPrefs(module, dataStoreProvider.get());
  }

  public static Modules_AppPrefsFactory create(Modules module,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new Modules_AppPrefsFactory(module, dataStoreProvider);
  }

  public static AppPreferences appPrefs(Modules instance, DataStore<Preferences> dataStore) {
    return Preconditions.checkNotNullFromProvides(instance.appPrefs(dataStore));
  }
}
