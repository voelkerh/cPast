package com.voelkerh.cPast.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.voelkerh.cPast.ui.home.HomeViewModel;
import com.voelkerh.cPast.ui.notes.NotesViewModel;

/**
 * Factory for creating ViewModels with dependencies for ui fragments.
 * Gets dependencies from AppModule automatically so the fragments do not need to know them.
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotesViewModel.class)) {
            return (T) new NotesViewModel(
                    AppModule.getInstance().getRecentCapturesRepository()
            );
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(
                    AppModule.getInstance().getArchiveRepository(),
                    AppModule.getInstance().getImageRepository(),
                    AppModule.getInstance().getNotesRepository(),
                    AppModule.getInstance().getRecentCapturesRepository()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
