package com.example.root.alertme;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 2/23/17.
 */
public class LocationChangeResult implements RoutingListener, ResultCallback<Status> {
    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {
        HashMap<String, Integer> distanceValues = new HashMap<>();
        for (int k = 0; k < route.size(); k++) {
            distanceValues.put(route.get(k).getDistanceText(), route.get(k).getDistanceValue());
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
