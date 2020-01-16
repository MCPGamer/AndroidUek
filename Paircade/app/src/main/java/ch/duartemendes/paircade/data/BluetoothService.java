package ch.duartemendes.paircade.data;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class BluetoothService {
    public static final UUID UUID_BLE_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARA_USERNAME = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHARA_DESCRIPTOR = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");

    public static BluetoothGattService createService(){
        BluetoothGattService service = new BluetoothGattService(UUID_BLE_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Current Time characteristic
        BluetoothGattCharacteristic username = new BluetoothGattCharacteristic(UUID_CHARA_USERNAME,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        BluetoothGattDescriptor configDescriptor = new BluetoothGattDescriptor(UUID_CHARA_DESCRIPTOR,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        username.addDescriptor(configDescriptor);

        service.addCharacteristic(username);
        return  service;
    }
}
