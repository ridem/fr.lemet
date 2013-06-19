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
package fr.lemet.application.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.MenuItem;

import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import fr.lemet.application.R;
import fr.lemet.application.activity.TransportsMetz;
import fr.lemet.application.activity.bus.DetailArret;
import fr.lemet.application.activity.bus.TabFavoris;
import fr.lemet.application.database.TransportsMetzDatabase;
import fr.lemet.application.database.modele.AlertBdd;
import fr.lemet.application.keolis.Keolis;
import fr.lemet.application.keolis.modele.bus.Alert;
import fr.lemet.application.services.UpdateTimeService;
import fr.lemet.application.util.CalculItineraires;
import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.DonnesSpecifiques;
import fr.lemet.transportscommun.activity.AccueilActivity;
import fr.lemet.transportscommun.activity.commun.ActivityHelper;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.donnees.manager.gtfs.CoupleResourceFichier;
import fr.lemet.transportscommun.donnees.modele.Bounds;
import fr.lemet.transportscommun.util.ErreurReseau;
import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;

//import fr.lemet.application.util.AlarmReceiver;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
@SuppressLint("SimpleDateFormat")
public class TransportsMetzApplication extends AbstractTransportsApplication {

	@Override
	protected void initDonneesSpecifiques() {
		donnesSpecifiques = new DonnesSpecifiques() {

			@Override
			public String getApplicationName() {
				return "LeMet";
			}

			@Override
			public int getCompactLogo() {
				return R.drawable.logo_lemet;
			}

			@Override
			public Class<?> getDrawableClass() {
				return R.drawable.class;
			}

			@Override
			public int getIconeLigne() {
				return R.drawable.icone_bus;
			}

			@Override
			public Class<? extends BaseActivity.BaseListActivity> getDetailArretClass() {
				return DetailArret.class;
			}
		};
	}

	public void constructDatabase() {
		databaseHelper = new TransportsMetzDatabase(this);
	}

	public void postCreate() {
		RESOURCES_PRINCIPALE = Arrays.asList(new CoupleResourceFichier(R.raw.arrets, "arrets.txt"),
				new CoupleResourceFichier(R.raw.arrets_routes, "arrets_routes.txt"), new CoupleResourceFichier(
						R.raw.calendriers, "calendriers.txt"), new CoupleResourceFichier(R.raw.directions,
						"directions.txt"), new CoupleResourceFichier(R.raw.lignes, "lignes.txt"),
				new CoupleResourceFichier(R.raw.trajets, "trajets.txt"));

		startService(new Intent(UpdateTimeService.ACTION_UPDATE));
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("fr.lemet.application", ".services.UpdateTimeService"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		final String dateCourante = new SimpleDateFormat("ddMMyyyy").format(new Date());
		Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
		if (boundsBdd != null) {
			if (!dateCourante.equals(boundsBdd.getDate())) {
				getDataBaseHelper().delete(boundsBdd);
			}
		}

		AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
		if (alertBdd != null) {
			if (!dateCourante.equals(alertBdd.getDate())) {
				getDataBaseHelper().delete(alertBdd);
			}
		}

		// Récupération des alertes
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				try {
					AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
					if (alertBdd == null) {
						alertBdd = new AlertBdd();
						alertBdd.setDate(dateCourante);
						Set<String> lignes = new HashSet<String>();
						for (Alert alert : Keolis.getInstance().getAlerts()) {
							lignes.addAll(alert.lines);
						}
						StringBuilder stringBuilder = new StringBuilder();
						for (String ligne : lignes) {
							stringBuilder.append(ligne);
							stringBuilder.append(',');
						}
						alertBdd.setLignes(stringBuilder.toString());
						getDataBaseHelper().insert(alertBdd);
					}

					Collections.addAll(getLignesWithAlerts(), alertBdd.getLignes().split(","));
				} catch (ErreurReseau ignore) {
				}
				try {
					Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
					if (boundsBdd == null) {
						GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
						if (metadata != null) {
							boundsBdd = new Bounds();
							boundsBdd.setDate(dateCourante);
							boundsBdd.setMinLatitude(metadata.getMinLatitude());
							boundsBdd.setMaxLatitude(metadata.getMaxLatitude());
							boundsBdd.setMinLongitude(metadata.getMinLongitude());
							boundsBdd.setMaxLongitude(metadata.getMaxLongitude());
							getDataBaseHelper().insert(boundsBdd);
						}
					}
					if (boundsBdd != null) {
						setBounds(new LatLngBounds(new LatLng(new BigDecimal(boundsBdd.getMinLatitude()),
								new BigDecimal(boundsBdd.getMinLongitude())), new LatLng(new BigDecimal(
								boundsBdd.getMaxLatitude()), new BigDecimal(boundsBdd.getMaxLongitude()))));
					}
				} catch (OpenTripPlannerException ignore) {
				}
				return null;
			}
		}.execute((Void) null);

		//setRecurringAlarm(this);
	}

	private static final long INTERVAL_ALARM = AlarmManager.INTERVAL_HALF_DAY;

/*	private void setRecurringAlarm(Context context) {
		Intent alarm = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
	}*/

	public Class<? extends AccueilActivity> getAccueilActivity() {
		return TransportsMetz.class;
	}

	public boolean onOptionsItemSelected(MenuItem item, Activity activity, ActivityHelper helper) {

		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				helper.goHome();
				return true;
			case R.id.menu_bus_favoris:
				activity.startActivity(new Intent(activity, TabFavoris.class));
				return true;
			//case R.id.menu_velo_favoris:
			//	activity.startActivity(new Intent(activity, ListStationsFavoris.class));
			//	return true;
			//case R.id.menu_prefs:
			//	activity.startActivity(new Intent(activity, PreferencesMetz.class));
			//	return true;
			//case R.id.menu_refresh:
			//	if (activity instanceof Refreshable) {
			//		((Refreshable) activity).refresh();
			//	}
			//	return true;
			default:
				return false;
		}

	}

}
