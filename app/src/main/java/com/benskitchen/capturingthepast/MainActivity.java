package com.benskitchen.capturingthepast;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;

import android.net.Uri;
import android.provider.MediaStore;

import android.util.Log;
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

import com.benskitchen.capturingthepast.domainLogic.*;
import com.benskitchen.capturingthepast.persistence.*;
import com.benskitchen.capturingthepast.ui.AddArchiveDialog;
import com.benskitchen.capturingthepast.ui.ArchiveAdapter;
import com.benskitchen.capturingthepast.ui.EditArchiveDialog;

import java.io.IOException;

import capturingthepast.R;
import com.benskitchen.capturingthepast.ui.InfoDialog;

public class MainActivity extends AppCompatActivity implements AddArchiveDialog.Listener, EditArchiveDialog.Listener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "MainActivity";

    // UI components
    private Spinner dropdown;
    private EditText recordReferenceEditText;
    private TextView noteText;

    // Domain logic dependencies
    private ArchiveRepository archiveRepository;
    private ImageRepository imageRepository;
    private ImageRepository.TempImageInfo tempImageInfo;
    private NoteRepository noteRepository;
    private RecentCapturesRepository recentCapturesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAppRepos();
        initViews();
    }

    private void initAppRepos(){
        ArchiveStore jsonArchiveStore = new JsonArchiveStore(getApplicationContext());
        archiveRepository = new ArchiveRepository(jsonArchiveStore);
        RecentCapturesStore recentCapturesStore = new JsonRecentCapturesStore(getApplicationContext());
        recentCapturesRepository = new RecentCapturesRepository(recentCapturesStore);
        ImageStore mediaImageStore = new MediaImageStore(getApplicationContext());
        imageRepository = new ImageRepository(getApplicationContext(), mediaImageStore);
        NoteStore csvNoteStore = new CsvNoteStore(this);
        noteRepository = new NoteRepository(csvNoteStore);
    }

    private void initViews(){
        dropdown = findViewById(R.id.spinnerArchive);
        recordReferenceEditText = findViewById(R.id.editTextRef);
        TextView recordReferenceText = findViewById(R.id.textViewRef);
        noteText = findViewById(R.id.textViewNote);
        TextView recordReferenceLabel = findViewById(R.id.refLabel);
        Button cameraButton = findViewById(R.id.cameraButton);
        Button filesButton = findViewById(R.id.filesButton);
        Button infoButton = findViewById(R.id.infoButton);
        Button clearNoteButton = findViewById(R.id.buttonClearNote);
        Button clearReferenceButton = findViewById(R.id.buttonClearRef);

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

        clearNoteButton.setOnClickListener(v -> noteText.setText(""));

        recordReferenceLabel.setOnClickListener(view -> showDataEntryToolTips(getString(R.string.ref_description_heading), getString(R.string.ref_description_text)));
        recordReferenceEditText.addTextChangedListener(new TextWatcher() {
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
        clearReferenceButton.setOnClickListener(v -> recordReferenceEditText.setText(""));

        cameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
        filesButton.setOnClickListener(v -> openGallery());
        infoButton.setOnClickListener(v -> InfoDialog.show(this, recentCapturesRepository.getRecentCaptures()));
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
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open gallery", e);
        }
    }

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
                showMessage("Error: No image path available");
                return;
            }
            String imageFileName = createFileName();
            try {
                String note = noteText.getText().toString();
                boolean saved = imageRepository.saveImageToGallery(imageFileName, note, tempImageInfo.path, "CapturingThePast");
                if (saved) {
                    recentCapturesRepository.addFileToRecentCaptures(imageFileName);
                    boolean noteSaved = noteRepository.saveNote(imageFileName, noteText.getText().toString());
                    tempImageInfo = null;
                    if(noteSaved) showMessage("Image saved to " + imageFileName + "\n Note saved to output file.");
                    else showMessage("Image saved to " + imageFileName + "\nNote could not be saved");
                }
            } catch (IOException e) {
                showMessage("Error: Image not saved.\n" + e.getMessage());
            }
        }
    }

    @Override
    public void onArchiveCreated(String fullName, String shortName) {
        boolean created = archiveRepository.createArchive(fullName, shortName);
        if (created) showMessage(fullName + " created");
        else showMessage(fullName + " could not be created");
        updateDropdown();
    }

    @Override
    public void onArchiveEdited(String oldFullName, String oldShortName, String shortArchiveName, String fullArchiveName) {
        archiveRepository.updateArchive(oldFullName, oldShortName, fullArchiveName, shortArchiveName);
        updateDropdown();
        showMessage(fullArchiveName + " updated");
    }

    @Override
    public void onArchiveDeleted(String fullArchiveName) {
        archiveRepository.deleteArchive(fullArchiveName);
        updateDropdown();
        showMessage(getString(R.string.deleted)+ " - " + fullArchiveName);
    }

    private void updateDropdown(){
        ArrayAdapter<String> dataAdapter =
                new ArchiveAdapter(this, archiveRepository.readArchives(), this, this);
        dropdown.setAdapter(dataAdapter);
    }

    private String createFileName() {
        String shortArchiveName = getShortArchiveName();
        String recordReference = recordReferenceEditText.getText().toString();
        String counter = "0"; // placeholder, replace when image counter is implemented
        String fileName = RecordReferenceCreator.createRecordReference(shortArchiveName, recordReference, counter);
        if (fileName.length() > 128) showMessage("Your catalogue reference is very long and may result in unusable file names.");
        return fileName;
    }

    private String getShortArchiveName(){
        Object item = dropdown.getSelectedItem();
        if (item == null) return "";

        String selectedArchive = item.toString();
        if (selectedArchive.equals("Select Archive")) return "";
        String[] parts = selectedArchive.split("-");
        if (parts.length >= 2) return parts[1].trim();
        else return selectedArchive.trim();
    }
}
