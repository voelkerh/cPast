package com.voelkerh.cPast.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.voelkerh.cPast.ui.home.HomeViewModel;
import com.voelkerh.cPast.ui.notes.NotesViewModel;

/**
 * Factory for creating ViewModel instances with their required dependencies.
 *
 * <p>This factory centralizes ViewModel creation and resolves all required
 * dependencies via {@link AppModule}, ensuring that UI fragments do not
 * depend on use cases, repositories or their construction logic.</p>
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotesViewModel.class)) {
            return (T) new NotesViewModel(
                    AppModule.getInstance().getManageRecentCapturesUseCase()
            );
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(
                    AppModule.getInstance().getManageArchivesUseCase(),
                    AppModule.getInstance().getImageRepository(),
                    AppModule.getInstance().getWriteNotesUseCase(),
                    AppModule.getInstance().getManageRecentCapturesUseCase()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
