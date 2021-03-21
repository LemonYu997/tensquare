package com.tensquare.article.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//相当于@Controller+@ResponseBody
@RestController
@RequestMapping("/article")
@CrossOrigin        //跨域处理
public class ArticleController {
    //注入service
    @Autowired
    private ArticleService articleService;

    //异常处理测试
    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public Result exception() throws Exception {
        throw new Exception("测试统一异常处理");
    }

    //POST article/search/{page}/{size} 文章分页
    //{page}为页码，{size}为大小
    @RequestMapping(value = "search/{page}/{size}", method = RequestMethod.POST)
    //之前接收文章数据，使用pojo，现在根据条件查询
    //所有的条件都需要进行判断，遍历pojo的所有属性都需要使用反射的方式，成本较高，性能较低
    //直接使用集合的方式遍历，这里接收数据改为Map集合
    public Result findByPage(@PathVariable Integer page, @PathVariable Integer size, @RequestBody Map<String, Object> map) {
        //根据条件分页查询
        Page<Article> pageData = articleService.findByPage(map, page, size);
        //封装分页返回对象
        PageResult<Article> pageResult = new PageResult<>(
                //总记录数和分页结果
                pageData.getTotal(),pageData.getRecords()
        );
        //返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }


    //DELETE /article/{articleId}  根据ID删除文章
    @RequestMapping(value = "{articleId}", method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String articleId) {
        articleService.deleteById(articleId);

        return new Result(true, StatusCode.OK, "删除成功");
    }

    //PUT /article/{articleId} 修改文章
    @RequestMapping(value = "{articleId}", method = RequestMethod.PUT)
    public Result updateById(@PathVariable String articleId, @RequestBody Article article) {
        //设置id
        article.setId(articleId);
        //执行修改
        articleService.updateById(article);

        return new Result(true, StatusCode.OK, "修改成功");
    }

    //POST /article 添加文章
    @RequestMapping(method = RequestMethod.POST)
    //@RequestBody注解可以把json字符串转换成pojo实体
    public Result save(@RequestBody Article article) {
        articleService.save(article);
        //不需要返回实体
        return new Result(true, StatusCode.OK, "新增成功");
    }


    //GET /article/{articleId} 根据ID查询文章
    @RequestMapping(value = "{articleId}" ,method = RequestMethod.GET)
    public Result findById(@PathVariable String articleId) {
        Article article = articleService.findById(articleId);
        return new Result(true, StatusCode.OK, "查询成功", article);
    }


    //GET /article 文章全部列表
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Article> list = articleService.findAll();

        return new Result(true, StatusCode.OK, "查询成功", list);
    }
}
