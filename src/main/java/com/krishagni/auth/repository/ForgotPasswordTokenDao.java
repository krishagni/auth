package com.krishagni.auth.repository;

import com.krishagni.auth.domain.ForgotPasswordToken;
import com.krishagni.commons.repository.Dao;

public interface ForgotPasswordTokenDao extends Dao<ForgotPasswordToken> {
	ForgotPasswordToken getByToken(String token);

	ForgotPasswordToken getByUserId(Long userId);
}
