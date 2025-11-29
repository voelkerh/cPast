package com.benskitchen.capturingthepast.ui;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import capturingthepast.R;

public class AddArchiveDialog {

    public interface Listener {
        void onArchiveCreated(String fullName, String shortName);
    }

    public static void show(Context context, int headingColor, Listener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        TextView addArchiveHeading = new TextView(context);
        addArchiveHeading.setText(Html.fromHtml(context.getString(R.string.add_repo_heading), Html.FROM_HTML_MODE_LEGACY));
        addArchiveHeading.setTextColor(headingColor);
        addArchiveHeading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView fullArchiveNameLabel = new TextView(context);
        fullArchiveNameLabel.setText(Html.fromHtml(context.getString(R.string.full_archive_name_label), Html.FROM_HTML_MODE_LEGACY));
        fullArchiveNameLabel.setTextSize(18f);

        EditText fullArchiveNameInput = new EditText(context);
        fullArchiveNameInput.setHint(R.string.full_archive_name_hint);

        TextView shortArchiveNameLabel = new TextView(context);
        shortArchiveNameLabel.setText(Html.fromHtml(context.getString(R.string.short_archive_name_label), Html.FROM_HTML_MODE_LEGACY));
        shortArchiveNameLabel.setTextSize(18f);

        EditText shortArchiveNameInput = new EditText(context);
        shortArchiveNameInput.setHint(R.string.short_archive_name_hint);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        int topMarginInDp = 16;
        float scale = context.getResources().getDisplayMetrics().density;
        int topMarginInPx = (int) (topMarginInDp * scale + 0.5f);
        labelParams.setMargins(0, topMarginInPx, 0, 0);
        shortArchiveNameLabel.setLayoutParams(labelParams);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(addArchiveHeading);
        layout.addView(fullArchiveNameLabel);
        layout.addView(fullArchiveNameInput);
        layout.addView(shortArchiveNameLabel);
        layout.addView(shortArchiveNameInput);
        layout.setPadding(50, 80, 50, 10);

        alertDialog.setView(layout);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {
            String fullName = fullArchiveNameInput.getText().toString();
            String shortName = shortArchiveNameInput.getText().toString();
            if (listener != null) {
                listener.onArchiveCreated(fullName, shortName);
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }
}