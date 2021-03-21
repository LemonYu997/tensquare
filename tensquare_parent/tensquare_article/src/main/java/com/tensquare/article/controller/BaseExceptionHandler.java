package com.tensquare.article.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//控制器增强，常用于异常处理
@ControllerAdvice
public class BaseExceptionHandler {

    //异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody   //返回json字符串
    public Result handler(Exception e) {
        System.out.println("处理异常");

        //返回错误信息
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
