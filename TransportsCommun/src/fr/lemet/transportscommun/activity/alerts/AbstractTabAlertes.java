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
package fr.lemet.transportscommun.activity.alerts;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity.BaseTabFragmentActivity;

public abstract class AbstractTabAlertes extends BaseActivity.BaseTabFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AbstractTransportsApplication.majTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		configureTabs();
		addTab("alertes", getString(R.string.alertes), getListAlertsClass());
		addTab("twitter", getString(R.string.twitter), getListTwitterClass());
		setCurrentTab(savedInstanceState);
	}

	protected abstract void setupActionBar();

	protected abstract Class<? extends ListFragment> getListAlertsClass();

	protected abstract Class<? extends ListFragment> getListTwitterClass();

	protected abstract int getLayout();

}
