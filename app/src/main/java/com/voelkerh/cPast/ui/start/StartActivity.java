package com.voelkerh.cPast.ui.start;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.voelkerh.cPast.ui.navigation.MainActivity;
import com.voelkerh.cPast.R;

/**
 * Entry activity of the application.
 *
 * <p>This activity launches an informative screen upon application start.
 * It forwards the user to the {@link MainActivity} after interaction.
 * It is not part of the main navigation flow.</p>
 *
 * <p>This activity does not handle any business logic.</p>
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}


