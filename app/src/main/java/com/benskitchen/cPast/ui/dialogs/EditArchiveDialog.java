package com.benskitchen.cPast.ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import capturingthepast.R;

public class EditArchiveDialog {


    public interface Listener {
        void onArchiveEdited(String oldFullName, String oldShortName, String shortArchiveName, String fullArchiveName);
        void onArchiveDeleted(String fullArchiveName);
    }

    public static void show(Context context, String archiveName, Listener listener) {
        String fullArchiveName = archiveName.split("-")[0].trim();
        String shortArchiveName = archiveName.split("-")[1].trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView editArchiveHeading = new TextView(context);
        String titleText = context.getString(R.string.heading_edit_archive);
        editArchiveHeading.setMovementMethod(LinkMovementMethod.getInstance());
        editArchiveHeading.setText(Html.fromHtml(titleText, Html.FROM_HTML_MODE_LEGACY));
        editArchiveHeading.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        editArchiveHeading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView fullArchiveNameLabel = new TextView(context);
        fullArchiveNameLabel.setText(Html.fromHtml(context.getString(R.string.full_archive_name_label), Html.FROM_HTML_MODE_LEGACY));
        fullArchiveNameLabel.setTextSize(18f);

        EditText fullArchiveNameInput = new EditText(context);
        fullArchiveNameInput.setText(fullArchiveName);


        TextView shortArchiveNameLabel = new TextView(context);
        shortArchiveNameLabel.setText(Html.fromHtml(context.getString(R.string.short_archive_name_label), Html.FROM_HTML_MODE_LEGACY));
        shortArchiveNameLabel.setTextSize(18f);

        EditText shortArchiveNameInput = new EditText(context);
        shortArchiveNameInput.setText(shortArchiveName);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        int topMarginInDp = 16;
        float scale = context.getResources().getDisplayMetrics().density;
        int topMarginInPx = (int) (topMarginInDp * scale + 0.5f);
        labelParams.setMargins(0, topMarginInPx, 0, 0);
        shortArchiveNameLabel.setLayoutParams(labelParams);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editArchiveHeading);
        linearLayout.addView(fullArchiveNameLabel);
        linearLayout.addView(fullArchiveNameInput);
        linearLayout.addView(shortArchiveNameLabel);
        linearLayout.addView(shortArchiveNameInput);

        linearLayout.setPadding(50, 80, 50, 10);
        builder.setView(linearLayout);

        builder.setNegativeButton("Delete", (dialog, which) -> {
            if (listener != null) {
                ConfirmDeleteArchiveDialog.show(context, listener, fullArchiveName);
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (listener != null) {
                listener.onArchiveEdited(fullArchiveName, shortArchiveName, String.valueOf(shortArchiveNameInput.getText()), String.valueOf(fullArchiveNameInput.getText()));
            }
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
        neutralButton.setTextColor(Color.GRAY);
    }
}
