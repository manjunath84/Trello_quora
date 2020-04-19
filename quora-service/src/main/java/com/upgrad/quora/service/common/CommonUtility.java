package com.upgrad.quora.service.common;

import com.upgrad.quora.service.dao.UserAuthDao;
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

    /**
     * This method authorizes the token from header.
     *
     * @param authToken               The JWT access token of the user
     * @param signoutExceptionMessage The Exception message to show for Exception ATHR-002
     * @return UserEntity The user data of the authorized user
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     */
    public UserEntity getAuthenticatedUser(final String authToken, final String signoutExceptionMessage) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authToken);
        //Check if the userAuthToken is not present in the database
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //User is signed out if either JWT token is expired or user has logged out
        final ZonedDateTime now = ZonedDateTime.now();
        if (userAuthTokenEntity.getExpiresAt().isBefore(now) || userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", signoutExceptionMessage);
        }

        return userAuthTokenEntity.getUser();
    }

}
