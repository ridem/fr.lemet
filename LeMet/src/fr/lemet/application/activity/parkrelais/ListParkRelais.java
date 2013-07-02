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
package fr.lemet.application.activity.parkrelais;

import java.util.List;

import fr.lemet.application.R;
import fr.lemet.application.keolis.Keolis;
import fr.lemet.application.keolis.modele.bus.ParkRelai;
import fr.lemet.transportscommun.activity.commun.BaseActivity.BaseMapActivity;
import fr.lemet.transportscommun.activity.parkings.AbstractListParkings;
import fr.lemet.transportscommun.util.ErreurReseau;

/**
 * Activité de type liste permettant de lister les parcs relais par distances de
 * la position actuelle.
 *
 * @author ybonnel
 */
public class ListParkRelais extends AbstractListParkings<ParkRelai> {

	private final Keolis keolis = Keolis.getInstance();

	@Override
	protected int getDialogueRequete() {
		return R.string.dialogAPropos;
	}

	@Override
	protected int getLayout() {
		return R.layout.listparkrelais;
	}

	@Override
	protected List<ParkRelai> getParkings() throws ErreurReseau {
		return keolis.getParkRelais();
	}

	@Override
	protected Class<? extends BaseMapActivity> getParkingsOnMap() {
		return ParkRelaisOnMap.class;
	}

	@Override
	protected void setupActionBar() {
		getActivityHelper().setupActionBar(R.menu.listparkings_menu_items, R.menu.holo_listparkings_menu_items);
	}
}
