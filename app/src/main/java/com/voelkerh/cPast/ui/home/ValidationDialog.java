package com.voelkerh.cPast.ui.home;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import com.voelkerh.cPast.R;

/**
 * Dialog that provides information about input requirements after attempt to take photo with invalid reference information.
 *
 * <p>The content to display contains HTML markup and is rendered using {@link HtmlCompat}.</p>
 *
 * <p>This dialog is part of the UI layer and does not contain any business logic.</p>
 */
public class ValidationDialog {

    /**
     * Displays the validation dialog explaining the required input format.
     *
     * @param context context used to build and display the dialog
     */
    public static void show(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView heading = new TextView(context);
        heading.setMovementMethod(LinkMovementMethod.getInstance());
        heading.setText(Html.fromHtml(context.getString(R.string.input_warning_heading), Html.FROM_HTML_MODE_LEGACY));
        heading.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
        heading.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        TextView inputWarning = new TextView(context);
        inputWarning.setMovementMethod(LinkMovementMethod.getInstance());
        inputWarning.setText(Html.fromHtml(context.getString(R.string.input_warning_text), Html.FROM_HTML_MODE_LEGACY));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(heading);
        layout.addView(inputWarning);
        layout.setPadding(40, 40, 40, 16);

        builder.setView(layout);
        builder.setNegativeButton(context.getString(R.string.ok), (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }
}
