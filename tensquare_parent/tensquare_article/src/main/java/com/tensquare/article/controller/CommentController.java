package com.tensquare.article.controller;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    //注入redisTemplate，用来解决重复点赞问题
    @Autowired
    private RedisTemplate redisTemplate;

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

    //GET /comment/article/{articleId} 根据文章id查询文章评论
    //测试链接：http://127.0.0.1:9004/comment/article/333
    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public Result findByArticleId(@PathVariable String articleId) {
        List<Comment> list = commentService.findByArticleId(articleId);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }

    //PUT /comment/thumbup/{commentId} 根据评论id点赞评论
    //测试连接：http://127.0.0.1:9004/comment/thumbup/4
    @RequestMapping(value = "thumbup/{commentId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String commentId) {
        //把用户点赞信息保存到redis中
        //每次点赞之前，先查询用户点赞信息
        //如果没有点赞信息，用户可以点赞；如果有点赞信息，用户不能重复点赞

        //模拟用户id
        String userId = "123";

        //查询用户点赞信息，根据用户id和评论id
        Object flag = redisTemplate.opsForValue().get("thumbup_" + userId + "_" + commentId);
        //判断查询到的结果是否为空
        //如果为空，表示用户吗没有点过赞，可以点赞
        if (flag == null) {
            //执行点赞操作
            commentService.thumbup(commentId);

            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set("thumbup_" + userId + "_" + commentId, 1);

            //返回结果
            return new Result(true, StatusCode.OK, "点赞成功");
        }

        //如果不为空，表示用户点过赞，不可以重复点赞
        return new Result(false, StatusCode.REPERROR, "不能重复点赞");
    }
}
