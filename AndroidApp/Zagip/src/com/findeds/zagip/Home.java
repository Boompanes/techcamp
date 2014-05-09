package com.findeds.zagip;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.findeds.zagip.location.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Emman on 3/13/14.
 */
public class Home extends Fragment implements View.OnClickListener, LocationListener, GoogleMap.OnMyLocationButtonClickListener, RoutingListener {

    ///////////////////MAPS/////////////////////
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private static FragmentManager fm;
    private static ConnectivityManager connectivityManager;
    //AUTO COMPLETE DESTINATION
    // Obtain browser autocomplete_key from https://code.google.com/apis/console
    AutoCompleteTextView et_destination;
    PlacesTask placesTask;
    PlacesDetailsTask placesDetailsTask;
    ProgressDialog progressBar;
    private View rootView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location myLocation = null;
    private int autocomplete_delay = 3; //TODO
    private Runnable r;
    private EditText et_origin;
    private Geo geo;
    private TextView tv_distance;
    private int retry_count = 0;
    private Button plan_travel;
    private boolean alreadySet;
    private boolean location_acquired;
    private HashMap<String, String> myDestination = null;
    private ImageView iv_destination;
    private ImageView icon_exchange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.home, container, false);
            init();
        }
        return rootView;
    }

    private void init() {
        Button create_trip = (Button) rootView.findViewById(R.id.plan_travel);
        create_trip.setOnClickListener(this);

        tv_distance = (TextView) rootView.findViewById(R.id.tv_distance);
        et_origin = (EditText) rootView.findViewById(R.id.et_origin);
        icon_exchange = (ImageView) rootView.findViewById(R.id.icon_exchange);
        icon_exchange.setOnClickListener(this);

        et_destination = (AutoCompleteTextView) rootView.findViewById(R.id.et_destination);
        et_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination_focused();
            }
        });
        et_destination.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                destination_focused();
                return false;
            }
        });
        plan_travel = (Button) rootView.findViewById(R.id.plan_travel);
        iv_destination = (ImageView) rootView.findViewById(R.id.iv_destination);

        scanNetwork();

        createMap();
        initialize_autocomplete();
    }

    private void destination_focused() {
        myDestination = null;
        et_destination.setText("");
        et_destination.setTextColor(Color.DKGRAY);
        et_destination.setCursorVisible(true);
        et_destination.setFocusableInTouchMode(true);
        if(routing != null && !routing.isCancelled())
            routing.cancel(true);
        googleMap.clear();
        checkIfPlanTravelOK();
    }

    private boolean travel_ready;
    private boolean checkIfPlanTravelOK() {
        if (myLocation != null && myDestination != null && last_distance != null) {
            plan_travel.setBackgroundResource(R.drawable.button_green_selector);
            iv_destination.setImageResource(R.drawable.ic_action_place_red);
            travel_ready = true;
            hide_keyboard();
            return true;
        } else {
            plan_travel.setBackgroundResource(R.drawable.button_light_selector);
            iv_destination.setImageResource(R.drawable.ic_action_place);
            travel_ready = false;
            if (et_destination.isEnabled())
                tv_distance.setText("");
            return false;
        }
    }

    private boolean scanNetwork() {
        if(closed) return false;
        if (retry_count > Config.retry_after_failed_getlocation_address) {
            et_origin.setHint("(Unable to get response from server)");
        }
//        et_destination.setEnabled(false);

        if (isNetworkAvailable()) {
            if(travel_ready) return true; //do not check onResume if location already set

            alreadySet = true;
            plan_travel.setBackgroundResource(R.drawable.button_disabled_selector);
            plan_travel.setText(getString(R.string.eds_plan_travel));
            tv_distance.setText(""); //Distance: 0.00 km
            tv_distance.setTextColor(getResources().getColor(R.color.gray_default));
//            et_destination.setHint(getString(R.string.eds_enter_destination));
            et_origin.setEnabled(true);
            isLocationServiceEnabled();
            return true;
        } else { // if (!alreadySet)
            plan_travel.setBackgroundResource(R.drawable.button_light_selector);
            plan_travel.setText(getString(R.string.eds_rescan));
            tv_distance.setText(getString(R.string.eds_no_internet));
            tv_distance.setTextColor(Color.RED);
//            et_destination.setHint("");
            et_origin.setEnabled(false);
//            et_destination.setEnabled(false);
        }

        if (!location_acquired) {
            plan_travel.setText(getString(R.string.eds_rescan));
            plan_travel.setBackgroundResource(R.drawable.button_light_selector);
        }
        return false;
    }

    @Override
    public void onResume() {
        destination_focused();
        if(isLocationServiceEnabled() && isNetworkAvailable()){
            requestCurrentLocation();
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isLocationServiceEnabled();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void delayScanNetwork(int milliseconds) {
        final Handler handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                scanNetwork();
                handler.removeCallbacks(r);
            }
        };
        handler.removeCallbacks(r);
        handler.postDelayed(r, milliseconds);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plan_travel:
                String plan_travel_text = plan_travel.getText().toString();
                if (checkIfPlanTravelOK() && plan_travel_text.equalsIgnoreCase(getString(R.string.eds_plan_travel))) {
                    travel();
                } else if (plan_travel_text.equalsIgnoreCase(getString(R.string.eds_rescan))) {
                    requestCurrentLocation();
                } else {
                    scanNetwork();
                }
                break;
        }
    }

    private void createMap() {
        try {
            // Loading map
            initilizeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true); //TODO - return

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            googleMap.setOnMyLocationButtonClickListener(this);

            requestCurrentLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestCurrentLocation() {
        et_origin.setHint(getString(R.string.eds_requesting_location));
        isLocationServiceEnabled();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        try{
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }catch (Exception e){
            Log.e(null, e.getMessage());
        }
    }

    /**
     * function to load map If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longtitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.animateCamera(cameraUpdate);

        locationManager.removeUpdates(this);
        setGeoLocation(location);
    }

    private void setGeoLocation(Location currentLocation) {
        Log.e(null, "setGeoLocation");
        geo = new Geo();
        geo.setLatitude(currentLocation.getLatitude());
        geo.setLongitude(currentLocation.getLongitude());
        geo.setTime(currentLocation.getTime());
        new getAddressAsync().execute();
        et_destination.setEnabled(true);
        et_destination.setHint(getString(R.string.eds_enter_destination));
    }

    public boolean isLocationServiceEnabled() {
        LocationManager lm = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = lm.getBestProvider(new Criteria(), true);
        boolean isEnabled = (provider.length() > 0 &&
                !LocationManager.PASSIVE_PROVIDER.equals(provider));
        if(!isEnabled)
            tv_distance.setText("Location service is disabled.");
        else if(et_destination.getText().toString().length() == 0)
            tv_distance.setText("");
        return isEnabled;

//        LocationManager lm = null;
//        boolean gps_enabled = false,network_enabled = false;
//        if(lm==null)
//            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        try{
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        }catch(Exception ex){}
//        try{
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        }catch(Exception ex){}
//
//        return !gps_enabled && !network_enabled;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        scanNetwork();
        if (isNetworkAvailable()) {
            if(googleMap.getMyLocation() != null)
                setGeoLocation(googleMap.getMyLocation());
            else
                Toast.makeText(getActivity(), getString(R.string.eds_requesting_location), Toast.LENGTH_SHORT).show();
        }
        Log.e(null, "location button clicked");
        return false;
    }

    public boolean isNetworkAvailable() {
        if (connectivityManager == null)
            connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (isAvailable) {
//            Log.e(null, "network available");
        } else {
            Log.e(null, "no network");
        }
        return isAvailable;
    }

    //////////////////AUTOCOMPLETE/////////////////////
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void initialize_autocomplete() {

//        et_destination.setThreshold(3);

//        et_destination.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                placesTask = new PlacesTask();
//                placesTask.execute(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//            }
//        });

        et_destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        i == EditorInfo.IME_ACTION_GO) {
                    if (isNetworkAvailable() && isLocationServiceEnabled() && et_origin.getText().toString().length() > 0)
                        toggleSearchDialog();
                    else if (!isNetworkAvailable())
                        Toast.makeText(getActivity(), getString(R.string.eds_unable_to_connect), Toast.LENGTH_LONG).show();
                    else if (!isLocationServiceEnabled())
                        Toast.makeText(getActivity(), "Location service disabled", Toast.LENGTH_LONG).show();
                    else if(et_origin.getText().toString().length() <= 0)
                        Toast.makeText(getActivity(), getString(R.string.eds_requesting_location), Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void requestAutoComplete() {
        final Handler handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                placesTask = new PlacesTask();
                placesTask.execute(et_destination.getText().toString());

                handler.removeCallbacks(r);
            }
        };
        handler.removeCallbacks(r);
        handler.postDelayed(r, 300);
    }

    private void toggleSearchDialog() {
        if (et_destination.getText().toString().replace(" ", "").length() > 0) {
            placesTask = new PlacesTask();
            placesTask.execute(et_destination.getText().toString());

            progressBar = new ProgressDialog(rootView.getContext());
            progressBar.setCancelable(true);
            progressBar.setIndeterminate(true);
            progressBar.setMessage(getString(R.string.eds_loading_dialog));

            progressBar.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try{
                        placesTask.cancel(true);
                        if(parserTask != null)
                            parserTask.cancel(true);
                    }catch (Exception e){
                        Log.e(null, e.getMessage());
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            progressBar.show();
        } else {
            Toast.makeText(getActivity(), "Enter keyword", Toast.LENGTH_LONG).show();
        }
    }

    private void showDestinationDialog(final List<HashMap<String, String>> result) {
        Log.e(null, "progressBar.isShowing() -> " + progressBar.isShowing());
        if (!progressBar.isShowing())
            return;

        progressBar.dismiss();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_action_place);
        String cancel_text = "Cancel";

        if (result != null && !result.isEmpty()) {
            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
            builderSingle.setTitle("Locations similar to '" + et_destination.getText().toString() + "'");

            builderSingle.setAdapter(adapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            String strName = (String) adapter.getItem(which);
                            if (which >= 0) {
                                if(isNetworkAvailable()){
                                    myDestination = result.get(which);
                                    et_destination.setText(myDestination.get("description"));
                                    et_destination.setTextColor(Color.BLACK);
                                    et_destination.setCursorVisible(false);
                                    et_destination.setFocusableInTouchMode(false);
                                    last_distance = null;
                                    tv_distance.setText(getString(R.string.eds_getting_location));
                                    destination_selected(myDestination);

                                    placesDetailsTask = new PlacesDetailsTask();
                                    placesDetailsTask.execute(myDestination.get("reference"));

//                                    checkIfPlanTravelOK();
                                }else{
                                    Toast.makeText(getActivity(), getString(R.string.eds_unable_to_connect), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            builderSingle.setTitle("No Similar Locations");
            cancel_text = "Close";
        }

        builderSingle.setNegativeButton(cancel_text,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_destination.setEnabled(true);
                        dialog.dismiss();
                    }
                });

        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                et_destination.setEnabled(true);
            }
        });

        builderSingle.show();
    }

    private void destination_selected(HashMap<String, String> myDestination) {
//        String distance = Distance.CalculationByDistance(myDestination.get(""), myLocation.getLatitude(), myLocation.getLongitude());
//        tv_distance.setText(distance);
    }

    private void hide_keyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_destination.getWindowToken(), 0);
    }

    private static boolean closed;

    @Override
    public void onDestroy() {
        closed = true;
        super.onDestroy();
    }

    private String myLocationName = null;
    private class getAddressAsync extends AsyncTask<String, Void, Geo> {
        @Override
        protected Geo doInBackground(String... params) {
            if (geo.setAddress(getActivity(), geo.getLatitude(), geo.getLongitude())) {
                retry_count = 0;
                location_acquired = true;
            } else {
                retry_count++;
                if (retry_count <= Config.retry_after_failed_getlocation_address && !travel_ready) {
                    new getAddressAsync().execute();
                }
            }
            return geo;
        }

        @Override
        protected void onPostExecute(Geo geo) {
            super.onPostExecute(geo);
            if(closed) return;
            if (geo.getAddress() != null){
                et_origin.setText(geo.getAddress());
                myLocationName = geo.getName();
            }
            scanNetwork();
        }

    }

    ParserTask parserTask;
    public class PlacesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            String input = "";

            try {
                String place_input = place[0];
                input = "input=" + URLEncoder.encode(place_input, "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=true"; //PlaceProvideroriginal: sensor=false

            // If Only Local is enabled
            String country_filter = "";
            if (MainActivity.onlyLocal)
                country_filter += "&components=country:ph";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + Config.autocomplete_key + country_filter;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            Log.e(null, "places_url: " + url);
            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }


        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            showDestinationDialog(result);
        }
    }

    public class PlacesDetailsTask extends AsyncTask<String, Void, String> {
        ParserDetailsTask parserDetailsTask;

        @Override
        protected String doInBackground(String... reference) {
            String data = "";

            // Sensor enabled
            String sensor = "sensor=true";

            // Building the parameters to the web service
            String parameters = sensor + "&" + Config.autocomplete_key + "&reference=" + reference[0];

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;
            Log.e(null, "detail_url: " + url);

            try {
                data = downloadUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            Log.e(null, "details: " + result);
            // Creating ParserTask
            parserDetailsTask = new ParserDetailsTask();

            // Starting Parsing the JSON string returned by Web Service
            parserDetailsTask.execute(result);
        }


        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";

            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserDetailsTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceDetailsJSONParser placeJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            Log.e(null, "lat: " + result.get(0).get("lat") +
                    ", long: " + result.get(0).get("lat") +
                    ", formatted_address: " + result.get(0).get("formatted_address"));

            myDestination = result.get(0);
            setDistanceAfterSelection();
        }
    }

    private void drawDirection(double lat1, double lon1, String str_lat2, String str_lon2, GoogleMap mMap){
        double lat2 = Distance.stringToDouble(str_lat2);
        double lon2 = Distance.stringToDouble(str_lon2);
        LatLng fromPosition = new LatLng(lat1, lon1);
        LatLng toPosition = new LatLng(lat2, lon2);

        GMapV2Direction md = new GMapV2Direction();

        Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

        for(int i = 0 ; i < directionPoint.size() ; i++) {
            rectLine.add(directionPoint.get(i));
        }

        mMap.addPolyline(rectLine);
    }

    private String last_distance = null;
    private void setDistanceAfterSelection() {
        if (myLocation != null && myDestination != null) {
            last_distance = Distance.CalculationByDistance(
                    myLocation.getLatitude(), myLocation.getLongitude(),
                    myDestination.get("lat"), myDestination.get("lng"));
            tv_distance.setText(last_distance);
        } else {
            tv_distance.setText("...");
        }
        checkIfPlanTravelOK();

        LatLng latLng = new LatLng(Distance.stringToDouble(myDestination.get("lat")), Distance.stringToDouble(myDestination.get("lng")));
        googleMap.clear(); //only 1 marker
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Destination: " + myDestination.get("formatted_address")));

//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            builder.include(latLng);
//            LatLngBounds bounds = builder.build();
//            int padding = 0; // offset from edges of the map in pixels
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cu);

//        drawDirection(myLocation.getLatitude(), myLocation.getLongitude(),
//                myDestination.get("lat"), myDestination.get("lng"),
//                googleMap);
        route();
    }

    private void route(){
        if(travel_ready){
            startRouting(myLocation.getLatitude(), myLocation.getLongitude(),
                    myDestination.get("lat"), myDestination.get("lng"));
        }
    }

    protected LatLng start;
    protected LatLng end;
    public static Routing routing;
    private void startRouting(double lat1, double lon1, String str_lat2, String str_lon2){
        double lat2 = Distance.stringToDouble(str_lat2);
        double lon2 = Distance.stringToDouble(str_lon2);

        start = new LatLng(lat1, lon1);
        end = new LatLng(lat2, lon2);

        CameraUpdate center = CameraUpdateFactory.newLatLng(end);
        CameraUpdate zoom =  CameraUpdateFactory.zoomTo(14);

//        googleMap.moveCamera(center);
//        googleMap.animateCamera(zoom);

        if(routing != null && !routing.isCancelled())
            routing.cancel(true);
        routing = new Routing(Routing.TravelMode.WALKING);
        routing.registerListener(this);
        routing.execute(start, end);
    }


    @Override
    public void onRoutingFailure() {
        Toast.makeText(getActivity(), "No route available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions) {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE); //Color.rgb(0,148,255));
        polyoptions.width(10);
        polyoptions.addAll(mPolyOptions.getPoints());
        googleMap.addPolyline(polyoptions);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
//        googleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
//        googleMap.addMarker(options);
    }

    private void sendIntent(double lat1, double lon1, String str_lat2, String str_lon2, String name1, String name2,
                            String distance, String caption, long time){
        double lat2 = Distance.stringToDouble(str_lat2);
        double lon2 = Distance.stringToDouble(str_lon2);

        Session.createNewNotLoad = true;
        Session.lat1 = lat1;
        Session.lon1 = lon1;
        Session.lat2 = lat2;
        Session.lon2 = lon2;
        Session.name1 = name1;
        Session.name2 = name2;
        Session.distance = distance;
        Session.caption = caption;
        Session.time = time;
    }


    private void travel() {
//        myDestination.get("name"); - title
        if(last_distance == null)
            return;
        sendIntent(myLocation.getLatitude(), myLocation.getLongitude(),
                myDestination.get("lat"), myDestination.get("lng"),
                et_origin.getText().toString(), et_destination.getText().toString(),
                last_distance, myLocationName + " -> " + myDestination.get("name"), System.currentTimeMillis());
        create_traveller();
    }

    private void create_traveller(){
        hide_keyboard();
        fm = getFragmentManager();
        Fragment fragment = new Traveller();
        fm.beginTransaction()
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .addToBackStack("home")
                .replace(R.id.container, fragment)
                .commit();
    }
}
