package fr.lemet.application.activity.alerts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Auteur: ridem
 * Date: 09/07/13
 * Time: 12:48
 */

public class GetPerturbations {

    public String data() {
        String data = "";
        Document doc = null;
        try {
            doc = Jsoup.connect("http://m.tcrm-metz.fr/trafic/perturbation").get();
            Elements ele = doc.select("div#content");
            Element head = doc.head();
            head.append("<link rel=\"stylesheet\" href=\"http://m.tcrm-metz.fr/extension/tcrm_mobile/design/tcrm_mobile_v1/stylesheets/master.css\">");
            System.out.println(head.html());
            data = "<html><head>"+head.html()+"</head><body>" + ele.html() + "</body></html>";
            //System.out.println(data);
        } catch (Exception e) {
            return e.toString();
        }

        return data;
    }
}
