package ch.duartemendes.paircade;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

import ch.duartemendes.paircade.data.BluetoothService;
import ch.duartemendes.paircade.data.PaircadePersistedData;

public class CreateLobbyActivity extends AppCompatActivity {

    private int myLocationPermissionCode = 99;
    private int myBluetoothPermissionCode = 100;
    private int myBluetoothAdminPermissionCode = 101;
    private int enableBluetooth = 102;

    private PaircadePersistedData.PaircadeDataHelper dbHelper;
    private String gamemode;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;
    private BluetoothGattService bluetoothService;

    private ArrayAdapter<String> newDevicesNames;
    private ArrayAdapter<String> pairedDevicesNames;

    private ListView newDevicesListView;
    private ListView pairedDevicesListView;

    // Receiver to add new Found devices
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() != null) {
                        boolean containsAlready = false;
                        String name = device.getName() + "\n" + device.getAddress();

                        for (int i = 0; i < newDevicesNames.getCount(); i++) {
                            if (newDevicesNames.getItem(i).equals(name)) {
                                containsAlready = true;
                            }
                        }

                        if (!containsAlready) {
                            newDevicesNames.add(name);
                        }
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (newDevicesNames.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.no_devices).toString();
                    newDevicesNames.add(noDevices);
                }
            }
        }
    };

    // Clicklistener for Pairing
    private OnItemClickListener deviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            bluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Get the BluetoothDevice to connect
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            try {
                Log.d("Connection to user", "Connecting");
                device.createBond();
                bluetoothGatt = device.connectGatt(CreateLobbyActivity.this, false, gattCallback);
                if(bluetoothService != null){
                    bluetoothService.getCharacteristic(BluetoothService.UUID_CHARA_USERNAME).setValue(dbHelper.getUsername().getBytes());
                    bluetoothGatt.readCharacteristic(bluetoothService.getCharacteristic(BluetoothService.UUID_CHARA_USERNAME));
                }
                Log.d("Connection to user", "Connected");
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            checkConnected();
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            broadcastUpdate(getString(R.string.host_client), characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            broadcastUpdate(getString(R.string.host_client), characteristic);
        }
    };

    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(getString(R.string.host_client), characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            broadcastUpdate(getString(R.string.host_client), characteristic);
        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(getString(R.string.host_client), new String(data) + "\n" +
                    stringBuilder.toString());
        }

        sendBroadcast(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlobby);

        if()

        // Add Icon to Bar at top of screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.paircade_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        checkLocationPermission();
        checkBluetoothPermission();
        checkBluetoothAdminPermission();

        dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());

        // Init Array Adapters
        newDevicesNames = new ArrayAdapter<String>(this, R.layout.device_name);
        pairedDevicesNames = new ArrayAdapter<String>(this, R.layout.device_name);

        // Init ListViews
        newDevicesListView = findViewById(R.id.viewNewDevices);
        pairedDevicesListView = findViewById(R.id.viewPairedDevices);

        // Set Lists
        newDevicesListView.setAdapter(newDevicesNames);
        pairedDevicesListView.setAdapter(pairedDevicesNames);

        // Set OnClick Listeners
        newDevicesListView.setOnItemClickListener(deviceClickListener);
        pairedDevicesListView.setOnItemClickListener(deviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        // Init Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Your phone aint got bluetooth otherwise
        if (bluetoothAdapter != null) {
            checkConnected();

            bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            bluetoothGattServer = bluetoothManager.openGattServer(CreateLobbyActivity.this, gattServerCallback);
            bluetoothService = BluetoothService.createService();
            bluetoothGattServer.addService(bluetoothService);
        }
    }

    private void doDiscovery() {
        // Restart Discovering
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop searching
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkConnected() {
        if (checkBluetoothPermission() && checkBluetoothAdminPermission()) {
            PaircadePersistedData.PaircadeDataHelper dbHelper = new PaircadePersistedData.PaircadeDataHelper(getBaseContext());
            bluetoothAdapter.setName(dbHelper.getUsername());

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, enableBluetooth);
            }

            doDiscovery();

            // Get a set of currently paired devices
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            pairedDevicesNames.clear();

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesNames.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.no_devices).toString();
                pairedDevicesNames.add(noDevices);
            }
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

    @Override
    protected void onStop() {
        bluetoothGatt.disconnect();
        bluetoothGattServer.close();
        super.onStop();
    }

    public void startGameSolo(View view){
        if()
    }

    public void startGame(View view){

    }
}
