package com.findeds.zagip;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;
import com.findeds.zagip.db.Storm;
import com.findeds.zagip.entity.TravelTB;
import com.findeds.zagip.entity.dao.TravelTBDao;
import com.findeds.zagip.location.Distance;
import com.slidinglayer.SlidingLayer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static boolean onlyLocal = true;
    private static FragmentManager fm;
    private static SlidingLayer mSlidingLayer;
    private boolean mShowShadow = true;
    private boolean mShowOffset = false;
    private static boolean animateSlider = true;
    private static ListView list;
    private Storm storm;
    public static TravelTBDao travelTBDao;
    private static ArrayList<String> travelNames;
    private static ArrayList<Long> travelIds;
    private static Context context;
    private static TextView distance_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storm = Storm.getInstance(this);
        travelTBDao = new TravelTBDao(this);
        list = (ListView) findViewById(R.id.list);
        distance_total = (TextView) findViewById(R.id.distance_total);
        context = this;
        fm = getFragmentManager();

        init_fragment();
        init_slider();
        scan_travel_list();
    }

    public static void scan_travel_list() {
        double totalDistance = 0;
        travelNames = new ArrayList<String>();
        travelIds = new ArrayList<Long>();
        List<TravelTB> travels = travelTBDao.listAll();

        for(int i = travels.size() - 1; i >= 0; i--){
            travelIds.add(travels.get(i).getId());
            travelNames.add(travels.get(i).getCaption());

            totalDistance += Distance.CalculationByDistance(
                    travels.get(i).getLat1(), travels.get(i).getLon1(),
                    travels.get(i).getLat2(), travels.get(i).getLon2());
        }
        distance_total.setText(distanceToString(totalDistance));

//        for(TravelTB aTravels : travels){
//            travelIds.add(aTravels.getId());
//            travelNames.add(aTravels.getCaption());
//        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1, travelNames);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                show_traveller(travelIds.get(i));
                mSlidingLayer.closeLayer(animateSlider);
            }
        });
    }

    private static String distanceToString(double valueResult){
        String distance = "";
        if (valueResult > 1)
            distance = String.format("%.2f", valueResult) + " km";
        else
            distance = String.format("%.0f", valueResult * 1000) + " m";
        return "Total Distance: " + distance;
    }

    private static void show_traveller(long id){
//        Toast.makeText(context, "id: " + id, Toast.LENGTH_SHORT).show();
        Fragment fragment = new Traveller(id);
        Log.e(null, "entry: " + fm.getBackStackEntryCount());
        if(fm.getBackStackEntryCount() > 0)
            fm.popBackStack();
            fm.beginTransaction()
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.container, fragment)
                    .addToBackStack("travel" + id)
                    .commit();
    }

    private void init_fragment() {
        fm = getFragmentManager();
        Fragment fragment = new Home();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goUp();
                break;
            case R.id.action_travel_log:
                if (mSlidingLayer.isOpened())
                    mSlidingLayer.closeLayer(animateSlider);
                else
                    mSlidingLayer.openLayer(animateSlider);
                break;
            case R.id.action_local_destinations:
                onlyLocal = !item.isChecked();
                item.setChecked(onlyLocal);
                Log.e(null, "onlyLocal: " + onlyLocal);
                break;
        }
        return true;
    }

    /**
     * View binding and initializes the origin state of the layer
     */
    private void init_slider() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);

        // Sticks container to right or left
/*        int textResource;

        textResource = R.string.travel_list_title;
        Drawable d = getResources().getDrawable(R.drawable.container_rocket_right);


        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        travel_log_title.setCompoundDrawables(null, d, null, null);
        travel_log_title.setText(getResources().getString(textResource));
*/

        LayoutParams rlp = (LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mSlidingLayer.setLayoutParams(rlp);

        // Sets the shadow of the container
        if (mShowShadow) {
            mSlidingLayer.setShadowWidthRes(R.dimen.shadow_width);
            mSlidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
        } else {
            mSlidingLayer.setShadowWidth(0);
            mSlidingLayer.setShadowDrawable(null);
        }
        if (mShowOffset) {
            mSlidingLayer.setOffsetWidth(getResources().getDimensionPixelOffset(R.dimen.offset_width));
        } else {
            mSlidingLayer.setOffsetWidth(0);
        }
    }

    public void buttonClicked(View v) {
        switch (v.getId()) {
            case R.id.plan_travel:
                if (mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(animateSlider);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(animateSlider);
                    return true;
                }

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private boolean goUp() {
        int popEntry = getFragmentManager().getBackStackEntryCount();
        if (popEntry > 0)
            getFragmentManager().popBackStack();
        else {
            finish();
//            moveTaskToBack(true);
        }
        return true;
    }

}
