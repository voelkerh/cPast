package com.benskitchen.capturingthepast.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import capturingthepast.R;
import com.benskitchen.capturingthepast.MainActivity;

import java.util.List;

public class InfoDialog {

    public static void show(Context context, List<String> recentFiles, int captureCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            sb.append(recentFiles.get(i)).append("\n");
        }
        String folderStatus = context.getString(R.string.latest_captures_message) + sb; //"Latest captures (Most recent first):\n" + sb;
        String message = "<p>" + captureCount + "</p> ";
        showFolderStatusMessage(context, message, folderStatus, captureCount);
    }

    private static void showFolderStatusMessage(Context context, String message, String report, int captureCount) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        TextView tvTip = new TextView(context);
        String sLink = context.getString(R.string.resources_note);
        TextView tvLogInfo = new TextView(context);
        String strLogInfo = context.getString(R.string.log_information);//"<h3>Capture Log</h3><p>A log (called CapturingThePast) of all captures is saved in your Documents folder. " +
        //"Delete the log to reset it, or rename it to preserve it and start a fresh one. " +
        //"</p>"; //log_information
        tvLogInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogInfo.setText(Html.fromHtml(strLogInfo, Html.FROM_HTML_MODE_LEGACY));

        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(sLink, Html.FROM_HTML_MODE_LEGACY));
        TextView tvList = new TextView(context);
        tvList.setText(report);
        TextView tvCaptureCount = new TextView(context);
        tvCaptureCount.setMovementMethod(LinkMovementMethod.getInstance());
        tvCaptureCount.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
        String fLink = context.getString(R.string.footer_link);//"<p><br/>Capturing the Past is a <a href=https://www.sussex.ac.uk/research/centres/sussex-humanities-lab/ >Sussex Humanities Lab</a> project funded by the <a href=https://ahrc.ukri.org/ >Arts and Humanities Research Council</a>.</p>";
        TextView tvHeader = new TextView(context);
        tvHeader.setMovementMethod(LinkMovementMethod.getInstance());
        tvHeader.setText(Html.fromHtml(fLink, Html.FROM_HTML_MODE_LEGACY));
        String fCap = context.getString(R.string.counter_label);//"<h4>Capture Counter</h4>";
        TextView tvCap = new TextView(context);
        tvCap.setMovementMethod(LinkMovementMethod.getInstance());
        tvCap.setText(Html.fromHtml(fCap, Html.FROM_HTML_MODE_LEGACY));
        final Button btnResetCount = new Button(context);
        btnResetCount.setText(context.getString(R.string.reset));
        btnResetCount.setAllCaps(false);
        LinearLayout counterReset = new LinearLayout(context);
        counterReset.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout lpset = new LinearLayout(context);
        lpset.setOrientation(LinearLayout.VERTICAL);
        tvTip.setTextSize(16f);
        tvCap.setTextSize(16f);
        lpset.addView(tvTip);
        lpset.addView(tvLogInfo);

        tvCaptureCount.setWidth(150);
        tvCaptureCount.setGravity(1);
        btnResetCount.setBackgroundColor(0); //setHeight(50)
        btnResetCount.setTextColor(Color.DKGRAY);
        counterReset.addView(tvCap);
        counterReset.addView(tvCaptureCount);
        counterReset.addView(btnResetCount);
        lpset.addView(counterReset);
        lpset.addView(tvList);
        tvHeader.setTextSize(11.0f);
        lpset.addView(tvHeader);
        lpset.setPadding(40, 40, 40, 16);
        alertDialog.setView(lpset);
        alertDialog.setNegativeButton(context.getString(R.string.close), (dialog, which) -> dialog.cancel());
        btnResetCount.setOnClickListener(view -> {
            String strCaptureCount = "<p>" + captureCount + "</p> ";
            tvCaptureCount.setMovementMethod(LinkMovementMethod.getInstance());
            tvCaptureCount.setText(Html.fromHtml(strCaptureCount, Html.FROM_HTML_MODE_LEGACY));
        });
        alertDialog.show();
    }
}
