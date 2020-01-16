package ch.duartemendes.paircade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ch.duartemendes.paircade.cardgames.Deck;
import ch.duartemendes.paircade.cardgames.Playingcard;
import ch.duartemendes.paircade.data.PaircadePersistedData;

public class HigherLowerActivity extends AppCompatActivity {

    private PaircadePersistedData.PaircadeDataHelper dbHelper;
    private String playerMode;
    private ArrayList<String> players;
    private HashMap<String, Integer> tries;
    private TextView playerLabel;
    private TextView triesLabel;
    private TextView streakLabel;
    private ImageView cardImage;

    private Deck deck;
    private ArrayList<Playingcard> playedCards = new ArrayList<>();
    private Playingcard lastCard, newCard;

    private String currentPlayer;
    private int currentStreak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_higher_lower);

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
            playerMode = (String) savedInstanceState.getSerializable(getString(R.string.player_mode));
            if(playerMode.equals(getString(R.string.multi))){
                players = (ArrayList<String>) savedInstanceState.getSerializable(getString(R.string.players));
            } else {
                players = null;
            }
        }

        // Get DB for own username
        dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());

        //TODO: Let Host only initialize this stuff
        tries = new HashMap<>();
        currentStreak = 0;

        if(players == null){
            // Singleplayer
            currentPlayer = dbHelper.getUsername();

            tries.put(currentPlayer, 0);
        } else {
            players.add(dbHelper.getUsername());

            currentPlayer = players.get(0);
            for(String username : players){
                tries.put(username, 0);
            }
        }

        cardImage = findViewById(R.id.cardImage);
        playerLabel = findViewById(R.id.playernameLabel);
        triesLabel = findViewById(R.id.triesLabel);
        streakLabel = findViewById(R.id.streakLabel);

        playerLabel.setText(currentPlayer);
        triesLabel.setText(tries.get(currentPlayer).toString());
        streakLabel.setText(String.valueOf(currentStreak));

        new DeckTask().execute();
    }

    private void setCard(){
        Playingcard card = playedCards.get(playedCards.size() - 1);
        new ImageTask().execute(card.getImage());
    }


    public void guessSmaller(View view){
        new CardTask().execute("1", "isGuess", "Lower");
    }

    public void guessEquals(View view){
        new CardTask().execute("1", "isGuess", "Equals");
    }

    public void guessBigger(View view){
        new CardTask().execute("1", "isGuess", "Higher");
    }

    private int getCardValue(Playingcard card){
        switch (card.getValue()){
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
                return Integer.valueOf(card.getValue());
            case "KING":
                return 13;
            case "QUEEN":
                return 12;
            case "JACK":
                return 11;
            case "ACE":
                return 14;
        }
        return 0;
    }

    private class DeckTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            deck = gson.fromJson(result, Deck.class);
            new CardTask().execute("1");
        }
    }

    private class CardTask extends AsyncTask<String, String, String> {
        private boolean isGuess;
        private String guess;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            if(params.length > 1){
                isGuess = true;
                guess = params[2];
            } else {
                isGuess = false;
                guess = "";
            }

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://deckofcardsapi.com/api/deck/" + deck.getDeck_id() + "/draw/?count=" + params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String json = result;

                JSONObject deckObj = new JSONObject(json);
                JSONArray cardsArray = deckObj.getJSONArray("cards");

                ArrayList<JSONObject> cards = new ArrayList<>();
                for (int i = 0; i < cardsArray.length(); i++) {
                    cards.add((JSONObject) cardsArray.get(i));
                }

                for (JSONObject card : cards) {
                    Playingcard playingcard = new Playingcard();

                    playingcard.setImage(card.getString("image"));
                    playingcard.setCode(card.getString("code"));
                    playingcard.setSuit(card.getString("suit"));
                    playingcard.setValue(card.getString("value"));

                    if(newCard != null){
                        lastCard = newCard;
                    }
                    newCard = playingcard;
                    playedCards.add(playingcard);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setCard();

            if(isGuess){
                if(getCardValue(lastCard) > getCardValue(newCard) && "Lower".equals(guess)){
                    // Correct
                    processCorrectGuess();
                } else if(getCardValue(lastCard) < getCardValue(newCard) && "Higher".equals(guess)){
                    // Correct
                    processCorrectGuess();
                } else if(getCardValue(lastCard) == getCardValue(newCard) && "Equals".equals(guess)){
                    // Correct
                    processCorrectGuess();
                } else {
                    // False
                    currentStreak = 0;
                    int triesBefore = tries.get(currentPlayer) + 1;
                    tries.put(currentPlayer, triesBefore);

                    lastCard = newCard;
                    newCard = null;
                }

                triesLabel.setText(tries.get(currentPlayer).toString());
                streakLabel.setText(String.valueOf(currentStreak));
            }
        }
    }

    private class ImageTask extends AsyncTask<String, String, Bitmap> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                URL urlForImage = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) urlForImage.openConnection();
                connection.setDoInput(true);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            cardImage.setImageBitmap(result);
        }
    }
    public void processCorrectGuess(){
        if(currentStreak == 4){
            if(playerMode.equals(getString(R.string.multi))){
                //TODO: Next players turn
            } else {
                goToEndScreen();
            }
        } else {
            currentStreak++;
        }
    }

    public void goToEndScreen(){
        String minTries = null;
        int currentMin = Integer.MAX_VALUE;
        for(String user : tries.keySet()) {
            int value = tries.get(user);
            if(value < currentMin) {
                currentMin = value;
                minTries = user;
            } else if(value == currentMin){
                minTries = minTries + " And " + user;
            }
        }

        Intent endGameScreen = new Intent(this, EndGameActivity.class);
        endGameScreen.putExtra(getString(R.string.winner), minTries);
        startActivity(endGameScreen);
    }
}
