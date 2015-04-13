package com.synaptix.toast.plugin.synaptix.runtime.service;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public final class ConnectionBuilder {

	private ConnectionBuilder() {

	}

	public static XMPPConnection connect() throws XMPPException {
		return connect("localhost");
	}

	public static XMPPConnection connect(final String adresseServeur) throws XMPPException {
		final XMPPConnection xmppConnection = getXMPPConnection(adresseServeur);
		xmppConnection.connect();
		xmppConnection.login(getUsername(), getPassword());
		return xmppConnection;
	}

	public static XMPPConnection getXMPPConnection(final String adresseServeur) {
		return new XMPPConnection(getConnectionConfiguration(adresseServeur));
	}

	public static ConnectionConfiguration getConnectionConfiguration(final String adresseServeur) {
		final ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(adresseServeur);
		connectionConfiguration.setCompressionEnabled(false);
		connectionConfiguration.setSASLAuthenticationEnabled(false);
		return connectionConfiguration;
	}

	public static String getUsername() {
		return "ROOT";
	}

	public static String getPassword() {
		return "kao2k2gy";
	}
}