package com.jadaptive.mail.bounce.mailet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.james.core.MailAddress;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BounceMailRemoteUpdate extends GenericMailet {

	private static final Logger log = LoggerFactory.getLogger(BounceMailRemoteUpdate.class);
	

	private MailetConfig config;
	private String remoteUrl;
	private String paramName;
	
	private Map<String,String> remoteUrlDomains = new HashMap<>();
	
	private RemoteUpdater remoteUpdater;
	
	public BounceMailRemoteUpdate() {
		this.remoteUpdater = new RemoteUpdater();
	}

	@Override
	public String getMailetInfo() {
		return "Jadpative Bounce Mail Remote Update Mailet";
	}

	@Override
	public String getMailetName() {
		return config.getMailetName();
	}

	@Override
	public void init(MailetConfig newConfig) throws MessagingException {
		super.init(newConfig);
		this.config = newConfig;
		this.remoteUrl = getInitParameter("defaultUrl");
		this.paramName = getInitParameter("paramName", "email");
		
		log.info("The default remote url is {}", this.remoteUrl);
		log.info("The param name is {}", this.paramName);
		
		for(int i=1;;i++) {
			String url = getInitParameter("domainUrl." + i);
			if(url==null) {
				break;
			}
			
			String domain = StringUtils.substringBefore(url, "=");
			String alternativeUrl = StringUtils.substringAfter(url, "=");
			remoteUrlDomains.put(domain, alternativeUrl);
			
			log.info("Alternative url for domain {} is {}", domain, alternativeUrl);
		}
		
	}

	@Override
	public void service(Mail mail) throws MessagingException {

		if (StringUtils.isBlank(this.remoteUrl) 
				|| StringUtils.isBlank(this.paramName)) {
			throw new IllegalStateException("Remote URL to update is not set or the param name.");
		}
		
		
		String emailSender = mail.getMaybeSender().asOptional().map(MailAddress::asString).orElse("NA");
		log.info("Email bounced for user {}", emailSender);

		String domain = StringUtils.substringAfter(emailSender, "@");
		String url = remoteUrlDomains.get(domain);
		if(url==null) {
			url = remoteUrl;
		}
		Collection<MailAddress> recipients = mail.getRecipients();
		if (!recipients.isEmpty()) {
				this.remoteUpdater.service(url, this.paramName, recipients);
		}

	}

	@Override
	public void destroy() {
		super.destroy();
		this.remoteUpdater.destroy();
	}
	
}
