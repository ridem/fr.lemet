package fr.lemet.transportscommun;

import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.activity.commun.BaseActivity.BaseListActivity;

public interface DonnesSpecifiques {

	public String getApplicationName();

	public int getCompactLogo();

	public Class<?> getDrawableClass();

	public int getIconeLigne();

	public Class<? extends BaseActivity.BaseListActivity> getDetailArretClass();

}
