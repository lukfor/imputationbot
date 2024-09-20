package genepi.imputationbot.client;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.json.JSONObject;

public class CloudgeneApiToken {

	private JSONObject token;

	public CloudgeneApiToken(JSONObject token) {
		this.token = token;
	}

	public boolean isValid() {
		if (token == null) {
			return false;
		}

		if (token.has("valid")) {
			return token.getBoolean("valid");
		} else {
			return false;
		}
	}

	public boolean isExpired() {
		if (token.has("expire")) {
			return parseExpire(token.getString("expire")).getTime() < System.currentTimeMillis();
		} else {
			return true;
		}
	}

	public Date getExpire() {
		if (token.has("expire")) {
			return parseExpire(token.getString("expire"));
		} else {
			return null;
		}
	}

	public int getExpiresInDays() {
		long startTime = getExpire().getTime();
		long endTime = System.currentTimeMillis();
		long diffTime = startTime - endTime;
		long diffDays = diffTime / (1000 * 60 * 60 * 24);
		return (int) diffDays;
	}

	public String getName() {
		return token.getString("name");
	}

	public String getUsername() {
		return token.getString("username");
	}

	@Override
	public String toString() {
		if (token.has("message")) {
			return token.getString("message");
		} else {
			return "The provided API token is malformed.";
		}
	}

	private Date parseExpire(String expire) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(expire, formatter);
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

}
