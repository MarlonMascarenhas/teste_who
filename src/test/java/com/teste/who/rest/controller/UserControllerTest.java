package com.teste.who.rest.controller;

import com.teste.who.converter.UserResponseVoConverter;
import com.teste.who.exception.InvalidBodyException;
import com.teste.who.rest.vo.UserInfoRequestVo;
import com.teste.who.rest.vo.UserInfoResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserResponseVoConverter userResponseVoConverter;

    private UserInfoRequestVo userInfoRequestVo;
    private UserInfoResponseVo userInfoResponseVo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userInfoRequestVo = new UserInfoRequestVo("4f3f9fe2-2f19-43b4-b611-b871a80ecfec", "b3788d36-037d-40d8-b448-90bc9724f388", null);
        userInfoResponseVo = new UserInfoResponseVo("4f3f9fe2-2f19-43b4-b611-b871a80ecfec", "b3788d36-037d-40d8-b448-90bc9724f388", null);
    }

    @Test
    public void testCreateUser_Success() {
        Mockito.when(userResponseVoConverter.convert(userInfoRequestVo)).thenReturn(userInfoResponseVo);

        ResponseEntity<UserInfoResponseVo> response = userController.createUser(userInfoRequestVo);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(userInfoResponseVo, response.getBody());
    }

    @Test
    public void testCreateUser_InvalidBodyException() {
        Mockito.when(userResponseVoConverter.convert(userInfoRequestVo)).thenThrow(new InvalidBodyException("Invalid body"));

        ResponseEntity<UserInfoResponseVo> response = userController.createUser(userInfoRequestVo);

        Assertions.assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    public void testCreateUser_RuntimeException() {
        Mockito.when(userResponseVoConverter.convert(userInfoRequestVo)).thenThrow(new RuntimeException("Internal error"));

        ResponseEntity<UserInfoResponseVo> response = userController.createUser(userInfoRequestVo);

        Assertions.assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }
}
