# james-bounce-webhook
A Mailet for Apache James that calls a configurable URL with the bounced email address.

This mailet was developed after we switched from Amazon SES to our own SMTP relay using Apache James. When James bounces an email a URL can be called to notify an external system. This is useful for marking email addresses as disabled to prevent further bounces.

# Installing

With Maven and a Java 8 JDK installed clone the git repository.

Build the project using

mvn clean package

Get the jar with dependencies from the target folder and place it in [james install]/conf/lib

To install the mailet, open file [james install]/conf/mailetcontainer.xml

Add the entry in processor "bounces" as the first entry as follows

<pre>
%lt;mailet match="All" class="com.jadaptive.mail.bounce.mailet.BounceMailRemoteUpdate"&gt;
%lt;remoteUrlToUpdate&gt;https://example.com/bounce</remoteUrlToUpdate&gt;
%lt;urlEmailParamName&gt;email&lt;/urlEmailParamName&gt;
%lt;/mailet&gt;
</pre>

The above configuration will send a HTTP GET request to https://example.com/bounce?email=<bounced_address>
