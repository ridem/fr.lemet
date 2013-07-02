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
package fr.lemet.application.activity.bus;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.Set;

import fr.lemet.application.R;
import fr.lemet.application.adapters.bus.NotifAdapter;
import fr.lemet.application.application.TransportsMetzApplication;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.donnees.modele.Arret;
import fr.lemet.transportscommun.donnees.modele.Ligne;
import fr.lemet.transportscommun.donnees.modele.Notification;
import fr.lemet.transportscommun.util.UpdateTimeUtil;
import fr.lemet.transportscommun.util.UpdateTimeUtil.UpdateTime;

/**
 * @author ybonnel
 */
public class ListNotif extends BaseActivity.BaseListActivity {

    private UpdateTimeUtil updateTimeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listnotif);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        setListAdapter(new NotifAdapter(getApplicationContext(), TransportsMetzApplication.getDataBaseHelper().selectAll(Notification.class)));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notification notification = (Notification) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ListNotif.this, DetailArret.class);
                Ligne ligne = Ligne.getLigne(notification.getLigneId());
                Arret arret = Arret.getArret(notification.getArretId());
                intent.putExtra("ligne", ligne);
                intent.putExtra("idArret", notification.getArretId());
                intent.putExtra("nomArret", arret.nom);
                intent.putExtra("direction", notification.getDirection());
                intent.putExtra("macroDirection", notification.getMacroDirection());
                startActivity(intent);
            }
        });
        registerForContextMenu(getListView());
        updateTimeUtil = new UpdateTimeUtil(new UpdateTime() {

            public void update(Calendar calendar) {
                ((NotifAdapter) getListAdapter()).majCalendar();
                ((NotifAdapter) getListAdapter()).notifyDataSetChanged();
            }

			@Override
			public boolean updateSecond() {
				return false;
			}

			@Override
			public Set<Integer> secondesToUpdate() {
				return null;
			}
        }, this);
        updateTimeUtil.start();
    }

    @Override
    protected void onResume() {
        updateTimeUtil.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateTimeUtil.stop();
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            menu.add(Menu.NONE, R.id.supprimerNotif, 0, getString(R.string.supprimerNotif));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.supprimerNotif:
                Notification notification = (Notification) getListAdapter().getItem(info.position);
                TransportsMetzApplication.getDataBaseHelper().delete(notification);
                ((NotifAdapter) getListAdapter()).getNotifications().clear();
                ((NotifAdapter) getListAdapter()).getNotifications().addAll(TransportsMetzApplication.getDataBaseHelper().selectAll(Notification.class));
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
