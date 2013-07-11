package fr.lemet.application.activity.alerts;

import android.os.Bundle;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import fr.lemet.application.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity;

public class Perturbations extends BaseActivity.BaseSimpleActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planweb);

        //planweb.loadUrl("http://m.tcrm-metz.fr/trafic/perturbation");
        WebView planweb = (WebView)findViewById(R.id.webview);
        planweb.setBackgroundColor(0);
        planweb.loadUrl("file:///android_asset/chargement2.html");
        //planweb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        Thread t = new Thread() {
            public volatile String data;
            public void run() {
                Document doc=null;
                Document doc2=null;
                try {
                    WebView planweb = (WebView)findViewById(R.id.webview);
                    doc = Jsoup.connect("http://m.tcrm-metz.fr/trafic/perturbation").get();
                    Elements ele = doc.select("div#content");
                    //Element head = doc.head();
                    //head.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"http://m.tcrm-metz.fr/extension/tcrm_mobile/design/tcrm_mobile_v1/stylesheets/screen.css\">");
                    //System.out.println(head.html());
                    String data = "<html><head>"+doc.head().html()+"</head><body>"+ele.html()+"</body></html>";
                    String mime = "text/html";
                    String encoding = "utf-8";
                    //System.out.println(data);
                    planweb.loadDataWithBaseURL("http://m.tcrm-metz.fr/",data,mime,encoding,"");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public String getValue() {
                return data;
            }
        };
        t.start();



        //planweb.loadData(html, mime, encoding);
    }



}