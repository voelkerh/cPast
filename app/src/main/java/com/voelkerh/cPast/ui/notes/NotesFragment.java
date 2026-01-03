package com.voelkerh.cPast.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.domain.Capture;
import com.voelkerh.cPast.domain.RecentCapturesRepository;

import java.util.List;

public class NotesFragment extends Fragment {

    RecentCapturesRepository recentCapturesRepository;

    public NotesFragment(RecentCapturesRepository recentCapturesRepository) {
        this.recentCapturesRepository = recentCapturesRepository;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        TextView notesInfo = view.findViewById(R.id.notes_text_info);
        notesInfo.setText(HtmlCompat.fromHtml(
                getString(R.string.notes_fragment_text_info),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));

        TextView notesText = view.findViewById(R.id.notes_text_files);
        notesText.setText(HtmlCompat.fromHtml(
                getString(R.string.notes_fragment_text_files),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));

        TextView notesList = view.findViewById(R.id.notes_capture_list);
        notesList.setText(getFileList());

        return view;
    }

    private String getFileList() {
        List<Capture> recentFiles = recentCapturesRepository.getRecentCaptures();

        StringBuilder sb = new StringBuilder();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            Capture capture = recentFiles.get(i);
            sb.append("File: ").append(capture.getFileName()).append("\n");
            sb.append("Note: ").append(capture.getNote()).append("\n\n");
        }

        return sb.toString();
    }
}