/*
 * Copyright (c) 2011-2019 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.example.hereapi.locationservices;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoutingActivity extends FragmentActivity implements AsyncResponse {
    private static final String LOG_TAG = RoutingActivity.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;

    // TextView for displaying the current map scheme
    private TextView textViewResult = null;

    // MapRoute for this activity
    private static MapRoute mapRoute = null;

    // Set this to PositioningManager.getInstance() upon Engine Initialization
    private PositioningManager posManager = null;
    private PositioningManager.OnPositionChangedListener positionListener = null;

    double a = 12.9618;
    double b = 80.2382;
    double c = 13.121;
    double d = 80.225;

    double latitude = 12.9618;
    double longitude = 80.2382;

    Button b1;
    Button b2;
    TextView t1;
    TextView t2;

    String default1 = "Car";
    String default2 = "Fastest";

    String mode[] = {"Car", "Walk"};
    String type[] = {"Fastest", "Shortest"};

    boolean paused = false;
    boolean updated = false;

    String street2 = new String();
    String errorCode2 = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        b1 = findViewById(R.id.pick);
        b2 = findViewById(R.id.drop);
        t1 = findViewById(R.id.source);
        t2 = findViewById(R.id.destination);

        triggerRevGeocodeRequest(a, b, true);
        triggerRevGeocodeRequest(c, d, false);

        Spinner z1 = findViewById(R.id.spinner1);
        Spinner z2 = findViewById(R.id.spinner2);

        ArrayAdapter a1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mode);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        z1.setAdapter(a1);

        ArrayAdapter a2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, type);
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        z2.setAdapter(a2);

        z1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1)
                {
                    default1 = "Pedestrian";
                }
                else
                {
                    default1 = "Car";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        z2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1)
                {
                    default2 = "Shortest";
                }
                else
                {
                    default2 = "Fastest";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater inflater = LayoutInflater.from(RoutingActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.start, null);

                final SupportMapFragment mapFragment1 = getSupportMapFragment2();
                mapFragment1.init(new OnEngineInitListener() {
                        @Override
                        public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                        if (error == OnEngineInitListener.Error.NONE) {
                            // retrieve a reference of the map from the map fragment
                            Map map1 = mapFragment1.getMap();
                            // Set the map center coordinate to the GPS region (no animation)
                            map1.setCenter(new GeoCoordinate(a, b, 0.0),
                                    Map.Animation.NONE);
                            // Set the map zoom level to the average between min and max (no animation)
                            map1.setZoomLevel((map1.getMaxZoomLevel() + map1.getMinZoomLevel()) / 2);

                            MapMarker defaultMarker1 = new MapMarker();
                            defaultMarker1.setCoordinate(new GeoCoordinate(a, b, 0.0));
                            defaultMarker1.setDraggable(true);
                            map1.addMapObject(defaultMarker1);

                            mapFragment1.setMapMarkerDragListener(new MapMarker.OnDragListener() {
                                @Override
                                public void onMarkerDrag(MapMarker mapMarker) {
                                }

                                @Override
                                public void onMarkerDragEnd(MapMarker mapMarker) {
                                    GeoCoordinate c1 = mapMarker.getCoordinate();
                                    a = c1.getLatitude();
                                    b = c1.getLongitude();
                                }

                                @Override
                                public void onMarkerDragStart(MapMarker mapMarker) {
                               }
                            });
                        } else {
                            Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                        }
                    }
                    });

                AlertDialog dialog = new AlertDialog.Builder(RoutingActivity.this)
                        .setTitle("Select pick up location")
                        .setView(yourCustomView)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (mapFragment1 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment1).commit();
                                }
                                triggerRevGeocodeRequest(a, b, true);
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mapFragment1 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment1).commit();
                                }
                                triggerRevGeocodeRequest(a, b, true);
                            }
                        }).create();
                dialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LayoutInflater inflater = LayoutInflater.from(RoutingActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.end, null);

                // Search for the map fragment to finish setup by calling init().
                final SupportMapFragment mapFragment2 = getSupportMapFragment3();
                mapFragment2.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                        if (error == OnEngineInitListener.Error.NONE) {
                            // retrieve a reference of the map from the map fragment
                            Map map2 = mapFragment2.getMap();
                            // Set the map center coordinate to the GPS region (no animation)
                            map2.setCenter(new GeoCoordinate(c, d, 0.0),
                                    Map.Animation.NONE);
                            // Set the map zoom level to the average between min and max (no animation)
                            map2.setZoomLevel((map2.getMaxZoomLevel() + map2.getMinZoomLevel()) / 2);

                            MapMarker defaultMarker2 = new MapMarker();
                            defaultMarker2.setCoordinate(new GeoCoordinate(c, d, 0.0));
                            defaultMarker2.setDraggable(true);
                            map2.addMapObject(defaultMarker2);

                            mapFragment2.setMapMarkerDragListener(new MapMarker.OnDragListener() {
                                @Override
                                public void onMarkerDrag(MapMarker mapMarker) {
                                }

                                @Override
                                public void onMarkerDragEnd(MapMarker mapMarker) {
                                    GeoCoordinate c2 = mapMarker.getCoordinate();
                                    c = c2.getLatitude();
                                    d = c2.getLongitude();
                                }

                                @Override
                                public void onMarkerDragStart(MapMarker mapMarker) {
                                }
                            });
                        } else {
                            Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                        }
                    }
                });

                AlertDialog dialog = new AlertDialog.Builder(RoutingActivity.this)
                        .setTitle("Select drop off location")
                        .setView(yourCustomView)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (mapFragment2 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment2).commit();
                                }
                                triggerRevGeocodeRequest(c, d, false);
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mapFragment2 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment2).commit();
                                }
                                triggerRevGeocodeRequest(c, d, false);
                            }
                        }).create();
                dialog.show();
            }
        });

        paused = true;

        // Define positioning listener
        positionListener = new PositioningManager.OnPositionChangedListener() {

                    @Override
                    public void onPositionUpdated(PositioningManager.LocationMethod method,
                                                  GeoPosition position, boolean isMapMatched) {
                        // set the center only when the app is in the foreground
                        // to reduce CPU consumption
                        if (! paused) {
                            GeoCoordinate geoCoordinate = position.getCoordinate();
                            latitude = geoCoordinate.getLatitude();
                            longitude = geoCoordinate.getLongitude();

                            if(! updated) {
                                map.setCenter(new GeoCoordinate(latitude, longitude, 0.0),
                                        Map.Animation.NONE);

                                a = latitude;
                                b = longitude;
                                c = latitude;
                                d = longitude;

                                triggerRevGeocodeRequest(a, b, true);
                                triggerRevGeocodeRequest(c, d, false);

                                updated = true;
                            }
                        }
                    }

                    @Override
                    public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                     PositioningManager.LocationStatus status) {
                    }
                };

        // Register positioning listener
        PositioningManager.getInstance().addListener(
                new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));
    }

    // Resume positioning listener on wake up
    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (posManager != null) {
            posManager.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }

    // To pause positioning listener
    @Override
    public void onPause() {
        if (posManager != null) {
            posManager.stop();
        }
        super.onPause();
        paused = true;
    }

    // To remove the positioning listener
    @Override
    public void onDestroy() {
        if (posManager != null) {
            // Cleanup
            posManager.removeListener(
                    positionListener);
        }
        map = null;
        super.onDestroy();
    }

    private SupportMapFragment getSupportMapFragment1() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment1);
    }

    private SupportMapFragment getSupportMapFragment2() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment2);
    }

    private SupportMapFragment getSupportMapFragment3() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment3);
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = getSupportMapFragment1();
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                posManager = PositioningManager.getInstance();

                if (posManager != null) {
                    posManager.start(
                            PositioningManager.LocationMethod.GPS_NETWORK);
                }

                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    map.getPositionIndicator().setVisible(true);
                    // Set the map center coordinate to the GPS region (no animation)
                    map.setCenter(new GeoCoordinate(latitude, longitude, 0.0),
                            Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                }
            }
        });

        textViewResult = (TextView) findViewById(R.id.title);
        textViewResult.setText(R.string.textview_routecoordinates_2waypoints);
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_routing, menu);
        return true;
    }

    // Functionality for taps of the "Get Directions" button
    public void getDirections(View view) {
        // 1. clear previous results
        textViewResult.setText("");
        if (map != null && mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }

        // 2. Initialize RouteManager
        RouteManager routeManager = new RouteManager();

        // 3. Select routing options
        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        if (default1.equals("Pedestrian")) {
            routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
        } else {
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        }
        if (default2.equals("Shortest")) {
            routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        }
        else {
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        }
        routePlan.setRouteOptions(routeOptions);

        // 4. Select Waypoints for your routes
        // START: Source
        routePlan.addWaypoint(new GeoCoordinate(a, b));

        // END: Destination
        routePlan.addWaypoint(new GeoCoordinate(c, d));

        // 5. Retrieve Routing information via RouteManagerEventListener
        RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);
        if (error != RouteManager.Error.NONE) {
            Toast.makeText(getApplicationContext(),
                    "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private RouteManager.Listener routeManagerListener = new RouteManager.Listener() {
        public void onCalculateRouteFinished(RouteManager.Error errorCode,
                List<RouteResult> result) {

            if (errorCode == RouteManager.Error.NONE && result.get(0).getRoute() != null) {
                // create a map route object and place it on the map
                mapRoute = new MapRoute(result.get(0).getRoute());
                map.addMapObject(mapRoute);

                // Get the bounding box containing the route and zoom in (no animation)
                GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);

                textViewResult.setText(String.format("Route calculated with %d maneuvers.",
                        result.get(0).getRoute().getManeuvers().size()));
            } else {
                textViewResult.setText(
                        String.format("Route calculation failed: %s", errorCode.toString()));
            }
        }

        public void onProgress(int percentage) {
            textViewResult.setText(String.format("... %d percent done ...", percentage));
        }
    };

    private void triggerRevGeocodeRequest(double x, double y, boolean g) {
        /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
        GeoCoordinate coordinate = new GeoCoordinate(x, y);
        String address = new String();
        ReverseGeocodeListener listener = new ReverseGeocodeListener(g);
        listener.delegate = this;
        ReverseGeocodeRequest request = new ReverseGeocodeRequest(coordinate);
        request.execute(listener);

//        if (request.execute(listener) != ErrorCode.NONE) {
//            // Handle request error
//            address = "ERROR: RevGeocode Request returned error code:" + errorCode2;
//        }
//        else
//        {
//            // Process result data
//            /*
//             * From the start object, we retrieve the address and display to the screen.
//             * Please refer to HERE Android SDK doc for other supported APIs.
//             */
//            address = street2;
//        }
    }

    @Override
    public void processFinish(String output1, String output2, boolean output3) {
        street2 = output1;
        errorCode2 = output2;

        if(output3 == true) {
            t1.setText(street2);
        }
        else {
            t2.setText(street2);
        }
    }
}

// Implementation of ResultListener
class ReverseGeocodeListener implements ResultListener<Address> {
    public AsyncResponse delegate = null;

    String street1 = new String();
    String errorCode1 = new String();
    boolean begin;

    public ReverseGeocodeListener(boolean g) {
        begin = g;
    }

    @Override
    public void onCompleted(Address data, ErrorCode error) {
        errorCode1 = error.toString();
        if (error != ErrorCode.NONE) {
            // Handle error
            street1 = "ERROR: RevGeocode Request returned error code:" + error.toString();
            delegate.processFinish(street1, errorCode1, begin);
        } else {
            // Process result data
            /*
             * From the start object, we retrieve the address and display to the screen.
             * Please refer to HERE Android SDK doc for other supported APIs.
             */
            street1 = data.getText();
            delegate.processFinish(street1, errorCode1, begin);
        }
    }
}