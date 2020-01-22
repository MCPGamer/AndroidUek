package ch.duartemendes.paircade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class TicTacToeActivity extends AppCompatActivity {

    private PaircadePersistedData.PaircadeDataHelper dbHelper;
    private String playerMode;
    private ArrayList<String> players;
    private String currentPlayer;
    private Character currentPlayerSymbol;

    private Character[][] fields;
    private TextView topLeft, topMid, topRight, midLeft, midMid, midRight, bottomLeft, bottomMid, bottomRight;
    private TextView currentPlayerLabel;

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
                playerMode = extras.getString(getString(R.string.player_mode));
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

        // Get DB for own username
        dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());

        //TODO: Let Host only initialize this stuff
        currentPlayerSymbol = 'X';
        initFields();

        if(players == null){
            // Singleplayer
            currentPlayer = dbHelper.getUsername();
        } else {
            // Host go First
            ArrayList<String> oldPlayers = players;
            players = new ArrayList<>();
            players.add(dbHelper.getUsername());
            players.addAll(oldPlayers);

            currentPlayer = players.get(0);
        }

        topLeft = findViewById(R.id.TopLeft);
        topMid = findViewById(R.id.TopMid);
        topRight = findViewById(R.id.TopRight);
        midLeft = findViewById(R.id.MidLeft);
        midMid = findViewById(R.id.MidMid);
        midRight = findViewById(R.id.MidRight);
        bottomLeft = findViewById(R.id.BottomLeft);
        bottomMid = findViewById(R.id.BottomMid);
        bottomRight = findViewById(R.id.BottomRight);

        currentPlayerLabel = findViewById(R.id.currentPlayerLabel);
        draw();
    }

    private void initFields(){
        fields = new Character[3][3];

        for(int i = 0; i < 3; i++){
            for(int j= 0; j < 3; j++){
                fields[i][j] = ' ';
            }
        }
    }

    public void onFieldClick(View v) {
        v.getId();

        if(!((TextView)v).getText().equals(" ")){
            return;
        }

        if(v.getId() == R.id.TopLeft){
          execTurn(0, 0);
        } else if(v.getId() == R.id.TopMid){
            execTurn(0, 1);
        } else if(v.getId() == R.id.TopRight){
            execTurn(0, 2);
        } else if(v.getId() == R.id.MidLeft){
            execTurn(1, 0);
        } else if(v.getId() == R.id.MidMid){
            execTurn(1, 1);
        } else if(v.getId() == R.id.MidRight){
            execTurn(1, 2);
        }else if(v.getId() == R.id.BottomLeft){
            execTurn(2, 0);
        } else if(v.getId() == R.id.BottomMid){
            execTurn(2, 1);
        } else if(v.getId() == R.id.BottomRight){
            execTurn(2, 2);
        }
    }

    private void draw(){
        currentPlayerLabel.setText(currentPlayerSymbol + " - " + currentPlayer);

        // +"" Casted Character zu String
        topLeft.setText(fields[0][0] + "");
        topMid.setText(fields[0][1] + "");
        topRight.setText(fields[0][2] + "");
        midLeft.setText(fields[1][0] + "");
        midMid.setText(fields[1][1] + "");
        midRight.setText(fields[1][2] + "");
        bottomLeft.setText(fields[2][0] + "");
        bottomMid.setText(fields[2][1] + "");
        bottomRight.setText(fields[2][2] + "");
    }

    private void execTurn(int posHeight, int posLength){
        fields[posHeight][posLength] = currentPlayerSymbol;
        draw();

        if(gameWon()){
            goToEndScreen();
        } else {
            if(currentPlayerSymbol.equals('X')){
                currentPlayerSymbol = 'O';
            } else {
                currentPlayerSymbol = 'X';
            }

            if (playerMode.equals(getString(R.string.multi))) {
                if (currentPlayer.equals(players.get(0))) {
                    currentPlayer = players.get(1);
                } else {
                    currentPlayer = players.get(0);
                }
            } else {
                if (currentPlayer.equals(dbHelper.getUsername())) {
                    currentPlayer = currentPlayer + " Partner";
                } else {
                    currentPlayer = dbHelper.getUsername();
                }
            }

            draw();
        }
    }

    private boolean gameWon(){
        if(checkHorizontal()){
            return true;
        } else if(checkVertical()){
            return true;
        } else if(checkDiagonal()){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkHorizontal() {
        // Make Character to String
        String symbol = currentPlayerSymbol + "";

        if(topLeft.getText().equals(symbol)
                && topMid.getText().equals(symbol)
                && topRight.getText().equals(symbol)){
            return true;
        } else if(midLeft.getText().equals(symbol)
                && midMid.getText().equals(symbol)
                && midRight.getText().equals(symbol)){
            return true;
        } else if(bottomLeft.getText().equals(symbol)
                && bottomMid.getText().equals(symbol)
                && bottomRight.getText().equals(symbol)){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkVertical() {
        // Make Character to String
        String symbol = currentPlayerSymbol + "";

        if(topLeft.getText().equals(symbol)
                && midLeft.getText().equals(symbol)
                && bottomLeft.getText().equals(symbol)){
            return true;
        } else if(topMid.getText().equals(symbol)
                && midMid.getText().equals(symbol)
                && bottomMid.getText().equals(symbol)){
            return true;
        } else if(topRight.getText().equals(symbol)
                && midRight.getText().equals(symbol)
                && bottomRight.getText().equals(symbol)){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkDiagonal() {
        // Make Character to String
        String symbol = currentPlayerSymbol + "";

        if(topLeft.getText().equals(symbol)
                && midMid.getText().equals(symbol)
                && bottomRight.getText().equals(symbol)){
            return true;
        } else if(topRight.getText().equals(symbol)
                && midMid.getText().equals(symbol)
                && bottomLeft.getText().equals(symbol)){
            return true;
        } else {
            return false;
        }
    }

    private void goToEndScreen(){
        Intent endGameScreen = new Intent(this, EndGameActivity.class);
        endGameScreen.putExtra(getString(R.string.winner), currentPlayer);
        startActivity(endGameScreen);
    }

    public void resetFields(View v){
        initFields();
        draw();
    }
}
