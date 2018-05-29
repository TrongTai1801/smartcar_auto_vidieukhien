package com.appproteam.sangha.android_bluetooth_rc;


import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;


public class MainActivity extends AppCompatActivity{
    Dialog dialog;
    private PatternLockView mPatternLockView;
    private Button mBtnAddress;
    private List<String> mList = new ArrayList<>();
    private List<Integer> mListPointInteger = new ArrayList<>();
    private static int mOriginalDirection = 2;
    private static int mSizeOfMatrix = 5;
    static Handler handler = new Handler();
    static byte Masat=2;
    static int currentDo;
    static int visible=0;
    private static final int REQUEST_ENABLE_BT = 1;
    List<String> soDo= new ArrayList<>();
    byte[] bytesTosendRight={4};
    byte[] bytesTosendLeft={3};
    byte[] bytesTosendDown={2};
    byte[] bytesToSendUp = {1};
    byte[] bytesToSendRelease = {6};
    byte[] bytesAutoUp={7,6};
    byte[] bytesAutoDown={8,6};
    byte[] bytesAutoLeft={9,28,2,6};
    byte[] bytesAutoRight={10,28,2,6};



    BluetoothAdapter bluetoothAdapter;


    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    Spinner spnSoDo;
    EditText edtMasat;
    TextView  textStatus;
    ListView listViewPairedDevice;
    RelativeLayout inputPane,rlTeamInfo;
    LinearLayout llTest;
    Button btnUp, btnDown, btnRight, btnLeft,btnGoAhead,btnTurnLeft,btnTurnRight,btnMasat,btnBackWard,btnTest;


    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    static ThreadConnected myThreadConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog= new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_test);
        soDo.add("45");
        soDo.add("90");
        btnBackWard=(Button)dialog.findViewById(R.id.btnBackWard);
        btnUp=(Button)findViewById(R.id.btnUp);
        btnDown=(Button)findViewById(R.id.btnDown);
        btnLeft=(Button)findViewById(R.id.btnLeft);
        btnRight=(Button)findViewById(R.id.btnRight);
        btnTurnLeft=(Button)dialog.findViewById(R.id.btnTurnLeft);
        btnTurnRight=(Button)dialog.findViewById(R.id.btnTurnRight);
        btnTest=(Button)findViewById(R.id.btnTest);
        textStatus = (TextView)findViewById(R.id.status);
        btnMasat=(Button)dialog.findViewById(R.id.btnMasat);
        edtMasat=(EditText)dialog.findViewById(R.id.edtMaSat);

        listViewPairedDevice = (ListView)findViewById(R.id.pairedlist);
        btnGoAhead=(Button)dialog.findViewById(R.id.btnGoAhead);
        spnSoDo=(Spinner)dialog.findViewById(R.id.spnSoDo);
        mBtnAddress = (Button) findViewById(R.id.btn_address);
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);

        spnSoDo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,soDo));
        handler=new Handler();
        inputPane = (RelativeLayout) findViewById(R.id.inputpane);
        llTest=(LinearLayout)findViewById(R.id.llTest);
        rlTeamInfo=(RelativeLayout)findViewById(R.id.rlTeamInfo);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }



        btnMasat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Masat = Byte.valueOf(edtMasat.getText().toString());
            }
        });

        btnUp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {

                    myThreadConnected.write(bytesToSendUp);
                }
                else if (motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                       myThreadConnected.write(bytesToSendRelease);
                }
                return false;
            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {
                    myThreadConnected.write(bytesTosendDown);
                }
                else if (motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    myThreadConnected.write(bytesToSendRelease);
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {
                    myThreadConnected.write(bytesTosendRight);
                }
                else if (motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    myThreadConnected.write(bytesToSendRelease);
                }
                return false;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {
                    myThreadConnected.write(bytesTosendLeft);
                }
                else if (motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    myThreadConnected.write(bytesToSendRelease);
                }
                return false;
            }
        });

        spnSoDo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentDo=Integer.valueOf(spnSoDo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnGoAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goAhead();
        }
        });
        btnBackWard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backWard();
            }
        });
        btnTurnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               turnLeft(currentDo);



            }
        });
        btnTurnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                turnRight(currentDo);

            }
        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visible==0) {
                    dialog.show();
                    visible=1;
                }
                else
                if(visible==1)
                {

                    dialog.dismiss();
                    visible=0;
                }
            }
        });
        //Patern Lock
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                mList.add(PatternLockUtils.patternToString(mPatternLockView, progressPattern));
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if(mList.size() != 0){
                    mListPointInteger.removeAll(mListPointInteger);
                    for (int i = 0; i < mList.size(); i++){
                        if (i == 0){
                            mListPointInteger.add(Integer.parseInt(mList.get(i)));
                        }
                        else{
                            mListPointInteger.add(Integer.parseInt(mList.get(i).substring((mList.get(i-1)).length())));
                        }
                    }
                }
                mList.removeAll(mList);
                //                else{
//                    Log.e("abc","wrong");
//                }

//                if(mListPointInteger.size() != 0){
//                    for (int i = 0; i < mListPointInteger.size(); i++){
//                        if (i == 0){
//                            Log.e("abc", ""+ mListPointInteger.get(i));
//                        }
//                        else{
//                              Log.e("abc", "" + mListPointInteger.get(i));
//                        }
//                    }
//                }else{
//                    Log.e("abc","wrong");
//                }
                //TODO everything here
            }

            @Override
            public void onCleared() {
                Log.e("cleared", "here");
            }
        });

        mBtnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListPointInteger.size() == 0){
                    Toast.makeText(getApplicationContext(), "wrong drawing", Toast.LENGTH_LONG).show();
                    mListPointInteger.removeAll(mListPointInteger);
                    mList.removeAll(mList);
                    return;
                }
                if (mListPointInteger.get(0) != 0){
                    Toast.makeText(getApplicationContext(), "wrong drawing", Toast.LENGTH_LONG).show();
                    mListPointInteger.removeAll(mListPointInteger);
                    mList.removeAll(mList);
                    return;
                }
//                }
                Address(mListPointInteger);
            }
        });

    }
    public int GetConnet(int nextDirection, int currentDirection){
        switch (Math.abs(nextDirection - currentDirection)){
            case 1:
                return 45;
            case 2:
                return 90;
            case 3:
                return 45;
            case 5:
                return 45;
            case 6:
                return 90;
            case 7:
                return 45;
            default:
                return 0;
        }
    }


    public String GetTurn(int nextDirection, int currentDirection){
        if ((nextDirection >= currentDirection)){
            if (((Math.abs(nextDirection - currentDirection) >= 4) &&
                    (Math.abs(nextDirection - currentDirection) < 6)) ||
                    (Math.abs(nextDirection - currentDirection) >= 0) &&
                            (Math.abs(nextDirection - currentDirection) <= 2)){
                return "right";
            }
            return "left";
        }else{
            if (((Math.abs(nextDirection - currentDirection) >= 4) &&
                    (Math.abs(nextDirection - currentDirection) < 6)) ||
                    (Math.abs(nextDirection - currentDirection) >= 0) &&
                            (Math.abs(nextDirection - currentDirection) <= 2)){
                return "left";
            }
            return "right";
        }
    }


    public String Getdirection(int nextDirection, int currentDirection){
        if((currentDirection <= 1) || (currentDirection >= 5)){
            if((Math.abs(nextDirection - currentDirection) <= 2) ||
                    (Math.abs(nextDirection - currentDirection) >= 6))
                return "ahead";
            else return "back";
        }else{
            if((Math.abs(nextDirection - currentDirection) <= 2))
                return "ahead";
            else return "back";
        }
//        return "back";
    }


    public void Address(List<Integer> listPoint){
        int mCurrentPoint = 0;
        int mNextPoint = 0;
        int mCurrentDirection = mOriginalDirection;
        int mNextDirection = mOriginalDirection;
        for (int i = 0; i < listPoint.size(); i++){
            if(!checkNumber(mCurrentPoint, mNextPoint)) {
                Toast.makeText(getApplicationContext(), "wrong drawing", Toast.LENGTH_LONG).show();
                return;
            }
        }
        for (int i = 0; i < listPoint.size() - 1; i++){
            mCurrentPoint = listPoint.get(i);
            mNextPoint = listPoint.get(i+1);
            if ((mNextPoint - mCurrentPoint) == 1){ // direction 2
                mNextDirection = 2;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if ((mNextPoint - mCurrentPoint) == 5){ // direction 4
                mNextDirection = 4;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if ((mCurrentPoint - mNextPoint) == 1){ // direction 6
                mNextDirection = 6;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if ((mCurrentPoint - mNextPoint) == 5){ // direction 0
                mNextDirection = 0;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if (((mCurrentPoint + 5) + (mCurrentPoint + 1) - mCurrentPoint) == mNextPoint){ // direction 3
                mNextDirection = 3;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if (((mCurrentPoint - 5) + (mCurrentPoint + 1) - mCurrentPoint) == mNextPoint){ // direction 1
                mNextDirection = 1;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if (((mCurrentPoint - 5) + (mCurrentPoint - 1) - mCurrentPoint) == mNextPoint){ // direction 7
                mNextDirection = 7;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
            if (((mCurrentPoint + 5) + (mCurrentPoint - 1) - mCurrentPoint) == mNextPoint){ // direction 5
                mNextDirection = 5;
                //TODO address
                GetWay(mCurrentDirection, mNextDirection);
                Log.e("Direct: ", mCurrentDirection + ", " + mNextDirection);
                if (Getdirection(mNextDirection, mCurrentDirection).equals("back")){
                    if (mNextDirection > 4){
                        mCurrentDirection = mNextDirection - 4;
                    }else {
                        mCurrentDirection = mNextDirection + 4;
                    }
                }else
                    mCurrentDirection = mNextDirection;
            }
        }
        mList.removeAll(mList);
        mListPointInteger.removeAll(mListPointInteger);
        mCurrentDirection = mOriginalDirection;
        mNextDirection = mOriginalDirection;
    }

    public void GetWay(int currentDirection, int nextDirection){
        int sodo;
        sodo = Integer.valueOf(GetConnet(nextDirection, currentDirection));
        if (GetTurn(nextDirection, currentDirection).equals("left"))
        {

            turnLeft(sodo);

        }

        else
        if (GetTurn(nextDirection, currentDirection).equals("right")) {

          turnRight(sodo);

        }
      if (  Getdirection(nextDirection, currentDirection).equals("ahead"))
              goAhead();
      else
      if (  Getdirection(nextDirection, currentDirection).equals("back"))
              backWard();


        Log.e("way: ", GetTurn(nextDirection, currentDirection) + ", " +
                GetConnet(nextDirection, currentDirection) + ", " +
                Getdirection(nextDirection, currentDirection));
    }
    public boolean checkNumber(int n, int m){
        if(m == n+1){
            if((m/5) == 0){ // chia cho kich thuoc ma tran
                return false;
            }
        }
        if(Math.abs(m - n) > 6) // kich thuoc ma tran  + 1
            return false;
        return true;
    }





    public void turnLeft(int soDo)
    {
        if (soDo==0) {}
        if (soDo==45){

            bytesAutoLeft[2]=Masat;
            myThreadConnected.write(bytesAutoLeft);

        }
        else if (soDo==90)
        {

            byte Masat2= (byte) (Masat+Masat);
            bytesAutoLeft[2]=Masat2;
            myThreadConnected.write(bytesAutoLeft);

        }




    }
    public void goAhead()
    {
        myThreadConnected.write(bytesAutoUp);



    }
    public void turnRight(int soDo)
    {
        if (soDo==0) {}
        if (soDo==45){


            bytesAutoRight[2]=Masat;
            myThreadConnected.write(bytesAutoRight);

    }
        else if (soDo==90)
    {
        byte Masat2= (byte) (Masat+Masat);
        bytesAutoRight[2]=Masat2;
        myThreadConnected.write(bytesAutoRight);


    }

    }
    public void backWard()
    {
        myThreadConnected.write(bytesAutoDown);

    }
    public void delay(long a)
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 4s = 5000ms
                    myThreadConnected.write(bytesToSendRelease);
            }
        }, a);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);
                    Toast.makeText(MainActivity.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress() + "\n"
                                    + "BondState: " + device.getBondState() + "\n"
                                    + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                    + "Class: " + device.getClass(),
                            Toast.LENGTH_LONG).show();

                    textStatus.setText("start ThreadConnectBTdevice");
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("Bắt đầu kết nối đến thiết bị BlueTooth");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("Lỗi Kết Nối BluetoothSocket  \n Kết Nối Thất Bại!!");
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "Kết Nối Thành Công\n"

                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);

                        listViewPairedDevice.setVisibility(View.GONE);
                        inputPane.setVisibility(View.VISIBLE);
                        rlTeamInfo.setVisibility(View.GONE);
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "Đóng bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }});
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}