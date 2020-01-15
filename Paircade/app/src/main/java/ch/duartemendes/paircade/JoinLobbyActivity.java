package ch.duartemendes.paircade;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

import ch.duartemendes.paircade.data.PaircadePersistedData;

public class JoinLobbyActivity extends AppCompatActivity {
    private TextView waitInviteLabel;
    private TextView waitGamestartLabel;

    private int MyLocationPermissionCode = 99;
    private int MyBluetoothPermissionCode = 100;
    private int MyBluetoothAdminPermissionCode = 101;

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

        // Init Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Your phone aint got bluetooth otherwise
        if(bluetoothAdapter != null){
            checkConnected();
        }
    }

    private void checkConnected(){
        if(checkBluetoothPermission() && checkBluetoothAdminPermission()){
            PaircadePersistedData.PaircadeDataHelper dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());
            bluetoothAdapter.setName(dbHelper.getUsername());

            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MyLocationPermissionCode);

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MyBluetoothPermissionCode);

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MyBluetoothAdminPermissionCode);

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
}
