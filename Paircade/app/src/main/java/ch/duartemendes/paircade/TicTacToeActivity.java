package ch.duartemendes.paircade;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class TicTacToeActivity extends AppCompatActivity {

    private PaircadePersistedData.PaircadeDataHelper dbHelper;
    private String playerMode;
    private ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Load Playersettings
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                playerMode = null;
                players = null;
            } else {
                playerMode = extras.getString(getString(R.string.game_types));
                if(playerMode.equals(getString(R.string.multi))){
                    players = (ArrayList<String>) extras.getSerializable(getString(R.string.players));
                } else {
                    players = null;
                }
            }
        } else {
            playerMode = (String) savedInstanceState.getSerializable(getString(R.string.game_types));
            if(playerMode.equals(getString(R.string.multi))){
                players = (ArrayList<String>) savedInstanceState.getSerializable(getString(R.string.players));
            } else {
                players = null;
            }
        }

        if(players == null){
            // Get DB for own username
            dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());
        }

    }
}
