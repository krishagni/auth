package com.krishagni.auth.repository;

import java.util.Date;

import com.krishagni.auth.domain.UserApiCallLog;
import com.krishagni.commons.repository.Dao;

public interface UserApiCallLogDao extends Dao<UserApiCallLog> {
	Date getLatestApiCallTime(Long userId, String token);
}

