
package com.krishagni.auth.services;

import java.util.List;

import com.krishagni.auth.events.AuthDomainDetail;
import com.krishagni.auth.events.AuthDomainSummary;
import com.krishagni.auth.events.ListAuthDomainCriteria;

public interface AuthDomainService {
	List<AuthDomainSummary> getDomains(ListAuthDomainCriteria criteria);
	
	AuthDomainDetail registerDomain(AuthDomainDetail authDomainDetail);
	
	AuthDomainDetail updateDomain(AuthDomainDetail authDomainDetail);
}
