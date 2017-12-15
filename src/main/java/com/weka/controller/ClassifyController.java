package com.weka.controller;

import com.weka.entity.Result;
import com.weka.service.ClassifyService;
import com.weka.util.ResultUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import static org.slf4j.LoggerFactory.getLogger;
/**
 * Created by shan on 2017/12/14.
 */
@RestController
public class ClassifyController {
    private static final Logger logger = getLogger(ClassifyController.class);
    @Autowired
    private ClassifyService classifyService;

    @GetMapping("/exerciseModel")
    public Result<String> modelExercise() {
        try {
            classifyService.createWekaModel();
            return ResultUtil.success("训练模型成功");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return ResultUtil.error(500, "训练模型失败");
    }

    @GetMapping("/getResult/{word}")
    public Result<String> modelExercise(@PathVariable("word")String word) {
        try {
            String result = classifyService.getResultByExecuteParticipleAndClassify(word);
            return ResultUtil.success(result);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return ResultUtil.error(500, "训练模型失败");
    }

}