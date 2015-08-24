package survfate.poesimpleguild;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Account {
	/* Parameters */
	private String profile;
	private String status = "";
	private Element details;
	private Date joined;
	private Date lastVisited;
	private int forumPosts;
	private Date lastLadderOnline;
	private boolean poeTradeOnline = false;
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);;

	public Account(String profile) throws IOException, ParseException {
		// Encode the profile parameter
		this.profile = java.net.URLEncoder.encode(profile, "UTF-8");

		// Retry when timeout
		for (int i = 0; i < 10; i++) {
			try {
				Document jsoupDoc = Jsoup.connect("http://www.pathofexile.com/account/view-profile/" + profile)
						.timeout(5000).get();
				details = jsoupDoc.getElementsByClass("details").first();
				break;
			} catch (java.net.SocketTimeoutException e) {
				// e.printStackTrace();
				if (i == 2)
					throw e;
				else
					continue;
			}
		}

		this.joined = sDateFormat.parse(details.child(3).childNode(3).toString().trim());
		this.lastVisited = sDateFormat.parse(details.child(4).childNode(3).toString().trim());
		this.forumPosts = Integer.parseInt(details.child(5).childNode(3).toString().trim());
	}

	/* Methods */
	// Return an Profile URL of an account
	public URL getURL() {
		try {
			return new URL("http://www.pathofexile.com/account/view-profile/" + this.profile);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Return a joined date of an account
	public Date getJoinedDate() {
		return this.joined;
	}

	// Return the last forum visited date of an account
	public Date getLastVisitedDate() {
		return this.lastVisited;
	}

	// Return the total forum posts of an account
	public int getForumPosts() {
		return this.forumPosts;
	}

	// Return the last ladder online date of an account from all the characters,
	// using api.exiletools.com
	public Date getLastLadderOnline() throws IOException {
		lastLadderOnline = new Date(0);

		URL url = new URL("http://api.exiletools.com/ladder?league=all&short=1&account=" + profile);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

			return this.lastLadderOnline;
		}

		// Find the lastest date
		for (String charName : jsonObject.names()) {
			int epochSecond = 0;
			JsonValue jsonValue = jsonObject.get(charName);
			if (jsonValue.asObject().get("lastOnline") != null) {
				epochSecond = Integer.parseInt(jsonValue.asObject().get("lastOnline").asString());
			}

			// Epoch Timestamp to Human
			Date lastOnlineHuman = Date.from(Instant.ofEpochSecond(epochSecond));

			if (lastOnlineHuman.after(lastLadderOnline)) {
				this.lastLadderOnline = lastOnlineHuman;
			}
		}
		reader.close();
		input.close();

		return this.lastLadderOnline;
	}

	// Return the Poe.Trade Online status of an account, using a brute-force
	// query trick (bad speed)
	public boolean getPoeTradeOnlineStatus() throws IOException {
		String[] leagues = { "Standard", "Hardcore", "Warbands", "Tempest" };

		for (String league : leagues) {
			Document jsoupDoc = Jsoup.connect("http://poe.trade/search")
					.data("league", league, "seller", profile, "online", "x").timeout(5000).post();

			if (!jsoupDoc.getElementsByClass("search-results-block").text().equals("")) {
				this.poeTradeOnline = true;
				break;
			}
		}
		return this.poeTradeOnline;
	}

	// Return a String contain Supporter Tags of an account
	public String getSupporterTagKeys() {
		String tagKeys = "";

		for (Element tag : details.getElementsByAttributeValueContaining("class", "roleLabel")) {
			if (tag.attr("class").equals("roleLabel in-alpha")) {
				status = "Alpha Member";
				continue;
			} else if (tag.attr("class").equals("roleLabel onProbation")) {
				status = "On Probation";
				continue;
			} else if (tag.attr("class").equals("roleLabel banned")) {
				status = "Banned";
				continue;
			}
			String imgSrc = tag.child(0).attr("src");
			String imgFileName = imgSrc.substring(imgSrc.lastIndexOf('/') + 1, imgSrc.length());
			String imgKey = imgFileName.substring(0, imgFileName.lastIndexOf('.')).toLowerCase().replace('-', '_');
			tagKeys += imgKey + " ";
		}
		return tagKeys.trim();
	}

	// Return "On Probation" or "Banned" status of an account if there is one
	// Must be called after getSupporterTagKeys()
	public String getStatus() {
		if (!this.status.equals(""))
			return " (" + this.status + ")";
		else
			return this.status;
	}

	// Return a list of currently active leagues, currently not used
	public String[] getActiveLeagues() throws IOException {
		ArrayList<String> activeLeagues = new ArrayList<String>();

		URL url = new URL("http://api.exiletools.com/ladder?activeleagues=1");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		urlConnection.setConnectTimeout(10 * 1000);
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		InputStream input = urlConnection.getInputStream();
		InputStreamReader reader = new InputStreamReader(input);
		JsonObject jsonObject = JsonObject.readFrom(reader);

		for (String leagueName : jsonObject.names()) {
			activeLeagues.add(jsonObject.get(leagueName).asString());
		}

		return activeLeagues.toArray(new String[activeLeagues.size()]);
	}
}
