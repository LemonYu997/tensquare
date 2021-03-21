package com.tensquare.article.controller;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    //GET /comment 查询所有评论
    //测试连接：http://127.0.0.1:9004/comment
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Comment> list = commentService.findAll();
        //返回
        return new Result(true, StatusCode.OK, "查询成功", list);
    }

    //GET /comment/{commentId} 根据评论id查询评论
    //测试链接：http://127.0.0.1:9004/comment/1
    @RequestMapping(value = "{commentId}", method = RequestMethod.GET)
    public Result findById(@PathVariable String commentId) {
        Comment comment = commentService.findById(commentId);
        return new Result(true, StatusCode.OK, "查询成功", comment);
    }

    //POST /comment 新增评论，接受json格式数据
    //测试链接：http://127.0.0.1:9004/comment 自己写Body内容
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Comment comment) {
        commentService.save(comment);
        return new Result(true, StatusCode.OK, "新增成功");
    }

    //PUT /comment/{commentId} 修改评论，接收json
    //测试连接：http://127.0.0.1:9004/comment/3 自己写Body内容
    @RequestMapping(value = "{commentId}", method = RequestMethod.PUT)
    public Result updateById(@PathVariable String commentId, @RequestBody Comment comment) {
        //设置评论主键
        comment.set_id(commentId);
        //执行修改
        commentService.updateById(comment);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    //DELETE /comment/{commentId} 根据id删除评论
    //测试连接：http://127.0.0.1:9004/comment/1373649182300180480 前边新增评论时自动生成的id
    @RequestMapping(value = "{commentId}", method = RequestMethod.DELETE)
    public Result DeleteById(@PathVariable String commentId) {
        commentService.deleteById(commentId);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
