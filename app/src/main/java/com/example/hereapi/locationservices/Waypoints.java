package com.example.hereapi.locationservices;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.AutoSuggest;
import com.here.android.mpa.search.AutoSuggestPlace;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here.android.mpa.search.TextAutoSuggestionRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Waypoints extends FragmentActivity implements AsyncResponse2 {
    private static final String LOG_TAG = Waypoints.class.getSimpleName();

    PopupWindow popupWindow3;
    MapMarker defaultMarker3;

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map3 = null;

    private List<AutoSuggest> suggestions3 = new ArrayList<AutoSuggest>();

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment3 = null;

    double a;
    double b;
    double c;
    double d;
    double latitude;
    double longitude;
    boolean updated;

    double e;
    double f;

    double latwp[];
    double longwp[];

    String default1;
    String default2;

    Button b1;
    Button b2;
    ListView l1;

    Button g3;
    EditText e3;

    List<String> addresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypoints);

        Intent intent2 = getIntent();
        a = intent2.getDoubleExtra("a", 12.9618);
        b = intent2.getDoubleExtra("b", 80.2382);
        c = intent2.getDoubleExtra("c", 13.121);
        d = intent2.getDoubleExtra("d", 80.225);
        latitude = intent2.getDoubleExtra("latitude", 12.9618);
        longitude = intent2.getDoubleExtra("longitude", 80.2382);
        updated = intent2.getBooleanExtra("updated", true);

        latwp = intent2.getDoubleArrayExtra("latwp");
        longwp = intent2.getDoubleArrayExtra("longwp");

        default1 = intent2.getStringExtra("default1");
        default2 = intent2.getStringExtra("default2");

        checkPermissions();

        b1 = findViewById(R.id.add);
        b2 = findViewById(R.id.back);
        l1 = findViewById(R.id.list);

        addresses = new ArrayList<String>();

        if(latwp != null)
        {
            triggerRevGeocodeRequest(latwp[0], longwp[0]);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                e = latitude;
                f = longitude;

                LayoutInflater inflater = LayoutInflater.from(Waypoints.this);
                final View yourCustomView = inflater.inflate(R.layout.middle, null);

                mapFragment3 = getSupportMapFragment4();
                mapFragment3.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                        if (error == OnEngineInitListener.Error.NONE) {
                            // retrieve a reference of the map from the map fragment
                            map3 = mapFragment3.getMap();
                            // Set the map center coordinate to the GPS region (no animation)
                            map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                    Map.Animation.NONE);
                            // Set the map zoom level to the average between min and max (no animation)
                            map3.setZoomLevel((map3.getMaxZoomLevel() + map3.getMinZoomLevel()) / 2);

                            defaultMarker3 = new MapMarker();
                            defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                            defaultMarker3.setDraggable(true);
                            map3.addMapObject(defaultMarker3);

                            mapFragment3.setMapMarkerDragListener(new MapMarker.OnDragListener() {
                                @Override
                                public void onMarkerDrag(MapMarker mapMarker) {
                                }

                                @Override
                                public void onMarkerDragEnd(MapMarker mapMarker) {
                                    GeoCoordinate c3 = mapMarker.getCoordinate();
                                    e = c3.getLatitude();
                                    f = c3.getLongitude();
                                }

                                @Override
                                public void onMarkerDragStart(MapMarker mapMarker) {
                                }
                            });

                            g3 = yourCustomView.findViewById(R.id.reset3);
                            e3 = yourCustomView.findViewById(R.id.search3);

                            g3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    e = latitude;
                                    f = longitude;

                                    map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                            Map.Animation.NONE);
                                    defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                                }
                            });

                            e3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(popupWindow3 != null) {
                                        popupWindow3.dismiss();
                                    }

                                    if((e3.getText()).length() > 0) {
                                        AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                        listener.delegate = Waypoints.this;
                                        TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                        request.execute(listener);
                                    }
                                }
                            });

                            e3.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    // you can call or do what you want with your EditText here
                                    // yourEditText...
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if(popupWindow3 != null) {
                                        popupWindow3.dismiss();
                                    }

                                    if((e3.getText()).length() > 0) {
                                        AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                        listener.delegate = Waypoints.this;
                                        TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                        request.execute(listener);
                                    }
                                }
                            });
                        } else {
                            Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                        }
                    }
                });

                AlertDialog dialog = new AlertDialog.Builder(Waypoints.this)
                        .setTitle("Select pick up location")
                        .setView(yourCustomView)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                List<Double> l3 = new ArrayList<Double>();
                                List<Double> l4 = new ArrayList<Double>();

                                if(latwp != null)
                                {
                                    for(int z = 0; z < latwp.length; z++)
                                    {
                                        l3.add(latwp[z]);
                                        l4.add(longwp[z]);
                                    }
                                }

                                l3.add(e);
                                l4.add(f);

                                if(l3.size() > 0) {
                                    latwp = new double[l3.size()];
                                    longwp = new double[l4.size()];
                                    for(int j = 0; j < l3.size(); j++)
                                    {
                                        latwp[j] = l3.get(j);
                                        longwp[j] = l4.get(j);
                                    }
                                }
                                else {
                                    latwp = null;
                                    longwp = null;
                                }

                                if (mapFragment3 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                }

                                Intent intent6 = new Intent(Waypoints.this, Waypoints.class);
                                intent6.putExtra("a", a);
                                intent6.putExtra("b", b);
                                intent6.putExtra("c", c);
                                intent6.putExtra("d", d);
                                intent6.putExtra("latitude", latitude);
                                intent6.putExtra("longitude", longitude);
                                intent6.putExtra("updated", updated);
                                intent6.putExtra("latwp", latwp);
                                intent6.putExtra("longwp", longwp);
                                intent6.putExtra("default1", default1);
                                intent6.putExtra("default2", default2);
                                startActivity(intent6);
                                finish();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                List<Double> l3 = new ArrayList<Double>();
                                List<Double> l4 = new ArrayList<Double>();

                                if(latwp != null)
                                {
                                    for(int z = 0; z < latwp.length; z++)
                                    {
                                        l3.add(latwp[z]);
                                        l4.add(longwp[z]);
                                    }
                                }

                                l3.add(e);
                                l4.add(f);

                                if(l3.size() > 0) {
                                    latwp = new double[l3.size()];
                                    longwp = new double[l4.size()];
                                    for(int j = 0; j < l3.size(); j++)
                                    {
                                        latwp[j] = l3.get(j);
                                        longwp[j] = l4.get(j);
                                    }
                                }
                                else {
                                    latwp = null;
                                    longwp = null;
                                }

                                if (mapFragment3 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                }

                                if (mapFragment3 != null) {
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                }

                                Intent intent7 = new Intent(Waypoints.this, Waypoints.class);
                                intent7.putExtra("a", a);
                                intent7.putExtra("b", b);
                                intent7.putExtra("c", c);
                                intent7.putExtra("d", d);
                                intent7.putExtra("latitude", latitude);
                                intent7.putExtra("longitude", longitude);
                                intent7.putExtra("updated", updated);
                                intent7.putExtra("latwp", latwp);
                                intent7.putExtra("longwp", longwp);
                                intent7.putExtra("default1", default1);
                                intent7.putExtra("default2", default2);
                                startActivity(intent7);
                                finish();
                            }
                        }).create();
                dialog.show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(Waypoints.this, RoutingActivity.class);
                intent3.putExtra("a", a);
                intent3.putExtra("b", b);
                intent3.putExtra("c", c);
                intent3.putExtra("d", d);
                intent3.putExtra("latitude", latitude);
                intent3.putExtra("longitude", longitude);
                intent3.putExtra("updated", updated);
                intent3.putExtra("latwp", latwp);
                intent3.putExtra("longwp", longwp);
                intent3.putExtra("foremost", 1);
                intent3.putExtra("default1", default1);
                intent3.putExtra("default2", default2);
                startActivity(intent3);
                finish();
            }
        });
    }

    public void onBackPressed()
    {
        Intent intent4 = new Intent(Waypoints.this, RoutingActivity.class);
        intent4.putExtra("a", a);
        intent4.putExtra("b", b);
        intent4.putExtra("c", c);
        intent4.putExtra("d", d);
        intent4.putExtra("latitude", latitude);
        intent4.putExtra("longitude", longitude);
        intent4.putExtra("updated", updated);
        intent4.putExtra("latwp", latwp);
        intent4.putExtra("longwp", longwp);
        intent4.putExtra("foremost", 1);
        intent4.putExtra("default1", default1);
        intent4.putExtra("default2", default2);
        startActivity(intent4);
        finish();
    }

    private SupportMapFragment getSupportMapFragment4() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment4);
    }

    // Search for the map fragment to finish setup by calling init().

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
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_routing, menu);
        return true;
    }

    private void triggerRevGeocodeRequest(double x, double y) {
        /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
        GeoCoordinate coordinate = new GeoCoordinate(x, y);
        String address = new String();
        ReverseGeocodeListener2 listener = new ReverseGeocodeListener2();
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
    public void processFinish1(String output1, String output2) {
        addresses.add(output1);

        if(addresses.size() == latwp.length) {
            customadapter cusadapter = new customadapter(Waypoints.this, R.layout.listelement, addresses);
            l1.setAdapter(cusadapter);
        }
        else {
            triggerRevGeocodeRequest(latwp[addresses.size()], longwp[addresses.size()]);
        }
    }

    public class customadapter extends ArrayAdapter<String> {
        Context con;
        int _resource;
        List<String> lival;

        public customadapter(Context context, int resource, List<String> li) {
            super(context, resource, li);
            // TODO Auto-generated constructor stub
            con = context;
            _resource = resource;
            lival = li;
        }

        @Override
        public View getView(final int position, View v, ViewGroup vg) {
            View vi = null;
            LayoutInflater linflate = (LayoutInflater) (Waypoints.this).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = linflate.inflate(_resource, null);

            TextView t1 = (TextView) vi.findViewById(R.id.address);
            Button b3 = (Button) vi.findViewById(R.id.edit);
            Button b4 = (Button) vi.findViewById(R.id.insert);
            Button b5 = (Button) vi.findViewById(R.id.delete);

            final String data = lival.get(position);
            t1.setText(data);

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // TODO Auto-generated method stub
                    e = latwp[position];
                    f = longwp[position];

                    LayoutInflater inflater = LayoutInflater.from(Waypoints.this);
                    final View yourCustomView = inflater.inflate(R.layout.middle, null);

                    mapFragment3 = getSupportMapFragment4();
                    mapFragment3.init(new OnEngineInitListener() {
                        @Override
                        public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                            if (error == OnEngineInitListener.Error.NONE) {
                                // retrieve a reference of the map from the map fragment
                                map3 = mapFragment3.getMap();
                                // Set the map center coordinate to the GPS region (no animation)
                                map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                        Map.Animation.NONE);
                                // Set the map zoom level to the average between min and max (no animation)
                                map3.setZoomLevel((map3.getMaxZoomLevel() + map3.getMinZoomLevel()) / 2);

                                defaultMarker3 = new MapMarker();
                                defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                                defaultMarker3.setDraggable(true);
                                map3.addMapObject(defaultMarker3);

                                mapFragment3.setMapMarkerDragListener(new MapMarker.OnDragListener() {
                                    @Override
                                    public void onMarkerDrag(MapMarker mapMarker) {
                                    }

                                    @Override
                                    public void onMarkerDragEnd(MapMarker mapMarker) {
                                        GeoCoordinate c3 = mapMarker.getCoordinate();
                                        e = c3.getLatitude();
                                        f = c3.getLongitude();
                                    }

                                    @Override
                                    public void onMarkerDragStart(MapMarker mapMarker) {
                                    }
                                });

                                g3 = yourCustomView.findViewById(R.id.reset3);
                                e3 = yourCustomView.findViewById(R.id.search3);

                                g3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        e = latitude;
                                        f = longitude;

                                        map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                                Map.Animation.NONE);
                                        defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                                    }
                                });

                                e3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(popupWindow3 != null) {
                                            popupWindow3.dismiss();
                                        }

                                        if((e3.getText()).length() > 0) {
                                            AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                            listener.delegate = Waypoints.this;
                                            TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                            request.execute(listener);
                                        }
                                    }
                                });

                                e3.addTextChangedListener(new TextWatcher() {
                                    public void afterTextChanged(Editable s) {
                                        // you can call or do what you want with your EditText here
                                        // yourEditText...
                                    }

                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if(popupWindow3 != null) {
                                            popupWindow3.dismiss();
                                        }

                                        if((e3.getText()).length() > 0) {
                                            AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                            listener.delegate = Waypoints.this;
                                            TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                            request.execute(listener);
                                        }
                                    }
                                });
                            } else {
                                Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                            }
                        }
                    });

                    AlertDialog dialog = new AlertDialog.Builder(Waypoints.this)
                            .setTitle("Add new waypoint")
                            .setView(yourCustomView)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    List<Double> l3 = new ArrayList<Double>();
                                    List<Double> l4 = new ArrayList<Double>();

                                    if(latwp != null)
                                    {
                                        for(int z = 0; z < latwp.length; z++)
                                        {
                                            l3.add(latwp[z]);
                                            l4.add(longwp[z]);
                                        }
                                    }

                                    l3.set(position, e);
                                    l4.set(position, f);

                                    if(l3.size() > 0) {
                                        latwp = new double[l3.size()];
                                        longwp = new double[l4.size()];
                                        for(int j = 0; j < l3.size(); j++)
                                        {
                                            latwp[j] = l3.get(j);
                                            longwp[j] = l4.get(j);
                                        }
                                    }
                                    else {
                                        latwp = null;
                                        longwp = null;
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    Intent intent8 = new Intent(Waypoints.this, Waypoints.class);
                                    intent8.putExtra("a", a);
                                    intent8.putExtra("b", b);
                                    intent8.putExtra("c", c);
                                    intent8.putExtra("d", d);
                                    intent8.putExtra("latitude", latitude);
                                    intent8.putExtra("longitude", longitude);
                                    intent8.putExtra("updated", updated);
                                    intent8.putExtra("latwp", latwp);
                                    intent8.putExtra("longwp", longwp);
                                    intent8.putExtra("default1", default1);
                                    intent8.putExtra("default2", default2);
                                    startActivity(intent8);
                                    finish();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    List<Double> l3 = new ArrayList<Double>();
                                    List<Double> l4 = new ArrayList<Double>();

                                    if(latwp != null)
                                    {
                                        for(int z = 0; z < latwp.length; z++)
                                        {
                                            l3.add(latwp[z]);
                                            l4.add(longwp[z]);
                                        }
                                    }

                                    l3.set(position, e);
                                    l4.set(position, f);

                                    if(l3.size() > 0) {
                                        latwp = new double[l3.size()];
                                        longwp = new double[l4.size()];
                                        for(int j = 0; j < l3.size(); j++)
                                        {
                                            latwp[j] = l3.get(j);
                                            longwp[j] = l4.get(j);
                                        }
                                    }
                                    else {
                                        latwp = null;
                                        longwp = null;
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    Intent intent10 = new Intent(Waypoints.this, Waypoints.class);
                                    intent10.putExtra("a", a);
                                    intent10.putExtra("b", b);
                                    intent10.putExtra("c", c);
                                    intent10.putExtra("d", d);
                                    intent10.putExtra("latitude", latitude);
                                    intent10.putExtra("longitude", longitude);
                                    intent10.putExtra("updated", updated);
                                    intent10.putExtra("latwp", latwp);
                                    intent10.putExtra("longwp", longwp);
                                    intent10.putExtra("default1", default1);
                                    intent10.putExtra("default2", default2);
                                    startActivity(intent10);
                                    finish();
                                }
                            }).create();
                    dialog.show();
                }
            });

            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // TODO Auto-generated method stub
                    e = latitude;
                    f = longitude;

                    LayoutInflater inflater = LayoutInflater.from(Waypoints.this);
                    final View yourCustomView = inflater.inflate(R.layout.middle, null);

                    mapFragment3 = getSupportMapFragment4();
                    mapFragment3.init(new OnEngineInitListener() {
                        @Override
                        public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                            if (error == OnEngineInitListener.Error.NONE) {
                                // retrieve a reference of the map from the map fragment
                                map3 = mapFragment3.getMap();
                                // Set the map center coordinate to the GPS region (no animation)
                                map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                        Map.Animation.NONE);
                                // Set the map zoom level to the average between min and max (no animation)
                                map3.setZoomLevel((map3.getMaxZoomLevel() + map3.getMinZoomLevel()) / 2);

                                defaultMarker3 = new MapMarker();
                                defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                                defaultMarker3.setDraggable(true);
                                map3.addMapObject(defaultMarker3);

                                mapFragment3.setMapMarkerDragListener(new MapMarker.OnDragListener() {
                                    @Override
                                    public void onMarkerDrag(MapMarker mapMarker) {
                                    }

                                    @Override
                                    public void onMarkerDragEnd(MapMarker mapMarker) {
                                        GeoCoordinate c3 = mapMarker.getCoordinate();
                                        e = c3.getLatitude();
                                        f = c3.getLongitude();
                                    }

                                    @Override
                                    public void onMarkerDragStart(MapMarker mapMarker) {
                                    }
                                });

                                g3 = yourCustomView.findViewById(R.id.reset3);
                                e3 = yourCustomView.findViewById(R.id.search3);

                                g3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        e = latitude;
                                        f = longitude;

                                        map3.setCenter(new GeoCoordinate(e, f, 0.0),
                                                Map.Animation.NONE);
                                        defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                                    }
                                });

                                e3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(popupWindow3 != null) {
                                            popupWindow3.dismiss();
                                        }

                                        if((e3.getText()).length() > 0) {
                                            AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                            listener.delegate = Waypoints.this;
                                            TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                            request.execute(listener);
                                        }
                                    }
                                });

                                e3.addTextChangedListener(new TextWatcher() {
                                    public void afterTextChanged(Editable s) {
                                        // you can call or do what you want with your EditText here
                                        // yourEditText...
                                    }

                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if(popupWindow3 != null) {
                                            popupWindow3.dismiss();
                                        }

                                        if((e3.getText()).length() > 0) {
                                            AutoSuggestionQueryListener3 listener = new AutoSuggestionQueryListener3();
                                            listener.delegate = Waypoints.this;
                                            TextAutoSuggestionRequest request = new TextAutoSuggestionRequest((e3.getText()).toString()).setSearchCenter(map3.getCenter());
                                            request.execute(listener);
                                        }
                                    }
                                });
                            } else {
                                Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                            }
                        }
                    });

                    AlertDialog dialog = new AlertDialog.Builder(Waypoints.this)
                            .setTitle("Add new waypoint")
                            .setView(yourCustomView)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    List<Double> l3 = new ArrayList<Double>();
                                    List<Double> l4 = new ArrayList<Double>();

                                    if(latwp != null)
                                    {
                                        for(int z = 0; z < latwp.length; z++)
                                        {
                                            l3.add(latwp[z]);
                                            l4.add(longwp[z]);
                                        }
                                    }

                                    l3.add(position, e);
                                    l4.add(position, f);

                                    if(l3.size() > 0) {
                                        latwp = new double[l3.size()];
                                        longwp = new double[l4.size()];
                                        for(int j = 0; j < l3.size(); j++)
                                        {
                                            latwp[j] = l3.get(j);
                                            longwp[j] = l4.get(j);
                                        }
                                    }
                                    else {
                                        latwp = null;
                                        longwp = null;
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    Intent intent11 = new Intent(Waypoints.this, Waypoints.class);
                                    intent11.putExtra("a", a);
                                    intent11.putExtra("b", b);
                                    intent11.putExtra("c", c);
                                    intent11.putExtra("d", d);
                                    intent11.putExtra("latitude", latitude);
                                    intent11.putExtra("longitude", longitude);
                                    intent11.putExtra("updated", updated);
                                    intent11.putExtra("latwp", latwp);
                                    intent11.putExtra("longwp", longwp);
                                    intent11.putExtra("default1", default1);
                                    intent11.putExtra("default2", default2);
                                    startActivity(intent11);
                                    finish();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    List<Double> l3 = new ArrayList<Double>();
                                    List<Double> l4 = new ArrayList<Double>();

                                    if(latwp != null)
                                    {
                                        for(int z = 0; z < latwp.length; z++)
                                        {
                                            l3.add(latwp[z]);
                                            l4.add(longwp[z]);
                                        }
                                    }

                                    l3.add(position, e);
                                    l4.add(position, f);

                                    if(l3.size() > 0) {
                                        latwp = new double[l3.size()];
                                        longwp = new double[l4.size()];
                                        for(int j = 0; j < l3.size(); j++)
                                        {
                                            latwp[j] = l3.get(j);
                                            longwp[j] = l4.get(j);
                                        }
                                    }
                                    else {
                                        latwp = null;
                                        longwp = null;
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    if (mapFragment3 != null) {
                                        getSupportFragmentManager().beginTransaction().remove(mapFragment3).commit();
                                    }

                                    Intent intent12 = new Intent(Waypoints.this, Waypoints.class);
                                    intent12.putExtra("a", a);
                                    intent12.putExtra("b", b);
                                    intent12.putExtra("c", c);
                                    intent12.putExtra("d", d);
                                    intent12.putExtra("latitude", latitude);
                                    intent12.putExtra("longitude", longitude);
                                    intent12.putExtra("updated", updated);
                                    intent12.putExtra("latwp", latwp);
                                    intent12.putExtra("longwp", longwp);
                                    intent12.putExtra("default1", default1);
                                    intent12.putExtra("default2", default2);
                                    startActivity(intent12);
                                    finish();
                                }
                            }).create();
                    dialog.show();
                }
            });

            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    List<Double> l1 = new ArrayList<Double>();
                    List<Double> l2 = new ArrayList<Double>();

                    if(latwp != null)
                    {
                        for(int x = 0; x < latwp.length; x++)
                        {
                            l1.add(latwp[x]);
                            l2.add(longwp[x]);
                        }
                    }

                    l1.remove(position);
                    l2.remove(position);

                    if(l1.size() > 0) {
                        latwp = new double[l1.size()];
                        longwp = new double[l2.size()];
                        for (int i = 0; i < l1.size(); i++) {
                            latwp[i] = l1.get(i);
                            longwp[i] = l2.get(i);
                        }
                    }
                    else {
                        latwp = null;
                        longwp = null;
                    }

                    Intent intent9 = new Intent(Waypoints.this, Waypoints.class);
                    intent9.putExtra("a", a);
                    intent9.putExtra("b", b);
                    intent9.putExtra("c", c);
                    intent9.putExtra("d", d);
                    intent9.putExtra("latitude", latitude);
                    intent9.putExtra("longitude", longitude);
                    intent9.putExtra("updated", updated);
                    intent9.putExtra("latwp", latwp);
                    intent9.putExtra("longwp", longwp);
                    intent9.putExtra("default1", default1);
                    intent9.putExtra("default2", default2);
                    startActivity(intent9);
                    finish();
                }
            });
            return vi;
        }
    }

    public PopupWindow popupWindow3() {
        final PopupWindow popupWindow3 = new PopupWindow(this);
        ListView listView = new ListView(this);
        List<String> suggestlist = new ArrayList<String>();
        final List<Double> latitudelist = new ArrayList<Double>();
        final List<Double> longitudelist = new ArrayList<Double>();
        for(AutoSuggest item : suggestions3) {
            if(suggestlist.size() < 5 && item instanceof AutoSuggestPlace) {
                AutoSuggestPlace itemPlace = (AutoSuggestPlace) item;
                suggestlist.add(item.getTitle());
                latitudelist.add((itemPlace.getPosition()).getLatitude());
                longitudelist.add((itemPlace.getPosition()).getLongitude());
            }
        }
        listView.setAdapter(myAdapter(suggestlist));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                e = latitudelist.get(arg2);
                f = longitudelist.get(arg2);
                defaultMarker3.setCoordinate(new GeoCoordinate(e, f, 0.0));
                map3.setCenter(new GeoCoordinate(e, f, 0.0),
                        Map.Animation.NONE);
                if(popupWindow3 != null) {
                    popupWindow3.dismiss();
                }
            }
        });
        popupWindow3.setContentView(listView);
        return popupWindow3;
    }

    private ArrayAdapter<String> myAdapter(List<String> suggestarray) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestarray);
        return adapter;
    }

    @Override
    public void processFinish2(List<AutoSuggest> output1, String output2) {
        if(popupWindow3 != null) {
            popupWindow3.dismiss();
        }

        suggestions3 = output1;
        popupWindow3 = popupWindow3();
        popupWindow3.showAsDropDown(e3, -5, 0);
    }
}

class AutoSuggestionQueryListener3 implements ResultListener<List<AutoSuggest>> {
    public AsyncResponse2 delegate = null;

    private List<AutoSuggest> data;
    private ErrorCode error;

    @Override
    public void onCompleted(List<AutoSuggest> data, ErrorCode error) {
        this.data = data;
        this.error = error;
        if (error != ErrorCode.NONE) {
            // Handle error
            delegate.processFinish2(data, error.toString());
        } else {
            // Process result data
            /*
             * From the start object, we retrieve the address and display to the screen.
             * Please refer to HERE Android SDK doc for other supported APIs.
             */
            delegate.processFinish2(data, error.toString());
        }
    }
}

// Implementation of ResultListener
class ReverseGeocodeListener2 implements ResultListener<Address> {
    public AsyncResponse2 delegate = null;

    String street1 = new String();
    String errorCode1 = new String();

    @Override
    public void onCompleted(Address data, ErrorCode error) {
        errorCode1 = error.toString();
        if (error != ErrorCode.NONE) {
            // Handle error
            street1 = "ERROR: RevGeocode Request returned error code:" + error.toString();
            delegate.processFinish1(street1, errorCode1);
        } else {
            // Process result data
            /*
             * From the start object, we retrieve the address and display to the screen.
             * Please refer to HERE Android SDK doc for other supported APIs.
             */
            street1 = data.getText();
            delegate.processFinish1(street1, errorCode1);
        }
    }
}