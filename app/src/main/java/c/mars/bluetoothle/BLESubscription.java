package c.mars.bluetoothle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by Constantine Mars on 3/25/15.
 */
public class BLESubscription {
    public static final String UUID_SERVICE_GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_DEVICE_NAME = "00002A00-0000-1000-8000-00805f9b34fb";

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic nameCharacteristic;
    private BluetoothLEConnector.BLECallbacks bleCallbacks;

    public BLESubscription(BluetoothGatt gatt, BluetoothLEConnector.BLECallbacks bleCallbacks) {
        this.gatt = gatt;
        this.bleCallbacks = bleCallbacks;
    }

    public void requestDeviceName() {
        BluetoothGattService service = gatt.getService(UUID.fromString(UUID_SERVICE_GENERIC_ACCESS));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_DEVICE_NAME));
            if (characteristic!=null)
                nameCharacteristic = characteristic;
                gatt.readCharacteristic(nameCharacteristic);
        }
    }

    public void requestAllCharacteristics() {
        List<BluetoothGattService> services = gatt.getServices();
        for(BluetoothGattService service:services){
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics) {
                gatt.readCharacteristic(characteristic);
                bleCallbacks.log("request read for c["+characteristic.getUuid().toString()+"]");
            }
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic, int status) {
        bleCallbacks.log("c["+characteristic+"]: "+bytesToString(characteristic.getValue())+", status:"+status);
    }

    public static String readDeviceName(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTERISTIC_DEVICE_NAME))){
            byte[] data = characteristic.getValue();
            return bytesToString(data);
        }
        return null;
    }

    public static String bytesToString(byte[] data){
        if (data!=null && data.length>0){
            StringBuilder sb = new StringBuilder(data.length);
            for (byte b:data){
                sb.append(String.format("%02X ", b));
            }
            sb.append(" -> ");
            for (byte b:data) {
                sb.append((char)b);
            }
            return sb.toString();
        }
        return null;
    }

    public void writeDeviceName(String newName) {
        nameCharacteristic.setValue(newName.getBytes());
        gatt.writeCharacteristic(nameCharacteristic);
    }
}
