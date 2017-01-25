package com.krishagni.auth.repository.impl;

import org.hibernate.SessionFactory;

import com.krishagni.auth.repository.AuthDao;
import com.krishagni.auth.repository.AuthDaoFactory;
import com.krishagni.auth.repository.ForgotPasswordTokenDao;
import com.krishagni.auth.repository.UserApiCallLogDao;
import com.krishagni.commons.repository.AbstractDao;

public class AuthDaoFactoryImpl implements AuthDaoFactory {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public AuthDao getAuthDao() {
		AuthDaoImpl dao = new AuthDaoImpl();
		setSessionFactory(dao);
		return dao;
	}

	@Override
	public ForgotPasswordTokenDao getForgotPasswordTokenDao() {
		ForgotPasswordtokenDaoImpl dao = new ForgotPasswordtokenDaoImpl();
		setSessionFactory(dao);
		return dao;
	}

	@Override
	public UserApiCallLogDao getUserApiCallLogDao() {
		UserApiCallLogDaoImpl dao = new UserApiCallLogDaoImpl();
		setSessionFactory(dao);
		return dao;
	}

	private void setSessionFactory(AbstractDao<?> dao) {
		dao.setSessionFactory(sessionFactory);
	}
}