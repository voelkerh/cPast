package com.benskitchen.capturingthepast.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import capturingthepast.R;
import com.benskitchen.capturingthepast.domainLogic.*;
import com.benskitchen.capturingthepast.persistence.*;
import com.benskitchen.capturingthepast.ui.dialogs.AddArchiveDialog;
import com.benskitchen.capturingthepast.ui.dialogs.EditArchiveDialog;
import com.benskitchen.capturingthepast.ui.dialogs.ValidationDialog;
import com.benskitchen.capturingthepast.ui.ui_elements.ArchiveAdapter;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment implements AddArchiveDialog.Listener, EditArchiveDialog.Listener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "com.benskitchen.capturingthepast.HomeFragment";

    // UI components
    private Spinner dropdown;
    private EditText recordReferenceEditText;
    private TextView noteText;

    // Domain logic dependencies
    private ArchiveRepository archiveRepository;
    private ImageRepository imageRepository;
    private ImageRepository.TempImageInfo tempImageInfo;
    private NoteRepository noteRepository;
    private final RecentCapturesRepository recentCapturesRepository;

    public HomeFragment(RecentCapturesRepository recentCapturesRepository) {
        this.recentCapturesRepository = recentCapturesRepository;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAppRepos();
        initViews(view);
    }

    private void initAppRepos(){
        ArchiveStore jsonArchiveStore = new JsonArchiveStore(requireContext().getApplicationContext());
        archiveRepository = new ArchiveRepository(jsonArchiveStore);
        ImageStore mediaImageStore = new MediaImageStore(requireContext().getApplicationContext());
        imageRepository = new ImageRepository(requireContext().getApplicationContext(), mediaImageStore);
        NoteStore csvNoteStore = new CsvNoteStore(requireContext());
        noteRepository = new NoteRepository(csvNoteStore);
    }

    private void initViews(View view){
        dropdown = view.findViewById(R.id.spinnerArchive);
        recordReferenceEditText = view.findViewById(R.id.editTextRef);
        TextView recordReferenceText = view.findViewById(R.id.textViewRef);
        noteText = view.findViewById(R.id.textViewNote);
        TextView recordReferenceLabel = view.findViewById(R.id.refLabel);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button clearNoteButton = view.findViewById(R.id.buttonClearNote);
        Button clearReferenceButton = view.findViewById(R.id.buttonClearRef);

        ArrayAdapter<String> dataAdapter =
                new ArchiveAdapter(requireContext(), archiveRepository.readArchives(), this, this);
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

        recordReferenceLabel.setOnClickListener(v -> showDataEntryToolTips(getString(R.string.ref_description_heading), getString(R.string.ref_description_text)));
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
    }

    private void showMessage(String str) {
        Toast.makeText(requireContext(), str, LENGTH_SHORT).show();
    }

    private void showDataEntryToolTips(String heading, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        TextView tvTip = new TextView(requireContext());
        String tipText = "<h4>" + heading + "</h4><p>" + message + "</p>";
        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(tipText, Html.FROM_HTML_MODE_LEGACY));
        LinearLayout lpset = new LinearLayout(requireContext());
        lpset.setOrientation(LinearLayout.VERTICAL);
        lpset.addView(tvTip);
        lpset.setPadding(50, 80, 50, 10);
        alertDialog.setView(lpset);
        alertDialog.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void dispatchTakePictureIntent() {
        if (!isInputValid()) {
            ValidationDialog.show(requireContext());
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            tempImageInfo = imageRepository.getTempImageInfo();
            if (tempImageInfo != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageInfo.uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            else showMessage("No photoURI to capture image");
        }
    }

    private boolean isInputValid() {
        Object item = dropdown.getSelectedItem();
        if (item == null || item.toString().equals("Select Archive")) return false;
        String recordReference = recordReferenceEditText.getText().toString();
        return !recordReference.contains("-") &&
                !recordReference.contains(";") &&
                !recordReference.contains("\\") &&
                !recordReference.contains(",");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (tempImageInfo == null) {
                showMessage("Error: No image path available");
                return;
            }
            String imageFileName = createFileName();
            try {
                String note = noteText.getText().toString().trim();
                noteText.setText("");
                boolean saved = imageRepository.saveImageToGallery(imageFileName, note, tempImageInfo.path, "CapturingThePast");
                if (saved) {
                    Capture capture = new Capture(imageFileName, note);
                    recentCapturesRepository.addFileToRecentCaptures(capture);
                    boolean noteSaved = noteRepository.saveNote(capture);
                    tempImageInfo = null;
                    if(noteSaved) showMessage("Note and image saved\n " + imageFileName);
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
                new ArchiveAdapter(requireContext(), archiveRepository.readArchives(), this, this);
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