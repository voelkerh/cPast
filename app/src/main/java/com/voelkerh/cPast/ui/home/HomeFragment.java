package com.voelkerh.cPast.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.di.ViewModelFactory;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.TempImageData;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Home screen fragment that coordinates user input for capturing images.
 *
 * <p>This fragment allows the user to:
 * <ul>
 *   <li>Select or manage archives</li>
 *   <li>Enter a record reference and optional note</li>
 *   <li>Trigger image capture via the camera app</li>
 * </ul>
 *
 * <p>It observes {@link HomeViewModel} state and delegates all business logic to the ViewModel.
 * Image capture is handled using the Activity Result API with a temporary file provided by the domain layer.</p>
 *
 * <p>This class belongs to the UI layer and contains no business logic.</p>
 */
public class HomeFragment extends Fragment implements AddArchiveDialog.Listener, EditArchiveDialog.Listener {

    private HomeViewModel homeViewModel;
    private String tempImagePath;

    // UI components
    private Spinner dropdown;
    private EditText recordReferenceEditText;
    private TextView recordReferenceText;
    private TextView noteText;

    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(HomeViewModel.class);

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                handleCameraResult();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        observeViewModel();
    }

    private void initViews(View view) {
        dropdown = view.findViewById(R.id.spinnerArchive);
        recordReferenceEditText = view.findViewById(R.id.editTextRef);
        recordReferenceText = view.findViewById(R.id.textViewRef);
        noteText = view.findViewById(R.id.textViewNote);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button clearNoteButton = view.findViewById(R.id.buttonClearNote);
        Button clearReferenceButton = view.findViewById(R.id.buttonClearRef);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Archive selected = (Archive) parent.getItemAtPosition(position);
                homeViewModel.onArchiveSelected(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recordReferenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.onRecordReferenceChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearReferenceButton.setOnClickListener(v -> recordReferenceEditText.setText(""));
        clearNoteButton.setOnClickListener(v -> noteText.setText(""));
        cameraButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void observeViewModel() {
        homeViewModel.getArchives().observe(getViewLifecycleOwner(), archives -> {
            ArrayAdapter<Archive> adapter = new ArchiveAdapter(
                    requireContext(), archives, this, this
            );
            dropdown.setAdapter(adapter);
        });

        homeViewModel.getNextFileName().observe(getViewLifecycleOwner(), fileName -> recordReferenceText.setText(fileName));

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showMessage(error);
            }
        });

        homeViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.isEmpty()) {
                showMessage(success);
            }
        });
    }

    /**
     * Make sure to recalculate counter after visit of other menu fragment.
     * Ensures to update counter, if user deletes images in photos fragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.refreshCounter();
    }


    private void dispatchTakePictureIntent() {
        String recordReference = recordReferenceEditText.getText().toString();

        if (!homeViewModel.isInputValid(recordReference)) {
            ValidationDialog.show(requireContext());
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            TempImageData tempData = homeViewModel.prepareCameraCapture();

            if (tempData != null) {
                tempImagePath = tempData.getPath();
                android.net.Uri uri = tempData.getUri();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private void handleCameraResult() {
        if (tempImagePath == null) {
            showMessage("Error: No image path available");
            return;
        }

        String note = noteText.getText().toString().trim();
        homeViewModel.saveCapturedImage(tempImagePath, note);

        noteText.setText("");
        tempImagePath = null;
    }

    @Override
    public void onArchiveCreated(String fullName, String shortName) {
        homeViewModel.createArchive(fullName, shortName);
    }

    @Override
    public void onArchiveEdited(String oldFullName, String oldShortName, String fullArchiveName, String shortArchiveName) {
        homeViewModel.updateArchive(oldFullName, oldShortName, shortArchiveName, fullArchiveName);
    }

    @Override
    public void onArchiveDeleted(String fullArchiveName) {
        homeViewModel.deleteArchive(fullArchiveName);
    }

    private void showMessage(String str) {
        Toast.makeText(requireContext(), str, LENGTH_SHORT).show();
    }
}