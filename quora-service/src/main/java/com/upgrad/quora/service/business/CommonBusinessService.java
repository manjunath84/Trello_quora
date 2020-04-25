package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    /**
     * This method fetches user details from the system.
     *
     * @param userId      The userId of the User to be fetched from database.
     * @param accessToken The JWT access token of the user passed in the request header.
     * @return UserEntity The user object with all the details
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws UserNotFoundException        This exception is thrown if the user is not present in the database for the requested userUuid
     */
    public UserEntity userProfile(String accessToken, String userId) throws AuthorizationFailedException, UserNotFoundException {

        final String signoutExceptionMessage = "User is signed out.Sign in first to get user details";
        UserEntity authenticatedUser = getAuthenticatedUser(accessToken, signoutExceptionMessage);

        //fetch user profile based on given user id.
        UserEntity fetchedUserProfile = userDao.getUserByUuid(userId);

        if (fetchedUserProfile == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return fetchedUserProfile;
    }

    /**
     * This method fetches the authenticated user entity based on given authToken.
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
