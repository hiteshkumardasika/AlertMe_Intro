package com.example.root.alertme;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by root on 2/22/17.
 */
public class MockLocationProvider {
    String providerName;
    Context ctx;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.addTestProvider(providerName, true, false, false, false, true, true, true,
                Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        lm.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(double lat, double lon) {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setElapsedRealtimeNanos(System.nanoTime());
        mockLocation.setAccuracy((float) 0.0);
        lm.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }
}
