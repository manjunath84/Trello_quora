package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * This method exposes endpoint to register a new user in the Quora Application
     *
     * @param signupUserRequest The signup user request details
     * @return ResponseEntity
     * @throws SignUpRestrictedException This exception is thrown if either given username or email address already exists in the application
     */
    @RequestMapping(method = RequestMethod.POST, path = "user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        //Transform the signupUserRequest to UserEntity object
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        //Invoke business service class to sign up the user
        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse()
                .id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(signupUserResponse, HttpStatus.CREATED);
    }

    /**
     * This method exposes endpoint to sigin a user in the Quora Application
     *
     * @param authorization The signin user request details
     * @return ResponseEntity
     * @throws UserNotFoundException AuthenticationFailedException This exception is thrown if either given username or email address already exists in the application
     */
    @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, UserNotFoundException {

        //Decoding the authorization header to split username and password
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = userBusinessService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<>(signinResponse, headers, HttpStatus.OK);
    }

    /**
     * This method exposes endpoint to signout a user in the Quora Application
     *
     * @param authorization The signout user request details
     * @return ResponseEntity
     * @throws SignOutRestrictedException This exception is thrown if either given username or email address already exists in the application
     */
    @RequestMapping(method = RequestMethod.POST, path = "user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.signout(authorization);
        final String userUUID = userAuthTokenEntity.getUser().getUuid();
        SignoutResponse signoutResponse = new SignoutResponse().id(userUUID).message("SIGNED OUT SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("user-uuid", userUUID);
        return new ResponseEntity<>(signoutResponse, headers, HttpStatus.OK);
    }
}
