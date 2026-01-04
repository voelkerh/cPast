package com.voelkerh.cPast.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;

import java.util.List;

public class NotesViewModel extends ViewModel {

    private final RecentCapturesRepository recentCapturesRepository;

    private final MutableLiveData<String> recentCaptures = new MutableLiveData<>();

    public NotesViewModel(RecentCapturesRepository recentCapturesRepository) {
        this.recentCapturesRepository = recentCapturesRepository;
        loadRecentCaptures();
    }

    public LiveData<String> getRecentCaptures() {
        return recentCaptures;
    }

    private void loadRecentCaptures() {
        List<Capture> recentFiles = recentCapturesRepository.getRecentCaptures();

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
