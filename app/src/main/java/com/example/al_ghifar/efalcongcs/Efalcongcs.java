package com.example.al_ghifar.efalcongcs;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.ui.IconGenerator;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;
import org.videolan.libvlc.MediaPlayer;

public class Efalcongcs extends FragmentActivity implements OnMapReadyCallback, VlcListener,
        TextureView.SurfaceTextureListener, SettingPID.DialogListener {
    private static final String TAG = "Efalcongcs";
    public final String ACTION_USB_PERMISSION = "package com.example.al_ghifar.efalcon.USB_PERMISSION";
    private final String stream_url ="https://192.168.43.1:8080/video";//"rtsp://192.168.43.153:8554/unicast";
    boolean isFlip = true, isFlipWP = true, isFlipDS = true, isFlipPlay = true, inH=false, inbef=false;
    public String Rollp, Rolli, Rolld, Pitchp, Pitchi, Pitchd, Yawp, Yawi, Yawd, Altp, Alti, Altd, Posp, Posi, Posd, PosRp, PosRi, PosRd, NavRp, NavRi, NavRd ;
    int i = 0, mTracking = 0, countTracking= 0, lst = 0, countMarker= 0;
    public Bitmap icon;
    public IconGenerator icongenerator;
    long startTime, difference;
    RelativeLayout LayoutMonitor, LayoutWaypoint, LayoutCh;
    TableLayout LayoutStream;
    Button sendWPButton, pidButton;
    double wpla,wpln,wpradius;
    ImageButton monitorButton, wpButton,startButton, stopButton, btnPlay, captureButton, BtStream;
    TextView textView, Troll, Tyaw, Tlat, Tlong, Talt, tvTime, tch1, tch2, tch3, tch4,TvMode, TBattery, Tpitch, TvDevice;
    SeekBar AltitudeBar;//, ThrottleBar;
    private ProgressBar Aileron, Elevator, Throttle, Rudder;
    private ImageView HeadingRoll, HeadingYaw;
    private EditText etEndpoint;
    CardView cvNamebar;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    private Circle [] circle = new Circle[10];
    private VlcVideoLibrary vlcVideoLibrary;
    private TextureView mStream;
    private Surface surface;
    private String[] options = new String[]{":fullscreen"};
    ListView lvLat, lvLng, lvNum, lvDis;
    private float DegreeStartRoll=0f, DegreeStartYaw=0f;
    private Chronometer timeFlight;
    private long pauseoffset;
    private boolean running;
    private GoogleMap mMap;
    Marker currentMarker;
    Marker wpMarker, wpMarkerB;
    Polyline polyline;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    ArrayList<LatLng> points;
    ArrayList<LatLng> tracking;
    ArrayList<String> listnumber = new ArrayList<>();
    ArrayList<String> listLat = new ArrayList<>();
    ArrayList<String> listLng = new ArrayList<>();
    ArrayList<String> listDistance = new ArrayList<>();
    ArrayAdapter<String> adapterNumber, adapterLat, adapterLng, adapterDistance;
    float[] distance = new float[2];
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {///
            try {
                final String data = new String(arg0, "UTF-8");
                final String[] parsing = data.split(",");
                int count = parsing.length;
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Serial Monitor : "+ data);
                        //TvDevice.setText("Multiwii");
                    }
                });*/
                if (count == 13 && parsing[0].equals("MW")) {
                    dataParsing(parsing[0],parsing[1],parsing[2],parsing[3],parsing[4],parsing[5],parsing[6],
                            parsing[7],parsing[8],parsing[9],parsing[10], parsing[11], parsing[12]);
                    datalog(parsing[0],parsing[1],parsing[2],parsing[3],parsing[4],parsing[5],parsing[6]);//*/
                }
                if (count == 13 && parsing[0].equals("EF")) {
                    //TvDevice.setText("Multiwii");
                    dataParsing(parsing[0],parsing[1],parsing[2],parsing[3],parsing[4],parsing[5],parsing[6],
                            parsing[7],parsing[8],parsing[9],parsing[10], parsing[11], parsing[12]);
                    datalog(parsing[0],parsing[1],parsing[2],parsing[3],parsing[4],parsing[5],parsing[6]);//
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TvDevice.setText("Efalcon");
                        }
                    });
                }//*/
                if (count == 22) {
                    Rollp=parsing[1];
                    Rolli=parsing[2];
                    Rolld=parsing[3];

                    Pitchp=parsing[4];
                    Pitchi=parsing[5];
                    Pitchd=parsing[6];

                    Yawp=parsing[7];
                    Yawi=parsing[8];
                    Yawd=parsing[9];

                    Altp=parsing[10];
                    Alti=parsing[11];
                    Altd=parsing[12];

                    Posp=parsing[13];
                    Posi=parsing[14];
                    Posd=parsing[15];

                    PosRp=parsing[16];
                    PosRi=parsing[17];
                    PosRd=parsing[18];

                    NavRp=parsing[19];
                    NavRi=parsing[20];
                    NavRd=parsing[21];
                }//*/
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(57600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                            Toast.makeText(Efalcongcs.this, "PORT NOT OPEN", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                        Toast.makeText(Efalcongcs.this, "PORT IS NULL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                    Toast.makeText(Efalcongcs.this, "PORT NOT GRANTED", Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    //protected
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efalcongcs);
        Log.d(TAG, "onCreate: Started");
        reqPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (ImageButton) findViewById(R.id.BtConnect);
        stopButton = (ImageButton) findViewById(R.id.BtDisconnect);
        monitorButton = (ImageButton) findViewById(R.id.BtMonitor);
        wpButton = (ImageButton) findViewById(R.id.BtWP);
        BtStream = (ImageButton) findViewById(R.id.btnStream);
        wpButton.setImageResource(R.drawable.wp02);
        sendWPButton = (Button) findViewById(R.id.BtsendWP);
        textView = (TextView) findViewById(R.id.tvSerial);
        TvDevice = (TextView) findViewById(R.id.tvDevice);
        tvTime = (TextView) findViewById(R.id.tvTimeDelay);
        TvMode = (TextView) findViewById(R.id.tvMode);
        lvLat = (ListView) findViewById(R.id.lvlat);
        lvLng = (ListView) findViewById(R.id.lvlng);
        lvNum = (ListView) findViewById(R.id.lvnumber);
        lvDis = (ListView) findViewById(R.id.lvdistance);
        Troll = (TextView) findViewById(R.id.tvRoll);
        Tpitch = (TextView) findViewById(R.id.tvPitch);
        Tyaw = (TextView) findViewById(R.id.tvYaw);
        Tlat = (TextView) findViewById(R.id.tvLat);
        Tlong = (TextView) findViewById(R.id.tvLong);
        Talt = (TextView) findViewById(R.id.tvAlt);
        AltitudeBar = (SeekBar) findViewById(R.id.altitude);
        //ThrottleBar = (SeekBar)findViewById(R.id.seekThrottle);
        HeadingRoll = (ImageView) findViewById(R.id.roll_image);
        HeadingYaw = (ImageView) findViewById(R.id.yaw_image);
        LayoutMonitor = (RelativeLayout) findViewById(R.id.monitorLayout);
        LayoutWaypoint = (RelativeLayout) findViewById(R.id.waypointLayout);
        LayoutStream = (TableLayout) findViewById(R.id.streamDisplay);
        LayoutStream.setVisibility(LayoutStream.GONE);
        LayoutCh = (RelativeLayout) findViewById(R.id.chlayout);
        LayoutWaypoint.setVisibility(LayoutWaypoint.INVISIBLE);
        sendWPButton.setVisibility(sendWPButton.INVISIBLE);
        monitorButton.setImageResource(R.drawable.ic_flipgreen);
        monitorButton.setBackgroundResource(R.drawable.circlewhite);
        etEndpoint = (EditText) findViewById(R.id.url);
        captureButton = (ImageButton) findViewById(R.id.BtCapture);
        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        mStream = (TextureView) findViewById(R.id.Stream);
        mStream.setSurfaceTextureListener(this);
        Aileron = (ProgressBar)findViewById(R.id.Remotech1);
        Elevator = (ProgressBar)findViewById(R.id.Remotech2);
        Throttle = (ProgressBar)findViewById(R.id.Remotech3);
        Rudder = (ProgressBar)findViewById(R.id.Remotech4);
        cvNamebar = (CardView) findViewById(R.id.namebar);
        cvNamebar.setBackgroundResource(R.drawable.rounded_namebar);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, mStream);
        vlcVideoLibrary.setOptions(Arrays.asList(options));
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        timeFlight = (Chronometer) findViewById(R.id.timer);
        points = new ArrayList<LatLng>();
        tracking = new ArrayList<LatLng>();
        adapterNumber = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listnumber){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);

                //Set your Font Size Here.
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textview.setGravity(Gravity.CENTER);

                return view;
            }
        };
        adapterLat = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listLat){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);

                //Set your Font Size Here.
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textview.setGravity(Gravity.CENTER);

                return view;
            }
        };
        adapterLng = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listLng){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);

                //Set your Font Size Here.
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textview.setGravity(Gravity.CENTER);

                return view;
            }
        };
        adapterDistance = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDistance){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);

                //Set your Font Size Here.
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                textview.setGravity(Gravity.CENTER);

                return view;
            }
        };
        registerReceiver(broadcastReceiver, filter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUiEnabled(false);
        pidButton = (Button) findViewById(R.id.BtPID);
        pidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pid = "PPPPPPPPPP";
                serialPort.write(pid.getBytes());
                openDialog(Rollp,Rolli,Rolld,Pitchp,Pitchi,Pitchd,Yawp,Yawi,Yawd,Altp,Alti,Altd,Posp,Posi,Posd,PosRp,PosRi,PosRd,NavRp,NavRi,NavRd);
            }
        });

        icongenerator = new IconGenerator(this);
    }

    public void openDialog(final String rollp, final String rolli, final String rolld, final String pitchp, final String pitchi, final String pitchd, final String yawp, final String yawi, final String yawd, final String altp, final String alti, final String altd, final String posp, final String posi, final String posd, final String posRp, final String posRi, final String posRd, final String navRp, final String navRi, final String navRd){
        Bundle bundle = new Bundle();
        SettingPID settingPID = new SettingPID();
        settingPID.setArguments(bundle);
        settingPID.show(getSupportFragmentManager(), "setting pid");

        bundle.putString("Rollp", rollp);
        bundle.putString("Rolli",rolli);
        bundle.putString("Rolld",rolld);

        bundle.putString("Pitchp",pitchp);
        bundle.putString("Pitchi",pitchi);
        bundle.putString("Pitchd",pitchd);

        bundle.putString("Yawp",yawp);
        bundle.putString("Yawi",yawi);
        bundle.putString("Yawd",yawd);

        bundle.putString("Altp",altp);
        bundle.putString("Alti",alti);
        bundle.putString("Altd",altd);

        bundle.putString("Posp",posp);
        bundle.putString("Posi",posi);
        bundle.putString("Posd",posd);

        bundle.putString("PosRp",posRp);
        bundle.putString("PosRi",posRi);
        bundle.putString("PosRd",posRd);

        bundle.putString("NavRp",navRp);
        bundle.putString("NavRi",navRi);
        bundle.putString("NavRd",navRd);

    }

    @Override
    public void applyTexts(String rollp, String rolli, String rolld, String pitchp, String pitchi, String pitchd, String yawp, String yawi, String yawd, String altp, String alti, String altd, String posp, String posi, String posd, String posRp, String posRi, String posRd, String navRp, String navRi, String navRd) {
        String dataPID = rollp+"/"+rolli+"/"+rolld+"/"
                +pitchp+"/"+pitchi+"/"+pitchd+"/"
                +yawp+"/"+yawi+"/"+yawd+"/"
                +altp+"/"+alti+"/"+altd+"/"
                +posp+"/"+posi+"/"+posd+"/"
                +posRp+"/"+posRi+"/"+posRd+"/"
                +navRp+"/"+navRi+"/"+navRd+"/";
        serialPort.write(dataPID.getBytes());
    }

    public void onSendWP(View view) {
        String WPdata = "W";
        for(int i = 0; i < lst;i++){
            WPdata = WPdata+listLat.get(i)+","+listLng.get(i)+"#";
        }
        serialPort.write(WPdata.getBytes());
        textView.setText(WPdata);
    }

    public void onMonitor(View view) {
        mTracking = 1;
        if(isFlip){
            monitorButton.setImageResource(R.drawable.ic_flip);
            monitorButton.setBackgroundResource(R.drawable.circle);
            LayoutMonitor.setVisibility(LayoutMonitor.INVISIBLE);
            LayoutCh.setVisibility(LayoutCh.INVISIBLE);
        }
        else{
            mTracking = 2;
            monitorButton.setImageResource(R.drawable.ic_flipgreen);
            monitorButton.setBackgroundResource(R.drawable.circlewhite);
            LayoutMonitor.setVisibility(LayoutMonitor.VISIBLE);
            LayoutCh.setVisibility(LayoutCh.VISIBLE);
            if (isFlipDS == false){
                LayoutCh.setVisibility(LayoutCh.INVISIBLE);
            }
            else {
                LayoutCh.setVisibility(LayoutCh.VISIBLE);
            }
        }
        isFlip = !isFlip; // reverse
    }

    public void onWP(View view) {

        //int c = 0;
        if(isFlipWP){
            mTracking = 2;
            wpButton.setImageResource(R.drawable.wp02green);
            wpButton.setBackgroundResource(R.drawable.circlewhite);
            LayoutWaypoint.setVisibility(LayoutWaypoint.VISIBLE);
            sendWPButton.setVisibility(sendWPButton.VISIBLE);
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    countMarker ++;
                    //markerOptions.snippet("Latitude:"+point.latitude+","+"Longitude:"+point.longitude);
                    Double dtLat = new Double(point.latitude);
                    Double dtLng = new Double(point.longitude);
                    String dataLat = String.format("%.7f", dtLat);
                    String dataLng = String.format("%.7f", dtLng);
                    // Adding the taped point to the ArrayList
                    points.add(point);
                    float disWP = 0f;
                    for(int x=0; x < points.size()-1; x++){
                        LatLng pointA = points.get(x);
                        LatLng pointB = points.get(x + 1);
                        float[] results = new float[3];
                        Location.distanceBetween(pointA.latitude, pointA.longitude,pointB.latitude, pointB.longitude, results);
                        disWP = results[0];
                    }
                    listDistance.add(String.valueOf(disWP));
                    adapterDistance.notifyDataSetChanged();
                    // Adding the marker to the map
                    icongenerator.setBackground(getResources().getDrawable(R.drawable.pinwp));
                    icongenerator.setTextAppearance(R.style.iconGenText);
                    icongenerator.setContentPadding(26,10,26,0);
                    wpMarkerB = mMap.addMarker(new MarkerOptions().position(point));;
                    wpMarkerB.setIcon(BitmapDescriptorFactory.fromBitmap(icongenerator.makeIcon(String.valueOf(countMarker))));//*/

                    listnumber.add(String.valueOf(countMarker));
                    adapterNumber.notifyDataSetChanged();
                    listLat.add(dataLat);
                    adapterLat.notifyDataSetChanged();
                    listLng.add(dataLng);
                    adapterLng.notifyDataSetChanged();

                    lst++;
                    // Setting points of polyline
                    //confWP(point);
                    circle[i] = mMap.addCircle(new CircleOptions()
                            .center(point)
                            .radius(20)
                            .strokeWidth(1.0f)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x1500FFFF));
                    // Adding the polyline to the map
                    //mMap.addPolyline(polylineOptions);
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().color(Color.RED).width(3).addAll(points));
                    //wpMarker.showInfoWindow();
                    i++;
                }
            });

            lvNum.setAdapter(adapterNumber);
            lvLat.setAdapter(adapterLat);
            lvLng.setAdapter(adapterLng);
            lvDis.setAdapter(adapterDistance);

            mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng point) {
                    // Clearing the markers and polylines in the google map
                    mMap.clear();
                    adapterNumber.clear();
                    adapterLat.clear();
                    adapterLng.clear();
                    adapterDistance.clear();
                    lst = 0;
                    countMarker = 0;
                    // Empty the array list
                    points.clear();
                }
            });
        }
        else{
            wpButton.setImageResource(R.drawable.wp02);
            wpButton.setBackgroundResource(R.drawable.circle);
            LayoutWaypoint.setVisibility(LayoutWaypoint.INVISIBLE);
            sendWPButton.setVisibility(sendWPButton.INVISIBLE);
            mMap.setOnMapClickListener(null);
        }

        isFlipWP = !isFlipWP; // reverse
    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        stopButton.setEnabled(bool);
    }

    public void onCapture(View view) {
        storeImage(getBitmap());
        Toast.makeText(this, "Capture", Toast.LENGTH_SHORT).show();
    }

    public void onClickStart(View view) {
        startButton.setImageResource(R.drawable.ic_connectgreen);
        startButton.setBackgroundResource(R.drawable.circlewhite);
        stopButton.setImageResource(R.drawable.ic_disconnectred);
        stopButton.setBackgroundResource(R.drawable.circlewhite);
        //startButton.setBackground(getDrawable(ic));
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x10C4 || deviceVID == 0x1a86)//3DR 433 Telemetry Vendor IDdeviceVID == 0x10C4 0x1a86 ||
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }
                if (!keep)
                    break;
            }
        }
        starttimeFlight();
    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        startButton.setImageResource(R.drawable.ic_connect);
        startButton.setBackgroundResource(R.drawable.circle);
        stopButton.setImageResource(R.drawable.ic_disconnect);
        stopButton.setBackgroundResource(R.drawable.circle);
        TvMode.setText("Flight Mode");
        TvDevice.setText("Device");
        TBattery.setText("Battery");
        serialPort.close();
        stoptimeFlight();
    }

    public void onDisplayStream(View view) {
        if(isFlipDS){
            BtStream.setImageResource(R.drawable.ic_streamgreen);
            BtStream.setBackgroundResource(R.drawable.circlewhite);
            LayoutStream.setVisibility(LayoutStream.VISIBLE);
            mStream.setVisibility(mStream.VISIBLE);
            etEndpoint.setVisibility(etEndpoint.VISIBLE);
                    //LayoutMonitor.setVisibility(LayoutMonitor.VISIBLE);
            LayoutCh.setVisibility(LayoutCh.GONE);
            }else{
            BtStream.setImageResource(R.drawable.ic_streamwhite);
            BtStream.setBackgroundResource(R.drawable.circle);
            //LayoutMonitor.setVisibility(LayoutMonitor.VISIBLE);
            LayoutCh.setVisibility(LayoutCh.VISIBLE);
            LayoutStream.setVisibility(LayoutStream.GONE);
            //mStream.setVisibility(mStream.GONE);
            //vlcVideoLibrary.stop();
            }
                isFlipDS = !isFlipDS; // reverse


    }

    public void onPlay(View view) {
        if(isFlipPlay && !vlcVideoLibrary.isPlaying()){
            vlcVideoLibrary.play(etEndpoint.getText().toString());
            btnPlay.setImageResource(R.drawable.ic_stop);
        }else{
            vlcVideoLibrary.stop();
            btnPlay.setImageResource(R.drawable.ic_play);
        }
        isFlipPlay = !isFlipPlay; // reverse

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Efalcongcs my = new Efalcongcs();
        mMap = googleMap;
        UiSettings settings = mMap.getUiSettings();
        // mMap.addMarker(new MarkerOptions().position(my.koordinat).icon(BitmapDescriptorFactory.fromResource(R.drawable.efalcon)));
        // Add a marker in Sydney and move the camera
        LatLng Indonesia = new LatLng(-2.021746310975856, 113.36910344660284);
        //mMap.addMarker(new MarkerOptions().position(koordinat).title("Surabaya"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Indonesia, 5));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //settings.setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            mMap.setMyLocationEnabled(true);
            //setPadding(left, top, right, bottom)
            mMap.setPadding(0,580,1,0);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }//*/

    }

    private void dataParsing(final String device,final String roll, final String pitch, final String yaw, final String alt, final String lat, final String lng, final String ch1, final String ch2, final String ch3, final String ch4, final String lvl, final String fm) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                difference = System.currentTimeMillis() - startTime;
                tvTime.setText("Time Delay : "+ difference);
                FDevice(device);
                DataRoll(roll);
                Tpitch.setText("Pitch\n" + pitch);
                DataYaw(yaw);
                DataAlt(alt);
                TrackingDrone(lat,lng);
                Aileron (ch1);
                Elevator(ch2);
                Throttle(ch3);
                Rudder(ch4);
                FMode(fm);
                BatteryLevel(lvl);//*/
                startTime = System.currentTimeMillis();
            }
        });
    }

    private void DataRoll(String dataRoll) {
        Float aroll = Float.valueOf(dataRoll);
        Troll.setText("Roll\n" + dataRoll);
        RotateAnimation raR = new RotateAnimation(
                -DegreeStartRoll,
                aroll,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        raR.setFillAfter(true);
        // set how long the animation for the compass image will take place
        raR.setDuration(1);
        // Start animation of compass image
        HeadingRoll.startAnimation(raR);
        DegreeStartRoll = -aroll;
    }

    private void DataYaw(String dataYaw) {
        Float ayaw = Float.valueOf(dataYaw);
        Tyaw.setText("Yaw\n" + dataYaw);
        RotateAnimation raY = new RotateAnimation(
                -DegreeStartYaw,
                ayaw,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        raY.setFillAfter(true);
        // set how long the animation for the compass image will take place
        raY.setDuration(1);
        // Start animation of compass image
        HeadingYaw.startAnimation(raY);
        DegreeStartYaw  = -ayaw;
    }

    private void DataAlt(String dataAlt) {
        AltitudeBar.setEnabled(false);
        Talt.setText(dataAlt + " M");
        Float aalt = Float.valueOf(dataAlt);
        Integer i = Math.round(aalt);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(AltitudeBar, "progress", i, i);
        progressAnimator.setDuration(1);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }

    private void Aileron(String channel1) {
        tch1 = (TextView) findViewById(R.id.tvCH1);
        tch1.setText("CH1 : " + channel1);
        Float chAileron = Float.valueOf(channel1);
        Integer j = Math.round(chAileron);
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(Aileron, "progress", j, j);
        progressBarAnimator.setDuration(1);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.start();
    }

    private void Elevator(String channel2) {
        tch2 = (TextView) findViewById(R.id.tvCH2);
        tch2.setText("CH2 : " + channel2);
        Float chElevator = Float.valueOf(channel2);
        Integer k = Math.round(chElevator);
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(Elevator, "progress", k, k);
        progressBarAnimator.setDuration(1);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.start();
    }

    private void Throttle(String channel3) {
        tch3 = (TextView) findViewById(R.id.tvCH3);
        tch3.setText("CH3 : " + channel3);
        Float chThrottle = Float.valueOf(channel3);
        Integer l = Math.round(chThrottle);
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(Throttle, "progress", l, l);
        progressBarAnimator.setDuration(1);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.start();
    }

    private void Rudder(String channel4) {
        tch4 = (TextView) findViewById(R.id.tvCH4);
        tch4.setText("CH4 : " + channel4);
        Float chRudder = Float.valueOf(channel4);
        Integer m = Math.round(chRudder);
        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(Rudder, "progress", m, m);
        progressBarAnimator.setDuration(1);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.start();
    }

    private void TrackingDrone(String dataLat, String dataLng) {
        Double locLat = new Double(dataLat);
        Double locLng = new Double(dataLng);
        LatLng koordinat = new LatLng(locLat, locLng);
      //  if(koordinat.latitude > -7.000000 && koordinat.latitude < -5.000000){
            if (currentMarker!=null) {
                currentMarker.remove();
                currentMarker=null;
            }
            if (currentMarker==null) {
                currentMarker = mMap.addMarker(new MarkerOptions().position(koordinat)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.efalcon)));
                Toast.makeText(this, "Lat: " + dataLat + "/Lng: " + dataLng, Toast.LENGTH_SHORT).show();
            }
            for ( int k = 0; k < i;k++) {
                WPconf(k);
            }
            //if(koordinat.latitude > -7.000000 && koordinat.latitude < -5.000000){
            PolylineOptions polylineOptions = new PolylineOptions();
            // Setting the color of the polyline
            polylineOptions.color(Color.BLUE);
            // Setting the width of the polyline
            polylineOptions.width(2);
            //textView.getText((int) wpla);
            // Adding the taped point to the ArrayList
            if (mTracking == 2){
                tracking.add(koordinat);
                countTracking++;
                // Setting points of polyline
                polylineOptions.addAll(tracking);
                polyline = mMap.addPolyline(polylineOptions);
                if(countTracking == 3){
                    tracking.remove(0);
                    countTracking = 2;
                }
            }if(mTracking == 1){
                polyline.remove();
            }
    //    }
    }

    private void BatteryLevel(String level){
        //ImageView battery = (ImageView)findViewById(R.id.battery);
        //battery.setVisibility(battery.INVISIBLE);
        TBattery = (TextView) findViewById(R.id.tvBattery);
        TBattery.setText(level + " V");
        Float lvl = Float.valueOf(level);
        if( lvl < 11.80  ){
            TBattery.setTextColor(Color.RED);
            //Drawable btrempty = getResources().getDrawable(R.drawable.ic_btr5);
            //battery.setImageDrawable(btrempty);
        }else{
            TBattery.setTextColor(Color.DKGRAY);
        }
        // image.setImageDrawable(d);
    }//*/

    private void FDevice(String device) {
        if (device.equals("MW")){
            TvDevice.setText("Multiwii");
        }
        else if (device.equals("EF")){
            TvDevice.setText("Efalcon");
        }

    }//*/

    private void FMode(String mode) {
        if (mode.equals("0")){
            TvMode.setText("Flight Mode : Manual");
        }
        else if (mode.equals("1")){
            TvMode.setText("Flight Mode : Safe-Flight");
        }
        else if (mode.equals("2")){
            TvMode.setText("Flight Mode : Loiter");
        }
        else if(mode.equals("6")){
            TvMode.setText("Quad X");
        }
    }//*/

    private void starttimeFlight(){
        if(!running){
            timeFlight.setBase(SystemClock.elapsedRealtime() - pauseoffset);
            timeFlight.start();
            running = true;
        }
    }

    private void stoptimeFlight(){
        if(running) {
            timeFlight.setBase(SystemClock.elapsedRealtime());
            timeFlight.stop();
            pauseoffset = 0;
            running = false;
        }
    }

    private void WPconf(int k){
        wpla = circle[k].getCenter().latitude;
        wpln = circle[k].getCenter().longitude;
        wpradius = circle[k].getRadius();
        Location.distanceBetween(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude,
                wpla, wpln, distance);
        if (distance[0] < wpradius) {
            inH = true;
        } else {
            inH = false;
        }
        if (inH == true && inbef == false) {
            //if( distance[0] < wpradius  ){
            Toast.makeText(getBaseContext(), "WP Success", Toast.LENGTH_SHORT).show();
            circle[k] = mMap.addCircle(new CircleOptions()
                    .center(circle[k].getCenter())
                    .radius(6)
                    .strokeWidth(1.0f)
                    .strokeColor(Color.YELLOW)
                    .fillColor(0x1500FFFF));
            wpMarkerB = mMap.addMarker(new MarkerOptions().position(circle[k].getCenter())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        inbef = inH;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            Toast.makeText(this, "Error creating media file, check storage permissions: ", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
            Toast.makeText(this, "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
            Toast.makeText(this, "Error accessing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private  File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="PIGEON_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public Bitmap getBitmap() {
        return mStream.getBitmap();
    }

    public void reqPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        //SurfaceTexture surfaceTexture;
        surface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int i, int i2) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void datalog(String fc, String roll, String pitch, String yaw, String lat, String lng, String alt){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="PIGEON_LOG_"+ timeStamp +".txt";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        String datalog = String.valueOf("FC : " + fc + " " + "Roll : " + roll + " " + "Pitch : " + pitch + " " + "Yaw : " + yaw + " " + "Latitude : " +
                lat + " " + "Longitude : " + lng + " " + "Altitude : " + alt + " " +  "Delay : " + difference +"ms"+ "\n");
        Save(mediaFile,datalog);
    }

    public static void Save (File file, String data){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(data);
            //fos.write(data.getBytes());
        }
        catch (IOException e){e.printStackTrace();}
        finally {
            try {
                if(bw!=null)
                    bw.close();
                if(fw!=null)
                    fw.close();
            }catch (IOException e){e.printStackTrace();}
        }
    }//*/

}