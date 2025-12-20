package com.benskitchen.capturingthepast.ui.dialogs;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import capturingthepast.R;

import java.util.List;

public class InfoDialog {

    public static void show(Context context, List<String> recentFiles) {
        StringBuilder sb = new StringBuilder();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            sb.append(recentFiles.get(i)).append("\n");
        }
        String folderStatus = context.getString(R.string.latest_captures_message) + sb;
        showFolderStatusMessage(context, folderStatus);
    }

    private static void showFolderStatusMessage(Context context, String report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView heading = new TextView(context);
        heading.setMovementMethod(LinkMovementMethod.getInstance());
        heading.setText(Html.fromHtml(context.getString(R.string.info_heading), Html.FROM_HTML_MODE_LEGACY));
        heading.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        heading.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        TextView notesInfo = new TextView(context);
        notesInfo.setMovementMethod(LinkMovementMethod.getInstance());
        notesInfo.setText(Html.fromHtml(context.getString(R.string.notes_information), Html.FROM_HTML_MODE_LEGACY));

        TextView filesList = new TextView(context);
        filesList.setText(report);

        TextView counterLabel = new TextView(context);
        counterLabel.setMovementMethod(LinkMovementMethod.getInstance());
        counterLabel.setText(Html.fromHtml(context.getString(R.string.counter_label), Html.FROM_HTML_MODE_LEGACY));

        TextView furtherInfo = new TextView(context);
        furtherInfo.setMovementMethod(LinkMovementMethod.getInstance());
        furtherInfo.setText(Html.fromHtml(context.getString(R.string.further_information), Html.FROM_HTML_MODE_LEGACY));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(heading);
        layout.addView(notesInfo);
        layout.addView(counterLabel);
        layout.addView(filesList);
        layout.addView(furtherInfo);
        layout.setPadding(40, 40, 40, 16);

        builder.setView(layout);
        builder.setNegativeButton(context.getString(R.string.close), (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }
}
