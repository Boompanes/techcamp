package com.findeds.zagip;

import android.app.*;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.findeds.zagip.db.Storm;
import com.findeds.zagip.entity.dao.TravelTBDao;
import com.findeds.zagip.receiver.AlarmReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

/**
 * Created by Emman on 3/13/14.
 */
public class Traveller extends Fragment implements View.OnClickListener {
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    removeTravel();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
    private TravelTBDao travelTBDao;
    private View rootView;
    private EditText et_destinaton;
    private Dialog dialog;
    private Storm storm;
    private EditText notes;
    private ImageButton schedule;
    private Button set_schedule;
    private ImageButton upload_photos;
    private String mSelectedImagePath;
    private ImageView iv;

    public Traveller() {
        storm = Storm.getInstance(getActivity());
        Session.notes = null;
        Session.image_path = null;
    }

    public Traveller(long id) {
        storm = Storm.getInstance(getActivity());
        travelTBDao = new TravelTBDao(getActivity());
        Session.id = travelTBDao.get(id).getId();
        Session.lat1 = travelTBDao.get(id).getLat1();
        Session.lon1 = travelTBDao.get(id).getLon1();
        Session.lat2 = travelTBDao.get(id).getLat2();
        Session.lon2 = travelTBDao.get(id).getLon2();
        Session.name1 = travelTBDao.get(id).getName1();
        Session.name2 = travelTBDao.get(id).getName2();
        Session.distance = travelTBDao.get(id).getDistance();
        Session.caption = travelTBDao.get(id).getCaption();
        Session.time = travelTBDao.get(id).getTime();
        Session.image_path = storm.getImagePath();
        Session.createNewNotLoad = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.traveller, container, false);
        init();
        return rootView;
    }

    private void init() {
        final EditText et_caption = (EditText) rootView.findViewById(R.id.et_caption);
        EditText et_origin = (EditText) rootView.findViewById(R.id.et_origin);
        et_destinaton = (EditText) rootView.findViewById(R.id.et_destination);
        TextView tv_distance = (TextView) rootView.findViewById(R.id.tv_distance);
        ImageButton web_search = (ImageButton) rootView.findViewById(R.id.web_search);
        web_search.setOnClickListener(this);
        ImageButton share = (ImageButton) rootView.findViewById(R.id.share);
        share.setOnClickListener(this);
        ImageButton delete = (ImageButton) rootView.findViewById(R.id.delete);
        delete.setOnClickListener(this);
        schedule = (ImageButton) rootView.findViewById(R.id.schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity());

                dialog.setContentView(R.layout.date_time);
                dialog.setTitle("Schedule Notification");

                dialog.show();

                set_schedule = (Button) dialog.findViewById(R.id.set_schedule);
                set_schedule.setOnClickListener(Traveller.this);
            }
        });

        notes = (EditText) rootView.findViewById(R.id.notes);
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveNotes();
            }
        });

        et_caption.setText(Session.caption);
        et_origin.setText(Session.name1);
        et_destinaton.setText(Session.name2);
        tv_distance.setText(Session.distance);

        iv = (ImageView) rootView.findViewById(R.id.iv);

        if (Session.createNewNotLoad) {
            //new travel
            createTravel();
            notes.setText("");
            iv.setVisibility(View.GONE);
        } else {
            //load travel
            notes.setText(storm.getNotes());
        }

        upload_photos = (ImageButton) rootView.findViewById(R.id.upload_photos);
        upload_photos.setOnClickListener(this);

        if(Session.image_path != null){
            File file = new File(Session.image_path);
            if(file.exists()){
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(BitmapFactory.decodeFile(Session.image_path));
            }else{
                iv.setVisibility(View.GONE);
            }
        }else{
            iv.setVisibility(View.GONE);
        }
    }

    private void saveNotes() {
        if (notes.getText().toString().trim().length() > 0) {
            Session.notes = notes.getText().toString();
            storm.saveNotes();
        }
    }

    @Override
    public void onClick(View view) {
        String google_url = "https://www.google.com.ph/search?q=" + et_destinaton.getText().toString().replace(" ", "+");
        switch (view.getId()) {
            case R.id.web_search:
                Uri uri = Uri.parse(google_url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Travelling from " + Session.caption.replace(" -> ", " to ") + " via #traveltracker " + google_url;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Travel Tracker");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Travel via"));
                break;
            case R.id.delete:
                confirm_remove();
                break;
            case R.id.set_schedule:
                setAlarm();
                break;
            case R.id.upload_photos:
                imageFromCamera();
//                imageFromGallery();
                break;
        }
    }

    private Uri mCapturedImageURI;
    public void imageFromCamera(){
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, Config.CAMERA_IMAGE);
    }

    public void imageFromGallery() {
        Intent getImageFromGalleryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(getImageFromGalleryIntent, Config.SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch(requestCode) {
                case Config.SELECT_IMAGE:
                    mSelectedImagePath = getPath(data.getData());
                    copyFile(mSelectedImagePath);
                    Toast.makeText(getActivity(), mSelectedImagePath, Toast.LENGTH_LONG).show();
                    break;
                case Config.CAMERA_IMAGE:
                    String[] projection = { MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String capturedImageFilePath = cursor.getString(column_index_data);
//                    copyFile(capturedImageFilePath);
                    iv.setVisibility(View.VISIBLE);
                    setImagePath(capturedImageFilePath);
                    Log.e(null, "captured image path: " + capturedImageFilePath);
                    break;
            }
        }
    }

    private void setImagePath(String imagePath){
        Session.image_path = imagePath;
        if(storm.saveImagePath()){
            File file = new File(imagePath);
            if(file.exists())
                Toast.makeText(getActivity(), "Image saved", Toast.LENGTH_LONG).show();
        }
        iv.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        getActivity().startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void copyFile(String sourceImagePath){
        String destinationImagePath = "";
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                destinationImagePath = assignDestination();
                File source= new File(data, sourceImagePath);
                File destination= new File(sd, destinationImagePath);
                if (true || source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }else{
                    Log.e(null, "Source not exists: " + source.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(null, e.getMessage());
        }

        Log.e(null, "copying " + sourceImagePath + " -> " + destinationImagePath);
    }
    private String assignDestination(){
        String dir = Environment.getExternalStorageDirectory() + "/Travels/" + Session.id + "/";
        createDirIfNotExists(dir);
        return dir + "1.jpg";
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    private void createTravel() {
        Session.id = storm.setTravel();
        if (Session.id != 1) {
            Toast.makeText(getActivity(), "Travel Plan Created", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getActivity(), "Unable to create Travel Plan", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeTravel() {
        if (storm.removeTravel(Session.id)) {
            Toast.makeText(getActivity(), "Travel Plan Deleted", Toast.LENGTH_SHORT).show();
            close_traveller();
        } else {
            Toast.makeText(getActivity(), "Unable to delete Travel Plan", Toast.LENGTH_SHORT).show();
        }
    }

    private void close_traveller() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            getActivity().finish();
        }
    }

    private void confirm_remove() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete Travel Plan?").setPositiveButton("Delete", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private void setAlarm() {
        if (!dialog.isShowing()) return;

        Calendar cal = Calendar.getInstance();
        //for using this you need to import java.util.Calendar;
        // add minutes to the calendar object

        DatePicker datePicker1 = (DatePicker) dialog.findViewById(R.id.datePicker1);
        TimePicker timePicker1 = (TimePicker) dialog.findViewById(R.id.timePicker1);

        cal.set(Calendar.MONTH, datePicker1.getMonth());
        cal.set(Calendar.YEAR, datePicker1.getYear());
        cal.set(Calendar.DAY_OF_MONTH, datePicker1.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, timePicker1.getCurrentHour());
        cal.set(Calendar.MINUTE, timePicker1.getCurrentMinute());

        //cal.set will set the alarm to trigger exactly at: 21:43, 5 May 2011
        //if you want to trigger the alarm after let's say 5 minutes after is activated you need to put
        //cal.add(Calendar.MINUTE, 5);
        Intent alarmintent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        String str_notes = notes.getText().toString();
        if (str_notes.length() > 100)
            str_notes = str_notes.substring(0, 99);
        alarmintent.putExtra("note", "Scheduled Travel Plan");
        alarmintent.putExtra("title", Session.caption + " (" + Session.distance + ")");
        //HELLO_ID is a static variable that must be initialised at the BEGINNING OF CLASS with 1;

        //example:protected static int HELLO_ID =1;
        PendingIntent sender = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int) Session.id,
                alarmintent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        //VERY IMPORTANT TO SET FLAG_UPDATE_CURRENT... this will send correct extra's informations to
        //AlarmReceiver Class
        // Get the AlarmManager service

        AlarmManager am = (AlarmManager) getActivity().getSystemService("alarm");
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

        dialog.dismiss();
        Toast.makeText(getActivity(), "Notification successfully scheduled", Toast.LENGTH_LONG).show();
    }
}
