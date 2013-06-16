package fr.lemet.transportscommun.activity.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity.BasePreferenceActivity;

public abstract class AbstractPreferences extends BaseActivity.BasePreferenceActivity implements OnSharedPreferenceChangeListener {


	protected abstract int getXmlPreferences();

	protected abstract void setupActionBar();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		AbstractTransportsApplication.majTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		setupActionBar();
		addPreferencesFromResource(getXmlPreferences());
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if ((AbstractTransportsApplication.getDonnesSpecifiques().getApplicationName() + "_choixTheme").equals(key)) {
			restart();
		} else if ((AbstractTransportsApplication.getDonnesSpecifiques().getApplicationName() + "_debug").equals(key)) {
			AbstractTransportsApplication.setDebug(sharedPreferences.getBoolean(AbstractTransportsApplication
					.getDonnesSpecifiques().getApplicationName() + "_debug", false));
		}
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	public void restart() {
		startActivity(new Intent(this, this.getClass()));
		finish();
	}

}
