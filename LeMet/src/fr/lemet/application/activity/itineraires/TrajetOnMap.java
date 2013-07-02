/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.lemet.application.activity.itineraires;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.lemet.application.R;
import fr.lemet.application.itineraires.PortionTrajet;
import fr.lemet.application.itineraires.Trajet;
import fr.lemet.application.map.LineItemizedOverlay;
import fr.lemet.application.map.MapItemizedOverlayTrajet;
import fr.lemet.application.util.Coordinate;
import fr.lemet.application.util.PolylineEncoder;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.util.FixedMyLocationOverlay;
import fr.lemet.transportscommun.util.IconeLigne;

@SuppressLint("SimpleDateFormat")
public class TrajetOnMap extends BaseActivity.BaseMapActivity {

    private static final SimpleDateFormat SDF_HEURE = new SimpleDateFormat("HH:mm");

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajet_map);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        Trajet trajet = (Trajet) getIntent().getSerializableExtra("trajet");

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        MapController mc = mapView.getController();

        // Creation du geo point
        List<Overlay> mapOverlays = mapView.getOverlays();
        LinearLayout layoutTrajet = (LinearLayout) findViewById(R.id.trajetDetail);
        layoutTrajet.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        if (!trajet.getPortions().isEmpty()) {
            int minLatitude = Integer.MAX_VALUE;
            int maxLatitude = Integer.MIN_VALUE;
            int minLongitude = Integer.MAX_VALUE;
            int maxLongitude = Integer.MIN_VALUE;
            int latitude;
            int longitude;
            ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
            for (PortionTrajet portion : trajet.getPortions()) {
                // icône
                RelativeLayout portionLayout = (RelativeLayout) inflater.inflate(R.layout.portion_trajet, null);
                TextView directionTrajet = (TextView) portionLayout.findViewById(R.id.directionTrajet);
                int iconeMarkee;
                int icone;
                if (portion.getMode().isOnStreetNonTransit()) {
                    iconeMarkee = R.drawable.mpieton;
                    icone = R.drawable.ipieton;
                    directionTrajet.setVisibility(View.GONE);
                } else {
                    iconeMarkee = IconeLigne.getMarkeeResource(portion.getLigneId());
                    icone = IconeLigne.getIconeResource(portion.getLigneId());

                    directionTrajet.setVisibility(View.VISIBLE);
                    directionTrajet.setText(getString(R.string.directionEntete) + ' ' + portion.getDirection());
                }

                // icône du départ.
                MapItemizedOverlayTrajet itemizedOverlay = new MapItemizedOverlayTrajet(getResources().getDrawable(
                        iconeMarkee));
                latitude = (int) (portion.getFromLat() * 1.0E6);
                longitude = (int) (portion.getFromLon() * 1.0E6);
                maxLatitude = Math.max(latitude, maxLatitude);
                minLatitude = Math.min(latitude, minLatitude);
                maxLongitude = Math.max(longitude, maxLongitude);
                minLongitude = Math.min(longitude, minLongitude);
                OverlayItem item = new OverlayItem(new GeoPoint(latitude, longitude), portion.getFromName(), null);
                itemizedOverlay.addOverlay(item);
                mapOverlays.add(itemizedOverlay);

                for (Coordinate coordinate : PolylineEncoder.decode(portion.getGeometry())) {
                    geoPoints.add(coordinate.toGeoPoint());
                }

                // Détail du trajet.
                ((ImageView) portionLayout.findViewById(R.id.iconePortion)).setImageResource(icone);
                ((TextView) portionLayout.findViewById(R.id.departHeure)).setText(SDF_HEURE.format(portion
                        .getStartTime()));
                ((TextView) portionLayout.findViewById(R.id.depart)).setText(portion.getFromName());
                ((TextView) portionLayout.findViewById(R.id.arriveeHeure)).setText(SDF_HEURE.format(portion
                        .getEndTime()));
                ((TextView) portionLayout.findViewById(R.id.arrivee)).setText(portion.getToName());
                layoutTrajet.addView(portionLayout);
            }
            // icone de l'arrivee.
            PortionTrajet portion = trajet.getPortions().get(trajet.getPortions().size() - 1);

            MapItemizedOverlayTrajet itemizedOverlay = new MapItemizedOverlayTrajet(getResources().getDrawable(
                    R.drawable.mpieton));
            latitude = (int) (portion.getToLat() * 1.0E6);
            longitude = (int) (portion.getToLon() * 1.0E6);
            maxLatitude = Math.max(latitude, maxLatitude);
            minLatitude = Math.min(latitude, minLatitude);
            maxLongitude = Math.max(longitude, maxLongitude);
            minLongitude = Math.min(longitude, minLongitude);
            OverlayItem item = new OverlayItem(new GeoPoint(latitude, longitude), portion.getToName(), null);
            itemizedOverlay.addOverlay(item);
            mapOverlays.add(itemizedOverlay);

            LineItemizedOverlay lineItemizedOverlay = new LineItemizedOverlay(geoPoints);
            mapOverlays.add(lineItemizedOverlay);

            mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
        }
        mc.setZoom(14);

        myLocationOverlay = new FixedMyLocationOverlay(this, mapView);
        mapOverlays.add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();

        gestionButtonLayout();

    }

    private MyLocationOverlay myLocationOverlay;

    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        myLocationOverlay.disableMyLocation();
        super.onPause();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
