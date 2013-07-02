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
package fr.lemet.application.activity.alerts;

import android.os.Bundle;

import fr.lemet.application.R;
import fr.lemet.application.fragments.alerts.ListAlerts;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.donnees.modele.Ligne;

public class ListAlertsForOneLine extends BaseActivity.BaseFragmentActivity {

    private Ligne ligne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ligne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		setContentView(R.layout.listalert);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		ListAlerts fragmentAlert = (ListAlerts) getSupportFragmentManager().findFragmentById(R.id.fragment_alerts);
		fragmentAlert.setLigne(ligne);
    }

}
