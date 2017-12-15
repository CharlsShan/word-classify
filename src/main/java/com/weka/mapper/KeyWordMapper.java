package com.weka.mapper;

import com.weka.entity.KeyWord;
import java.util.List;

public interface KeyWordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyWord record);

    KeyWord selectByPrimaryKey(Integer id);

    List<KeyWord> selectAll();

    int updateByPrimaryKey(KeyWord record);

    List<KeyWord> selectByClassifyId(Integer classifyId);
}