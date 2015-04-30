package survfate.poesimpleguild;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Account {

	String profile;
	Element details;
	Date joined;
	Date lastVisited;
	Date lastLadderOnline;
	boolean poeTradeOnline = false;
	SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");;

	public Account(String profile) throws IOException, ParseException {
		this.profile = profile;
		Document jsoupDoc = Jsoup
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

	public Date getLastLadderOnline() throws IOException {

		lastLadderOnline = new Date(0);

		URL url = new URL(
				"http://api.exiletools.com/ladder?league=all&account="
						+ profile);
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
			// Account without Ladder data
			reader.close();
			input.close();

			return lastLadderOnline;
		}

		for (String charName : jsonObject.names()) {
			JsonValue jsonValue = jsonObject.get(charName);
			String epochSecond = "0";

			if (jsonValue.asObject().get("lastOnline") != null) {
				epochSecond = jsonValue.asObject().get("lastOnline").asString();
			}

			Date lastOnlineHuman = Date.from(Instant.ofEpochSecond(Integer
					.parseInt(epochSecond))); // Epoch Timestamp to Human

			if (lastOnlineHuman.after(lastLadderOnline)) {
				lastLadderOnline = lastOnlineHuman;
			}
		}
		reader.close();
		input.close();

		return lastLadderOnline;
	}

	public boolean getPoeTradeOnlineStatus() throws IOException {
		String[] leagues = { "Torment/Bloodlines", "Torment/Bloodlines HC",
				"Standard", "Hardcore" };

		for (String league : leagues) {
			Document jsoupDoc = Jsoup.connect("http://poe.trade/search")
					.data("league", league, "seller", profile, "online", "x")
					.timeout(5000).post();

			if (!jsoupDoc.getElementsByClass("search-results-block").text()
					.equals("")) {
				poeTradeOnline = true;
				break;
			}
		}
		return poeTradeOnline;
	}

	public static void main(String[] args) throws IOException, ParseException {
		Account account = new Account("surVfate");
		// System.out.println(account.profile);
		System.out.println(account.getLastLadderOnline());
	}
}
