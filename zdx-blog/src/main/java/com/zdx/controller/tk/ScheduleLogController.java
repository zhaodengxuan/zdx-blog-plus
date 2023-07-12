package com.zdx.controller.tk;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdx.annotation.Log;
import com.zdx.controller.BaseController;
import com.zdx.controller.dto.RequestParams;
import com.zdx.entity.tk.ScheduleLog;
import com.zdx.enums.LogEventEnum;
import com.zdx.handle.Result;
import com.zdx.service.tk.ScheduleLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/zdx.schedule-log")
@RequiredArgsConstructor
@Validated
@Api(tags = "任务日志")
public class ScheduleLogController extends BaseController<ScheduleLog> {

    private final ScheduleLogService scheduleLogService;
    @Override
    public IService<ScheduleLog> getService() {
        return scheduleLogService;
    }

    @Override
    public Wrapper<ScheduleLog> getQueryWrapper(RequestParams params) {
        LambdaQueryWrapper<ScheduleLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleLog::getJobId, params.getParam("scheduleId"));
        queryWrapper.eq(params.hasParam("name"), ScheduleLog::getName, params.getParam("name"));
        queryWrapper.eq(params.hasParam("status"), ScheduleLog::getStatus, params.getParam("status", Boolean.class));
        if (params.hasParam("time")) {
            String time = params.getParam("time", String.class);
            String[] split = time.split(",");
            queryWrapper.ge(ScheduleLog::getStartTime, split[0]);
            queryWrapper.le(ScheduleLog::getStartTime, split[1]);
        }
        return queryWrapper;
    }

    @GetMapping("/clear/{scheduleId}")
    @Log(type = LogEventEnum.DELETE, desc = "清空任务日志")
    @ApiOperation("清空任务日志")
    public Result<String> clear(@PathVariable @NotBlank String scheduleId) {
        return scheduleLogService.clear(scheduleId) ? Result.success() : Result.error();
    }
}