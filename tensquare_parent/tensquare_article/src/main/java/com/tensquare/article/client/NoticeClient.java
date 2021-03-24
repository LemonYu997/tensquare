package com.tensquare.article.client;

import com.tensquare.article.pojo.Notice;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("tensquare-notice")
public interface NoticeClient {
    //POST /notice 新增通知
    //测试链接：http://127.0.0.1:9014/notice
    @RequestMapping(value = "notice", method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice);
    //这里需要Notice类，将消息微服务pojo包中的Notice类复制到文章微服务的pojo包中
}
