package com.benskitchen.capturingthepast.ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import capturingthepast.R;

public class ConfirmDeleteArchiveDialog {

    public static void show(Context context, EditArchiveDialog.Listener listener, String fullArchiveName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView editArchiveHeading = new TextView(context);
        String titleText = context.getString(R.string.heading_delete_archive);
        editArchiveHeading.setMovementMethod(LinkMovementMethod.getInstance());
        editArchiveHeading.setText(Html.fromHtml(titleText, Html.FROM_HTML_MODE_LEGACY));
        editArchiveHeading.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
        editArchiveHeading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView deleteArchiveWarning = new TextView(context);
        deleteArchiveWarning.setText(Html.fromHtml(context.getString(R.string.delete_archive_warning), Html.FROM_HTML_MODE_LEGACY));
        deleteArchiveWarning.setTextSize(18f);

        TextView archiveNameLabel = new TextView(context);
        archiveNameLabel.setText(fullArchiveName);
        archiveNameLabel.setTextSize(18f);
        archiveNameLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        archiveNameLabel.setTextColor(ContextCompat.getColor(context, R.color.fontColor));

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        int topMarginInDp = 16;
        float scale = context.getResources().getDisplayMetrics().density;
        int topMarginInPx = (int) (topMarginInDp * scale + 0.5f);
        labelParams.setMargins(0, topMarginInPx, 0, 0);
        archiveNameLabel.setLayoutParams(labelParams);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editArchiveHeading);
        linearLayout.addView(deleteArchiveWarning);
        linearLayout.addView(archiveNameLabel);

        linearLayout.setPadding(50, 80, 50, 10);
        builder.setView(linearLayout);

        builder.setNegativeButton("Delete", (dialog, which) -> {
            if (listener != null) {
                listener.onArchiveDeleted(fullArchiveName);
            }
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
        neutralButton.setTextColor(Color.GRAY);
    }
}
