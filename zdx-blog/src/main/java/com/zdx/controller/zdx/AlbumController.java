package com.zdx.controller.zdx;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zdx.annotation.Log;
import com.zdx.entity.zdx.Album;
import com.zdx.enums.LogEventEnum;
import com.zdx.handle.Result;
import com.zdx.model.dto.RequestParams;
import com.zdx.model.vo.AlbumPhotoCountVo;
import com.zdx.model.vo.front.PhotoInfoVo;
import com.zdx.service.zdx.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = "相册管理")
public class AlbumController {

    private final AlbumService albumService;


    @GetMapping("/home/zdx.album/list")
    @ApiOperation("前台查询全部相册")
    public Result<List<Album>> homeList() {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Album::getStatus, Boolean.TRUE);
        return Result.success(albumService.list(queryWrapper));
    }

    @GetMapping("/home/zdx.album/photoList/{albumId}")
    @ApiOperation("前台相册id查询图片列表")
    public Result<PhotoInfoVo> homePhotoList(@PathVariable @NotBlank String albumId) {
        return Result.success(albumService.homePhotoList(albumId));
    }
    @GetMapping("/zdx.album/page")
    @ApiOperation("后台分页查询相册")
    public Result<IPage<AlbumPhotoCountVo>> adminPage(RequestParams params) {
        return Result.success(albumService.adminPage(params));
    }

    @GetMapping("/zdx.album/getById/{id}")
    @ApiOperation("通过相册id查询相册图片列表")
    public Result<AlbumPhotoCountVo> getAlbumPhotoCountById(@PathVariable @NotBlank String id) {
        return Result.success(albumService.getAlbumPhotoCountById(id));
    }

    @PostMapping("/zdx.album/save")
    @ApiOperation("保存相册")
    @Log(type = LogEventEnum.SAVE, desc = "保存相册")
    public Result<String> save(@RequestBody @Validated Album album) {
        return albumService.saveOrUpdate(album) ? Result.success() : Result.error();
    }

    @PostMapping("/zdx.album/batchDelete")
    @ApiOperation("批量删除相册")
    @Log(type = LogEventEnum.DELETE, desc = "批量删除相册")
    public Result<String> batchDelete(@RequestBody @ApiParam("标签id") @NotEmpty List<String> ids) {
        return albumService.removeBatchByIds(ids) ? Result.success() : Result.error();
    }
}
