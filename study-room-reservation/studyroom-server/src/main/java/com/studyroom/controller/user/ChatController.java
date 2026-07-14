package com.studyroom.controller.user;

import com.studyroom.result.Result;
import com.studyroom.service.ChatService;
import com.studyroom.vo.ChatReplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chat")
@Api(tags = "智能客服接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    @ApiOperation("发送消息")
    public Result<ChatReplyVO> send(String message) {
        log.info("智能客服收到消息：{}", message);
        ChatReplyVO replyVO = chatService.chat(message);
        return Result.success(replyVO);
    }
}
