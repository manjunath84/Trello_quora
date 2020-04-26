package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.*;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

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
     * @param answerEntity  The question entered by the user
     * @param authorization The JWT access token of the user
     * @return AnswerEntity The persited answer entity.
     * @throws AuthorizationFailedException This exception is thrown if the user is not signed in.
     * @throws InvalidQuestionException     This exception is thrown if the question doesn't exist in the database
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
                (userAuthTokenEntity.getLogoutAt() != null)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        QuestionEntity question = questionDao.getQuestionByUuid(questionID);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setQuestion(question);
        return answerDao.createAnswer(answerEntity);
    }

    /**
     * This method updates the answer entity in the system.
     *
     * @param answerUuid    The answerUuid entered by the user
     * @param authorization The JWT access token of the user
     * @return AnswerEntity The updated AnswerEntity from the database.
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws AnswerNotFoundException      This exception is thrown if the answer is not found in database for the entered answerUuid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(final String editedContent, final String authorization, final String answerUuid) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        UserEntity user = userAuthTokenEntity.getUser();
        AnswerEntity answerEntity = answerDao.getAnswerById(answerUuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        UserEntity answerOwner = answerEntity.getUser();

        if (answerOwner.getUuid().equals(user.getUuid())) {
            answerEntity.setAnswer(editedContent);
            return answerDao.editAnswerByUuid(answerEntity);
        }
        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }

    /**
     * This method deletes the answer entity in the system.
     *
     * @param answerUuid    The answerUuid entered by the user
     * @param authorization The JWT access token of the user
     * @return Count The count of answer entity deleted.
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws AnswerNotFoundException      This exception is thrown if the answer is not found in database for the entered answerUuid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteAnswer(final String answerUuid, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        UserEntity user = userAuthTokenEntity.getUser();
        AnswerEntity answerEntity = answerDao.getAnswerById(answerUuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        UserEntity answerOwner = answerEntity.getUser();

        if (answerOwner.getUuid().equals(user.getUuid()) || user.getRole().equals("admin")) {
            return answerDao.deleteAnswerByUuid(answerUuid);
        }
        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }

    /**
     * This method fetches all the answer entity for a given question in the system.
     *
     * @param questionId  The questionUuid entered by the user
     * @param accessToken The JWT access token of the user
     * @return AnswerEntity The list of AnswerEntity for a given question from the database.
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws InvalidQuestionException     This exception is thrown if the question is not found in database for the entered answerUuid
     */
    public List<AnswerEntity> getAllAnswersToQuestion(String questionId, String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException(
                    "QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersToQuestion(questionId);
    }
}
