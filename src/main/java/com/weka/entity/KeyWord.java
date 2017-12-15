package com.weka.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyWord {
    private Integer id;

    private Integer classifyId;

    private String keywordName;

}