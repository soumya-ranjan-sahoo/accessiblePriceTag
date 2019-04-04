package com.example.estimotetest;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;


import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.example.estimotetest.estimote.NotificationsManager;


import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;



public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private TextToSpeech TTS;
    private BeaconManager beaconManager;
    public String responsePriceTag;
    private String oldText;
    private String deviceID;
    private String deskOwner;
    private static final UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("88CF77CE-BC91-241A-B8EB-4D041F74ACDF");
    private static final BeaconRegion ALL_ESTIMOTE_BEACONS_REGION = new BeaconRegion("rid", ESTIMOTE_PROXIMITY_UUID, null, null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.textViewItemName);
                ImageView imageView = findViewById(R.id.imageView);
                textView.setText("Welcome to Accessible Price Tags");
                imageView.setImageResource(R.drawable.irl);
            }
        });


        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("proximity-for-multiple-bea-4sq", "92097cff0f208f0dd08a8217472686fe");


        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(1, 1);
        beaconManager.setForegroundScanPeriod(100, 1);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d("Function", "service ready" );
                // Beacons ranging.
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);

            }
        });



        final NotificationsManager notificationsManager;
        final NotificationManager notificationManager;
        notificationsManager = new NotificationsManager(this);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        final int notificationId = 1;


        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                   int result = TTS.setLanguage(Locale.ENGLISH);
                   if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                       Log.e("TTS","Language not supported");
                   }

                }
                else{
                    Log.e("TTS","Initialization Failed");
                }
            }
        });






        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("Room")
                .inCustomRange(1)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(final ProximityZoneContext context) {
                         //deviceID =
//                        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
//                            @Override
//                            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
//                                Log.d("Function", "Entered" + beacons);
//                                if (!beacons.isEmpty()) {
//                                    Beacon nearestBeacon = beacons.get(0);
//                                    double distance = com.estimote.coresdk.observation.region.RegionUtils.computeAccuracy(nearestBeacon);
//                                    Log.e("Distance", String.valueOf(distance));
//                                    majorID = String.valueOf(nearestBeacon.getMajor());
//                                    deviceID = String.valueOf(context.getAttachments());
//                                }
//                            }
//                        });
                        deskOwner = context.getAttachments().get("room-owner");
                        Log.d("app", "Welcome to " + deskOwner + "'s desk");



                        TextView textViewItemName =  findViewById(R.id.textViewItemName);



                       // notificationManager.notify(notificationId, helloNotification);
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        oldText = "";
                        deskOwner = null;
                        Log.d("app", "Bye bye, come again!");



                        return null;
                    }
                })
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
            @Override
            public Unit invoke(Set<? extends ProximityZoneContext> contexts) {
                List<String> deskOwners = new ArrayList<>();
                oldText = "";
                for (ProximityZoneContext context : contexts) {
                    deskOwners.add(context.getAttachments().get("room-owner"));
                }
                if(!deskOwners.isEmpty()) {
                    deskOwner = deskOwners.get(0);
                }
                        Log.d("app", "In range of desks: " + deskOwners);
                return null;
                }
                  })
                .build();


        ProximityObserver.Handler observationHandler =
                proximityObserver
                        .startObserving(zone);


        final JSONObject obj = new JSONObject();
        try {
            obj.put("Beacon", "hello");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String URI = "192.168.5.1";



        final Socket mSocket;
        {
            try {
                mSocket = IO.socket("http://192.168.5.1:3000");
                mSocket.connect();
                mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {


                    @Override
                    public void call(Object... args) {
                        boolean connected = mSocket.connected();
                        if(connected){
                            Log.e("Connected to", URI);


                        }
                        mSocket.emit("beacon", obj);

                        //ImageView image = findViewById(R.id.imageView);
                        //image.setImageResource(R.drawable.beetroot);


                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {


                    @Override
                    public void call(Object... args) {


                            Log.e("Disconnected ", URI);

                        mSocket.connect();
                        if(mSocket.connected()) {
                            Log.e("Connected", "to " + URI);
                        }
                        mSocket.emit("disconnect ", obj);



                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                    Log.e("Disconnected", " " + URI);
                    }

                }).on("scan", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject)args[0];
                        try {
                            Log.e("Response while scanning", obj.getString("scan"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("ID","id id "+ deskOwner);
                        mSocket.emit("beacon-id", "\n" +
                                deskOwner );
                    }

                }).on("price", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject)args[0];
                        try {
                            Log.e("Response", obj.getString("price"));
                            responsePriceTag = obj.getString("price");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(oldText != null) {

                            if (!responsePriceTag.contentEquals(oldText)) {
                                speak(responsePriceTag);
                                Notification priceNotification = notificationsManager.buildNotification("Hello", "" + responsePriceTag);
                                notificationManager.notify(notificationId, priceNotification);
                                oldText = responsePriceTag;
                                runOnUiThread(new Runnable(){

                                    @Override
                                    public void run() {
                                        TextView textViewItemName = findViewById(R.id.textViewItemName);
                                        textViewItemName.setText(responsePriceTag);
                                        ImageView imageView = findViewById(R.id.imageView);
                                        if(responsePriceTag.contains("bread"))
                                        {
                                            imageView.setImageResource(0);
                                            imageView.setImageResource(R.drawable.bread);
                                        }
                                        else if(responsePriceTag.contains("musli"))
                                        {
                                            imageView.setImageResource(0);
                                            imageView.setImageResource(R.drawable.musli);
                                        }
                                    }
                                });
                            }

                        }
                        else
                        {
                            speak(responsePriceTag);
                            oldText = responsePriceTag;
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    TextView textViewItemName = findViewById(R.id.textViewItemName);
                                    textViewItemName.setText(responsePriceTag);
                                    ImageView imageView = findViewById(R.id.imageView);
                                    if(responsePriceTag.contains("bread"))
                                    {
                                        imageView.setImageResource(0);
                                        imageView.setImageResource(R.drawable.bread);
                                    }
                                    else if(responsePriceTag.contains("musli"))
                                    {
                                        imageView.setImageResource(0);
                                        imageView.setImageResource(R.drawable.musli);
                                    }
                                }
                            });
                            Notification priceNotification = notificationsManager.buildNotification("Hello", "" + responsePriceTag);
                            notificationManager.notify(notificationId, priceNotification);
                        }


                    }

                });


            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }


    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }


    private void speak(String text) {
        TTS.setSpeechRate(0.75f);
        TTS.setPitch(0.85f);
        TTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        //oldText = null;

    }

    @Override
    protected void onDestroy() {
        if(TTS != null){
            TTS.stop();
            TTS.shutdown();

        }
        super.onDestroy();
    }
}
