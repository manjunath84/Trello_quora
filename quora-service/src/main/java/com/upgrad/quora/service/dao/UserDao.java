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
     * This method persists the given userAuthTokenEntity details in the database
     *
     * @param userAuthTokenEntity The UserAuthToken details to be persisted
     * @return userAuthTokenEntity The updated UserAuthToken details
     */

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * This method updates the given user in the database
     *
     * @param updatedUserEntity The User details to be persisted
     */
    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    /**
     * This method returns the UserAuthTokenEntity the given accessToken from the database
     *
     * @param authToken The UserAuthToken details to be fetched
     * @return UserAuthTokenEntity The updated UserAuthToken details
     */
    public UserAuthTokenEntity getUserAuthToken(String authToken) {
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", authToken).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    /**
     * This method updates the given UserAuthTokenEntity in the database
     *
     * @param userAuthTokenEntity The UserAuthToken details to be persisted
     */
    public void updateUserAuthToken(UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.merge(userAuthTokenEntity);
    }

    /**
     * This method fetches UserEntity by the given User Unique Identification.
     *
     * @param userUuid The uuid of the user
     * @return UserEntity The UserEntity of the given email address.
     */
    public UserEntity getUserByUuid(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("userUuid", userUuid).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * This method deletes UserEntity by the given User Unique Identification.
     *
     * @param userUuid The uuid of the user
     * @return Integer The Uuid of the given user.
     */
    public Integer deleteUserByUuid(final String userUuid) {
        try {
            //Using createNamedQuery throws exception due to which it is recommended to use this way
            return entityManager.createQuery("delete from UserEntity u where u.uuid = :userUuid").setParameter("userUuid", userUuid).executeUpdate();
        } catch (NoResultException e) {
            return null;
        }
    }

}
