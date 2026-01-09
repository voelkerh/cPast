package com.voelkerh.cPast.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.usecase.ManageRecentCapturesUseCase;

import java.util.List;

/**
 * ViewModel that provides information about recent captures to {@link NotesFragment}.
 *
 * <p>The ViewModel loads a list of recent {@link Capture} objects from {@link ManageRecentCapturesUseCase} upon creation.
 * It transforms the data into a textual representation and then provides it via {@link MutableLiveData}.</p>
 */
public class NotesViewModel extends ViewModel {

    private final ManageRecentCapturesUseCase manageRecentCapturesUseCase;

    private final MutableLiveData<String> recentCaptures = new MutableLiveData<>();

    /**
     * Creates the ViewModel and loads recent captures using the provided repository.
     *
     * @param manageRecentCapturesUseCase use case used to retrieve recent capture data
     */
    public NotesViewModel(ManageRecentCapturesUseCase manageRecentCapturesUseCase) {
        this.manageRecentCapturesUseCase = manageRecentCapturesUseCase;
        loadRecentCaptures();
    }

    /**
     * Provides list of recent captures as textual representation for observation by UI fragments.
     *
     * @return observable {@link LiveData} with formatted recent capture information
     */
    public LiveData<String> getRecentCaptures() {
        return recentCaptures;
    }

    private void loadRecentCaptures() {
        List<Capture> recentFiles = manageRecentCapturesUseCase.getRecentCaptures();

        if (recentFiles.isEmpty()) {
            recentCaptures.setValue("No recent captures");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            Capture capture = recentFiles.get(i);
            sb.append("File: ").append(capture.getFileName()).append("\n");
            sb.append("Note: ").append(capture.getNote()).append("\n\n");
        }

        recentCaptures.setValue(sb.toString());
    }
}
