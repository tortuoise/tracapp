package org.trac.app;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.app.IntentService;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.util.Log;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TracService extends IntentService {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static Location location;
    private ManagedChannel channel;
    private String tracname;
    public TracService() {
        super("TracService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        channel = ManagedChannelBuilder.forAddress("35.246.126.109", 9090).usePlaintext().build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                TracService.location = location;    
                Pointf point = Pointf.newBuilder().setLatitude((float)location.getLatitude()).setLongitude((float)location.getLongitude()).build();
                Coordinate coordinate = Coordinate.newBuilder().setAltitude((float)location.getAltitude()).setPoint(point).build();
                WrappedCoordinate request = WrappedCoordinate.newBuilder().setUser(1).setCoord(coordinate).setTrack(tracname).build();
                com.google.protobuf.Empty empty;
                empty = TracGrpc.newBlockingStub(channel).post(request);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, (float)1.0, locationListener);
        Log.v(Trac.TAG,"IntentService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        Log.v(Trac.TAG,"IntentService destroyed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.v(Trac.TAG,"onHandleIntent");
            tracname = (String)intent.getExtras().getCharSequence("tracname");
            //String tracfreq = (Integer)intent.getExtras().getInteger("tracname");
            //String trac = (String)intent.getExtras().getCharSequence("tracname");
            for(int x = 0; x < 2; x++) {
                /*Pointf point = Pointf.newBuilder().setLatitude((float)location.getLatitude()).setLongitude((float)location.getLongitude()).build();
                Coordinate coordinate = Coordinate.newBuilder().setAltitude((float)location.getAltitude()).setPoint(point).build();
                WrappedCoordinate request = WrappedCoordinate.newBuilder().setUser(1).setCoord(coordinate).build();
                com.google.protobuf.Empty empty;
                empty = TracGrpc.newBlockingStub(channel).post(request);
                */
                Thread.sleep(60000);
            }
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Log.v(Trac.TAG,"onHandleIntent interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
      
