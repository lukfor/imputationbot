package genepi.imputationbutler;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

import genepi.io.FileUtil;

public class ImputationServer {

	public static final String DOCKER_IMAGE = "genepi/imputationserver:v1.2.7";

	public static File IMPUTATIONSERVER_WORKSPACE = new File("imputationserver");

	public static File IMPUTATIONSERVER_DATABASE = new File("imputationserver/database");

	public static File IMPUTATIONSERVER_JOBS = new File("imputationserver/jobs");

	private static ImputationServer instance;

	private String url;

	private String adminToken;

	private ImputationServer() {

		// Delete database and jobs
		if (IMPUTATIONSERVER_DATABASE.exists()) {
			FileUtil.deleteDirectory(IMPUTATIONSERVER_DATABASE);
		}

		if (IMPUTATIONSERVER_JOBS.exists()) {
			FileUtil.deleteDirectory(IMPUTATIONSERVER_JOBS);
		}

		GenericContainer imputationserver = new GenericContainer(DOCKER_IMAGE);
		imputationserver.withExposedPorts(80);
		imputationserver.withPrivilegedMode(true);
		// avoid clone in test setup only (wehen apps already installed!)
		//imputationserver.addEnv("CLOUDGENE_REPOSITORY", "");
		imputationserver.withStartupTimeout(Duration.ofMinutes(5));
		//imputationserver.withFileSystemBind(IMPUTATIONSERVER_WORKSPACE.getAbsolutePath(), "/data/",
		//		BindMode.READ_WRITE);
		Consumer l = new Consumer() {
			@Override
			public void accept(Object arg0) {
				System.out.println(((OutputFrame) arg0).getUtf8String());

			}
		};
		imputationserver.withLogConsumer(l);

		System.out.println("Starting imputationserver...");
		imputationserver.start();
		url = "http://" + imputationserver.getContainerIpAddress() + ":" + imputationserver.getMappedPort(80);
		try {
			//wait until cloudgene is started.
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Imputationserver started on " + url);

		try {
			adminToken = createTokenForUser("admin", "admin1978");
			System.out.println("Admin token created.");
		} catch (Exception e) {
			e.printStackTrace();
			adminToken = null;
		}
	}

	public String getUrl() {
		return url;
	}

	public static ImputationServer getInstance() {
		if (instance == null) {
			instance = new ImputationServer();
		}
		return instance;
	}

	public String getAdminToken() {
		return adminToken;
	}

	public String revokeTokenForUser(String user) {
		return "";
	}

	public String createTokenForUser(String user, String password) throws Exception {

		LoginToken loginToken = login(user, password);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost(url + "/api/v2/users/" + user + "/api-token");
		httppost.addHeader("Cookie", loginToken.getCookie());
		httppost.addHeader("X-CSRF-Token", loginToken.getCsrfToken());

		List<NameValuePair> params = new Vector<NameValuePair>();
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		String content = EntityUtils.toString(responseEntity);
		JSONObject object = new JSONObject(content);

		return object.getString("token");

	}

	public LoginToken login(String username, String password) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url + "/login");

		List<NameValuePair> params = Form.form().add("loginUsername", username).add("loginPassword", password).build();

		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();

		Header cookie = response.getFirstHeader("Set-Cookie");

		String content = EntityUtils.toString(responseEntity);
		JSONObject object = new JSONObject(content);

		String csrfToken = object.getString("csrf");

		LoginToken loginToken = new LoginToken();
		loginToken.setCookie(cookie.getValue());
		loginToken.setCsrfToken(csrfToken);

		return loginToken;

	}

	class LoginToken {

		private String cookie;

		private String csrfToken = "";

		public String getCookie() {
			return cookie;
		}

		public void setCookie(String cookie) {
			this.cookie = cookie;
		}

		public String getCsrfToken() {
			return csrfToken;
		}

		public void setCsrfToken(String csrfToken) {
			this.csrfToken = csrfToken;
		}

	}

}
