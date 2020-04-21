package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.CommonUtility;
import com.upgrad.quora.service.dao.UserDao;
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

    @Autowired
    private CommonUtility commonUtility;

    /**
     * This method deletes user in system by admin.
     *
     * @param userUuid  The UUID of the User to be deleted
     * @param authToken The JWT access token of the user passed in the request header.
     * @return Integer count of users deleted by the uuid.
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteUser(final String userUuid, final String authToken) throws AuthorizationFailedException, UserNotFoundException {
        final String signoutExceptionMessage = "User is signed out";
        UserEntity userEntity = commonUtility.getAuthenticatedUser(authToken, signoutExceptionMessage);
        String userRole = userEntity.getRole();
        //Check if the user who is logged in is not an admin
        if (!userRole.equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity user = userDao.getUserByUuid(userUuid);
        //Check if the userId entered by the admin is present in the application or not.
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return userDao.deleteUserByUuid(userUuid);

    }
}