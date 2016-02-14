package t8kme.lightcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnDis;
    //TextView txtString, txtStringLength, txtWiew; / debug
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Link the buttons and textViews to respective views
        //txtString = (TextView) findViewById(R.id.txtDebug1); //debug
        //txtStringLength = (TextView) findViewById(R.id.txtDebug2);
        //txtWiew = (TextView) findViewById(R.id.txtDebug3);
        final ToggleButton tbutton = (ToggleButton) findViewById(R.id.toggleButton);
        final ToggleButton tbutton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        final ToggleButton tbutton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        final ToggleButton tbutton4 = (ToggleButton) findViewById(R.id.toggleButton4);
        btnDis = (Button) findViewById(R.id.button4);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();                          //get length of data received
                        //txtString.setText("Data Received = " + dataInPrint);
                        //txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String val0 = recDataString.substring(1, 2);             //get sensor value from string between indices 1-5
                            String val1 = recDataString.substring(3, 4);            //same again...
                            String val2 = recDataString.substring(5, 6);
                            String val3 = recDataString.substring(7, 8);

                            //txtWiew.setText(val0 + " , " + val1 + " , " + val2 + " , " + val3 + " , ");

                            if (val0.equals("1") && !tbutton.isChecked()) tbutton.setChecked(true);
                            if (val0.equals("0") && tbutton.isChecked()) tbutton.setChecked(false);
                            if (val1.equals("0") && !tbutton2.isChecked()) tbutton2.setChecked(true);
                            if (val1.equals("1") && tbutton2.isChecked()) tbutton2.setChecked(false);
                            if (val2.equals("0") && !tbutton3.isChecked()) tbutton3.setChecked(true);
                            if (val2.equals("1") && tbutton3.isChecked()) tbutton3.setChecked(false);
                            if (val3.equals("1") && !tbutton4.isChecked()) tbutton4.setChecked(true);
                            if (val3.equals("0") && tbutton4.isChecked()) tbutton4.setChecked(false);
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        //commands to be sent to bluetooth
        tbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    tbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go herea
                            turnLight1On();   //method to turn on
                        }
                    });
                } else {
                    // The toggle is disabled
                    tbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go here
                            turnLight1Off();   //method to turn on
                        }
                    });
                }
            }
        });

        tbutton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // The toggle is enabled
                    tbutton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go here
                            turnLight2On();   //method to turn on
                        }
                    });
                }
                else {
                    // The toggle is disabled
                    tbutton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go here
                            turnLight2Off();   //method to turn on
                        }
                    });
                }
            }
        });

        tbutton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // The toggle is enabled
                    tbutton3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go herea
                            turnLight3On();   //method to turn on
                        }
                    });
                } else {
                    // The toggle is disabled
                    tbutton3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go here
                            turnLight3Off();   //method to turn on
                        }
                    });
                }
            }
        });

        tbutton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    tbutton4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go herea
                            turnPrinterOn();   //method to turn on
                        }
                    });
                } else {
                    // The toggle is disabled
                    tbutton4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // your click actions go here
                            turnPrinterOff();   //method to turn on
                        }
                    });
                }
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause(); //close connection
            }
        });
    }

    private void turnPrinterOn() {
        if (btSocket != null) {
            mConnectedThread.write("P");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.printer_on, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight1On() {
        if (btSocket != null) {
            mConnectedThread.write("G");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.mainlight_on, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight2On() {
        if (btSocket != null) {
            mConnectedThread.write("S");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.rightlight_on, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight3On() {
        if (btSocket != null) {
            mConnectedThread.write("L");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.leftlight_on, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnPrinterOff() {
        if (btSocket != null) {
            mConnectedThread.write("p");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.printer_off, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight1Off() {
        if (btSocket != null) {
            mConnectedThread.write("g");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.mainlight_off, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight2Off() {
        if (btSocket != null) {
            mConnectedThread.write("s");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.rightlight_off, Toast.LENGTH_SHORT).show();
        }
    }

    private void turnLight3Off() {
        if (btSocket != null) {
            mConnectedThread.write("l");    // Send string via Bluetooth
            Toast.makeText(getBaseContext(), R.string.leftlight_off, Toast.LENGTH_SHORT).show();
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
            msg("ERROR!");
        }
        finish(); //return to the first layout
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Urządzenie nie wspiera bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Błąd połączenia", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}