package edu.niu.cs.ryandlewis.finalproject;//package edu.niu.cs.ryandlewis.androidfinalproject;

import android.os.AsyncTask;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.Set;

public class GetCapability extends AsyncTask<Void, Void, CapabilityInfo> {
    private static final String CAPABILITY_NAME = "wear_button_click_receiver";
    private CapabilityInfo capabilityInfo;
    //Using a weak reference to avoid memory leaks. Whoo!!
    private WeakReference<MainRemoteActivity> mainRemoteActivity;

    GetCapability(WeakReference<MainRemoteActivity> activity) {
        this.mainRemoteActivity = activity;
    }

    @Override
    protected CapabilityInfo doInBackground(Void... voids) {
        try {
            capabilityInfo = null;
            //Grab any devices that have apps listed with our desired capability
            capabilityInfo = Tasks.await(Wearable.getCapabilityClient(mainRemoteActivity.get().getApplicationContext())
                    .getCapability(CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return capabilityInfo;
    }

    //After our Async task completes, update our recieverNodeId
    @Override
    protected void onPostExecute(CapabilityInfo capabilityInfo) {
        updateTranscriptionCapability(capabilityInfo);
    }

    //Set the recieverNodeId to the id of one of our connected devices
    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        //Grab all Nodes (connected devices) and figure out which is the best
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        mainRemoteActivity.get().receiverNodeId = pickBestNodeId(connectedNodes);
    }

    //Finds the closest device to use
    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }
}
