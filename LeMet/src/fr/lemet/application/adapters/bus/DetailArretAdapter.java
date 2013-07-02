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
package fr.lemet.application.adapters.bus;

import android.content.Context;

import java.util.List;

import fr.lemet.application.R;
import fr.lemet.transportscommun.adapters.bus.AbstractDetailArretAdapter;
import fr.lemet.transportscommun.donnees.modele.DetailArretConteneur;

public class DetailArretAdapter extends AbstractDetailArretAdapter {

	public DetailArretAdapter(Context context, List<DetailArretConteneur> prochainsDeparts, int now, boolean isToday,
			String currentDirection, int secondesNow) {
		super(context, prochainsDeparts, now, isToday, currentDirection, secondesNow);
	}

	@Override
	protected int getLayout() {
		return R.layout.detailarretliste;
	}

}
