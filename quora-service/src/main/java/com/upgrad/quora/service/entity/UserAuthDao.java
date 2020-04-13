package com.upgrad.quora.service.entity;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Gets the user auth information based on the access token.
     *
     * @param accessToken access token of the user auth whose details is to be fetched.
     * @return A single user auth object or null
     */
    public UserAuthTokenEntity getUserAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Persist UserAuthEntity object in DB.
     *
     * @param userAuthTokenEntity to be persisted in the DB.
     * @return Persisted UserAuthEntity object
     */
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * Updates the UserAuthEntity object present in the DB.
     *
     * @param updatedUserAuthEntity Persisted UserAuthEntity object
     */
    public void updateUserAuth(final UserAuthTokenEntity updatedUserAuthEntity) {
        entityManager.merge(updatedUserAuthEntity);
    }
}
