package fr.lemet.transportscommun.activity.bus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.donnees.modele.ArretFavori;
import fr.lemet.transportscommun.donnees.modele.Ligne;
import fr.lemet.transportscommun.map.MapItemizedOverlayArret;
import fr.lemet.transportscommun.util.IconeLigne;

//import com.google.android.maps.MapActivity;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MyLocationOverlay;

public abstract class AbstractArretOnMap extends BaseActivity.BaseMapActivity {

	protected abstract int getLayout();

	protected abstract void setupActionBar();
    private GoogleMap mMap;
    private Context mContext;
    private List<ArretFavori> arretFavoris = new ArrayList<ArretFavori>();
    //private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Ligne myLigne = (Ligne) getIntent().getSerializableExtra("ligne");
		super.onCreate(savedInstanceState);
        //getActionBar().setTitle("Ligne " + myLigne.id);
		setContentView(getLayout());
		setupActionBar();

		String currentDirection = getIntent().getStringExtra("direction");

		//MapView mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();

        GoogleMapOptions mo = new GoogleMapOptions();
		//MapController mc = mapView.getController();

		// Creation du geo point
		//List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(IconeLigne.getMarkeeResource(myLigne.nomCourt));

		MapItemizedOverlayArret itemizedoverlay = new MapItemizedOverlayArret(drawable, this, myLigne.id,
				currentDirection);
		List<String> selectionArgs = new ArrayList<String>(2);
		selectionArgs.add(myLigne.id);
		StringBuilder requete = new StringBuilder();
		requete.append("select Arret.id as _id, Arret.nom as arretName,");
		requete.append(" Direction.direction as direction, Arret.latitude as latitude,");
		requete.append(" Arret.longitude, ArretRoute.macroDirection ");
		requete.append("from ArretRoute, Arret, Direction ");
		requete.append("where");
		requete.append(" ArretRoute.ligneId = :ligneId");
		requete.append(" and ArretRoute.arretId = Arret.id");
		requete.append(" and ArretRoute.directionId = Direction.id");
		if (currentDirection != null) {
			requete.append(" and Direction.direction = :direction");
			selectionArgs.add(currentDirection);
		}
		requete.append(" order by ArretRoute.sequence");
		Cursor cursor = AbstractTransportsApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
				selectionArgs);
		double minLatitude = Double.MAX_VALUE;
		double maxLatitude = Double.MIN_VALUE;
		double minLongitude = Double.MAX_VALUE;
		double maxLongitude = Double.MIN_VALUE;
        PolylineOptions rectOptions = new PolylineOptions();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.anchor(0,1);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			String nom = cursor.getString(cursor.getColumnIndex("arretName"));
			String direction = cursor.getString(cursor.getColumnIndex("direction"));
			int macroDirection = cursor.getInt(cursor.getColumnIndex("macroDirection"));
			double latitude = (cursor.getDouble(cursor.getColumnIndex("latitude")));
			double longitude = (cursor.getDouble(cursor.getColumnIndex("longitude")));
			//GeoPoint geoPoint = new GeoPoint(latitude, longitude);
			if (latitude < minLatitude) {
				minLatitude = latitude;
			}
			if (latitude > maxLatitude) {
				maxLatitude = latitude;
			}
			if (longitude < minLongitude) {
				minLongitude = longitude;
			}
			if (longitude > maxLongitude) {
				maxLongitude = longitude;
			}

			//OverlayItem overlayitem = new OverlayItem(geoPoint, nom, direction);
			ArretFavori arretFavori = new ArretFavori();
			arretFavori.direction = direction;
			arretFavori.nomArret = nom;
			arretFavori.ligneId = myLigne.id;
			arretFavori.nomCourt = myLigne.nomCourt;
			arretFavori.nomLong = myLigne.nomLong;
			arretFavori.arretId = id;
			arretFavori.macroDirection = macroDirection;
            arretFavoris.add(arretFavori);
            // Instantiates a new Polyline object and adds points to define a rectangle

            if (macroDirection == 1) {
                rectOptions.add(new LatLng(latitude, longitude))
                        .width(20)
                        .color(Color.argb(200,100,100,100))
                        .geodesic(true);
            }
             //.color(Color.rgb(179,46,116))

            mMap.addMarker(markerOptions
                    .position(new LatLng(latitude, longitude))
                    .title(nom)
                    .snippet("Direction " + direction)
                    .icon(BitmapDescriptorFactory.fromResource(IconeLigne.getMarkeeResource((myLigne.nomCourt)))));


                    //.icon(BitmapDescriptorFactory.fromResource(IconeLigne.getMarkeeResource(myLigne.nomCourt))));

			//itemizedoverlay.addOverlay(overlayitem, arretFavori);
		}
        Polyline polyline = mMap.addPolyline(rectOptions);
		//cursor.close();
        System.out.println(maxLatitude);
        System.out.println(minLatitude);
        System.out.println(maxLongitude);
        System.out.println(minLongitude);
		//mapOverlays.add(itemizedoverlay);
		//mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		//mc.setZoom(14);
        LatLng loc = new LatLng((double) (maxLatitude + minLatitude) / 2,(double) (maxLongitude + minLongitude)/2);
        GoogleMapOptions options = new GoogleMapOptions();
        //CameraPosition cam = CameraPosition.fromLatLngZoom(loc,14);
        MapFragment.newInstance(options);
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(false)
                .camera(CameraPosition.fromLatLngZoom(loc,13));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));

		//myLocationOverlay = new FixedMyLocationOverlay(this, mapView);
		//mapOverlays.add(myLocationOverlay);
		//myLocationOverlay.enableMyLocation();



		//gestionButtonLayout();
	}

	//private MyLocationOverlay myLocationOverlay;

	@Override
	protected void onResume() {
		super.onResume();
        mMap.setMyLocationEnabled(true);
        //myLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onPause() {
        mMap.setMyLocationEnabled(false);
		//myLocationOverlay.disableMyLocation();
		super.onPause();
	}
    protected void onInfoWindowClick(Marker marker){
/*        marker.getId();
        Intent intent = new Intent(mContext, AbstractTransportsApplication.getDonnesSpecifiques()
                .getDetailArretClass());
        intent.putExtra("favori", favori);
        mContext.startActivity(intent);*/
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
