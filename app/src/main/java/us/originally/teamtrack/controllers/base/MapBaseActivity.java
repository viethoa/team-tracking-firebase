package us.originally.teamtrack.controllers.base;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lorem_ipsum.utils.StringUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;
import us.originally.teamtrack.models.UserTeamModel;

/**
 * Created by VietHoa on 09/09/15.
 */
public abstract class MapBaseActivity extends BaseGraphActivity {

    protected class UserOnMap {
        public UserTeamModel user;
        public Marker marker;
    }

    protected GoogleMap map;
    protected ArrayList<UserOnMap> mUsersOnMap;

    @InjectView(R.id.mapview)
    protected MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        ButterKnife.inject(this);
        initialiseMap(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    protected abstract void setContentView();

    //----------------------------------------------------------------------------------------------
    //  Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
    }

    //----------------------------------------------------------------------------------------------
    //  Event
    //----------------------------------------------------------------------------------------------

    protected void showLocationWithCamera(double lat, double lng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15);
        map.animateCamera(cameraUpdate);
    }

    protected void addUserToMapWithNoneCamera(UserTeamModel user) {
        if (user == null)
            return;

        UserOnMap userOnMap = new UserOnMap();
        userOnMap.user = user;

        //Add marker to my map
        LatLng latLng = new LatLng(user.lat, user.lng);
        userOnMap.marker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(user.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.user_location_onl))
        );

        //Show marker title always
        userOnMap.marker.showInfoWindow();

        //Save user
        if (mUsersOnMap == null)
            mUsersOnMap = new ArrayList<>();
        mUsersOnMap.add(userOnMap);
    }

    protected void removeUserFromMap(UserTeamModel user) {
        if (user == null || StringUtils.isNull(user.device_uuid))
            return;

        if (mUsersOnMap == null || mUsersOnMap.size() <= 0)
            return;

        UserOnMap userRemoved = null;
        for (UserOnMap userOnMap : mUsersOnMap) {
            if (userOnMap == null || userOnMap.user == null || StringUtils.isNull(userOnMap.user.device_uuid))
                continue;

            if (userOnMap.user.device_uuid.equals(user.device_uuid)) {
                userRemoved = userOnMap;
                break;
            }
        }
        if (userRemoved == null || userRemoved.marker == null)
            return;

        //Change location
        userRemoved.marker.remove();
        mUsersOnMap.remove(userRemoved);
    }

    protected void changeUserLocation(UserTeamModel user) {
        if (user == null || StringUtils.isNull(user.device_uuid))
            return;

        if (mUsersOnMap == null || mUsersOnMap.size() <= 0)
            return;

        UserOnMap userChanged = null;
        for (UserOnMap userOnMap : mUsersOnMap) {
            if (userOnMap == null || userOnMap.user == null || StringUtils.isNull(userOnMap.user.device_uuid))
                continue;

            if (userOnMap.user.device_uuid.equals(user.device_uuid)) {
                userChanged = userOnMap;
                break;
            }
        }
        if (userChanged == null)
            return;

        //Change location
        userChanged.marker.setPosition(new LatLng(user.lat, user.lng));
    }
}
