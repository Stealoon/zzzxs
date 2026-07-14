package com.studyroom.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 智能客服返回VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //客服回复内容
    private String reply;

}
