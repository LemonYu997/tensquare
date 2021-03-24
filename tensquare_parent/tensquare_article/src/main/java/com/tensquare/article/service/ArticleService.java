package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.pojo.Notice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {
    //注入Dao，可以忽视报错
    @Autowired
    private ArticleDao articleDao;

    //注入IdWorker
    @Autowired
    private IdWorker idWorker;

    //注入redisTemplate
    private RedisTemplate redisTemplate;

    //注入NoticeClient
    @Autowired
    private NoticeClient noticeClient;

    //注入rabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //解决key乱码问题！！
    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        //使用字符串序列化
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    //查询所有文章
    public List<Article> findAll() {
        //调用BaseMapper的查询所有方法
        return articleDao.selectList(null);
    }

    //根据id查询文章
    public Article findById(String articleId) {
        return articleDao.selectById(articleId);
    }

    //增加文章
    public void save(Article article) {
        //TODO: 使用jwt鉴权获取当前用户的信息，用户id，也就是文章作者id
        String userId = "3";    //这里模拟一下
        article.setUserid(userId);

        //使用分布式ID生成器，这里要把Long类型拼接成字符串
        String id = idWorker.nextId() + "";
        article.setId(id);

        //初始化数据
        article.setVisits(0);   //设置浏览量
        article.setThumbup(0);  //设置点赞数
        article.setComment(0);  //设置评论数

        //新增
        articleDao.insert(article);

        //新增文章后，创建消息，通知给订阅者

        //获取订阅者信息
        //存放作者订阅者的信息的集合key，里面存放订阅者id
        String authorKey = "article_author_" + userId;
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();
        //这里最好加一个if判断，避免空指针异常
//        if (set != null && set.size() > 0) {
//
//        }

        //给订阅者创建消息通知
        //注意：这里使用遍历得话，如果用户人数很多，性能会很低
        for (String uid : set) {
            // 创建消息对象
            Notice notice = new Notice();
            //接收消息的用户id
            notice.setReceiverId(uid);
            //进行操作的用户id
            notice.setOperatorId(userId);
            //操作类型 推送
            notice.setAction("publish");
            //被操作的对象 文章
            notice.setTargetType("article");
            //被操作的对象id 文章id
            notice.setTargetId(id);
            //发表日期在NoticeService中已经设置
            //通知类型
            notice.setType("sys");

            //保存到notice表中
            noticeClient.save(notice);
        }

        //入库成功后，发消息给RabbitMQ，内容是新消息通知id
        //参数1是交换机，参数2是路由键（文章作者id），参数3是消息内容（文章id）
        rabbitTemplate.convertAndSend("article_subscribe", userId, id);
    }

    //修改文章
    public void updateById(Article article) {
        //根据主键id修改
        articleDao.updateById(article);

        /*
        //根据条件修改
        //创建条件对象
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        //设置条件
        wrapper.eq("id", article.getId());
        articleDao.update(article, wrapper);
        */
    }

    //根据id删除文章
    public void deleteById(String articleId) {
        articleDao.deleteById(articleId);
    }

    //分页查询
    public Page<Article> findByPage(Map<String, Object> map, Integer page, Integer size) {
        //设置查询条件
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
//            //判断是否有查询条件
//            if (map.get(key) != null) {
//                //设置查询条件
//                wrapper.eq(key, map.get(key));
//            }

            //等同于上面的if逻辑
            //参数1表示是否把后面的条件加入到查询条件中
            wrapper.eq(map.get(key) != null, key, map.get(key));
        }
        //设置分页参数
        Page<Article> pageData = new Page<>(page, size);
        //执行查询
        //参数1是分页参数，参数2是查询条件
        List<Article> list = articleDao.selectPage(pageData, wrapper);

        //封装结果
        pageData.setRecords(list);
        //返回
        return pageData;
    }

    //根据文章id和用户id，建立订阅关系，保存的是文章作者id和用户id的关系
    public Boolean subscribe(String articleId, String userId) {
        //根据文章id查询文章作者id
        String authorId = articleDao.selectById(articleId).getUserid();

        //使用RabbitMQ存储新消息
        //1、创建RabbitMQ管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //2、声明Direct类型交换机，处理新增文章消息
        DirectExchange exchange = new DirectExchange("article_subscribe");
        rabbitAdmin.declareExchange(exchange);  //声明交换机

        //3、创建队列，每个用户都有自己的队列，通过用户id进行区分  参数1为队列名称  参数2为持久化存储
        Queue queue = new Queue("article_subscribe_" + userId, true);

        //4、声明交换机和队列的绑定关系，需要确保队列只收到对应作者的新增文章消息，
        // 通过路由键进行绑定作者，队列只收到绑定作者的文章消息
        // 第一个是队列，第二个是交换机，第三个是路由键作者id
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(authorId);

        //存放用户订阅信息的集合key，里面存放作者id
        String userKey = "article_subscribe_" + userId;

        //存放作者订阅者的信息的集合key，里面存放订阅者id
        String authorKey = "article_author_" + authorId;

        //查询用户的订阅关系，是否有订阅该作者
        //存在返回true，不存在返回false
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);
        if (flag == true) {
            //如果订阅作者，就取消订阅
            //在用户订阅信息的集合中，删除作者
            redisTemplate.boundSetOps(userKey).remove(authorId);

            //作者订阅者信息的集合中，删除订阅者
            redisTemplate.boundSetOps(authorKey).remove(userId);

            //如果取消订阅，删除队列绑定消息
            rabbitAdmin.removeBinding(binding);

            //返回false
            return false;
        } else {
            //如果没有订阅作者，就进行订阅
            //在用户订阅信息中，添加订阅的作者
            redisTemplate.boundSetOps(userKey).add(authorId);

            //在作者订阅者信息中，添加订阅者
            redisTemplate.boundSetOps(authorKey).add(userId);

            //如果订阅，声明要绑定的队列
            rabbitAdmin.declareQueue(queue);
            //添加绑定关系
            rabbitAdmin.declareBinding(binding);

            //返回true
            return true;
        }
    }

    //根据文章id点赞功能
    public void thumup(String articleId) {
        //TODO: 通过jwt鉴权获取用户id，这里模拟一下
        String userId = "3";

        Article article = articleDao.selectById(articleId);
        article.setThumbup(article.getThumbup() + 1);
        articleDao.updateById(article);

        //点赞成功后，需要发送消息给文章作者（点对点消息类型）
        Notice notice = new Notice();

        //封装notice
        //接收消息用户id  即文章作者
        notice.setReceiverId(article.getUserid());
        //进行操作用户id  即用户
        notice.setOperatorId(userId);
        //操作类型 点赞
        notice.setAction("publish");
        //被操作对象 文章
        notice.setTargetType("article");
        //被操作对象id 文章id
        notice.setTargetId(articleId);
        //通知类型
        notice.setType("user");

        //保存消息
        noticeClient.save(notice);
    }
}
