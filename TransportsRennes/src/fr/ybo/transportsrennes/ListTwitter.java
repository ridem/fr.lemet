package fr.ybo.transportsrennes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import fr.ybo.transportsrennes.activity.MenuAccueil;
import fr.ybo.transportsrennes.adapters.TwitterAdapter;
import fr.ybo.transportsrennes.util.LogYbo;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListTwitter extends MenuAccueil.ListActivity {

	private static TwitterFactory twitterFactory = null;

	synchronized protected TwitterFactory getFactory() {
		if (twitterFactory == null) {
			twitterFactory = new TwitterFactory();
		}
		return twitterFactory;
	}

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy à hh:mm");

	private static final LogYbo LOG_YBO = new LogYbo(ListTwitter.class);

	private ProgressDialog myProgressDialog;

	private final List<Status> allStatus = Collections.synchronizedList(new ArrayList<Status>());

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste);
		setListAdapter(new TwitterAdapter(this, R.layout.onetwitter, allStatus));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		myProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialogRequeteTwitter), true);
		new AsyncTask<Void, Void, Void>() {

			private boolean erreur = false;
			private boolean rateLimit = false;
			private String dateReset = null;

			@Override
			protected Void doInBackground(final Void... pParams) {
				Twitter twitter = getFactory().getInstance();
				try {
					allStatus.addAll(twitter.getUserTimeline("@starbusmetro"));
				} catch (TwitterException e) {
					if (e.exceededRateLimitation()) {
						rateLimit = true;
						dateReset = SDF.format(e.getRateLimitStatus().getResetTime());
					}
					LOG_YBO.erreur("Erreur lors de la récupération des status twitter", e);
					erreur = true;
				}
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void onPostExecute(final Void pResult) {
				super.onPostExecute(pResult);
				((TwitterAdapter) getListAdapter()).notifyDataSetChanged();
				myProgressDialog.dismiss();
				if (rateLimit) {
					Toast.makeText(ListTwitter.this,
							"Le nombre de demande à twitter pour votre IP a été dépassé (150/h). Cette limitation sera remise à zero le " + dateReset,
							Toast.LENGTH_LONG).show();
				} else if (erreur) {
					Toast.makeText(ListTwitter.this, "Erreur lors de la récupération des messages twitter.", Toast.LENGTH_LONG).show();
					ListTwitter.this.finish();
				}
			}
		}.execute();
	}

}