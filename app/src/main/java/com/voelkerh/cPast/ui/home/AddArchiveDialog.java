package com.voelkerh.cPast.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import com.voelkerh.cPast.R;

/**
 * Dialog that allows to add a new archive.
 *
 * <p>The content to display contains HTML markup and is rendered using {@link HtmlCompat}.</p>
 *
 * <p>This dialog is part of the UI layer and does not contain any business logic.</p>
 */
public class AddArchiveDialog {

    /**
     * Displays the add dialog with input fields.
     *
     * @param context context used to build and display the dialog
     */
    public static void show(Context context, Listener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView addArchiveHeading = new TextView(context);
        addArchiveHeading.setText(Html.fromHtml(context.getString(R.string.add_repo_heading), Html.FROM_HTML_MODE_LEGACY));
        addArchiveHeading.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
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

        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String fullName = fullArchiveNameInput.getText().toString();
            String shortName = shortArchiveNameInput.getText().toString();
            if (listener != null) {
                listener.onArchiveCreated(fullName, shortName);
            }
        });
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        neutralButton.setTextColor(Color.GRAY);
    }

    /**
     * Callback interface for user actions performed in the add archive dialog.
     *
     * <p>Implementations are responsible for handling add actions initiated by the user.
     * No validation or persistence logic is performed by the dialog itself.</p>
     */
    public interface Listener {

        /**
         * Called when the user requests adding an archive.
         *
         * @param fullName full name of the archive to add
         * @param shortName short name of the archive to add
         */
        void onArchiveCreated(String fullName, String shortName);
    }
}