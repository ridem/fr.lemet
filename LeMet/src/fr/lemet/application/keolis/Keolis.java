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
package fr.lemet.application.keolis;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.lemet.application.keolis.modele.Answer;
import fr.lemet.application.keolis.modele.ParametreUrl;
import fr.lemet.application.keolis.modele.bus.Alert;
import fr.lemet.application.keolis.modele.bus.Departure;
import fr.lemet.application.keolis.modele.bus.ParkRelai;
import fr.lemet.application.keolis.modele.bus.PointDeVente;
import fr.lemet.application.keolis.modele.bus.ResultDeparture;
import fr.lemet.application.keolis.modele.velos.Station;
import fr.lemet.application.keolis.xml.sax.GetAlertsHandler;
import fr.lemet.application.keolis.xml.sax.GetDeparturesHandler;
import fr.lemet.application.keolis.xml.sax.GetParkRelaiHandler;
import fr.lemet.application.keolis.xml.sax.GetPointDeVenteHandler;
import fr.lemet.application.keolis.xml.sax.GetStationHandler;
import fr.lemet.application.keolis.xml.sax.KeolisHandler;
import fr.lemet.application.util.HttpUtils;
import fr.lemet.transportscommun.donnees.modele.ArretFavori;
import fr.lemet.transportscommun.util.ErreurReseau;
import fr.lemet.transportscommun.util.LogYbo;

/**
 * Classe d'accés aux API Keolis. Cette classe est une singletton.
 *
 * @author ybonnel
 */
public final class Keolis {

    private static final LogYbo LOG_YBO = new LogYbo(Keolis.class);

    /**
     * Instance du singletton.
     */
    private static Keolis instance;

    /**
     * URL d'accés au API Keolis.
     */
    private static final String URL = "http://data.keolis-rennes.com/xml/";

    /**
     * Version.
     */
    private static final String VERSION = "2.0";

    /**
     * Clé de l'application.
     */
    private static final String KEY = "G7JE45LI1RK3W1P";
    /**
     * Commande pour récupérer les stations.
     */
    private static final String COMMANDE_STATIONS = "getbikestations";
    /**
     * Commande pour récupérer les alerts.
     */
    private static final String COMMANDE_ALERTS = "getlinesalerts";
    /**
     * Commande pour récupérer les Park relais.
     */
    private static final String COMMANDE_PARK_RELAI = "getrelayparks";
    /**
     * Commande pour récupérer les points de vente.
     */
    private static final String COMMANDE_POS = "getpos";

	private static final String COMMANDE_DEPARTURE = "getbusnextdepartures";

	private static final String VERSION_DEPARTURE = "2.1";

    /**
     * Retourne l'instance du singletton.
     *
     * @return l'instance du singletton.
     */
    public static synchronized Keolis getInstance() {
        if (instance == null) {
            instance = new Keolis();
        }
        return instance;
    }

    /**
     * Constructeur privé.
     */
    private Keolis() {
    }

    /**
     * @param <ObjetKeolis> type d'objet Keolis.
     * @param url           url.
     * @param handler       handler.
     * @return liste d'objets Keolis.
     * @throws fr.lemet.transportscommun.util.ErreurReseau    en cas d'erreur réseau.
     * @throws KeolisException en cas d'erreur lors de l'appel aux API Keolis.
     */
    @SuppressWarnings("unchecked")
    private <ObjetKeolis> List<ObjetKeolis> appelKeolis(String url, KeolisHandler<ObjetKeolis> handler)
            throws ErreurReseau {
        LOG_YBO.debug("Appel d'une API Keolis sur l'url '" + url + '\'');
        long startTime = System.nanoTime() / 1000;
        HttpClient httpClient = HttpUtils.getHttpClient();
        HttpUriRequest httpPost = new HttpPost(url);
        Answer<?> answer;
        try {
            HttpResponse reponse = httpClient.execute(httpPost);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            reponse.getEntity().writeTo(ostream);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(new ByteArrayInputStream(ostream.toByteArray()), handler);
            answer = handler.getAnswer();
        } catch (IOException socketException) {
            throw new ErreurReseau(socketException);
        } catch (SAXException saxException) {
            throw new ErreurReseau(saxException);
        } catch (ParserConfigurationException exception) {
            throw new KeolisException("Erreur lors de l'appel à l'API keolis", exception);
        }
        if (answer == null || answer.getStatus() == null || !"0".equals(answer.getStatus().getCode())) {
            throw new ErreurReseau();
        }
        long elapsedTime = System.nanoTime() / 1000 - startTime;
        LOG_YBO.debug("Réponse de Keolis en " + elapsedTime + "µs");
        return (List<ObjetKeolis>) answer.getData();
    }

    /**
     * Appel les API Keolis pour récupérer les alertes.
     *
     * @return les alertes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public Iterable<Alert> getAlerts() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_ALERTS), new GetAlertsHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer les stations.
     *
     * @param url url à appeler.
     * @return la liste des stations.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    private List<Station> getStation(String url) throws ErreurReseau {
        return appelKeolis(url, new GetStationHandler());
    }

    /**
     * Appel aux API Keolis pour récupérer une station à partir de son number.
     *
     * @param number numéro de la station.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    private Station getStationByNumber(String number) throws ErreurReseau {
        ParametreUrl[] params = {new ParametreUrl("station", "number"), new ParametreUrl("value", number)};
        List<Station> stations = getStation(getUrl(COMMANDE_STATIONS, params));
        if (stations.isEmpty()) {
            return null;
        }
        return stations.get(0);
    }

    /**
     * Appel aux API Keolis pour récupérer les stations à partir de leurs
     * numéros.
     *
     * @param numbers numéros des stations.
     * @return la station.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public Collection<Station> getStationByNumbers(Collection<String> numbers) throws ErreurReseau {
        Collection<Station> stations = new ArrayList<Station>(5);
        if (numbers.size() <= 2) {
            for (String number : numbers) {
                stations.add(getStationByNumber(number));
            }
        } else {
            for (Station station : getStations()) {
                if (numbers.contains(station.number)) {
                    stations.add(station);
                }
            }
        }
        return stations;
    }

    /**
     * Appel aux API Keolis pour récupérer les stations.
     *
     * @return la listes des stations.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<Station> getStations() throws ErreurReseau {
        return getStation(getUrl(COMMANDE_STATIONS));
    }

    /**
     * @return les parks relais.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<ParkRelai> getParkRelais() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_PARK_RELAI), new GetParkRelaiHandler());
    }

    /**
     * @return les points de ventes.
     * @throws ErreurReseau pour toutes erreurs réseaux.
     */
    public List<PointDeVente> getPointDeVente() throws ErreurReseau {
        return appelKeolis(getUrl(COMMANDE_POS), new GetPointDeVenteHandler());
    }

	public ResultDeparture getDepartues(ArretFavori favori) throws ErreurReseau {
		ParametreUrl[] params =
				{ new ParametreUrl("mode", "stopline"), new ParametreUrl("route][", favori.ligneId),
						new ParametreUrl("direction][", Integer.toString(favori.macroDirection)),
						new ParametreUrl("stop][", favori.arretId) };

		GetDeparturesHandler handler = new GetDeparturesHandler();
		List<Departure> departures = appelKeolis(getUrl(COMMANDE_DEPARTURE, params, VERSION_DEPARTURE), handler);
		return new ResultDeparture(departures, handler.getDateApi());
	}

    /**
     * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
     * commande à exécuter.
     *
     * @param commande commande à exécuter.
     * @return l'url.
     */
    private String getUrl(String commande) {
		return getUrl(commande, VERSION);
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter.
	 * 
	 * @param commande
	 *            commande à exécuter.
	 * @return l'url.
	 */
	private String getUrl(String commande, String version) {
        StringBuilder stringBuilder = new StringBuilder(URL);
		stringBuilder.append("?version=").append(version);
        stringBuilder.append("&key=").append(KEY);
        stringBuilder.append("&cmd=").append(commande);
        return stringBuilder.toString();
    }

    /**
     * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
     * commande à exécuter et d'un paramètre.
     *
     * @param commande commande à exécuter.
     * @param params   liste de paramètres de l'url.
     * @return l'url.
     */
    private String getUrl(String commande, ParametreUrl[] params) {
		return getUrl(commande, params, VERSION);
	}

	/**
	 * Permet de récupérer l'URL d'accés aux API Keolis en fonction de la
	 * commande à exécuter et d'un paramètre.
	 * 
	 * @param commande
	 *            commande à exécuter.
	 * @param params
	 *            liste de paramètres de l'url.
	 * @return l'url.
	 */
	private String getUrl(String commande, ParametreUrl[] params, String version) {
		StringBuilder stringBuilder = new StringBuilder(getUrl(commande, version));
        for (ParametreUrl param : params) {

            try {
                stringBuilder.append("&param[").append(param.getName()).append("]=").append(URLEncoder.encode(param.getValue(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new KeolisException("Erreur lors de la construction de l'URL", e);
            }
        }
        return stringBuilder.toString();
    }

}
