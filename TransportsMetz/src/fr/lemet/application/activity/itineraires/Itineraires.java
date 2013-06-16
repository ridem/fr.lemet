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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import fr.lemet.application.R;
import fr.lemet.application.adapters.itineraires.TrajetAdapter;
import fr.lemet.application.itineraires.ItineraireReponse;
import fr.lemet.application.itineraires.Trajet;
import fr.lemet.transportscommun.activity.commun.BaseActivity;

public class Itineraires extends BaseActivity.BaseListActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itineraires);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        ItineraireReponse itineraireReponse = (ItineraireReponse) getIntent().getExtras().getSerializable(
                "itinerairesReponse");
        int heureDepart = getIntent().getIntExtra("heureDepart", 0);
        setListAdapter(new TrajetAdapter(this, itineraireReponse.getTrajets(), heureDepart));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Trajet trajet = (Trajet) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(Itineraires.this, TrajetOnMap.class);
                intent.putExtra("trajet", trajet);
                startActivity(intent);
            }

        });
    }
}
