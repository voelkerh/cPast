package com.benskitchen.capturingthepast.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import capturingthepast.R;

import java.util.List;

public class EditArchiveDialog {


    public interface Listener {
        void onArchiveEdited(String oldFullName, String shortArchiveName, String fullArchiveName);
        void onArchiveDeleted(String fullArchiveName);
    }

    public static void show(Context context, List<String> archives, int headingColor, Listener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView editArchiveHeading = new TextView(context);
        String titleText = context.getString(R.string.heading_edit_archive);
        editArchiveHeading.setMovementMethod(LinkMovementMethod.getInstance());
        editArchiveHeading.setText(Html.fromHtml(titleText, Html.FROM_HTML_MODE_LEGACY));
        editArchiveHeading.setTextColor(headingColor);
        editArchiveHeading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Spinner spinnerArchiveSelect = new Spinner(context);
        ArrayAdapter<String> dataAdapterR = new ArchiveAdapter(context, archives, headingColor, (AddArchiveDialog.Listener) context, (Listener) context);
        spinnerArchiveSelect.setAdapter(dataAdapterR);
        spinnerArchiveSelect.setPadding(0, 8, 8, 24);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        int topMarginInDp = 16;
        float scale = context.getResources().getDisplayMetrics().density;
        int topMarginInPx = (int) (topMarginInDp * scale + 0.5f);
        labelParams.setMargins(0, topMarginInPx, 0, 0);
        spinnerArchiveSelect.setLayoutParams(labelParams);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editArchiveHeading);
        linearLayout.addView(spinnerArchiveSelect);

        linearLayout.setPadding(50, 80, 50, 10);
        builder.setView(linearLayout);

        int[] selectedRepo = {-1};
        spinnerArchiveSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRepo[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setNegativeButton("Delete", (dialog, which) -> {
            String archiveToDelete = spinnerArchiveSelect.getSelectedItem().toString();
            String fullArchiveName = archiveToDelete.split("-")[0].trim();
            if (listener != null) {
                listener.onArchiveDeleted(fullArchiveName);
            }
        });

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String archiveToUpdate = spinnerArchiveSelect.getSelectedItem().toString();
            String fullArchiveName = archiveToUpdate.split("-")[0].trim();
            String shortArchiveName = archiveToUpdate.split("-")[1].trim();
            if (listener != null) {
                listener.onArchiveEdited(archiveToUpdate, shortArchiveName, fullArchiveName);
            }
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        negativeButton.setTextColor(Color.RED);
        neutralButton.setTextColor(Color.GRAY);
    }
}
