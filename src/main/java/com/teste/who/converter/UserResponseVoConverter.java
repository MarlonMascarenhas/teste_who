package com.teste.who.converter;

import com.teste.who.exception.InvalidBodyException;
import com.teste.who.rest.vo.DadosPessoaisUserInfoRequestVo;
import com.teste.who.rest.vo.DataUserInfoResponseVo;
import com.teste.who.rest.vo.UserInfoRequestVo;
import com.teste.who.rest.vo.UserInfoResponseVo;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UserResponseVoConverter
    implements CollectionConverter<UserInfoRequestVo, UserInfoResponseVo> {

    @Override
    public UserInfoResponseVo convert(UserInfoRequestVo source) {
        if (source == null) {
            return null;
        }

        DadosPessoaisUserInfoRequestVo dadosPessoais = source.getDadosPessoais();

        return new UserInfoResponseVo(
                source.getMessageId(),
                source.getProtocolId(),
                new DataUserInfoResponseVo(
                        dadosPessoais != null ? dadosPessoais.getNome() : null,
                        dadosPessoais != null ? dadosPessoais.getIdade() : null,
                        dadosPessoais != null ? formatDate(dadosPessoais.getDataNascimento()) : null,
                        dadosPessoais != null ? parseScore(dadosPessoais.getScore()) : null
                )
        );

    }

    private String formatDate(String dateStr) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (dateStr == null ) return null;

        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new InvalidBodyException("Invalid format to birthDate: " + dateStr);
        }
    }

    private Double parseScore(String scoreStr) {

        if (scoreStr == null ) return null;

        try {
            return Double.parseDouble(scoreStr);
        } catch (NumberFormatException e) {
            throw new InvalidBodyException("Invalid format to score: " + scoreStr);
        }
    }
}
