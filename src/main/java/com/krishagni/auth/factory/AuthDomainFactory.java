package com.krishagni.auth.factory;

import com.krishagni.auth.domain.AuthDomain;
import com.krishagni.auth.events.AuthDomainDetail;

public interface AuthDomainFactory {
	AuthDomain createDomain(AuthDomainDetail domainDetails);
}
