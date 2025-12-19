package com.benskitchen.capturingthepast.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import capturingthepast.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView aboutText = view.findViewById(R.id.about_text);
        aboutText.setText(HtmlCompat.fromHtml(
                getString(R.string.about_fragment_text),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));

        return view;
    }
}