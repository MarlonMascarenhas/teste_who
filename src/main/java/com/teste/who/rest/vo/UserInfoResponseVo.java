package com.teste.who.rest.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseVo {

    private String messageId;
    private String protocolId;
    private DataUserInfoResponseVo dataUserInfoResponseVo;

}
