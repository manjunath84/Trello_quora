package com.upgrad.quora.service.common;

import com.upgrad.quora.service.entity.UserAuthDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonUtility {
    @Autowired
    private UserAuthDao userAuthDao;

    public UserEntity getAutheticatedUser(final String AuthorizationToken, final String SignoutExceptionMessage) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(AuthorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        //User is signed out if either JWT token is expired or user has logged out
        if (userAuthTokenEntity.getExpiresAt().isBefore(now) &&
                (userAuthTokenEntity.getLogoutAt() != null)) {
            throw new AuthorizationFailedException("ATHR-002", SignoutExceptionMessage);
        }
        return userAuthTokenEntity.getUser();
    }
}
