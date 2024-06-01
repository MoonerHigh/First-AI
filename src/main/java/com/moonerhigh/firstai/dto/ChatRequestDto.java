package com.moonerhigh.firstai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChatRequestDto {

    /**
     * 用户输入消息
     */
    @NotBlank(message = "提问消息不能为空")
    private String message;
}
