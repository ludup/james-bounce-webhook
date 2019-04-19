package com.jadaptive.mail.bounce.mailet;

import java.net.URL;

import org.apache.http.client.utils.URIBuilder;

public class Util {

	public URL getEncodedUrl(String url, String paramName, String emailBounced) {
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameter(paramName, emailBounced);
			return uriBuilder.build().toURL();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
