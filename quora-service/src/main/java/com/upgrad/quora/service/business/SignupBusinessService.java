package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

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
        if(existingEntity != null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        //Check and throw SignUpRestrictedException if the email address has already been taken
        existingEntity = userDao.getUserByEmail(userEntity.getEmail());
        if(existingEntity != null){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }
        //For a non-existing user, generate the salt and hashed password and set it to UserEntity
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }
}
