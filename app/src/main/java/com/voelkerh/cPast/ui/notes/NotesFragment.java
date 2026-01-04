package com.voelkerh.cPast.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.di.ViewModelFactory;

public class NotesFragment extends Fragment {

    NotesViewModel notesViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            notesViewModel = new ViewModelProvider(this, new ViewModelFactory())
                    .get(NotesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        TextView notesInfo = view.findViewById(R.id.notes_text_info);
        notesInfo.setText(HtmlCompat.fromHtml(getString(R.string.notes_fragment_text_info), HtmlCompat.FROM_HTML_MODE_COMPACT));

        TextView notesText = view.findViewById(R.id.notes_text_files);
        notesText.setText(HtmlCompat.fromHtml(getString(R.string.notes_fragment_text_files), HtmlCompat.FROM_HTML_MODE_COMPACT));

        TextView notesList = view.findViewById(R.id.notes_capture_list);
        notesViewModel.getRecentCaptures().observe(getViewLifecycleOwner(), notesList::setText);

        return view;
    }

}