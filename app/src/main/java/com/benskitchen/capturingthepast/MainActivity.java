package com.benskitchen.capturingthepast;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

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

import com.benskitchen.capturingthepast.data.ImageRepository;
import com.benskitchen.capturingthepast.data.LogWriter;
import com.benskitchen.capturingthepast.data.SettingsRepository;
import com.benskitchen.capturingthepast.domainLogic.CaptureCounter;
import com.benskitchen.capturingthepast.domainLogic.CatRefCreator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
    private final String strPrefix = "cpast";
    private String strNote = "";
    char[] alphabet = new char[26];
    int nPart = 0;


    private String currentPhotoPath;

    // UI Elements
    private Spinner dropdown;
    private EditText tvCatRef;
    private EditText tvItemText;
    private EditText tvSubItemText;
    private EditText tvPart;
    private TextView refText;
    private TextView noteText;
    private TextView repoLabel;
    private TextView refLabel;
    private TextView itemLabel;
    private TextView subitemLabel;
    private TextView partLabel;
    private Button decItem;
    private Button incItem;
    private Button decSubItem;
    private Button incSubItem;
    private Button decPart;
    private Button incPart;
    private Button camButton;
    private Button filesButton;
    private Button addRepoButton;
    private Button deleteRepoButton;
    private Button infoButton;
    private Button btnClearNote;
    private Button btnClearRef;


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
        setupListeners();
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
        dropdown = findViewById(R.id.spinnerRepo);
        tvCatRef = findViewById(R.id.editTextRef);
        tvItemText = findViewById(R.id.editTextItem);
        tvSubItemText = findViewById(R.id.editTextSubItem);
        tvPart = findViewById(R.id.textViewPart);
        refText = findViewById(R.id.textViewRef);
        noteText = findViewById(R.id.textViewNote);
        repoLabel = findViewById(R.id.repoLabel);
        refLabel = findViewById(R.id.refLabel);
        itemLabel = findViewById(R.id.itemLabel);
        subitemLabel = findViewById(R.id.subItemLabel);
        partLabel = findViewById(R.id.detachedLabel);
        decItem = findViewById(R.id.buttonDecItem);
        incItem = findViewById(R.id.buttonincItem);
        decSubItem = findViewById(R.id.buttonDecSubItem);
        incSubItem = findViewById(R.id.buttonincSubItem);
        decPart = findViewById(R.id.buttonDecPart);
        incPart = findViewById(R.id.buttonIncPart);
        camButton = findViewById(R.id.cameraButton);
        filesButton = findViewById(R.id.filesButton);
        addRepoButton = findViewById(R.id.addRepoButton);
        deleteRepoButton = findViewById(R.id.deleteRepoButton);
        infoButton = findViewById(R.id.infoButton);
        btnClearNote = findViewById(R.id.buttonClearNote);
        btnClearRef = findViewById(R.id.buttonClearRef);
        FontDrawable drawable = new FontDrawable(this, R.string.fa_paper_plane_solid, true, false);
        drawable.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, settingsRepository.getRepos());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dataAdapter);
        strArchon = settingsRepository.getArchonAt(0);
        dropdown.setSelection(0);
    }

    private void setupListeners() {
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
        repoLabel.setOnClickListener(view -> showEntryTips(getString(R.string.repo_description_heading), getString(R.string.repo_description_text)));
        addRepoButton.setOnClickListener(v -> addRepo());
        deleteRepoButton.setOnClickListener(v -> deleteRepoGui());

        itemLabel.setOnClickListener(view -> showEntryTips(getString(R.string.item_description_heading), getString(R.string.item_descript_text)));
        subitemLabel.setOnClickListener(view -> showEntryTips(getString(R.string.sub_item_description_heading), getString(R.string.sub_item_description_text)));
        partLabel.setOnClickListener(view -> showEntryTips(getString(R.string.detached_description_heading), getString(R.string.detached_description_text)));
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

        refLabel.setOnClickListener(view -> showEntryTips(getString(R.string.ref_description_heading), getString(R.string.ref_description_text)));
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

        camButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });
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

    // Tool tip template for data entry when clicked on labels
    private void showEntryTips(String heading, String message) {
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
        alertDialog.setNegativeButton("CLose", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
        startActivity(intent);
    }

    // Domain Logic - move
    private String createCatRef() {
        String catRef = CatRefCreator.createCatRef(strArchon, strRef, strItem, strSubItem, strPart);
        if (catRef.length() > 128) showMessage("Your catalogue reference is very long and may result in unusable file names.");
        return catRef;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = imageRepository.createTempImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.benskitchen.capturingthepast.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            try {
                ExifInterface exif = new ExifInterface(currentPhotoPath);
                saveImageToGallery(bitmap, exif);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToGallery(Bitmap bitmap, ExifInterface exif) {
        OutputStream fos;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String humanisedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());
        String catRef = createCatRef();
        String imageFileName = strPrefix + "_" + timeStamp + "_" + catRef + ".jpg";
        String strCSV = "\"" + humanisedTime + "\",\"" + catRef + "\",\"" + imageFileName + "\",\"" + strNote + "\"";
        String message = logWriter.writePublicLog(strCSV);
        if (message.length() > 0) Toast.makeText(this, message, LENGTH_SHORT).show();

        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "CapturingThePast");
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


        try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(imageUri, "w")) {
            FileDescriptor fd = pfd.getFileDescriptor();

            try (OutputStream stream = new FileOutputStream(fd)) {
                // Perform operations on "stream".
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }

            // Sync data with disk. It's mandatory to be able later to call writeExif
            fd.sync();    // <---- HERE THE SOLUTION
            captureCounter.incrementCaptureCount();
            settingsRepository.addFileToRecentFiles(catRef);
            String strToastMessage = "Image saved";
            Toast.makeText(this, strToastMessage, LENGTH_SHORT).show();
            writeExif(imageUri, exif);

        } catch (IOException e) {
            Toast.makeText(this, "Image not saved\n" + e.getMessage(), LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Domain logic - Move
    // Writes Metadata from temp image to new image in gallery
    private void writeExif(Uri uri, ExifInterface exif) {

        try (ParcelFileDescriptor imagePfd = getContentResolver().openFileDescriptor(uri, "rw")) {
            ExifInterface exifNew = new ExifInterface(imagePfd.getFileDescriptor());

            // Copy existing tags
            Field[] fields = ExifInterface.class.getFields();
            for (Field field : fields) {
                if (field.getName().startsWith("TAG")) {
                    try {
                        String tag = (String) field.get(null);
                        String value = exif.getAttribute(tag);
                        if (value != null) exifNew.setAttribute(tag, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Add user comment
            final String userComment = "Capturing the Past image " +createCatRef()+ " " + strNote;
            exifNew.setAttribute(ExifInterface.TAG_USER_COMMENT, userComment);

            exifNew.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Split UI / data layer
    private void addRepo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        TextView tvTitle = new TextView(this);
        String tittleText = "";
        tittleText += getString(R.string.add_repo_heading);
        tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        tvTitle.setText(Html.fromHtml(tittleText, Html.FROM_HTML_MODE_LEGACY));
        TextView tvTip = new TextView(this);
        String tipText = getString(R.string.tna_tip);
        tipText += getString(R.string.repo_tip);
        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(tipText, Html.FROM_HTML_MODE_LEGACY));
        TextView textView = new TextView(this);
        textView.setText(R.string.repo_name_hint);
        textView.setTextSize(18f);
        tvTip.setTextSize(18f);
        final EditText inputRepo = new EditText(MainActivity.this);
        inputRepo.setHint(R.string.repo_hint);
        final EditText inputArchon = new EditText(MainActivity.this);
        inputArchon.setHint(R.string.archon_hint);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tvTitle);
        linearLayout.addView(inputRepo);
        linearLayout.addView(textView);
        linearLayout.addView(inputArchon);
        linearLayout.addView(tvTip);
        linearLayout.setPadding(50, 80, 50, 10);
        alertDialog.setView(linearLayout);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {
            String strRepo = inputRepo.getText().toString();
            String strArchon = inputArchon.getText().toString();
            strArchon = strArchon.replaceAll("\\s+", "").toUpperCase();
            strArchon = strArchon.replaceAll("/", "_");
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("Repository", strRepo);
                jsonObj.put("Archon", strArchon);
                jsonObj.put("Enabled", "TRUE");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray list = new JSONArray();
            list.put(jsonObj);
            int len = settingsRepository.getRepositories().length();
            if (len > 0) {
                try {
                    for (int i = 0; i < len; i++) {
                        list.put(settingsRepository.getRepositories().get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            settingsRepository.setRepositories(list);
            settingsRepository.writePreferences();
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void deleteRepoGui() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        TextView tvTip = new TextView(this);
        tvTip.setText(R.string.set_prefix);
        TextView tvTitle = new TextView(this);
        String tittleText = getString(R.string.heading_select_repo);
        tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
        tvTitle.setText(Html.fromHtml(tittleText, Html.FROM_HTML_MODE_LEGACY));

        TextView labelSelect = new TextView(this);
        labelSelect.setText(R.string.select_repo);
        labelSelect.setPadding(20, 20, 0, 20);
        labelSelect.setTextSize(20f);
        TextView tvPresetsLabel = new TextView(this);
        tvPresetsLabel.setText(R.string.repo_presets);
        final EditText inputPrefix = new EditText(MainActivity.this);
        inputPrefix.setText(strPrefix);
        inputPrefix.setHint(R.string.prefix);
        final Switch switchTimestamp = new Switch(this);
        switchTimestamp.setChecked(settingsRepository.isTimestamped());
        switchTimestamp.setText(R.string.include_timestamp);
        final Button loadReposDefault = new Button(this);
        loadReposDefault.setText(R.string.default_repo_list);
        loadReposDefault.setAllCaps(false);
        loadReposDefault.setBackgroundColor(Color.DKGRAY);
        loadReposDefault.setTextColor(Color.WHITE);
        final Button loadReposShort = new Button(this);
        loadReposShort.setText(R.string.short_repo_list);
        loadReposShort.setAllCaps(false);
        loadReposShort.setBackgroundColor(Color.DKGRAY);
        loadReposShort.setTextColor(Color.WHITE);
        final Button loadReposAlt = new Button(this);
        loadReposAlt.setText(R.string.alternative);
        loadReposAlt.setTextColor(Color.WHITE);
        loadReposAlt.setBackgroundColor(Color.DKGRAY);
        loadReposAlt.setAllCaps(false);
        final Button deleteRepo = new Button(this);
        deleteRepo.setText(R.string.delete_selected_repository);
        deleteRepo.setAllCaps(false);
        deleteRepo.setBackgroundColor(Color.DKGRAY);
        deleteRepo.setTextColor(Color.WHITE);

        Spinner spinnerRepoSelect = new Spinner(this);
        ArrayAdapter dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, settingsRepository.getRepos());
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepoSelect.setAdapter(dataAdapterR);
        spinnerRepoSelect.setPadding(0, 8, 8, 24);

        final int[] selectedRepo = {-1};
        final boolean[] bReset = {false};
        LinearLayout lpset = new LinearLayout(this);
        lpset.setOrientation(LinearLayout.VERTICAL);
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        tvPresetsLabel.setTextSize(18f);
        lpset.addView(tvTitle);
        lpset.addView(spinnerRepoSelect);
        lpset.addView(deleteRepo);

        String infoText = getString(R.string.repository_presets_note); // "<p><br /><hr />Note: Presets overwrite repository list customisations.</p>";
        TextView tvPresetTip = new TextView(this);
        tvPresetTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvPresetTip.setText(Html.fromHtml(infoText, Html.FROM_HTML_MODE_LEGACY));
        tvPresetTip.setTextSize(16f);
        lpset.addView(tvPresetTip);

        lpset.addView(tvPresetsLabel);
        btnRow.addView(loadReposDefault);
        btnRow.addView(loadReposShort);
        btnRow.addView(loadReposAlt);
        lpset.addView(btnRow);

        lpset.setPadding(50, 80, 50, 10);
        alertDialog.setView(lpset);
        spinnerRepoSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRepo[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        deleteRepo.setOnClickListener(view -> {
            String strToast = "";
            JSONObject jsonObj;
            try {
                jsonObj = settingsRepository.getRepositories().getJSONObject(selectedRepo[0]);
                strToast = jsonObj.getString("Repository");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            settingsRepository.deleteRepository(selectedRepo[0]);
            settingsRepository.writePreferences();
            spinnerRepoSelect.setAdapter(makeNewRemovalDropDown());
            CharSequence text = getString(R.string.deleted)+ " - " + strToast;
            int duration = 2000;//Toast.LENGTH_SHORT;
            Snackbar snack = Snackbar.make(lpset, text, duration);
            snack.show();
        });

        loadReposDefault.setOnClickListener(view -> {
            bReset[0] = true;
            settingsRepository.resetArchons("default");
            settingsRepository.writePreferences();
            spinnerRepoSelect.setAdapter(makeNewRemovalDropDown());
            CharSequence text = getString(R.string.repository_default_presets_message);//"Default repository list loaded";
            int duration = 2000;//Toast.LENGTH_SHORT;
            Snackbar snack = Snackbar.make(lpset, text, duration);
            snack.show();
        });

        loadReposShort.setOnClickListener(view -> {
            bReset[0] = true;
            settingsRepository.resetArchons("short");
            settingsRepository.writePreferences();
            spinnerRepoSelect.setAdapter(makeNewRemovalDropDown());
            CharSequence text = getString(R.string.repository_short_presets_message);//"Short repository list loaded";
            int duration = 2000;//Toast.LENGTH_SHORT;
            Snackbar snack = Snackbar.make(lpset, text, duration);
            snack.show();
        });
        loadReposAlt.setOnClickListener(view -> {
            bReset[0] = true;
            settingsRepository.resetArchons("alternative");
            settingsRepository.writePreferences();
            spinnerRepoSelect.setAdapter(makeNewRemovalDropDown());
            CharSequence text = getString(R.string.repository_alternative_presets_message);//"Alternative repository list loaded";
            int duration = 2000;//Toast.LENGTH_SHORT;
            Snackbar snack = Snackbar.make(lpset, text, duration);
            snack.show();
        });
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private ArrayAdapter makeNewRemovalDropDown() {
        String[] repos = settingsRepository.getRepos();
        ArrayAdapter dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, repos);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapterR;
    }

    public void showInfo() {
        StringBuilder str = new StringBuilder();
        List<String> recentFiles = settingsRepository.getRecentFiles();
        for (int i = recentFiles.size() - 1; i >= 0; i--) {
            str.append(recentFiles.get(i)).append("\n");
        }
        String folderStatus = getString(R.string.latest_captures_message) + str; //"Latest captures (Most recent first):\n" + str;
        String strMessage = "";
        strMessage = "<p>" + captureCounter.getCaptureCount() + "</p> ";
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

        String infoText = sLink;
        tvTip.setMovementMethod(LinkMovementMethod.getInstance());
        tvTip.setText(Html.fromHtml(infoText, Html.FROM_HTML_MODE_LEGACY));
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
            String str = "<p>" + captureCounter.getCaptureCount() + "</p> ";
            tvCaptureCount.setMovementMethod(LinkMovementMethod.getInstance());
            tvCaptureCount.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        });
        alertDialog.show();
    }
}
