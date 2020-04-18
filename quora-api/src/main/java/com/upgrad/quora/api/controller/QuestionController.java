package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
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
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;

    /**
     * This method creates the new question in system.
     *
     * @param questionRequest The question entered by the user
     * @param authorization The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown if user has not singed in or singed out.
     */
    @RequestMapping(method = RequestMethod.POST, path = "question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(
            final QuestionRequest questionRequest,
            @RequestHeader("authorization") String authorization) throws AuthorizationFailedException {

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setCreatedDate(ZonedDateTime.now());
        QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        final QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * This method gets all the questions posted by a user
     *
     * @param authorization The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown if user has not singed in or signed out.
     */
    @RequestMapping(method = RequestMethod.GET, path = "question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") String authorization) throws AuthorizationFailedException {

        final List<QuestionEntity> questionList = questionBusinessService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<>();
        for(QuestionEntity questionEntity: questionList){
            questionDetailsResponseList.add(
                    new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent()));
        }
        return new ResponseEntity<>(questionDetailsResponseList, HttpStatus.OK);
    }

    /**
     * This method deletes question in system.
     *
     * @param questionUuid  The questionId of the question to be deleted
     * @param authorization The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown if user has not singed in or singed out.
     * @throws InvalidQuestionException This exception is thrown if the question doesn't exist in the database
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        Integer deletedQuestions = questionBusinessService.deleteQuestion(questionUuid, authorization);
        final QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionUuid).status("QUESTION DELETED");
        return new ResponseEntity<>(questionDeleteResponse, HttpStatus.OK);
    }
}