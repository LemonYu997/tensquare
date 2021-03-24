package com.tensquare.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//参数为需要调用的微服务名称（在其yml配置文件中设置）
//使用Feign，编写接口即可调用对应的服务
@FeignClient("tensquare-article")
public interface ArticleClient {
    //根据文章id查询文章数据
    //GET /article/{articleId} 根据ID查询文章
    @RequestMapping(value = "article/{articleId}" ,method = RequestMethod.GET)
    public Result findById(@PathVariable("articleId") String articleId);
}
