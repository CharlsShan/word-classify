package com.weka.util;

import com.weka.entity.Result;

/**
 * Created by shan on 2017/12/14.
 */
public class ResultUtil {
	
	private ResultUtil(){}

    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg("保存成功");
        result.setData(object);
        return result;
    }

    public static Result success() {
        return success("");
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
