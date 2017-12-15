package com.weka.service;

import com.weka.entity.Classify;
import com.weka.entity.CreateData;
import com.weka.entity.KeyWord;
import com.weka.mapper.ClassifyMapper;
import com.weka.mapper.KeyWordMapper;
import com.weka.util.WekaUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by shan on 2017/12/14.
 */
@Service
public class ClassifyService {
    private static final Logger logger = getLogger(ClassifyService.class);

    @Autowired
    private ClassifyMapper classifyMapper;
    @Autowired
    private KeyWordMapper keyWordMapper;

    @Value("${model.path}")
    private String modelPath;

    @Transactional
    public void createWekaModel() {

        // 从数据库查找到所有的收入分类数据
        List<Classify> allClassifyList = classifyMapper.selectAll();
        if (allClassifyList == null || allClassifyList.isEmpty()) {
            logger.error("没有从数据库查找到分类数据");
            return;
        }
        logger.info("分类模型训练开始");
        generateInstanceAndModelLearn(allClassifyList);
    }


    private void generateInstanceAndModelLearn(List<Classify> allClassifyList) {
        // 生成Instances（每个Instances对应一个ARFF）
        Instances trainData = generateInstance(allClassifyList);
        // 模型学习
        FilteredClassifier evaluateAndLearn = WekaUtil.evaluateAndLearn(trainData);
        WekaUtil.saveModel(modelPath, evaluateAndLearn);
        logger.info("收入分类模型训练完毕并存储到硬盘");
    }

    /**
     * 程序构建Arff文件
     *
     * @param allClassifyList
     * @return
     */
    private Instances generateInstance(List<Classify> allClassifyList) {
        // 得到所有的分类名
        List<String> varietyOfClassify = new ArrayList<>(100);
        for (Classify classify : allClassifyList) {
            varietyOfClassify.add(classify.getClassifyName());
        }
        // 构建@data数据
        List<CreateData> entities = createArffData(allClassifyList);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("@@class@@", varietyOfClassify));
        attributes.add(new Attribute("text", true));

        // 构建instances
        Instances instances = new Instances("classify", attributes, 500);
        // 设置分类的索引
        instances.setClassIndex(instances.numAttributes() - 1);

        // 添加数据到@data
        for (CreateData secRepoEntity : entities) {
            Instance instance = new DenseInstance(attributes.size());
            // 必须放在创建一个新的instance后 否则会报没加入Dataset异常
            instance.setDataset(instances);
            if (varietyOfClassify.contains(secRepoEntity.getClassifyName())) {
                instance.setValue(0, secRepoEntity.getClassifyName());
                instance.setValue(1, secRepoEntity.getTestValue());
            }
            instances.add(instance);
        }

        instances.setClassIndex(0);
        return instances;
    }


    /**
     * 准备ArffData数据
     *
     * @param allClassifyList
     * @return
     */
    private List<CreateData> createArffData(List<Classify> allClassifyList) {
        List<CreateData> createArffData = new ArrayList<>();
        for (Classify classify : allClassifyList) {
            List<KeyWord> classifyKeywordByClassifyId = keyWordMapper.selectByClassifyId(classify.getId());
            for (int i = 0; i < classifyKeywordByClassifyId.size(); i++) {
                createArffData.add(new CreateData(classify.getClassifyName(), classifyKeywordByClassifyId.get(i).getKeywordName()));
            }
        }
        return createArffData;
    }

    @Transactional
    public String getResultByExecuteParticipleAndClassify(String word) {
        try {
            if (StringUtils.isBlank(word)) {
                return "";
            }
            logger.info("需要分类的词是" + word);

            // 加载词库模型
            FilteredClassifier model = WekaUtil.loadModel(modelPath);
            List<Classify> allClassifyList = classifyMapper.selectAll();
            List<String> nameString= allClassifyList.stream().map(Classify::getClassifyName).collect(Collectors.toList());
            // 得到分类结果
            String result = makeInstance(model, nameString,word);
            logger.info("分类结果" + result);
            return result;
        } catch (Exception e) {
            logger.error("wordclassify error ,  detail message:{}", e);
        }
        return "";
    }

    /**
     * 生成一个新的instance用于得出结果
     *
     * @param evaluateAndLearn
     * @param varietyOfClassify
     */
    public String makeInstance(FilteredClassifier evaluateAndLearn, List<String> varietyOfClassify,String word) {

        // 添加第一个分类值
        FastVector fvNominalVal = new FastVector(50);
        for (String classify : varietyOfClassify) {
            fvNominalVal.addElement(classify);
        }
        Attribute attribute1 = new Attribute("@@class@@", fvNominalVal);
        Attribute attribute2 = new Attribute("text", (FastVector) null);

        FastVector fvWekaAttributes = new FastVector(2);
        fvWekaAttributes.addElement(attribute1);
        fvWekaAttributes.addElement(attribute2);
        Instances instances = new Instances("cardniu_text_classify", fvWekaAttributes, 1);
        // 设置索引
        instances.setClassIndex(0);
        // 创造一个新instance
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(attribute2, word);
        instances.add(instance);
        double pred;
        try {
            pred = evaluateAndLearn.classifyInstance(instances.instance(0));
            return instances.classAttribute().value((int) pred);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "";
    }
}
