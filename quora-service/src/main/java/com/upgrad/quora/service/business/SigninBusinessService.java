package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SigninBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This method is used to successfully sign-in the given new user
     *
     * @param username,password The user details to be signed in
     * @return userAuthTokenEntity The persisted sign-in user details.
     * @throws AuthenticationFailedException,UserNotFoundException exception is thrown if the given password doesn't match with the password of the user in database
     * and when the user doesn't exist in the database respectively.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username,final String password) throws AuthenticationFailedException, UserNotFoundException {

        //Check and throw UserNotFoundException if the user doesn't exist in the database
        UserEntity userEntity = userDao.getUserByEmail(username);
        if (userEntity == null) {
            throw new UserNotFoundException("ATH-001", "This username does not exist");
        }

        //Encrypting the password using the salt value, to compare it with the stored password
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            //Setting the access token and other details for the user upon successful authentication.
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userAuthDao.createAuthToken(userAuthToken);
            userDao.updateUser(userEntity);


            return userAuthToken;
        }
        //Check and throw AuthenticationFailedException if the password entered doesn't match with the password stored in the database
        else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
    }
}
