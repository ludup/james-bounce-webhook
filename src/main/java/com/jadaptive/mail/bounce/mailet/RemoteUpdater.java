package com.jadaptive.mail.bounce.mailet;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.james.core.MailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteUpdater {

	private final class RemoteUpdateCall implements Callback {
		private final URL remoteUrl;

		private RemoteUpdateCall(URL remoteUrl) {
			this.remoteUrl = remoteUrl;
		}

		@Override
		public void onResponse(Call call, Response response) throws IOException {
			try {
				int statusCode = response.code();
				if (200 == statusCode) {
					log.info("Call to remote url was a success with status code {}", statusCode);
				} else {
					log.warn("Call to remote url was a success, but code other than 200, with status code {}", statusCode);
				}
			} finally {
				response.close();
			}
		}

		@Override
		public void onFailure(Call call, IOException e) {
			log.error("Remote call failed with error for url {}.", remoteUrl, e);
		}
	}

	private static final Logger log = LoggerFactory.getLogger(RemoteUpdater.class);
	private final OkHttpClient client = new OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build();
	
	private Util util;
	
	public RemoteUpdater() {
		this.util = new Util();
	}
	
	public void service(String url, String paramName, Collection<MailAddress> recipients) {
		try {
			recipients.forEach(rec -> {
				String recipient = rec.asString();
				log.info("Updating URL for recipient {}", recipient);
				URL remoteUrl = util.getEncodedUrl(url, paramName, recipient);
				Request request = new Request.Builder().url(remoteUrl).build();
				client.newCall(request).enqueue(new RemoteUpdateCall(remoteUrl));
			});
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public void destroy() {
		log.info("Closing ok http resources.");
		client.dispatcher().executorService().shutdown();
		client.connectionPool().evictAll();
		log.info("ok http resources close done.");
	}
}
