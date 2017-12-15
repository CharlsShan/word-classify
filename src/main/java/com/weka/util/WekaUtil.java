package com.weka.util;

import org.slf4j.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author shanchenyang
 * @time 创建时间：2017年12月15日 上午10:58:16 Weka的工具类
 */
public class WekaUtil {
	private static final Logger logger = getLogger(WekaUtil.class);

	private WekaUtil() {
	}

	/**
	 * 以ARFF格式加载数据生成Instances
	 * 
	 * @param fileName
	 * @return
	 */
	public static Instances loadDataset(String fileName) {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

			ArffReader arff = new ArffReader(reader);
			Instances trainData = arff.getData();
			// 设置分类索引为0 必须在训练库之前加上这条代码
			trainData.setClassIndex(0);
			return trainData;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 这个方法构建了一个分类器，然后分类器根据读到的数据进行训练、学习，生成模型
	 * 
	 * @param trainData
	 * @return FilteredClassifier
	 */
	public static FilteredClassifier evaluateAndLearn(Instances trainData) {
		try {

			StringToWordVector filter = new StringToWordVector();
			filter.setAttributeIndices("last");
			FilteredClassifier classifier = new FilteredClassifier();
			classifier.setFilter(filter);

			// 可选择不同算法 这里选择效率比较高的算法
			classifier.setClassifier(new RandomTree());

			classifier.buildClassifier(trainData);

			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, 2, new Random(1));

			trainData.setClassIndex(0);

			return classifier;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 把分类器模型存储到文件中
	 * 
	 * @param fileName
	 * @param classifier
	 */
	public static void saveModel(String fileName, FilteredClassifier classifier) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(classifier);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 这个方法加载分类器模型
	 * 
	 * @param fileName
	 * @return
	 */
	public static FilteredClassifier loadModel(String fileName) {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {

			Object tmp = in.readObject();
			return (FilteredClassifier) tmp;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
