package com.online.study.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.common.RoleEnum;
import com.online.study.common.arithmetic.*;
import com.online.study.controller.dto.HandPaperDTO;
import com.online.study.controller.dto.PaperDTO;
import com.online.study.entity.Paper;
import com.online.study.entity.PaperQuestion;
import com.online.study.entity.Question;
import com.online.study.entity.User;
import com.online.study.exception.ServiceException;
import com.online.study.service.IPaperQuestionService;
import com.online.study.service.IPaperService;
import com.online.study.service.IQuestionService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 
 */
@RestController
@RequestMapping("/paper")
public class PaperController {

    @Resource
    private IPaperService paperService;

    @Resource
    private IQuestionService questionService;

    @Resource
    private IPaperQuestionService paperQuestionService;






    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Paper paper) {
        if (paper.getId() == null) {
            //paper.setTime(DateUtil.now());
            //paper.setUser(TokenUtils.getCurrentUser().getUsername());
        }
        paperService.saveOrUpdate(paper);
        return Result.success();
    }

    @PostMapping("/handPaper")
    public Result handPaper(@RequestBody HandPaperDTO paperDTO) {
        // 删除老的试卷
        UpdateWrapper<PaperQuestion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("paper_id", paperDTO.getPaperId());
        paperQuestionService.remove(updateWrapper);

        if (CollUtil.isEmpty(paperDTO.getHandleQuestionIds())) {
            throw new ServiceException("-1", "题目数量不足");
        }
        List<Integer> handleQuestionIds = paperDTO.getHandleQuestionIds();
        List<PaperQuestion> list = new ArrayList<>();
        Integer sumScore = 0;
        for (Integer handleQuestionId : handleQuestionIds) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperDTO.getPaperId());
            paperQuestion.setQuestionId(handleQuestionId);
            list.add(paperQuestion);
        }
        paperQuestionService.saveBatch(list);
        return Result.success();
    }



    @PostMapping("/takePaper")
    public Result takePaper(@RequestBody PaperDTO paperDTO) {
        // 删除老的试卷
        UpdateWrapper<PaperQuestion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("paper_id", paperDTO.getPaperId());
        paperQuestionService.remove(updateWrapper);
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        // 根据课程id查出所有该课程的题目，然后再根据type去分
        List<Question> questionList = questionService.list(queryWrapper);
        List<Question> type1List = questionList.stream().filter(question -> question.getType() == 1).collect(Collectors.toList());  // 选择
        List<Question> type2List = questionList.stream().filter(question -> question.getType() == 2).collect(Collectors.toList());  // 填空
        List<Question> type3List = questionList.stream().filter(question -> question.getType() == 3).collect(Collectors.toList());  // 问答
        if (type1List.size() < paperDTO.getType1()) {
            throw new ServiceException("-1", "选择题数量不足");
        }
        if (type2List.size() < paperDTO.getType2()) {
            throw new ServiceException("-1", "判断题数量不足");
        }
        if (type3List.size() < paperDTO.getType3()) {
            throw new ServiceException("-1", "问答题数量不足");
        }
        // 开始随机组卷
        List<PaperQuestion> paperQuestion = getPaperQuestion(type1List.size(), paperDTO.getType1(), type1List, paperDTO.getPaperId());
        paperQuestion.addAll(getPaperQuestion(type2List.size(), paperDTO.getType2(), type2List, paperDTO.getPaperId()));
        paperQuestion.addAll(getPaperQuestion(type3List.size(), paperDTO.getType3(), type3List, paperDTO.getPaperId()));
        paperQuestionService.saveBatch(paperQuestion);  // 批量插入关联关系表
        return Result.success();
    }

    // 封装一个获取试卷和题目关联关系list的方法
    private List<PaperQuestion> getPaperQuestion(int questionSize, int paperQuestionSize, List<Question> source, Integer paperId) {
        List<Integer> typeRandomList = getEleList(questionSize, paperQuestionSize);
        List<PaperQuestion> list = new ArrayList<>();
        for (Integer index : typeRandomList) {
            Question question = source.get(index);
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(question.getId());
            list.add(paperQuestion);
        }
        return list;
    }
    // 封装一个获取随机数的方法
    private List<Integer> getEleList(int sourceSize, int resultSize) {
        List<Integer> list = CollUtil.newArrayList();
        for (int i = 0; i < sourceSize; i++) {
            list.add(i);
        }
        return RandomUtil.randomEleList(list, resultSize);
    }
    /***遗传算法组*/
    @PostMapping("/takePaperAuto")
    public Result takePaperAuto(@RequestBody PaperDTO paperDTO) {
        // 删除老的试卷
        UpdateWrapper<PaperQuestion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("paper_id", paperDTO.getPaperId());
        paperQuestionService.remove(updateWrapper);
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ctype_id",paperDTO.getCtypeId());
        // 根据课程id查出所有该课程的题目，然后再根据type去分
        List<Question> questionList = questionService.list(queryWrapper);
        List<Question> type1List = questionList.stream().filter(question -> question.getType() == 1).collect(Collectors.toList());  // 选择
        List<Question> type2List = questionList.stream().filter(question -> question.getType() == 2).collect(Collectors.toList());  // 填空
        List<Question> type3List = questionList.stream().filter(question -> question.getType() == 3).collect(Collectors.toList());  // 问答
        if (null!=paperDTO.getType1() && type1List.size() < paperDTO.getType1()) {
            throw new ServiceException("-1", "选择题数量不足");
        }
        if (null!=paperDTO.getType2() && type2List.size() < paperDTO.getType2()) {
            throw new ServiceException("-1", "判断题数量不足");
        }
        if (null!=paperDTO.getType3() && type3List.size() < paperDTO.getType3()) {
            throw new ServiceException("-1", "问答题数量不足");
        }
        // 开始随机组卷
        List<PaperQuestion> paperQuestion = getPaperQuestionByGA(paperDTO,paperDTO.getPaperId(),questionList);
        paperQuestionService.saveBatch(paperQuestion);
        return Result.success();
    }
    // 封装一个获取试卷和题目关联关系list的方法
    private List<PaperQuestion> getPaperQuestionByGA(PaperDTO paperDTO,Integer paperId,List<Question> questionList) {
        RuleBean ruleBean=new RuleBean();
        //题型1数量
        ruleBean.setSingleNum(paperDTO.getType1()==null ? 0:paperDTO.getType1());
        //题型1分值
        ruleBean.setSingleScore(paperDTO.getType1Score());
        //题型2数量
        ruleBean.setCompleteNum(paperDTO.getType2()==null ? 0:paperDTO.getType2());
        //题型2分值
        ruleBean.setCompleteScore(paperDTO.getType2Score());
        //题型3数量
        ruleBean.setSubjectiveNum(paperDTO.getType3()==null ? 0:paperDTO.getType3());
        //题型3分值
        ruleBean.setSubjectiveScore(paperDTO.getType3Score());
        //总分
        ruleBean.setTotalMark(ruleBean.getSingleNum()*ruleBean.getSingleScore()+ruleBean.getCompleteNum()*ruleBean.getCompleteScore()+ruleBean.getSubjectiveNum()*ruleBean.getSubjectiveScore());
        //难度系统
        Random random=new Random();
        double v = random.nextDouble();
        DecimalFormat df=new DecimalFormat("0.0");
        String s = df.format(v);
        ruleBean.setDifficulty(Double.parseDouble(s));
        //所属课程
        ruleBean.setCtypeId(paperDTO.getCtypeId());
        //知识覆盖点
        ruleBean.setPointIds("2#1");

        List<Question> resultPaper = this.getResultPaper(ruleBean,questionList);
        List<PaperQuestion> list = new ArrayList<>();
        for (Question rQuest : resultPaper) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(rQuest.getId());
            list.add(paperQuestion);
        }
        return list;
    }

    /**通过遗传算法得到的数据*/
    private List<Question> getResultPaper(RuleBean rule,List<Question> questionList) {

        GetPaper resultPaper = null;
        // 迭代计数器
        int count = 0;
        int runCount = 100;
        // 适应度期望值
        double expand = 0.98;
        if (rule != null) {
            // 初始化种群
            Population population = new Population(20,true,rule,questionList);
            //根据题型和课程查询的结果
            System.out.println("初次适应度  " + population.getFitness().getAdaptationDegree());
            while (count < runCount && population.getFitness().getAdaptationDegree() < expand) {
                count++;
                population = GA.evolvePopulation(population, rule,questionList);
                System.out.println("第 " + count + " 次进化，适应度为： " + population.getFitness().getAdaptationDegree());
            }
            System.out.println("进化次数： " + count);
            System.out.println(population.getFitness().getAdaptationDegree());
            resultPaper = population.getFitness();
        }
        return resultPaper.getQuestionList();
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        paperService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        paperService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping("/view/{paperId}")
    public Result view(@PathVariable Integer paperId) {
        List<Question> list = paperQuestionService.selectQuestions(paperId);
        User user = TokenUtils.getCurrentUser();
        if(user==null || RoleEnum.ROLE_USER.name().equals(user.getRole())) {
            list.forEach(e->{
                e.setAnswer("");
            });
        }
        return Result.success(list);
    }


    @GetMapping
    public Result findAll() {
        return Result.success(paperService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(paperService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Paper> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        Page<Paper> page = paperService.page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            e.setScore(paperService.sumScoreByPaperId(e.getId()));
        });
        return Result.success(page);
    }

    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Paper> list=paperService.list();
        fileName = URLEncoder.encode("试卷信息表", "UTF-8");
        EasyPOI.EasyExport(response, Paper.class, list,fileName + ".xls","试卷信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<Paper> list=EasyPOI.EasyImport(file.getInputStream(), Paper.class);
        boolean b=paperService.saveBatch(list);
        return Result.success(b);
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

