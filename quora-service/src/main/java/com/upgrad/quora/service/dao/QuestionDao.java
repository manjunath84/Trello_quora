package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * This method persists the given question entity to the database
     *
     * @param questionEntity The question details provided by user
     * @return QuestionEntity The persisted question object
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * This method fetches all the questions based on the user id
     *
     * @param userId The id of the user
     * @return List<QuestionEntity> List of all the questions asked by the user
     */
    public List<QuestionEntity> getAllQuestions(final long userId){
        return entityManager.createNamedQuery("getAllQuestionsByUserId", QuestionEntity.class).setParameter("userId", userId).getResultList();
    }

    /**
     * This method fetches the question entity from the database based on Question Uuid
     *
     * @param questionUuid The questionUuid provided by user
     * @return QuestionEntity The persisted question object
     */
    public QuestionEntity getQuestionById(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("questionUuid", questionUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method deletes the question entity from the database based on Question Uuid
     *
     * @param questionUuid The questionUuid provided by user
     * @return Integer The number of deleted question Entities
     */
    public Integer deleteQuestionByUuid(final String questionUuid) {
        return entityManager.createQuery("delete from QuestionEntity q where q.uuid = :questionUuid").setParameter("questionUuid", questionUuid).executeUpdate();
    }

}
