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
package fr.lemet.application.fragments.bus;

import fr.lemet.application.R;
import fr.lemet.application.activity.alerts.ListAlertsForOneLine;
import fr.lemet.application.activity.bus.DetailArret;
import fr.lemet.application.adapters.bus.ArretAdapter;
import fr.lemet.transportscommun.activity.bus.AbstractDetailArret;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.fragments.AbstractListArretFragment;

/**
 * Liste des arrêts d'une ligne de bus.
 * 
 * @author ybonnel
 */
public class ListArretFragment extends AbstractListArretFragment {

	@Override
	protected int getLayout() {
		return R.layout.fragment_listearrets;
	}

	@Override
	protected void setupAdapter() {
		setListAdapter(new ArretAdapter(getActivity(), currentCursor, myLigne));
	}

	@Override
	protected Class<? extends AbstractDetailArret> getDetailArret() {
		return DetailArret.class;
	}

	@Override
	protected Class<? extends BaseActivity.BaseFragmentActivity> getListAlertsForOneLine() {
		return ListAlertsForOneLine.class;
	}
}
