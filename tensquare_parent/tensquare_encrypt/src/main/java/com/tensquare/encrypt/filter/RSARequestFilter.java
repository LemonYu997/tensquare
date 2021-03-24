package com.tensquare.encrypt.filter;

import com.google.common.base.Charsets;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.assertj.core.util.Strings;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//网关过滤器
@Component
public class RSARequestFilter extends ZuulFilter {
    @Override
    public String filterType() {
        //过滤器在什么环节执行，解密操作需要在转发之前进行操作
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //设置过滤器的执行顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        //是否使用此过滤器，true为使用
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //过滤器具体执行的逻辑
        System.out.println("过滤器执行了");

        //获取requestContext容器
        RequestContext ctx = RequestContext.getCurrentContext();

        //获取request和response
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        //声明存放加密后的数据的变量
        String requestData = null;
        //声明存放解密后的数据变量
        String decryptData = null;

        //通过request获取inputStream
        try {
            ServletInputStream inputStream = request.getInputStream();
            //从inputStream得到加密后的数据
            requestData = StreamUtils.copyToString(inputStream, Charsets.UTF_8);
            System.out.println(requestData);

            //对加密后的数据进行解密操作
            //判断得到的加密后的数据是否为空
            if (!Strings.isNullOrEmpty(requestData)) {
                //进行解密
                System.out.println("使用对应的解密方法");
            }

            //把解密后的数据进行转发，需要放到request中
            if (!Strings.isNullOrEmpty(decryptData)) {
                //获取到解密后的数据的字节数组
                byte[] bytes = decryptData.getBytes();

                //使用RequestContext进行数据的转发
                ctx.setRequest(new HttpServletRequestWrapper(request){
                    //添加字节流
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        //使用解密后的字节数组
                        return new ServletInputStreamWrapper(bytes);
                    }

                    //设置长度
                    @Override
                    public int getContentLength() {
                        return bytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return bytes.length;
                    }
                });
            }

            //需要设置request请求头中的Content-Type，为json格式的数据
            //如果不设置，API接口模块就需要进行url转码的操作
            ctx.addZuulRequestHeader("Content-Type",
                    MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
