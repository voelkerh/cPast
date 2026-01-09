package com.voelkerh.cPast.ui.about;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import com.voelkerh.cPast.R;

/**
 * Fragment that displays background information and acknowledgments.
 *
 * <p>The content to display contains HTML markup and is rendered using {@link HtmlCompat}.</p>
 *
 * <p>This fragment is part of the UI layer and does not contain any business logic.</p>
 */
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
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}