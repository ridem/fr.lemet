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
package fr.lemet.transportscommun.map;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.donnees.modele.ArretFavori;

public class MapItemizedOverlayArret extends ItemizedOverlay<OverlayItem> {

    //Liste des marqueurs
    private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private final Context mContext;
    private final List<ArretFavori> arretFavoris = new ArrayList<ArretFavori>();
    private final String ligneId;
    private final String direction;

    private static Drawable leftBottom(Drawable drawable) {
        drawable.setBounds(0, 0 - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
        return drawable;
    }

    public MapItemizedOverlayArret(Drawable defaultMarker, Context context, String ligneId, String direction) {
        super(leftBottom(defaultMarker));
        mContext = context;
        this.ligneId = ligneId;
        this.direction = direction;
    }

    //Appeler quand on rajoute un nouvel marqueur a la liste des marqueurs
    public void addOverlay(OverlayItem overlay, ArretFavori favori) {
        mOverlays.add(overlay);
        arretFavoris.add(favori);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    //Appeer quand on clique sur un marqueur
    @Override
    protected boolean onTap(int index) {
        OverlayItem item = mOverlays.get(index);
        final ArretFavori favori = arretFavoris.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(item.getTitle());
        builder.setMessage("vers " + item.getSnippet() + "\nVoulez vous ouvrir le détail?");
        builder.setCancelable(true);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
				Intent intent = new Intent(mContext, AbstractTransportsApplication.getDonnesSpecifiques()
						.getDetailArretClass());
                intent.putExtra("favori", favori);
                mContext.startActivity(intent);
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent pEvent, MapView pMapView) {
        try {
            return super.onTouchEvent(pEvent, pMapView);
        } catch (NullPointerException nullPointerException) {
			throw new RuntimeException("NullPointer détecté, context : ligneId=" + ligneId + ", direction='"
					+ direction + "'", nullPointerException);
        }
    }
}
