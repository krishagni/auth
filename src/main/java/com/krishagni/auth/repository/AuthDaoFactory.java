package com.krishagni.auth.repository;

public interface AuthDaoFactory {
	AuthDao getAuthDao();

	ForgotPasswordTokenDao getForgotPasswordTokenDao();

	UserApiCallLogDao getUserApiCallLogDao();
}
