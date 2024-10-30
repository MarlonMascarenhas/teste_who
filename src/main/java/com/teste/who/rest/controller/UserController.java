package com.teste.who.rest.controller;


import com.teste.who.converter.UserResponseVoConverter;
import com.teste.who.exception.InvalidBodyException;
import com.teste.who.rest.vo.UserInfoRequestVo;
import com.teste.who.rest.vo.UserInfoResponseVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = "v1/user", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserResponseVoConverter UserResponseVoConverter;

    @PostMapping
    public ResponseEntity<UserInfoResponseVo> createUser(@Valid @RequestBody UserInfoRequestVo userInfoRequestVo) {

        try {
            UserInfoResponseVo response = UserResponseVoConverter.convert(userInfoRequestVo);
            return ResponseEntity.ok(response);
        } catch (InvalidBodyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
