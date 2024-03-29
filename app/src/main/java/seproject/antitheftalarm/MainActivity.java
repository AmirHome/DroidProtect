package seproject.antitheftalarm;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {
    Switch motionSwitch, proximitySwitch, chargerSwitch;
    CountDownTimer cdt;
    private SensorManager sensorMan;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    AlertDialog alertDialog;
    private static final int SENSOR_SENSITIVITY = 4;
//    PowerManager.WakeLock fullWakeLock,partialWakeLock;



//    SharedPreferences sharedpreferences;


    int mSwitchSet,pSwitchSet = 0;
    int chargerFlag, chargerFlag1, chargerFlag2 = 0;


    @Override
    public void onResume() {
        super.onResume();
//        if(fullWakeLock.isHeld()){
//            fullWakeLock.release();
//        }
//        if(partialWakeLock.isHeld()){
//            partialWakeLock.release();
//        }
//        sensorMan.unregisterListener(this);
//        mSensorManager.unregisterListener(this);
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
                sensorMan.unregisterListener(this);
        mSensorManager.unregisterListener(this);
//                sensorMan.registerListener(this, accelerometer,
//                SensorManager.SENSOR_DELAY_UI);
//        mSensorManager.registerListener(this, mSensor,
//                SensorManager.SENSOR_DELAY_NORMAL);
//        partialWakeLock.acquire();
//        sensorMan.unregisterListener(this);
//        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        alertDialog = new AlertDialog.Builder(this).create();
        chargerSwitch = (Switch) findViewById(R.id.sCharger);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                    chargerFlag = 1;
                } else if (plugged == 0) {
                    chargerFlag1 = 1;
                    chargerFlag = 0;
                    func();

                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        chargerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {

                    if (chargerFlag != 1) {
                        Toast.makeText(MainActivity.this, "Connect To Charger", Toast.LENGTH_SHORT).show();
                        chargerSwitch.setChecked(false);
                    } else {
                        Toast.makeText(MainActivity.this, "Charger Protection Mode On", Toast.LENGTH_SHORT).show();
                        chargerFlag2 = 1;
                        func();
                    }


                } else {
                    chargerFlag2 = 0;
                }

            }
        });

        //alertDialog.show();

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        motionSwitch = (Switch) findViewById(R.id.sMotion);
        motionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    alertDialog.setTitle("Will Be Activated In 10 Seconds");
                    alertDialog.setMessage("00:10");
                    //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();


                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            mSwitchSet = 1;
                            alertDialog.hide();
                            Toast.makeText(MainActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);


                } else {
                    Toast.makeText(MainActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    mSwitchSet = 0;
                }

            }
        });
        proximitySwitch = (Switch) findViewById(R.id.sProximity);
        proximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    alertDialog.setTitle("Keep Phone In Your Pocket");
                    alertDialog.setMessage("00:10");
                    //Toast.makeText(MainActivity.this, "Motion Switch On", Toast.LENGTH_SHORT).show();


                    cdt = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            //info.setVisibility(View.GONE);
                            pSwitchSet = 1;
                            alertDialog.hide();
//                            Toast.makeText(MainActivity.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();

                        }
                    }.start();
                    alertDialog.show();
                    alertDialog.setCancelable(false);


                } else {
                    Toast.makeText(MainActivity.this, "Motion Switch Off", Toast.LENGTH_SHORT).show();
                    pSwitchSet = 0;
                }

            }
        });

    }
//    protected void createWakeLocks(){
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
//        partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Loneworker - PARTIAL WAKE LOCK");
//    }
//    public void wakeDevice() {
//        fullWakeLock.acquire();
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
//        keyguardLock.disableKeyguard();
//    }







    public void func() {
//        Toast.makeText(MainActivity.this, "ChargerFlag"+chargerFlag+"Chargerflag1:"+chargerFlag1, Toast.LENGTH_SHORT).show();
        if (chargerFlag == 0 && chargerFlag1 == 1 && chargerFlag2 == 1) {
            startActivity(new Intent(MainActivity.this, EnterPin.class));
            chargerFlag2 = 0;
            finish();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify proximity parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, ResetPin.class));


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 0.5) {
                //Toast.makeText(MainActivity.this, "Sensor Run Hua Bc", Toast.LENGTH_SHORT).show();
                //MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren);
                //mPlayer.start();
                if (mSwitchSet == 1) {
//                    wakeDevice();
                    startActivity(new Intent(MainActivity.this, EnterPin.class));
                    finish();
                }

            }
        }
        else if (event.sensor.getType()== Sensor.TYPE_PROXIMITY){
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                    //near
//                    Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
                } else if (pSwitchSet==1) {
                    startActivity(new Intent(MainActivity.this, EnterPin.class));
                    finish();
                    //far
//                    Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();

                }
            }
        }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

