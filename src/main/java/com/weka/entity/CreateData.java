package com.weka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by shanchenyang on 2017/12/14.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**分类名*/
    private String classifyName;

    /**文本值*/
    private String testValue;

}