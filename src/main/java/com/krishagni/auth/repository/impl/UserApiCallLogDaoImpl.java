
package com.krishagni.auth.repository.impl;

import java.util.Date;
import java.util.List;

import com.krishagni.auth.domain.UserApiCallLog;
import com.krishagni.auth.repository.UserApiCallLogDao;
import com.krishagni.commons.repository.AbstractDao;

public class UserApiCallLogDaoImpl extends AbstractDao<UserApiCallLog> implements UserApiCallLogDao {

	@Override
	@SuppressWarnings("unchecked")
	public Date getLatestApiCallTime(Long userId, String token) {
		List<Date> result = getCurrentSession().getNamedQuery(GET_LATEST_API_CALL_TIME)
			.setParameter("userId", userId)
			.setParameter("authToken", token)
			.list();
		return result.isEmpty() ? null : result.get(0);
	}
	
	private static final String FQN = UserApiCallLog.class.getName();
	
	private static final String GET_LATEST_API_CALL_TIME = FQN + ".getLatestApiCallTime";
}
