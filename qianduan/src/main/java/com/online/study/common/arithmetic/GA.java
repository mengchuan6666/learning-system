package com.online.study.common.arithmetic;

import com.online.study.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 遗传算法组卷实现类
 */
@Component
public class GA {
    /**
     * 变异概率
     */
    private static final double mutationRate = 0.085;
    /**
     * 精英主义
     */
    private static final boolean elitism = true;
    /**
     * 淘汰数组大小
     */
    private static final int tournamentSize = 5;


    // 进化种群
    public static Population evolvePopulation(Population pop, RuleBean rule, List<Question> questionList) {
        Population newPopulation = new Population(pop.getLength());
        int elitismOffset;
        // 精英主义
        if (elitism) {
            elitismOffset = 1;
            // 保留上一代最优秀个体
            GetPaper fitness = pop.getFitness();
            fitness.setId(0);
            newPopulation.setPaper(0, fitness);
        }
        // 种群交叉操作，从当前的种群pop来创建下一代种群newPopulation
        for (int i = elitismOffset; i < newPopulation.getLength(); i++) {
            // 较优选择parent
            GetPaper parent1 = select(pop);
            GetPaper parent2 = select(pop);
            while (parent2.getId() == parent1.getId()) {
                parent2 = select(pop);
            }
            // 交叉
            GetPaper child = crossover(parent1, parent2, rule, questionList);
            child.setId(i);
            newPopulation.setPaper(i, child);
        }
        // 种群变异操作
        GetPaper tmpPaper;
        for (int i = elitismOffset; i < newPopulation.getLength(); i++) {
            tmpPaper = newPopulation.getPaper(i);
            mutate(tmpPaper, questionList);
            // 计算知识点覆盖率与适应度
            tmpPaper.setKpCoverage(rule);
            tmpPaper.setAdaptationDegree(rule, Global.KP_WEIGHT, Global.DIFFCULTY_WEIGHt);
        }
        return newPopulation;
    }

    /**
     * 交叉算子
     *
     * @param parent1
     * @param parent2
     * @return
     */
    public static GetPaper crossover(GetPaper parent1, GetPaper parent2, RuleBean rule, List<Question> questionList) {
        GetPaper child = new GetPaper(parent1.getQuestionSize());
        int s1 = (int) (Math.random() * parent1.getQuestionSize());
        int s2 = (int) (Math.random() * parent1.getQuestionSize());

        // parent1的startPos endPos之间的序列，会被遗传到下一代
        int startPos = s1 < s2 ? s1 : s2;
        int endPos = s1 > s2 ? s1 : s2;
        for (int i = startPos; i < endPos; i++) {
            child.saveQuestion(i, parent1.getQuestion(i));
        }

        // 继承parent2中未被child继承的question
        // 防止出现重复的元素
        for (int i = 0; i < startPos; i++) {
            if (!child.containsQuestion(parent2.getQuestion(i))) {
                child.saveQuestion(i, parent2.getQuestion(i));
            } else {
                int type = getTypeByIndex(i, rule);

                // 选择指定类型和知识点的试题数组
                Question[] singleArray = questionList.stream().filter(q -> q.getType() == type).filter(q -> q.getCtypeId() == rule.getCtypeId()).filter(q -> q.getPointId() == 1 || q.getPointId() == 2).toArray(Question[]::new);

                child.saveQuestion(i, singleArray[(int) (Math.random() * singleArray.length)]);

            }
        }
        for (int i = endPos; i < parent2.getQuestionSize(); i++) {
            if (!child.containsQuestion(parent2.getQuestion(i))) {
                child.saveQuestion(i, parent2.getQuestion(i));
            } else {
                int type = getTypeByIndex(i, rule);
                //查数据库
                Question[] singleArray = questionList.stream().filter(q -> q.getType() == type).filter(q -> q.getCtypeId() == rule.getCtypeId()).filter(q -> q.getPointId() == 1 || q.getPointId() == 2).toArray(Question[]::new);
                child.saveQuestion(i, singleArray[(int) (Math.random() * singleArray.length)]);
            }
        }

        return child;
    }

    private static int getTypeByIndex(int index, RuleBean rule) {
        int type = 0;
        // 单选
        if (index < rule.getSingleNum()) {
            type = 1;
        } else if (index < rule.getSingleNum() + rule.getCompleteNum()) {
            // 填空
            type = 2;
        } else {
            // 主观
            type = 3;
        }
        return type;
    }

    /**
     * 突变算子 每个个体的每个基因都有可能突变
     *
     * @param paper
     */
    public static void mutate(GetPaper paper, List<Question> questionList) {
        Question tmpQuestion;
        List<Question> list;
        int index;
        for (int i = 0; i < paper.getQuestionSize(); i++) {
            if (Math.random() < mutationRate) {
                // 进行突变，第i道
                tmpQuestion = paper.getQuestion(i);
                Question tempQuetion2 = paper.getQuestion(i);
                // 从题库中获取和变异的题目类型一样分数相同的题目（不包含变异题目）
                list = questionList.stream().filter(q -> q.getType() == tempQuetion2.getType()).filter(q -> q.getCtypeId() == tempQuetion2.getCtypeId()).filter(q -> q.getScore() == tempQuetion2.getScore()).collect(Collectors.toList());

                if (list.size() > 0) {
                    // 随机获取一道
                    index = (int) (Math.random() * list.size());
                    // 设置分数
                    list.get(index).setScore(tmpQuestion.getScore());
                    paper.saveQuestion(i, list.get(index));
                }
            }
        }
    }

    /**
     * 选择算子
     *
     * @param population
     */
    private static GetPaper select(Population population) {
        Population pop = new Population(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            pop.setPaper(i, population.getPaper((int) (Math.random() * population.getLength())));
            //System.out.println("选择算子========================" + i);
        }
        return pop.getFitness();
    }
}
