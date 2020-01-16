package ch.duartemendes.paircade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class EndGameActivity extends AppCompatActivity {
    private TextView winnerLabel;
    private String winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Load Winner
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                winner = null;
            } else {
                winner = extras.getString(getString(R.string.winner));
            }
        } else {
            winner = (String) savedInstanceState.getSerializable(getString(R.string.winner));
        }

        winnerLabel = findViewById(R.id.winnerLabel);
        winnerLabel.setText(winner);
    }

    public void backToMainMenu(View view){
        Intent mainMenuScreen = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenuScreen);
    }
}
