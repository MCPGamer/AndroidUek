package ch.duartemendes.paircade;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;

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
}
