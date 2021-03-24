package com.tensquare.notice.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tensquare.notice.pojo.NoticeFresh;
import org.springframework.stereotype.Repository;

@Repository //防止报错
public interface NoticeFreshDao extends BaseMapper<NoticeFresh> {
}
