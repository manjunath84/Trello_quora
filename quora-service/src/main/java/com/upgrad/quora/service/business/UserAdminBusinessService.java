package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteUser(final String userUuid, final String authToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        UserEntity userEntity = userAuthTokenEntity.getUser();
        String userRole = userEntity.getRole();
        if (!userRole.equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity user = userDao.getUserByUuid(userUuid);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return userDao.deleteUserByUuid(userUuid);

    }
}
