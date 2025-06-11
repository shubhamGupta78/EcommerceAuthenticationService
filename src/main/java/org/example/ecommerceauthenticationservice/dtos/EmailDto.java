package org.example.ecommerceauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EmailDto {
    private String from;
    private String to;
    private String subject;
    private String body;


    public static EmailDto toDto(String from, String to, String subject, String body) {
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom(from);
        emailDto.setTo(to);
        emailDto.setSubject(subject);
        emailDto.setBody(body);
        return emailDto;
    }
}
