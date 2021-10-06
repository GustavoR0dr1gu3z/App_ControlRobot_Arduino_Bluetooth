package com.gustavorc.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edtTextOut;
    TextView tvtMensaje, txtInst;
    Button btnDesconectar, ButtonUp, ButtonRight, ButtonDown, ButtonLeft, ButtonSaw, ButtonFlamethrower, ButtonHammer;


    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    //Interacción con los datos de ingreso 
                    char MyCaracter = (char) msg.obj;

                    if(MyCaracter == 'I'){
                        tvtMensaje.setText("ENCENDIDO");
                    }

                    if(MyCaracter == 'O'){
                        tvtMensaje.setText("APAGADO");
                    }

                    if(MyCaracter == 'U'){
                        tvtMensaje.setText("SECUENCIA 1");
                    }

                    if(MyCaracter == 'D'){
                        tvtMensaje.setText("SECUENCIA 2");
                    }

                    if(MyCaracter == 'T'){
                        tvtMensaje.setText("SECUENCIA 3");
                    }

                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        btnDesconectar = findViewById(R.id.btnDesconectar);
        ButtonUp = findViewById(R.id.ButtonUp);
        ButtonRight = findViewById(R.id.ButtonRight);
        ButtonDown = findViewById(R.id.ButtonDown);
        ButtonLeft = findViewById(R.id.ButtonLeft);
        ButtonSaw = findViewById(R.id.ButtonSaw);
        ButtonFlamethrower = findViewById(R.id.ButtonFlamethrower);
        ButtonHammer = findViewById(R.id.ButtonHammer);



        //String instruccion = " I: ENCENDER \n O: APAGAR\n U: SEC1\n D: SEC2\n T: SEC3             HECHO POR: Gustavo Rodriguez Calzada";
        //txtInst.setText(instruccion);
      /*  btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String dato = edtTextOut.getText().toString();
                //tvtMensaje.setText(dato);
                MyConexionBT.write(dato);
            }
        });
*/
        ButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("U");
            }
        });

        ButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("R");
            }
        });

        ButtonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("D");
            }
        });

        ButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("L");
            }
        });

        ButtonSaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("S");
            }
        });

        ButtonFlamethrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("F");
            }
        });

        ButtonHammer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                MyConexionBT.write("H");
            }
        });


        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] byte_in = new byte[1];
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



}