package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {
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
     * @throws AuthenticationFailedException,UserNotFoundException exception is thrown if the given password doesn't match with the password of the user in database and when the user doesn't exist in the database respectively.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException, UserNotFoundException {

        //Check and throw UserNotFoundException if the user doesn't exist in the database
        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new UserNotFoundException("ATH-001", "This username does not exist");
        }

        //Encrypting the password using the salt value, to compare it with the stored password
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        //Check and throw AuthenticationFailedException if the password entered doesn't match with the password stored in the database
        if (!encryptedPassword.equals(userEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
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

    /**
     * This method is used to sign up the given new user
     *
     * @param userEntity The user details to be signed up
     * @return UserEntity The persisted signed up user details.
     * @throws SignUpRestrictedException This exception is thrown if the given username or email already exists
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        //Check and throw SignUpRestrictedException if the Username has already been taken
        UserEntity existingEntity = userDao.getUserByUserName(userEntity.getUserName());
        if (existingEntity != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        //Check and throw SignUpRestrictedException if the email address has already been taken
        existingEntity = userDao.getUserByEmail(userEntity.getEmail());
        if (existingEntity != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        //For a non-existing user, generate the salt and hashed password and set it to UserEntity
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    /**
     * This method is used to sign out the given new user
     *
     * @param authToken The user details to be signed out
     * @return Uuid  The persisted sign-out  user details.
     * @throws SignOutRestrictedException This exception is thrown if the given username or email already exists
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signout(String authToken) throws SignOutRestrictedException {

        //Check and throw SignOutRestrictedException if the user doesn't exist in the database
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authToken);
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        userAuthTokenEntity.setLogoutAt(now);
        userAuthDao.updateUserAuth(userAuthTokenEntity);

        return userAuthTokenEntity;
    }
}
