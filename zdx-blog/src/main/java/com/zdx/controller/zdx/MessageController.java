package com.zdx.controller.zdx;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zdx.annotation.Log;
import com.zdx.entity.zdx.Message;
import com.zdx.enums.LogEventEnum;
import com.zdx.handle.Result;
import com.zdx.model.dto.RequestParams;
import com.zdx.service.zdx.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = "留言管理")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/home/zdx.message/list")
    @ApiOperation("前台查询留言列表")
    public Result<List<Message>> homeList() {
        return  Result.success(messageService.list());
    }
    @PostMapping("/home/zdx.message/add")
    @ApiOperation("前台添加评论")
    public Result<String> homeAddMessage(@RequestBody @Validated Message message) {
        return messageService.saveOrUpdate(message) ? Result.success() : Result.error();
    }
    @GetMapping("/zdx.message/page")
    @ApiOperation("分页查询留言数据")
    public Result<IPage<Message>> adminPage(RequestParams params) {
        return Result.success(messageService.adminPage(params));
    }
    @PostMapping("/zdx.message/save")
    @ApiOperation("保存留言数据")
    @Log(type = LogEventEnum.SAVE, desc = "保存留言数据")
    public Result<String> save(@RequestBody @Validated Message friend) {
        return messageService.saveOrUpdate(friend) ? Result.success() : Result.error();
    }

    @PostMapping("/zdx.message/batchDelete")
    @ApiOperation("批量删除留言数据")
    @Log(type = LogEventEnum.SAVE, desc = "批量删除留言数据")
    public Result<String> batchDelete(@RequestBody @ApiParam("留言id") @NotEmpty List<String> ids) {
        return messageService.removeBatchByIds(ids) ? Result.success() : Result.error();
    }

    @PostMapping("/zdx.message/through")
    @ApiOperation("批量审核通过留言")
    @Log(type = LogEventEnum.OTHER, desc = "批量审核通过留言")
    public Result<String> through(@RequestBody @ApiParam("留言id") @NotEmpty List<String> ids) {
        return messageService.through(ids) ? Result.success() : Result.error();
    }
}
