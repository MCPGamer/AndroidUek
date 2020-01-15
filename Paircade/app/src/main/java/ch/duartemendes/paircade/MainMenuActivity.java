package ch.duartemendes.paircade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        PaircadePersistedData.PaircadeDataHelper dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());

        TextView nameLabel = findViewById(R.id.mainMenuPlayerLabel);
        nameLabel.setText(dbHelper.getUsername());

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void goToChooseGame(View view) {
        Intent chooseGameScreen = new Intent(this, ChooseGameActivity.class);
        startActivity(chooseGameScreen);
    }

    public void goToJoinLobby(View view) {
        Intent joinLobbyScreen = new Intent(this, JoinLobbyActivity.class);
        startActivity(joinLobbyScreen);
    }
}
