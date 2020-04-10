package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * This method fetches UserEntity by the given User Name.
     *
     * @param userName The Username of the user.
     * @return UserEntity The UserEntity of the given Username.
     */
    public UserEntity getUserByUserName(final String userName){
        try{
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    /**
     * This method fetches UserEntity by the given email address.
     *
     * @param email The email address of the user
     * @return UserEntity The UserEntity of the given email address.
     */
    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    /**
     * This method persists the given user in the database
     *
     * @param userEntity The User details to be persisted
     * @return UserEntity The updated user details
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * This method persists the given user in the database
     *
     * @param userAuthTokenEntity The User details to be persisted
     * @return userAuthTokenEntity The updated user details
     */

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * This method persists the given user in the database
     *
     * @param updatedUserEntity The User details to be persisted
     */
    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    /**
     * This method persists the given user in the database
     *
     * @param authToken The User details to be persisted
     * @return userEntity The updated user details
     */
    public UserAuthTokenEntity getUserByAuthToken(String authToken) {
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", authToken).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    /**
     * This method persists the given user in the database
     *
     * @param userAuthTokenEntity The User details to be persisted
     */
    public void updateUserAuthToken(UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.merge(userAuthTokenEntity);
    }

    /**
     * This method persists the given user in the database
     *
     * @param authToken The User details to be persisted
     * @return userEntity The updated user details
     */
    public UserAuthTokenEntity getUuidByAuthToken(String authToken) {
        try{
            return entityManager.createNamedQuery("UuidByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", authToken).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

}
