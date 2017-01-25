package com.krishagni.auth.events;

import java.util.List;
import java.util.stream.Collectors;

import com.krishagni.auth.domain.AuthDomain;

public class AuthDomainSummary {
	private Long id;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static AuthDomainSummary from(AuthDomain domain) {
		AuthDomainSummary summary = new AuthDomainSummary();
		summary.setId(domain.getId());
		summary.setName(domain.getName());
		return summary;
	}
	
	public static List<AuthDomainSummary> from(List<AuthDomain> domains) {
		return domains.stream().map(AuthDomainSummary::from).collect(Collectors.toList());
	}
}
