package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.CommonUtility;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommonUtility commonUtility;

    /**
     * This method creates the question entity in the system.
     *
     * @param questionEntity The question entered by the user
     * @param authorization  The JWT access token of the user
     * @return QuestionEntity The persited question entity.
     * @throws AuthorizationFailedException This exception is thrown if user has not signed in or if he is signed out.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException {
        //Check and throw AuthorizationFailedException if the JWT token doesn't exist in the database
        final String signoutExceptionMessage = "User is signed out.Sign in first to post a question";
        UserEntity AuthorizedUser = commonUtility.getAuthenticatedUser(authorization, signoutExceptionMessage);
        questionEntity.setUser(AuthorizedUser);
        return questionDao.createQuestion(questionEntity);
    }

    /**
     * This method fetches all the questions asked by any user
     *
     * @param authorization The JWT access token of the user
     * @return List<QuestionEntity> List of all the questions asked by any user
     * @throws AuthorizationFailedException This exception is thrown if user has not signed in or if he is signed out.
     */
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException {
        //Check and throw AuthorizationFailedException if the JWT token doesn't exist in the database
        final String signoutExceptionMessage = "User is signed out.Sign in first to get all questions";
        UserEntity AuthorizedUser = commonUtility.getAuthenticatedUser(authorization, signoutExceptionMessage);
        return questionDao.getAllQuestions();
    }

    /**
     * This method fetches all the questions asked by an user
     *
     * @param authorization The JWT access token of the user
     * @param userUuid      The uuid of the user whose questions needs to be fetched
     * @return List<QuestionEntity> List of all the questions asked by the corresponding user
     * @throws AuthorizationFailedException This exception is thrown if user has not signed in or if he is signed out.
     * @throws UserNotFoundException        This exception is thrown if entered user uuid does not exist in the system.
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String authorization, final String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        //Check and throw AuthorizationFailedException if the JWT token doesn't exist in the database
        final String signoutExceptionMessage = "User is signed out.Sign in first to get all questions posted by a specific user";
        UserEntity AuthorizedUser = commonUtility.getAuthenticatedUser(authorization, signoutExceptionMessage);
        final UserEntity retrievedUser = userDao.getUserByUuid(userUuid);
        if (retrievedUser == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUserUuid(userUuid);
    }

    /**
     * This method is used to edit the given question in the system
     *
     * @param questionEntity The editted question entity
     * @param authorization  The JWT access token of the user
     * @throws AuthorizationFailedException This exception is thrown if user has not signed in or if he is signed out.
     * @throws InvalidQuestionException     This exception is thrown if the given question uuid does not exits.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void editQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        //Check and throw AuthorizationFailedException if the JWT token doesn't exist in the database
        final String signoutExceptionMessage = "User is signed out.Sign in first to edit the question";
        UserEntity AuthorizedUser = commonUtility.getAuthenticatedUser(authorization, signoutExceptionMessage);

        final QuestionEntity existingQuestionEntity = questionDao.getQuestionByUuid(questionEntity.getUuid());
        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (existingQuestionEntity.getUser().getId() != (AuthorizedUser.getId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setId(existingQuestionEntity.getId());
        questionEntity.setCreatedDate(existingQuestionEntity.getCreatedDate());
        questionEntity.setUser(existingQuestionEntity.getUser());
        questionDao.editQuestion(questionEntity);
    }

    /**
     * This method deletes the question entity in the system.
     *
     * @param questionUuid  The questionUuid entered by the user
     * @param authorization The JWT access token of the user
     * @return QuestionEntity The persited question entity.
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteQuestion(final String questionUuid, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        final String signoutExceptionMessage = "User is signed out.Sign in first to post a question";
        UserEntity user = commonUtility.getAuthenticatedUser(authorization, signoutExceptionMessage);
        QuestionEntity question = questionDao.getQuestionByUuid(questionUuid);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity questionOwner = question.getUser();

        if (questionOwner.getUuid().equals(user.getUuid()) || user.getRole().equals("admin")) {
            return questionDao.deleteQuestionByUuid(questionUuid);
        }
        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }
}
