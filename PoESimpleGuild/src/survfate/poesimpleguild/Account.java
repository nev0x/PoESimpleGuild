package survfate.poesimpleguild;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Account {

	String profile;
	Document jsoupDoc;
	Element details;
	Date joined;
	Date lastVisited;
	Date lastLadderOnline;
	// Date XYZOnline;
	SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");;

	public Account(String profile) throws IOException, ParseException {
		this.profile = profile;
		jsoupDoc = Jsoup
				.connect(
						"http://www.pathofexile.com/account/view-profile/"
								+ profile).timeout(5000).get();
		details = jsoupDoc.getElementsByClass("details").first();
	}

	public Date getJoinedDate() throws ParseException {
		joined = sDateFormat.parse(details.child(3).childNode(3).toString()
				.trim());
		return joined;
	}

	public Date getLastVisitedDate() throws ParseException {
		lastVisited = sDateFormat.parse(details.child(4).childNode(3)
				.toString().trim());
		return lastVisited;
	}

	@SuppressWarnings("deprecation")
	public Date getLastLadderOnline() throws IOException {
		String leagues[] = { "Standard", "Hardcore", "Torment", "Bloodlines" };
		lastLadderOnline = new Date("Wed Dec 31 19:00:00 1969 EST");

		for (String league : leagues) {
			URL url = new URL("http://poe.pwx.me/api/ladder?league=" + league
					+ "&account=" + profile);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			HttpURLConnection.setFollowRedirects(false);
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			InputStream input = urlConnection.getInputStream();
			InputStreamReader reader = new InputStreamReader(input);

			JsonObject jsonObject = null;
			try {
				jsonObject = JsonObject.readFrom(reader);
			} catch (com.eclipsesource.json.ParseException e) {
				continue; // If no League Ladder data found then just ignore
			}

			for (String charName : jsonObject.names()) {
				JsonValue jsonValue = jsonObject.get(charName);

				Date lastOnlineHuman = new Date(jsonValue.asObject()
						.get("lastOnlineHuman").asString());
				if (lastOnlineHuman.after(lastLadderOnline)) {
					lastLadderOnline = lastOnlineHuman;
				}
			}
			reader.close();
			input.close();
		}
		return lastLadderOnline;
	}
}
