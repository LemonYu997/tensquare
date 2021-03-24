package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    //注入持久层
    @Autowired
    private CommentRepository commentRepository;

    //注入分布式ID生成器
    @Autowired
    private IdWorker idWorker;

    //注入mongoTemplate
    @Autowired
    private MongoTemplate mongoTemplate;

    //查询全部评论
    public List<Comment> findAll() {
        List <Comment> list = commentRepository.findAll();
        return list;
    }

    //根据评论id查询评论
    public Comment findById(String commentId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        //防止get方法，由于输入的id有误，抛出空指针异常
        //先判断根据id查询到的comment是否有值
        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }

    //新增评论
    public void save(Comment comment) {
        //保证id唯一，使用分布式id生成器
        String id = idWorker.nextId() + "";
        //如果不设置，mongo为自动生成ObjectId
        comment.set_id(id);
        //初始化点赞数据，发布时间等
        comment.setThumbup(0);
        comment.setPublishdate(new Date());
        //保存数据
        commentRepository.save(comment);
    }

    //修改评论
    public void updateById(Comment comment) {
        //使用的是MongoRepository的方法
        //其中save方法，主键如果存在，执行修改，如果不存在，执行新增
        commentRepository.save(comment);
    }

    //删除评论
    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    //根据文章id查询评论
    public List<Comment> findByArticleId(String articleId) {
        //调用持久层，根据文章id查询即可
        List<Comment> list = commentRepository.findByArticleid(articleId);
        return list;
    }

    //根据评论id点赞
    public void thumbup(String commentId) {
//        //根据评论id查询评论数据
//        Comment comment = commentRepository.findById(commentId).get();
//        //对评论点赞数据加一
//        comment.setThumbup(comment.getThumbup() + 1);
//        //保存修改数据
//        commentRepository.save(comment);

        //点赞功能优化
        //封装修改的条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));

        //封装修改的数值
        Update update = new Update();
        //使用inc列值增长
        update.inc("thumbup", 1);

        //直接修改数据
        //参数1：修改的条件
        //参数2：修改的数值
        //参数3：MongoDB的集合名称
        mongoTemplate.updateFirst(query, update, "comment");
    }
}
