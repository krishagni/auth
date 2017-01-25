package com.krishagni.auth.repository.impl;

import java.util.List;

import com.krishagni.auth.domain.ForgotPasswordToken;
import com.krishagni.auth.repository.ForgotPasswordTokenDao;
import com.krishagni.commons.repository.AbstractDao;

public class ForgotPasswordtokenDaoImpl extends AbstractDao<ForgotPasswordToken> implements ForgotPasswordTokenDao {

	@SuppressWarnings("unchecked")
	@Override
	public ForgotPasswordToken getByToken(String token) {
		List<ForgotPasswordToken> tokens = getCurrentSession().getNamedQuery(GET_BY_TOKEN)
			.setParameter("token", token)
			.list();
		return tokens.isEmpty() ? null : tokens.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ForgotPasswordToken getByUserId(Long userId) {
		List<ForgotPasswordToken> tokens = getCurrentSession().getNamedQuery(GET_BY_USER_ID)
			.setParameter("userId", userId)
			.list();
		return tokens.isEmpty() ? null : tokens.get(0);
	}

	private static final String FQN = ForgotPasswordToken.class.getName();

	private static final String GET_BY_TOKEN = FQN + ".getByToken";

	private static final String GET_BY_USER_ID = FQN + ".getByUserId";
}
