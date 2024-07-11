package com.online.study.common.arithmetic;

import com.online.study.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 种群，即多套试卷
 */
@Component
public class Population {


    /***
    @Autowired
    private IQuestionService questionService;

    public static Population population;

    @PostConstruct
    public void init(){
        population=this;
        population.questionService=this.questionService;
    }
    ***/
    public Population() {
    }

    /**
     * 试卷集合
     */
    private GetPaper[] papers;

    /**
     * 初始种群
     *
     * @param populationSize 种群规模
     * @param initFlag       初始化标志 true-初始化
     * @param rule           规则bean
     */
    public Population(int populationSize, boolean initFlag, RuleBean rule,List<Question> questionList) {
        papers = new GetPaper[populationSize];

        if (initFlag) {
            GetPaper paper;
            Random random = new Random();
            for (int i = 0; i < populationSize; i++) {
                paper = new GetPaper();
                paper.setId(i + 1);
                while (paper.getTotalScore() != rule.getTotalMark()) {

                    paper.getQuestionList().clear();

                    // 单选题
                    if (rule.getSingleNum() > 0) {
                        generateQuestion(1, random, rule.getSingleNum(), rule.getSingleScore(),rule.getCtypeId(),paper,questionList);
                     }
                    // 填空题
                    if (rule.getCompleteNum() > 0) {
                        generateQuestion(2, random, rule.getCompleteNum(), rule.getCompleteScore(),rule.getCtypeId(),paper,questionList);
                    }
                    // 主观题
                    if (rule.getSubjectiveNum() > 0) {
                        generateQuestion(3, random, rule.getSubjectiveNum(), rule.getSubjectiveScore(),rule.getCtypeId(),paper,questionList);
                    }
                }
                // 计算试卷知识点覆盖率
                paper.setKpCoverage(rule);

                // 计算试卷适应度
                paper.setAdaptationDegree(rule, Global.KP_WEIGHT, Global.DIFFCULTY_WEIGHt);

                papers[i] = paper;
            }
        }
    }


    private void generateQuestion(int type, Random random, int qustionNum, double score,int ctypeId,GetPaper paper,List<Question> questionList) {

        Question[] singleArray = questionList.stream().filter(q->q.getType()==type).filter(q->q.getCtypeId()==ctypeId).filter(q->q.getPointId()==1 || q.getPointId()==2).toArray(Question[]::new);

        if (singleArray.length < qustionNum) {

            return;
        }
        Question tmpQuestion=null;
        for (int j = 0; j < qustionNum; j++) {
            int index = random.nextInt(singleArray.length - j);

            // 初始化分数
            singleArray[index].setScore(score);

            // 保证不会重复添加试题
            if(paper.isExit(paper.getQuestionList(),singleArray[index].getId())) {
                paper.addQuestion(singleArray[index]);
            }
        }
    }

    /**
     * 获取种群中最优秀个体
     *
     * @return
     */
    public GetPaper getFitness() {
        GetPaper paper = papers[0];
        for (int i = 1; i < papers.length; i++) {
            if (paper.getAdaptationDegree() < papers[i].getAdaptationDegree()) {
                paper = papers[i];
            }
        }
        return paper;
    }

    public Population(int populationSize) {
        papers = new GetPaper[populationSize];
    }

    /**
     * 获取种群中某个个体
     *
     * @param index
     * @return
     */
    public GetPaper getPaper(int index) {
        return papers[index];
    }

    /**
     * 设置种群中某个个体
     *
     * @param index
     * @param paper
     */
    public void setPaper(int index, GetPaper paper) {
        papers[index] = paper;
    }

    /**
     * 返回种群规模
     *
     * @return
     */
    public int getLength() {
        return papers.length;
    }

}
