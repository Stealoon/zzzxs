package com.studyroom.controller.admin;

import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/messages")
@Api(tags = "消息接口")
@Slf4j
public class MessagesController {

    @GetMapping("/countUnread")
    @ApiOperation("获取未读消息数")
    public Result countUnread() {
        return Result.success(0);
    }
}
