package org.trac.app;

import android.Manifest;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.trac.app.TracGrpc.TracBlockingStub;
import org.trac.app.TracGrpc.TracStub;
import java.lang.ref.WeakReference;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Trac extends AppCompatActivity
        implements OnMapReadyCallback 
{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private ManagedChannel channel;
    public static double latitude;
    public static double longitude;
    public static double altitude;
    public static double bearing;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager;
        Location location;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            bearing = location.getBearing();
        }
        }
        channel = ManagedChannelBuilder.forAddress("192.168.0.15", 9090).usePlaintext().build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.post_current:
                new GrpcTask1(new PostRunnable(), channel, this).execute();
                return true;
            case R.id.get_last:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        enableMyLocation(mMap); // Enable location tracking.
    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

  private static class GrpcTask1 extends AsyncTask<Void, Void, String> {
    private final GrpcRunnable1 grpcRunnable;
    private final ManagedChannel channel;
    private final WeakReference<Trac> activityReference;

    GrpcTask1(GrpcRunnable1 grpcRunnable, ManagedChannel channel, Trac activity) {
      this.grpcRunnable = grpcRunnable;
      this.channel = channel;
      this.activityReference = new WeakReference<Trac>(activity);
    }

    @Override
    protected String doInBackground(Void... nothing) {
      try {
        String logs =
            grpcRunnable.run(
                TracGrpc.newBlockingStub(channel), TracGrpc.newStub(channel));
        return "Success!\n" + logs;
      } catch (Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return "Failed... :\n" + sw;
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Trac activity = activityReference.get();
      if (activity == null) {
        return;
      }
      //activity.setResultText(result);
      //activity.enableButtons();
    }
  }

  private interface GrpcRunnable1 {
    /** Perform a grpcRunnable and return all the logs. */
    String run(TracBlockingStub blockingStub, TracStub asyncStub) throws Exception;
  }

  private static class GetLastRunnable implements GrpcRunnable1 {
    @Override
    public String run(TracBlockingStub blockingStub, TracStub asyncStub)
        throws Exception {
      return getLast(blockingStub);
    }
    private String getLast(TracBlockingStub blockingStub)
        throws StatusRuntimeException {
      StringBuffer logs = new StringBuffer();
      //appendLogs(logs, "*** GetLast: ");

      CoordinateRequest request = CoordinateRequest.newBuilder().setUser(1).setId(1).build();

      Coordinate coordinate;
      coordinate = blockingStub.getLast(request);
      /*appendLogs(
        logs,
        "Last coordinate at \"{0}\" at {1}, {2}",
        coordinate.getAltitude(),
        coordinate.getPoint().getLatitude(),
        coordinate.getPoint().getLongitude());*/
      return logs.toString();
    }
  }

  private static class PostRunnable implements GrpcRunnable1 {
    @Override
    public String run(TracBlockingStub blockingStub, TracStub asyncStub)
        throws Exception {
      return postLast(blockingStub);
    }
    private String postLast(TracBlockingStub blockingStub)
        throws StatusRuntimeException {
      StringBuffer logs = new StringBuffer();
      //appendLogs(logs, "*** Post: ");

      Point point = Point.newBuilder().setLatitude((int)latitude).setLongitude((int)longitude).build();
      Coordinate coordinate = Coordinate.newBuilder().setAltitude((float)altitude).setPoint(point).build();
      WrappedCoordinate request = WrappedCoordinate.newBuilder().setUser(1).setCoord(coordinate).build();

      com.google.protobuf.Empty empty;
      empty = blockingStub.post(request);
      /*appendLogs(
        logs,
        "Posted \"{0}\" at {1}, {2}",
        coordinate.getAltitude(),
        coordinate.getPoint().getLatitude(),
        coordinate.getPoint().getLongitude());
    */
      return logs.toString();
    }
  }
}
