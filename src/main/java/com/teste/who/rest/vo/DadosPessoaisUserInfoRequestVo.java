package com.teste.who.rest.vo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosPessoaisUserInfoRequestVo {
    private String nome;
    private Long idade;
    private String dataNascimento;
    private String score;
}
