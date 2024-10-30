package com.teste.who.converter;

import com.teste.who.exception.InvalidBodyException;
import com.teste.who.rest.vo.DadosPessoaisUserInfoRequestVo;
import com.teste.who.rest.vo.UserInfoRequestVo;
import com.teste.who.rest.vo.UserInfoResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

public class UserResponseVoConverterTest {

    private final UserResponseVoConverter converter = new UserResponseVoConverter();

    private static UserInfoRequestVo getUserInfoRequestVo() {
        DadosPessoaisUserInfoRequestVo dadosPessoaisUserInfoRequestVo = new DadosPessoaisUserInfoRequestVo();
        dadosPessoaisUserInfoRequestVo.setNome("Marlon");
        dadosPessoaisUserInfoRequestVo.setIdade(26L);
        dadosPessoaisUserInfoRequestVo.setScore("500.50");
        dadosPessoaisUserInfoRequestVo.setDataNascimento("25-03-1998 00:00:0");

        UserInfoRequestVo userInfoRequestVo = new UserInfoRequestVo();
        userInfoRequestVo.setProtocolId("b3788d36-037d-40d8-b448-90bc9724f388");
        userInfoRequestVo.setMessageId("4f3f9fe2-2f19-43b4-b611-b871a80ecfec");
        userInfoRequestVo.setDadosPessoais(dadosPessoaisUserInfoRequestVo);
        return userInfoRequestVo;
    }

    @Test
    public void testConvertSuccessfully() {
        UserInfoRequestVo userInfoRequestVo = getUserInfoRequestVo();

        UserInfoResponseVo convert = converter.convert(userInfoRequestVo);

        Assertions.assertEquals("4f3f9fe2-2f19-43b4-b611-b871a80ecfec", convert.getMessageId());
        Assertions.assertEquals("b3788d36-037d-40d8-b448-90bc9724f388", convert.getProtocolId());
        Assertions.assertEquals("25/03/1998", convert.getDataUserInfoResponseVo().getBirthDate());
        Assertions.assertEquals(500.50, convert.getDataUserInfoResponseVo().getScore());
        Assertions.assertEquals(26L, convert.getDataUserInfoResponseVo().getAge());
        Assertions.assertEquals("Marlon", convert.getDataUserInfoResponseVo().getName());
    }


    @Test
    public void shouldThrowInvalidBodyExceptionForInvalidScoreFormat() {
        UserInfoRequestVo userInfoRequestVo = getUserInfoRequestVo();

        userInfoRequestVo.getDadosPessoais().setScore("incorrect");

        InvalidBodyException exception = Assertions.assertThrows(InvalidBodyException.class, () -> converter.convert(userInfoRequestVo));
        Assertions.assertEquals("Invalid format to score: incorrect", exception.getMessage());
    }

    @Test
    public void shouldThrowInvalidBodyExceptionForInvalidBirthDateFormat() {
        UserInfoRequestVo userInfoRequestVo = getUserInfoRequestVo();

        userInfoRequestVo.getDadosPessoais().setDataNascimento("incorrect");

        InvalidBodyException exception = Assertions.assertThrows(InvalidBodyException.class, () -> converter.convert(userInfoRequestVo));
        Assertions.assertEquals("Invalid format to birthDate: incorrect", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidBodyExceptionWhenDadosPessoaisIsNull() {

        UserInfoRequestVo requestVo = new UserInfoRequestVo("4f3f9fe2-2f19-43b4-b611-b871a80ecfec", "b3788d36-037d-40d8-b448-90bc9724f388", null);

        UserInfoResponseVo convert = converter.convert(requestVo);

        Assertions.assertEquals("4f3f9fe2-2f19-43b4-b611-b871a80ecfec", convert.getMessageId());
        Assertions.assertEquals("b3788d36-037d-40d8-b448-90bc9724f388", convert.getProtocolId());
        Assertions.assertNull(convert.getDataUserInfoResponseVo().getBirthDate());
        Assertions.assertNull(convert.getDataUserInfoResponseVo().getScore());
        Assertions.assertNull(convert.getDataUserInfoResponseVo().getAge());
        Assertions.assertNull(convert.getDataUserInfoResponseVo().getName());
    }

}
