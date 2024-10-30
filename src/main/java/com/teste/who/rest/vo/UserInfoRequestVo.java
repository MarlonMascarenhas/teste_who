package com.teste.who.rest.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequestVo {

    @NotNull(message = "messageId can not be null")
    private String messageId;

    @NotNull(message = "protocolId can not be null")
    private String protocolId;

    private DadosPessoaisUserInfoRequestVo dadosPessoais;
}
