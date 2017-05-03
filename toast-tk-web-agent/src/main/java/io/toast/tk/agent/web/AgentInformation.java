package io.toast.tk.agent.web;

public class AgentInformation {

	
	private String host;
	private String token;


	protected AgentInformation(){
		
	}
	
	public AgentInformation(String localAddress, String token) {
		this.host = localAddress;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public String getHost() {
		return host;
	}

}
