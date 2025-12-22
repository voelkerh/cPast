package com.benskitchen.cPast.ui.fragments;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import capturingthepast.R;

public class HelpFragment extends Fragment {

    private View currentlyOpenBody = null;
    private ImageView currentlyOpenArrow = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        ScrollView scrollView = view.findViewById(R.id.help_scroll);

        setupAccordionElement(view, scrollView, R.id.acc_header_1, R.id.acc_body_1, R.id.acc_arrow_1);
        setHtml(view.findViewById(R.id.acc_body_1_text), R.string.help_fragment_acc_body_1_text);

        setupAccordionElement(view, scrollView, R.id.acc_header_2, R.id.acc_body_2, R.id.acc_arrow_2);
        setHtml(view.findViewById(R.id.acc_body_2_text), R.string.help_fragment_acc_body_2_text);

        setupAccordionElement(view, scrollView, R.id.acc_header_3, R.id.acc_body_3, R.id.acc_arrow_3);
        setHtml(view.findViewById(R.id.acc_body_3_text), R.string.help_fragment_acc_body_3_text);

        setupAccordionElement(view, scrollView, R.id.acc_header_4, R.id.acc_body_4, R.id.acc_arrow_4);
        setHtml(view.findViewById(R.id.acc_body_4_text), R.string.help_fragment_acc_body_4_text);

        setupAccordionElement(view, scrollView, R.id.acc_header_5, R.id.acc_body_5, R.id.acc_arrow_5);
        setHtml(view.findViewById(R.id.acc_body_5_text), R.string.help_fragment_acc_body_5_text);

        return view;
    }

    private void setupAccordionElement(View root, ScrollView scrollView, int headerId, int bodyId, int arrowId) {
        View header = root.findViewById(headerId);
        ImageView arrow = root.findViewById(arrowId);
        View body = root.findViewById(bodyId);

        header.setOnClickListener(v -> {
            boolean isThisOpen = body.getVisibility() == View.VISIBLE;

            if (currentlyOpenBody != null && currentlyOpenBody != body) {
                currentlyOpenBody.setVisibility(View.GONE);
                if (currentlyOpenArrow != null) currentlyOpenArrow.setRotation(0f);
            }

            if (isThisOpen) {
                body.setVisibility(View.GONE);
                arrow.setRotation(0f);
                currentlyOpenBody = null;
                currentlyOpenArrow = null;
            } else {
                body.setVisibility(View.VISIBLE);
                arrow.setRotation(180f);
                currentlyOpenBody = body;
                currentlyOpenArrow = arrow;

                scrollView.post(() -> scrollView.smoothScrollTo(0, header.getTop()));
            }
        });
    }

    private void setHtml(TextView tv, int stringRes) {
        tv.setText(HtmlCompat.fromHtml(getString(stringRes), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }


}