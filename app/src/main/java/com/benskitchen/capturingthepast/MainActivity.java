package com.benskitchen.capturingthepast;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.benskitchen.capturingthepast.domainLogic.ArchiveRepository;
import com.benskitchen.capturingthepast.domainLogic.ImageRepository;
import com.benskitchen.capturingthepast.persistence.JsonArchiveStore;
import com.benskitchen.capturingthepast.persistence.LogWriter;
import com.benskitchen.capturingthepast.persistence.MediaImageStore;
import com.benskitchen.capturingthepast.persistence.SettingsRepository;
import com.benskitchen.capturingthepast.domainLogic.CaptureCounter;
import com.benskitchen.capturingthepast.domainLogic.RecordReferenceCreator;
import com.benskitchen.capturingthepast.ui.AddArchiveDialog;
import com.benskitchen.capturingthepast.ui.ArchiveAdapter;
import com.benskitchen.capturingthepast.ui.EditArchiveDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import info.androidhive.fontawesome.FontDrawable;
import capturingthepast.R;

public class MainActivity extends AppCompatActivity implements AddArchiveDialog.Listener, EditArchiveDialog.Listener {

    // UI variables
    Spinner dropdown;
    EditText tvRecordReference;

    // Variables needed for file names and metadata
    private String strNote = "";
    char[] alphabet = new char[26];

    // Domain logic dependencies
    private CaptureCounter captureCounter;
    private ArchiveRepository archiveRepository;

    private ImageRepository.TempImageInfo tempImageInfo;

    // Data layer dependencies
    SettingsRepository settingsRepository;
    ImageRepository imageRepository;
    LogWriter logWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAppRepos();
        initState();
        initViews();
    }

    private void initAppRepos(){
        JsonArchiveStore jsonArchiveStore = new JsonArchiveStore(getApplicationContext());
        archiveRepository = new ArchiveRepository(jsonArchiveStore);
        settingsRepository = new SettingsRepository(this);
        MediaImageStore mediaImageStore = new MediaImageStore(getApplicationContext());
        imageRepository = new ImageRepository(getApplicationContext(), mediaImageStore);
    }

    private void initState(){
        int i = 0;
        for (char letter = 'a'; letter <= 'z'; letter++) {
            alphabet[i++] = letter;
        }

        logWriter = new LogWriter(this);
        captureCounter = new CaptureCounter(settingsRepository);
    }

    private void initViews(){
        dropdown = findViewById(R.id.spinnerArchive);
        tvRecordReference = findViewById(R.id.editTextRef);
        TextView recordReferenceText = findViewById(R.id.textViewRef);
        TextView noteText = findViewById(R.id.textViewNote);
        TextView recordReferenceLabel = findViewById(R.id.refLabel);
        Button cameraButton = findViewById(R.id.cameraButton);
        Button filesButton = findViewById(R.id.filesButton);
        Button infoButton = findViewById(R.id.infoButton);
        Button btnClearNote = findViewById(R.id.buttonClearNote);
        Button btnClearRef = findViewById(R.id.buttonClearRef);
        FontDrawable drawable = new FontDrawable(this, R.string.fa_paper_plane_solid, true, false);
        drawable.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        ArrayAdapter<String> dataAdapter =
                new ArchiveAdapter(this, archiveRepository.readArchives(), this, this);
        dropdown.setAdapter(dataAdapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordReferenceText.setText(createFileName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strNote = noteText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnClearNote.setOnClickListener(v -> noteText.setText(""));

        recordReferenceLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.ref_description_heading), getString(R.string.ref_description_text)));
        tvRecordReference.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recordReferenceText.setText(createFileName());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnClearRef.setOnClickListener(v -> tvRecordReference.setText(""));

        cameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
        filesButton.setOnClickListener(v -> {
            try {
                openGallery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        infoButton.setOnClickListener(v -> showInfo());
    }

    private void showMessage(String str) {
        Toast.makeText(this, str, LENGTH_SHORT).show();
    }

    private void showDataEntryToolTips(String heading, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        TextView tvTip = new TextView(this);
        String tipText = "<h4>" + heading + "</h4><p>" + message + "</p>";
        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(tipText, Html.FROM_HTML_MODE_LEGACY));
        LinearLayout lpset = new LinearLayout(this);
        lpset.setOrientation(LinearLayout.VERTICAL);
        lpset.addView(tvTip);
        lpset.setPadding(50, 80, 50, 10);
        alertDialog.setView(lpset);
        alertDialog.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
        startActivity(intent);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            tempImageInfo = imageRepository.getTempImageInfo();
            if (tempImageInfo != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageInfo.uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            else showMessage("No photoURI to capture image");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (tempImageInfo == null) {
                Toast.makeText(this, "Error: No image path available", LENGTH_SHORT).show();
                return;
            }
            String imageFileName = createFileName();
            try {
                boolean saved = imageRepository.saveImageToGallery(imageFileName, strNote, tempImageInfo.path, "CapturingThePast");
                if (saved) {
                    settingsRepository.addFileToRecentFiles(imageFileName);
                    triggerWriteLog(imageFileName);
                    tempImageInfo = null;
                    Toast.makeText(this, "Image saved", LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error: Image not saved.\n" + e.getMessage(), LENGTH_SHORT).show();
            }
        }
    }

    public void triggerWriteLog(String imageFileName) {
        String humanisedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());
        String strCSV = "\"" + humanisedTime + "\"" + imageFileName + "\",\"" + strNote + "\"";
        String message = logWriter.writePublicLog(strCSV);
        if (!message.isEmpty()) Toast.makeText(this, message, LENGTH_SHORT).show();
    }

    @Override
    public void onArchiveCreated(String fullName, String shortName) {
        boolean created = archiveRepository.createArchive(fullName, shortName);
        if (created) {
            Snackbar.make(dropdown, fullName + " created", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(dropdown, fullName + " could not be created", Snackbar.LENGTH_SHORT).show();
        }
        updateDropdown();
    }

    @Override
    public void onArchiveEdited(String oldFullName, String oldShortName, String shortArchiveName, String fullArchiveName) {
        archiveRepository.updateArchive(oldFullName, oldShortName, shortArchiveName, fullArchiveName);
        updateDropdown();
        Snackbar snack = Snackbar.make(dropdown, fullArchiveName + " updated", Snackbar.LENGTH_SHORT);
        snack.show();
    }

    @Override
    public void onArchiveDeleted(String fullArchiveName) {
        archiveRepository.deleteArchive(fullArchiveName);
        updateDropdown();
        Snackbar snack = Snackbar.make(dropdown, getString(R.string.deleted)+ " - " + fullArchiveName, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    private void updateDropdown(){
        ArrayAdapter<String> dataAdapter =
                new ArchiveAdapter(this, archiveRepository.readArchives(), this, this);
        dropdown.setAdapter(dataAdapter);
    }

    public void showInfo() {
        StringBuilder sb = new StringBuilder();
        List<String> recentFiles = settingsRepository.getRecentFiles();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            sb.append(recentFiles.get(i)).append("\n");
        }
        String folderStatus = getString(R.string.latest_captures_message) + sb; //"Latest captures (Most recent first):\n" + sb;
        String strMessage = "<p>" + captureCounter.getCaptureCount() + "</p> ";
        showFolderStatusMessage(strMessage, folderStatus);
    }

    private void showFolderStatusMessage(String strMessage, String strReport) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        TextView tvTip = new TextView(this);
        String sLink = getString(R.string.resources_note);//"<h3>Resources</h3>"; //resources_note
        TextView tvLogInfo = new TextView(this);
        String strLogInfo = getString(R.string.log_information);//"<h3>Capture Log</h3><p>A log (called CapturingThePast) of all captures is saved in your Documents folder. " +
        //"Delete the log to reset it, or rename it to preserve it and start a fresh one. " +
        //"</p>"; //log_information
        tvLogInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogInfo.setText(Html.fromHtml(strLogInfo, Html.FROM_HTML_MODE_LEGACY));

        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(sLink, Html.FROM_HTML_MODE_LEGACY));
        TextView tvList = new TextView(this);
        tvList.setText(strReport);
        TextView tvCaptureCount = new TextView(this);
        tvCaptureCount.setMovementMethod(LinkMovementMethod.getInstance());
        tvCaptureCount.setText(Html.fromHtml(strMessage, Html.FROM_HTML_MODE_LEGACY));
        String fLink = getString(R.string.footer_link);//"<p><br/>Capturing the Past is a <a href=https://www.sussex.ac.uk/research/centres/sussex-humanities-lab/ >Sussex Humanities Lab</a> project funded by the <a href=https://ahrc.ukri.org/ >Arts and Humanities Research Council</a>.</p>";
        TextView tvHeader = new TextView(this);
        tvHeader.setMovementMethod(LinkMovementMethod.getInstance());
        tvHeader.setText(Html.fromHtml(fLink, Html.FROM_HTML_MODE_LEGACY));
        String fCap = getString(R.string.counter_label);//"<h4>Capture Counter</h4>";
        TextView tvCap = new TextView(this);
        tvCap.setMovementMethod(LinkMovementMethod.getInstance());
        tvCap.setText(Html.fromHtml(fCap, Html.FROM_HTML_MODE_LEGACY));
        final Button btnResetCount = new Button(this);
        btnResetCount.setText(getString(R.string.reset));
        btnResetCount.setAllCaps(false);
        LinearLayout counterReset = new LinearLayout(this);
        counterReset.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout lpset = new LinearLayout(this);
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
        alertDialog.setNegativeButton(getString(R.string.close), (dialog, which) -> dialog.cancel());
        btnResetCount.setOnClickListener(view -> {
            captureCounter.setCaptureCount(0);
            settingsRepository.addFileToRecentFiles("");
            String strCaptureCount = "<p>" + captureCounter.getCaptureCount() + "</p> ";
            tvCaptureCount.setMovementMethod(LinkMovementMethod.getInstance());
            tvCaptureCount.setText(Html.fromHtml(strCaptureCount, Html.FROM_HTML_MODE_LEGACY));
        });
        alertDialog.show();
    }

    private String createFileName() {
        String strArchiveShort = getShortArchiveName();
        String strRecordReference = tvRecordReference.getText().toString();
        String strCounter = "0"; // placeholder, replace when image counter is implemented
        String catRef = RecordReferenceCreator.createRecordReference(strArchiveShort, strRecordReference, strCounter);
        if (catRef.length() > 128) showMessage("Your catalogue reference is very long and may result in unusable file names.");
        return catRef;
    }

    private String getShortArchiveName(){
        Object item = dropdown.getSelectedItem();
        if (item == null) return "";

        String selectedArchive = item.toString();
        if (selectedArchive.equals("Select Archive")) return "";
        String[] parts = selectedArchive.split("-");
        if (parts.length >= 2) {
            return parts[1].trim();
        } else {
            return selectedArchive.trim();
        }
    }
}
