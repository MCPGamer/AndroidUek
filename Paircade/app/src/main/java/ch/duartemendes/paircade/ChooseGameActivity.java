package ch.duartemendes.paircade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void playTicTacToe(View view){
        Intent createLobbyScreen = new Intent(this, CreateLobbyActivity.class);
        createLobbyScreen.putExtra(getString(R.string.game_types), getString(R.string.tic_tac_toe));
        startActivity(createLobbyScreen);
    }

    public void playHigherLower(View view){
        Intent createLobbyScreen = new Intent(this, CreateLobbyActivity.class);
        createLobbyScreen.putExtra(getString(R.string.game_types), getString(R.string.higher_lower));
        startActivity(createLobbyScreen);
    }
}
