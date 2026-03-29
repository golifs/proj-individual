package pt.unl.fct.di.adc.firstwebapp.util.auth;

import java.util.UUID;

public class AuthToken {

	public static final long EXPIRATION_TIME = 900; // 900 seconds
	
	public String username;
	public String tokenId;
	public String role;
	public long issuedAt;
	public long expiresAt;
	
	public AuthToken() { }
	
	public AuthToken(String username, String role) {
		this.username = username;
		this.tokenId = UUID.randomUUID().toString();
		this.role = role;
		this.issuedAt = System.currentTimeMillis() / 1000;
		this.expiresAt = this.issuedAt + EXPIRATION_TIME;
	}
	
}
