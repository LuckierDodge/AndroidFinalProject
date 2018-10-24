package edu.niu.cs.ryandlewis.finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;

public class MainRemoteActivity extends WearableActivity {

    public String receiverNodeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_remote);

        // Enables Always-on
        setAmbientEnabled();

        //Find applications that are capable of receiving input from our mobile app
        setupCapabilities();
    }

    //OnClick for the PUSH Button
    public void pushButton (View view) {
        //Make sure there's a capable app running
        if (receiverNodeId != null) {
            //Send a message to the app letting it know the button has been pushed.
            Wearable.getMessageClient(getApplicationContext()).sendMessage(
                    receiverNodeId, "/wear_button_click_receiver",  "pushed".getBytes());
        } else {
            //If not, send a Toast message on the wearable device
            Toast.makeText(getApplicationContext(), "No reciever available", Toast.LENGTH_LONG).show();
        }
    }

    //Find available applications to receive input
    private void setupCapabilities() {
        try {
            //Send an Async task to get capable applications
            GetCapability getCapability = new GetCapability(new WeakReference<>(this));
            getCapability.execute();

            final MainRemoteActivity superThis = this;
            //Register a listener in case the application opens up on the phone after the wearable app
            CapabilityClient.OnCapabilityChangedListener onCapabilityChangedListener = new CapabilityClient.OnCapabilityChangedListener() {
                @Override
                public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
                    GetCapability getCapability = new GetCapability(new WeakReference<>(superThis));
                    getCapability.execute();
                }
            };
            Wearable.getCapabilityClient(getApplicationContext()).addListener(onCapabilityChangedListener, "wear_button_click_receiver");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
