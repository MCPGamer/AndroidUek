package ch.duartemendes.paircade;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class ChooseNameActivity extends AppCompatActivity {
    private PaircadePersistedData.PaircadeDataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);

        dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());

        EditText nameField = findViewById(R.id.nameField);
        nameField.setText(dbHelper.getUsername());

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                Button button = findViewById(R.id.selectNameButton);
                if(s.toString().isEmpty()){
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void goToMainScreen(View view) {
        EditText nameField = findViewById(R.id.nameField);
        dbHelper.saveUsername(nameField.getText().toString());

        Intent mainMenuScreen = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenuScreen);
    }

    @Override
    protected void onStop() {
        dbHelper.close();
        super.onStop();
    }
}
