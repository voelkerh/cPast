package com.benskitchen.capturingthepast;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.benskitchen.capturingthepast.persistence.ImageRepository;
import com.benskitchen.capturingthepast.persistence.LogWriter;
import com.benskitchen.capturingthepast.persistence.SettingsRepository;
import com.benskitchen.capturingthepast.domainLogic.CaptureCounter;
import com.benskitchen.capturingthepast.domainLogic.CatRefCreator;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;

import info.androidhive.fontawesome.FontDrawable;
import capturingthepast.R;

public class MainActivity extends AppCompatActivity {

    // Variables needed to call reference creator
    private String strRef = "";
    private String strItem = "";
    private String strSubItem = "";
    private String strPart = "";
    private String strArchon = "GB0000";

    // Variables needed for file names and metadata
    private String strNote = "";
    char[] alphabet = new char[26];
    int nPart = 0;

    // Domain logic dependencies
    private CaptureCounter captureCounter;

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
        settingsRepository = new SettingsRepository(this);
        imageRepository = new ImageRepository(this);
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
        Spinner dropdown = findViewById(R.id.spinnerRepo);
        EditText tvCatRef = findViewById(R.id.editTextRef);
        EditText tvItemText = findViewById(R.id.editTextItem);
        EditText tvSubItemText = findViewById(R.id.editTextSubItem);
        EditText tvPart = findViewById(R.id.textViewPart);
        TextView refText = findViewById(R.id.textViewRef);
        TextView noteText = findViewById(R.id.textViewNote);
        TextView refLabel = findViewById(R.id.refLabel);
        TextView itemLabel = findViewById(R.id.itemLabel);
        TextView subitemLabel = findViewById(R.id.subItemLabel);
        TextView partLabel = findViewById(R.id.detachedLabel);
        Button decItem = findViewById(R.id.buttonDecItem);
        Button incItem = findViewById(R.id.buttonincItem);
        Button decSubItem = findViewById(R.id.buttonDecSubItem);
        Button incSubItem = findViewById(R.id.buttonincSubItem);
        Button decPart = findViewById(R.id.buttonDecPart);
        Button incPart = findViewById(R.id.buttonIncPart);
        Button camButton = findViewById(R.id.cameraButton);
        Button filesButton = findViewById(R.id.filesButton);
        Button addRepoButton = findViewById(R.id.addRepoButton);
        Button deleteRepoButton = findViewById(R.id.deleteRepoButton);
        Button infoButton = findViewById(R.id.infoButton);
        Button btnClearNote = findViewById(R.id.buttonClearNote);
        Button btnClearRef = findViewById(R.id.buttonClearRef);
        FontDrawable drawable = new FontDrawable(this, R.string.fa_paper_plane_solid, true, false);
        drawable.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, settingsRepository.getRepos());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
        strArchon = settingsRepository.getArchonAt(0);
        dropdown.setSelection(0);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strArchon = settingsRepository.getArchonAt(position);
                refText.setText(createCatRef());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Tooltips
        addRepoButton.setOnClickListener(v -> showAddRepoDialog());
        deleteRepoButton.setOnClickListener(v -> showDeleteRepoDialog());

        itemLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.item_description_heading), getString(R.string.item_descript_text)));
        subitemLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.sub_item_description_heading), getString(R.string.sub_item_description_text)));
        partLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.detached_description_heading), getString(R.string.detached_description_text)));
        tvItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strItem = tvItemText.getText().toString();
                if (!strItem.isEmpty()) {
                    try {
                        Integer.parseInt(strItem);
                    } catch (Exception e) {
                        showMessage("This control accepts numeric input. All other characters are removed");
                        strItem = strItem.replaceAll("[^\\d.]", "");
                        tvItemText.setText(strItem);
                        tvItemText.setSelection(tvItemText.length());
                    }
                }
                refText.setText(createCatRef());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tvSubItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strSubItem = tvSubItemText.getText().toString();
                if (!strSubItem.isEmpty()) {
                    try {
                        Integer.parseInt(strSubItem);
                    } catch (Exception e) {
                        strSubItem = strSubItem.replaceAll("[^\\d.]", "");
                        tvSubItemText.setText(strSubItem);
                        tvSubItemText.setSelection(tvSubItemText.length());
                        showMessage("This control accepts numeric input. All other characters are removed");
                    }
                }
                refText.setText(createCatRef());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tvPart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strPart = tvPart.getText().toString();
                refText.setText(createCatRef());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        decItem.setOnClickListener(v -> {
            try {
                int n = Integer.parseInt(tvItemText.getText().toString());
                if (n > 2) tvItemText.setText(String.valueOf(n - 1));
                else tvItemText.setText("");
            } catch (NumberFormatException e) {
                tvItemText.setText("");
            }
        });
        incItem.setOnClickListener(v -> {
            try {
                int n = Integer.parseInt(tvItemText.getText().toString());
                tvItemText.setText(String.valueOf(n + 1));
            } catch (NumberFormatException e) {
                tvItemText.setText(String.valueOf(1));
            }
        });
        decSubItem.setOnClickListener(v -> {
            try {
                int n = Integer.parseInt(tvSubItemText.getText().toString());
                if (n > 2) tvSubItemText.setText(String.valueOf(n - 1));
                else tvSubItemText.setText("");
            } catch (NumberFormatException e) {
                if (tvSubItemText.getText().toString().isEmpty()) tvSubItemText.setText("");
                else tvSubItemText.setText(String.valueOf(1));
            }
        });
        incSubItem.setOnClickListener(v -> {
            try {
                int n = Integer.parseInt(tvSubItemText.getText().toString());
                tvSubItemText.setText(String.valueOf(n + 1));
            } catch (NumberFormatException e) {
                tvSubItemText.setText(String.valueOf(1));
            }
        });
        decPart.setOnClickListener(v -> {
            nPart--;
            if (nPart < 1) {
                nPart = 0;
                strPart = "";
                tvPart.setText(strPart);
            } else if (nPart < alphabet.length) {
                strPart = "" + alphabet[nPart - 1];
                tvPart.setText(strPart);
            } else {
                strPart = alphabet[nPart % alphabet.length] + ":" + nPart / alphabet.length;
                tvPart.setText(strPart);
            }
            refText.setText(createCatRef());
        });
        incPart.setOnClickListener(v -> {
            nPart++;
            if (nPart < 1) {
                strPart = "";
                tvPart.setText(strPart);
            } else if (nPart < alphabet.length) {
                strPart = "" + alphabet[nPart - 1];
                tvPart.setText(String.valueOf(alphabet[nPart - 1]));
            } else {
                strPart = alphabet[nPart % alphabet.length] + ":" + nPart / alphabet.length;
                tvPart.setText(strPart);
            }
            refText.setText(createCatRef());
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

        refLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.ref_description_heading), getString(R.string.ref_description_text)));
        tvCatRef.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strRef = tvCatRef.getText().toString();
                refText.setText(createCatRef());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnClearRef.setOnClickListener(v -> tvCatRef.setText(""));

        camButton.setOnClickListener(v -> dispatchTakePictureIntent());
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoURI = imageRepository.getTempImageFileUri();
            if (photoURI != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                showMessage("No photoURI to capture image");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String catRef = createCatRef();
            String strPrefix = settingsRepository.getStrPrefix();
            String imageFileName = strPrefix + "_" + timeStamp + "_" + catRef + ".jpg";
            saveImageToGallery(catRef, imageFileName);
            triggerWriteLog(catRef, imageFileName);
        }
    }

    private void saveImageToGallery(String catRef, String imageFileName) {
        try {
            imageRepository.saveImageToGallery(imageFileName, catRef, strNote);
            captureCounter.incrementCaptureCount();
            settingsRepository.addFileToRecentFiles(catRef);
            Toast.makeText(this, "Image saved", LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: Image not saved.\n" + e.getMessage(), LENGTH_SHORT).show();
        }
    }

    public void triggerWriteLog(String catRef, String imageFileName) {
        String humanisedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());
        String strCSV = "\"" + humanisedTime + "\",\"" + catRef + "\",\"" + imageFileName + "\",\"" + strNote + "\"";
        String message = logWriter.writePublicLog(strCSV);
        if (!message.isEmpty()) Toast.makeText(this, message, LENGTH_SHORT).show();
    }

    private void showAddRepoDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        TextView addArchiveHeading = new TextView(this);
        addArchiveHeading.setMovementMethod(LinkMovementMethod.getInstance());
        addArchiveHeading.setText(Html.fromHtml(getString(R.string.add_repo_heading), Html.FROM_HTML_MODE_LEGACY));

        TextView fullArchiveNameLabel = new TextView(this);
        fullArchiveNameLabel.setText(R.string.full_archive_name_label);
        fullArchiveNameLabel.setTextSize(18f);

        EditText fullArchiveNameInput = new EditText(MainActivity.this);
        fullArchiveNameInput.setHint(R.string.full_archive_name_hint);

        TextView shortArchiveNameLabel = new TextView(this);
        shortArchiveNameLabel.setMovementMethod(LinkMovementMethod.getInstance());
        shortArchiveNameLabel.setText(Html.fromHtml(getString(R.string.short_archive_name_label), Html.FROM_HTML_MODE_LEGACY));
        shortArchiveNameLabel.setTextSize(18f);

        EditText shortArchiveNameInput = new EditText(MainActivity.this);
        shortArchiveNameInput.setHint(R.string.short_archive_name_hint);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(addArchiveHeading);
        linearLayout.addView(fullArchiveNameLabel);
        linearLayout.addView(fullArchiveNameInput);
        linearLayout.addView(shortArchiveNameLabel);
        linearLayout.addView(shortArchiveNameInput);
        linearLayout.setPadding(50, 80, 50, 10);

        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {
            String fullArchiveName = fullArchiveNameInput.getText().toString();
            String shortArchiveName = shortArchiveNameInput.getText().toString();
            settingsRepository.addRepository(fullArchiveName, shortArchiveName);
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void showDeleteRepoDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        TextView tvTitle = new TextView(this);
        String titleText = getString(R.string.heading_delete_archive);
        tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        tvTitle.setText(Html.fromHtml(titleText, Html.FROM_HTML_MODE_LEGACY));

        TextView labelSelect = new TextView(this);
        labelSelect.setText(R.string.select_repo);
        labelSelect.setPadding(20, 20, 0, 20);
        labelSelect.setTextSize(20f);

        Spinner spinnerRepoSelect = new Spinner(this);
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, settingsRepository.getRepos());
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepoSelect.setAdapter(dataAdapterR);
        spinnerRepoSelect.setPadding(0, 8, 8, 24);

        LinearLayout lpset = new LinearLayout(this);
        lpset.setOrientation(LinearLayout.VERTICAL);
        lpset.addView(tvTitle);
        lpset.addView(spinnerRepoSelect);

        lpset.setPadding(50, 80, 50, 10);
        alertDialog.setView(lpset);

        int[] selectedRepo = {-1};
        spinnerRepoSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRepo[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // TODO: Add delete confirmation
        alertDialog.setPositiveButton("Delete", (dialog, which) -> {
            if(selectedRepo[0] < 0) return;
            String strDeletedRepo = settingsRepository.deleteRepository(selectedRepo[0]);
            spinnerRepoSelect.setAdapter(makeNewRemovalDropDown());
            Snackbar snack = Snackbar.make(lpset, getString(R.string.deleted)+ " - " + strDeletedRepo, Snackbar.LENGTH_SHORT);
            snack.show();
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private ArrayAdapter<String> makeNewRemovalDropDown() {
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, settingsRepository.getRepos());
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapterR;
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

    private String createCatRef() {
        String catRef = CatRefCreator.createCatRef(strArchon, strRef, strItem, strSubItem, strPart);
        if (catRef.length() > 128) showMessage("Your catalogue reference is very long and may result in unusable file names.");
        return catRef;
    }
}
