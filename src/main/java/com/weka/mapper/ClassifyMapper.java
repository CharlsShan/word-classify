package com.weka.mapper;

import com.weka.entity.Classify;
import java.util.List;

public interface ClassifyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Classify record);

    Classify selectByPrimaryKey(Integer id);

    List<Classify> selectAll();

    int updateByPrimaryKey(Classify record);
}