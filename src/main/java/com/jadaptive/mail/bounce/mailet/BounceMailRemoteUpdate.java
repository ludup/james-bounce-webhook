package com.jadaptive.mail.bounce.mailet;

import java.util.Collection;

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
	private String remoteUrlToUpdate;
	private String urlEmailParamName;
	
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
		this.remoteUrlToUpdate = getInitParameter("remoteUrlToUpdate");
		this.urlEmailParamName = getInitParameter("urlEmailParamName");
		
		log.info("The remote url to update is {}", this.remoteUrlToUpdate);
		log.info("The url email param name is {}", this.urlEmailParamName);
	}

	@Override
	public void service(Mail mail) throws MessagingException {

		if (StringUtils.isBlank(this.remoteUrlToUpdate) 
				|| StringUtils.isBlank(this.urlEmailParamName)) {
			throw new IllegalStateException("Remote URL to update is not set or the param name.");
		}
		
		
		String emailSender = mail.getMaybeSender().asOptional().map(MailAddress::asString).orElse("NA");
		log.info("Email bounced for user {}", emailSender);

		Collection<MailAddress> recipients = mail.getRecipients();
		if (!recipients.isEmpty()) {
				this.remoteUpdater.service(this.remoteUrlToUpdate, this.urlEmailParamName, recipients);
		}

	}

	@Override
	public void destroy() {
		super.destroy();
		this.remoteUpdater.destroy();
	}
	
}
