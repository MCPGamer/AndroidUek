package ch.duartemendes.paircade;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class JoinLobbyActivity extends AppCompatActivity {
    private TextView waitInviteLabel;
    private TextView waitGamestartLabel;

    private int myLocationPermissionCode = 99;
    private int myBluetoothPermissionCode = 100;
    private int myBluetoothAdminPermissionCode = 101;
    private int enableBluetooth = 102;
    private int myInternetPermissionCode = 103;

    private BluetoothAdapter bluetoothAdapter;

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

        checkLocationPermission();
        checkBluetoothPermission();
        checkBluetoothAdminPermission();
        checkInternetPermission();

        // Init Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Your phone aint got bluetooth otherwise
        if(bluetoothAdapter != null){
            checkConnected();
        }
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (getString(R.string.host_client).equals(action)) {
                String data = intent.getStringExtra(getString(R.string.host_client));
                waitGamestartLabel.setText(waitGamestartLabel.getText() + " | Invited by " + data);
            }
        }
    };

    private void checkConnected(){
        if(checkBluetoothPermission() && checkBluetoothAdminPermission()){
            PaircadePersistedData.PaircadeDataHelper dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());
            bluetoothAdapter.setName(dbHelper.getUsername());

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, enableBluetooth);
            }

            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            registerReceiver(gattUpdateReceiver, new IntentFilter(getString(R.string.host_client)));
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myLocationPermissionCode);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, myBluetoothPermissionCode);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkBluetoothAdminPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, myBluetoothAdminPermissionCode);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, myInternetPermissionCode);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
