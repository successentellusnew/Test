package com.success.successEntellus.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.success.successEntellus.R;
import com.success.successEntellus.activity.CFTLocatorActivity;
import com.success.successEntellus.model.CFTOffice;
import com.success.successEntellus.model.UserLocation;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 9/18/2018.
 */


public class CFTOfficeInfo implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;
    //UserLocation userLocation;
    CFTLocatorActivity cftLocatorActivity;

    public CFTOfficeInfo(Context context) {
        this.context = context;
        // this.userLocation=userLocation;
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cftLocatorActivity=new CFTLocatorActivity();

        View dialog = inflater.inflate(R.layout.show_cftoffice_info, null);
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);



        TextView tv_office_name=dialog.findViewById(R.id.tv_office_name);
        TextView tv_office_address=dialog.findViewById(R.id.tv_office_address);
        TextView tv_office_email=dialog.findViewById(R.id.tv_office_email);



        CFTOffice cftOffice = (CFTOffice) marker.getTag();

        if (cftOffice!=null){
            tv_office_name.setText("Name: "+cftOffice.getCftOfficeName());
            tv_office_address.setText("Address: "+cftOffice.getCftOfficeAddress());
            tv_office_email.setText("Email: "+cftOffice.getCftOfficeEmail());
           // tv_user_email.setText("Email: "+userLocation.getEmail());
            //tv_user_phone.setText("Phone: "+userLocation.getPhone());
            // tv_user_address.setText("Address: "+userLocation.getUserAddress());
           /* if (userLocation.getDistance()!=null){
                tv_user_distance.setText("Distance: "+userLocation.getDistance());
            }*/

        }

       /* btn_find_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Global.TAG, "InfoWindow Click: ");
                cftLocatorActivity.markerPoints.clear();
                String dest_lat=userLocation.getUserLatitude();
                String dest_long=userLocation.getUserLongitude();
                LatLng point=new LatLng(Double.parseDouble(dest_lat),Double.parseDouble(dest_long));
                cftLocatorActivity.markerPoints.add(point);
            }
        });*/
        return dialog;
    }
}