package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private QuestionDao questionDao;

    /**
     * This method creates the answer entity in the system.
     *
     * @param answerEntity The question entered by the user
     * @param authorization  The JWT access token of the user
     * @return AnswerEntity The persited answer entity.
     * @throws AuthorizationFailedException This exception is thrown if the user is not signed in.
     * @throws InvalidQuestionException This exception is thrown if the question doesn't exist in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, String questionID, final String authorization)
            throws InvalidQuestionException, AuthorizationFailedException {
        //Check and throw AuthorizationFailedException if the JWT token doesn't exist in the database
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        //User is signed out if either JWT token is expired or user has logged out
        if (userAuthTokenEntity.getExpiresAt().isBefore(now) &&
                (userAuthTokenEntity.getLogoutAt() != null) ){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        if(questionDao.getQuestionById(questionID)==null) {
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setQuestion(questionDao.getQuestionById(questionID));
        return answerDao.createAnswer(answerEntity);
    }

}
