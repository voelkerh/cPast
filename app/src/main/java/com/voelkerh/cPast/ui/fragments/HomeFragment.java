package com.voelkerh.cPast.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.domainLogic.*;
import com.voelkerh.cPast.ui.dialogs.AddArchiveDialog;
import com.voelkerh.cPast.ui.dialogs.EditArchiveDialog;
import com.voelkerh.cPast.ui.dialogs.ValidationDialog;
import com.voelkerh.cPast.ui.ui_elements.ArchiveAdapter;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment implements AddArchiveDialog.Listener, EditArchiveDialog.Listener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private int currentCounter = 0;
    // UI components
    private Spinner dropdown;
    private EditText recordReferenceEditText;
    private TextView noteText;
    private TextView imageCounter;
    private TextView recordReferenceText;

    // Domain logic dependencies
    private final ArchiveRepository archiveRepository;
    private final ImageRepository imageRepository;
    private ImageRepository.TempImageInfo tempImageInfo;
    private final NoteRepository noteRepository;
    private final RecentCapturesRepository recentCapturesRepository;

    public HomeFragment(ArchiveRepository archiveRepository, ImageRepository imageRepository, NoteRepository notesRepository, RecentCapturesRepository recentCapturesRepository) {
        this.archiveRepository = archiveRepository;
        this.imageRepository = imageRepository;
        this.noteRepository = notesRepository;
        this.recentCapturesRepository = recentCapturesRepository;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view){
        dropdown = view.findViewById(R.id.spinnerArchive);
        recordReferenceEditText = view.findViewById(R.id.editTextRef);
        recordReferenceText = view.findViewById(R.id.textViewRef);
        imageCounter = view.findViewById(R.id.imageCounter);
        noteText = view.findViewById(R.id.textViewNote);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button clearNoteButton = view.findViewById(R.id.buttonClearNote);
        Button clearReferenceButton = view.findViewById(R.id.buttonClearRef);

        ArrayAdapter<Archive> dataAdapter =
                new ArchiveAdapter(requireContext(), archiveRepository.readArchives(), this, this);
        dropdown.setAdapter(dataAdapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String baseReference = getBaseReference();

                currentCounter = imageRepository.getHighestCounterForRecord(baseReference);
                imageCounter.setText(String.valueOf(currentCounter));

                String nextFileName = createFileName(baseReference, String.valueOf(currentCounter + 1));
                recordReferenceText.setText(nextFileName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        clearNoteButton.setOnClickListener(v -> noteText.setText(""));

        recordReferenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String baseReference = getBaseReference();

                currentCounter = imageRepository.getHighestCounterForRecord(baseReference);
                imageCounter.setText(String.valueOf(currentCounter));

                String fileName = createFileName(baseReference, String.valueOf(currentCounter + 1));
                recordReferenceText.setText(fileName);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        clearReferenceButton.setOnClickListener(v -> recordReferenceEditText.setText(""));

        cameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    /**
     * Make sure to recalculate counter after visit of other menu fragment.
     * Ensures to update counter, if user deletes images in photos fragment.
      */
    @Override
    public void onResume() {
        super.onResume();
        String baseReference = getBaseReference();

        if (baseReference.isEmpty()) {
            imageCounter.setText("0");
            return;
        }

        currentCounter = imageRepository.getHighestCounterForRecord(baseReference);
        imageCounter.setText(String.valueOf(currentCounter));

        recordReferenceText.setText(
                createFileName(baseReference, String.valueOf(currentCounter + 1))
        );
    }

    private void showMessage(String str) {
        Toast.makeText(requireContext(), str, LENGTH_SHORT).show();
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
        if (recordReference.isEmpty()) return false;
        return recordReference.matches("^(?=.*[A-Za-zäöüÄÖÜ])[A-Za-z0-9äöüÄÖÜ_/]+$");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (tempImageInfo == null) {
                showMessage("Error: No image path available");
                return;
            }

            String baseReference = getBaseReference();
            String imageFileName = createFileName(baseReference, String.valueOf(currentCounter + 1));

            try {
                String note = noteText.getText().toString().trim();
                noteText.setText("");

                boolean saved = imageRepository.saveImageToGallery(imageFileName, note, tempImageInfo.path);

                if (saved) {
                    currentCounter++;
                    imageCounter.setText(String.valueOf(currentCounter));

                    String newImageFileName = createFileName(baseReference, String.valueOf(currentCounter + 1));
                    recordReferenceText.setText(newImageFileName);

                    Capture capture = new Capture(getSelectedArchive(), imageFileName, note);
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
        ArrayAdapter<Archive> dataAdapter =
                new ArchiveAdapter(requireContext(), archiveRepository.readArchives(), this, this);
        dropdown.setAdapter(dataAdapter);
    }

    private String createFileName(String baseReference, String counter) {
        String fileName = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        if (fileName.length() > 128) showMessage("Your catalogue reference is very long and may result in unusable file names.");
        return fileName;
    }

    private String getBaseReference() {
        Archive archive = getSelectedArchive();
        String shortArchiveName = archive == null ? "" : archive.getShortName();
        String recordReference = recordReferenceEditText.getText().toString();

        return RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
    }

    private Archive getSelectedArchive(){
        return (Archive) dropdown.getSelectedItem();
    }
}