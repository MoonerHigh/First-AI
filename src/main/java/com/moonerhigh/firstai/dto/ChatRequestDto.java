package com.moonerhigh.firstai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ChatRequestDto {

    /**
     * 问题
     */
    @NotBlank(message = "提问消息不能为空")
    private String message;

    /**
     * 上下文
     */
    private String context;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 模型
     */
    private String model;

    /**
     * 语言
     */
    private String language;

}
