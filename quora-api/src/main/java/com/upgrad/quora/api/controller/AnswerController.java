package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    /**
     * This method creates answer for a particular question in system.
     *
     * @param answerRequest The answer entered by the user
     * @param accessToken   The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws InvalidQuestionException This exception is thrown when the question doesn't exists for which the answer is being created
     */
    @RequestMapping(method = RequestMethod.POST, path = "question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @RequestHeader("authorization") String accessToken, @PathVariable("questionId") String questionID)
            throws InvalidQuestionException, AuthorizationFailedException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setCreatedDate(ZonedDateTime.now());
        AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity, questionID, accessToken);
        final AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This method updates answer in the system.
     *
     * @param answerEditRequest The answer entered by the user
     * @param answerId The answerId for which the answer is to be updated
     * @param accessToken   The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws AnswerNotFoundException This exception is thrown if the answer is not found in database for the entered answerUuid
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @RequestHeader("authorization") String accessToken, @PathVariable("answerId") String answerId)
            throws AnswerNotFoundException, AuthorizationFailedException {

        String editedContent = answerEditRequest.getContent();
        AnswerEntity editedAnswer = answerBusinessService.editAnswer(editedContent, accessToken, answerId);
        final AnswerEditResponse answerResponse = new AnswerEditResponse().id(editedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This method deletes answer in system.
     *
     * @param answerUuid  The answerUuid of the answer to be deleted
     * @param accessToken The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws AnswerNotFoundException This exception is thrown if the answer is not found in database for the entered answerUuid
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        Integer deletedQuestions = answerBusinessService.deleteAnswer(answerUuid, accessToken);
        final AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerUuid).status("ANSWER DELETED");
        return new ResponseEntity<>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * This method fetches all the answers to a question in system.
     *
     * @param questionId  The questionUuid of the question for which the answers are to be fetched
     * @param accessToken The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws InvalidQuestionException This exception is thrown if the question is not found in database for the entered questionId
     */
    @RequestMapping(method = RequestMethod.GET, path="answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> answerResponses= answerBusinessService.getAllAnswersToQuestion(questionId, accessToken);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for(AnswerEntity answerEntity: answerResponses) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponses.add(answerDetailsResponse);
        }
        return new ResponseEntity<>(answerDetailsResponses, HttpStatus.OK);
    }
}
