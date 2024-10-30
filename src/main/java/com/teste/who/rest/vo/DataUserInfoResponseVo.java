package com.teste.who.rest.vo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataUserInfoResponseVo {
    private String name;
    private Long age;
    private String birthDate;
    private Double score;
}
