package com.uswproject.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter

public class PostCreateRequestDto {

    @Setter
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Setter
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

}
