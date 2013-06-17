package fr.lemet.application.activity.plans;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import fr.lemet.application.R;

public class PlanReseau extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planweb);
        WebView planweb = (WebView)findViewById(R.id.webview);
        //setTitle("Plan du réseau");
        //planweb.setPadding(0, 0, 0, 0);
        //planweb.setInitialScale(getScale());
        planweb.setInitialScale(50);
        WebSettings planSettings = planweb.getSettings();
        planSettings.setSupportZoom(true);
        planSettings.setBuiltInZoomControls(true);
        //planSettings.setLoadWithOverviewMode(true);
        planSettings.setUseWideViewPort(true);
        //planSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        planweb.loadUrl("file:///android_asset/tcrm.jpg");
}
}
