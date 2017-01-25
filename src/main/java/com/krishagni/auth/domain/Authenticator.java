
package com.krishagni.auth.domain;


public interface Authenticator {
	void authenticate(String username, String password);
}
