package com.tensquare.notice.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notice")
@CrossOrigin    //开启跨域请求
public class NoticeController {

    //注入service
    @Autowired
    private NoticeService noticeService;

    //GET /notice/{id}  根据id查询消息通知
    //测试连接：http://127.0.0.1:9014/notice/1
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Result selectById(@PathVariable String id) {
        Notice notice = noticeService.selectById(id);
        return new Result(true, StatusCode.OK, "查询成功", notice);
    }

    //POST /notice/search/{page}/{size} 根据条件分页查询消息通知
    //测试连接：http://127.0.0.1:9014/notice/search/1/5  请求体内容自己写
    @RequestMapping(value = "search/{page}/{size}", method = RequestMethod.POST)
    public Result selectByList(@RequestBody Notice notice,  //请求体使用notice对象（Json格式）
                               @PathVariable Integer page,
                               @PathVariable Integer size) {
        Page<Notice> pageData = noticeService.selectByPage(notice, page, size);
        //封装分页返回结果集
        PageResult<Notice> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );
        //返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    //POST /notice 新增通知
    //测试链接：http://127.0.0.1:9014/notice
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice) {
        noticeService.save(notice);

        return new Result(true, StatusCode.OK, "新增成功");
    }

    //PUT /notice 修改通知
    //测试链接：http://127.0.0.1:9014/notice
    @RequestMapping(method = RequestMethod.PUT)
    public Result updateById(@RequestBody Notice notice) {
        noticeService.updateById(notice);

        return new Result(true, StatusCode.OK, "修改成功");
    }

    //根据用户id查询该用户的待推送消息（新消息）
    //GET /notice/fresh/{userId}/{page}/{size}
    //测试链接：http://127.0.0.1:9014/notice/fresh/3/1/5
    @RequestMapping(value = "fresh/{userId}/{page}/{size}",method = RequestMethod.GET)
    public Result freshPage(@PathVariable String userId,
                            @PathVariable Integer page,
                            @PathVariable Integer size
                            ) {
        Page<NoticeFresh> pageData = noticeService.freshPage(userId, page, size);

        PageResult<NoticeFresh> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );

        //返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    //删除待推送消息（新消息）
    //DELETE /notice/fresh
    //测试链接：http://127.0.0.1:9014/notice/fresh
    @RequestMapping(value = "fresh", method = RequestMethod.DELETE)
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        noticeService.freshDelete(noticeFresh);

        return new Result(true, StatusCode.OK, "删除成功");
    }
}
