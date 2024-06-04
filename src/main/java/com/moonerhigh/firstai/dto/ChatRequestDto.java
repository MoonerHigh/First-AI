package com.moonerhigh.firstai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ChatRequestDto {

    /**
     * 用户输入消息
     */
    @NotBlank(message = "提问消息不能为空")
    private String message;
}
