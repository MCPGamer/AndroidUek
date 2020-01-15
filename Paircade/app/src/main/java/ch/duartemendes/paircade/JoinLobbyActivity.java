package ch.duartemendes.paircade;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JoinLobbyActivity extends AppCompatActivity {
    private TextView waitInviteLabel;
    private TextView waitGamestartLabel;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);

        waitInviteLabel = findViewById(R.id.waitInviteLabel);
        waitGamestartLabel = findViewById(R.id.waitStartLabel);

        waitGamestartLabel.setVisibility(View.INVISIBLE);

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
}
