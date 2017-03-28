package tz.co.fasthub.evoucher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;


/**
 * Created by bonifacechacha on 3/27/17.
 */

public class Printer {

    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream inputStream;

    int readBufferPosition;
    byte[] readBuffer;

    volatile boolean stopWorker;
    String value = "";

    public void print(String txtvalue, Activity activity) {
        //if failed to init printer then stop
        if (!initPrinter(activity)) return;

        byte[] buffer = txtvalue.getBytes();
        byte[] PrintHeader = {(byte) 0xAA, 0x55, 2, 0};
        PrintHeader[3] = (byte) buffer.length;

        if (PrintHeader.length > 128) {
            value += "\nValue is more than 128 size\n";
            Toast.makeText(activity, value, Toast.LENGTH_LONG).show();
        } else {
            try {

                outputStream.write(txtvalue.getBytes());
                outputStream.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                value += ex.toString() + "\n" + "Excep IntentPrint \n";
                Toast.makeText(activity, value, Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean initPrinter(Activity activity) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            BluetoothDevice bluetoothDevice = null;
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("SUP58M1")) {
                        bluetoothDevice = device;
                        break;
                    }
                }
            }

            if (bluetoothDevice != null) {
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();
                return true;
            } else {
                value += "No Devices found";
                Toast.makeText(activity, value, Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (Exception ex) {
            value += ex.getMessage() + "\n" + "InitPrinter \n";
            Toast.makeText(activity, value, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            Thread workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.d("e", data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
