package jhmk.clinic.cms.controller.cdss;

import com.alibaba.fastjson.JSONObject;
import jhmk.clinic.cms.SamilarService;
import jhmk.clinic.cms.controller.ruleService.*;
import jhmk.clinic.cms.service.CdssRunRuleService;
import jhmk.clinic.cms.service.CdssService;
import jhmk.clinic.cms.service.TestService;
import jhmk.clinic.cms.service.ZlfaService;
import jhmk.clinic.core.base.BaseController;
import jhmk.clinic.core.util.CompareUtil;
import jhmk.clinic.entity.pojo.YizhuBsjb;
import jhmk.clinic.entity.pojo.YizhuChange;
import jhmk.clinic.entity.pojo.YizhuResult;
import jhmk.clinic.entity.pojo.repository.SysDiseasesRepository;
import jhmk.clinic.entity.pojo.repository.SysHospitalDeptRepository;
import jhmk.clinic.entity.service.YizhuBsjbRepService;
import jhmk.clinic.entity.service.YizhuChangeRepService;
import jhmk.clinic.entity.service.YizhuOriRepService;
import jhmk.clinic.entity.service.YizhuResultRepService;
import jhmk.clinic.entity.test.YizhuTestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author ziyu.zhou
 * @date 2018/10/12 18:26
 * 病历质控
 */
@Controller
@RequestMapping("/test/blzk")
public class BlzkController extends BaseController {
    Logger logger = LoggerFactory.getLogger(CdssController.class);

    @Autowired
    SysDiseasesRepository sysDiseasesRepository;
    @Autowired
    SysHospitalDeptRepository sysHospitalDeptRepository;
    @Autowired
    BasyService basyService;
    @Autowired
    YizhuService yizhuService;
    @Autowired
    CyjlService cyjlService;
    @Autowired
    BlzdService blzdService;
    @Autowired
    RyjuService ryjuService;
    @Autowired
    SjyscflService sjyscflService;
    @Autowired
    SamilarService samilarService;
    @Autowired
    SyzdService syzdService;

    @Autowired
    CdssService cdssService;
    @Autowired
    TestService testService;
    @Autowired
    CdssRunRuleService cdssRunRuleService;
    @Autowired
    YizhuOriRepService yizhuOriRepService;
    @Autowired
    YizhuBsjbRepService yizhuBsjbRepService;
    @Autowired
    YizhuChangeRepService yizhuChangeRepService;
    @Autowired
    YizhuResultRepService yizhuResultRepService;
    @Autowired
    ZlfaService zlfaService;
    @Autowired
    RestTemplate restTemplate;

    /**
     * 下拉列表 病历质控所有唯一疾病名
     *
     * @param response
     */
    @PostMapping("/getDistinctIllName")
    @ResponseBody
    public void getDistinctIllName(HttpServletResponse response) {
        List<String> distinctIllName = yizhuResultRepService.getDistinctIllName();
        wirte(response, distinctIllName);
    }

    @PostMapping("/getDistinctBidByIllName")
    @ResponseBody
    public void getDistinctBidByIllName(HttpServletResponse response, @RequestBody(required = false) String map) {
        JSONObject jsonObject = JSONObject.parseObject(map);
        String name = jsonObject.getString("name");
        List<String> distinctIllName = yizhuResultRepService.getDistinctBidByIllName(name);
        List<Map<Integer, YizhuTestBean>> paramsList = new ArrayList<>();
        //病历id

        for (String id : distinctIllName) {
            Map<Integer, YizhuTestBean> params = new HashMap<>();
            int num = yizhuResultRepService.getMaxBid(id);
            for (int i = 1; i <= num; i++) {
                YizhuTestBean tempyizhuTestBean = new YizhuTestBean();

                List<YizhuResult> resultList = yizhuResultRepService.findAllByBIdAndNum(id, i);
                List<YizhuChange> changeList = yizhuChangeRepService.findAllByBIdAndNum(id, i);
                List<YizhuBsjb> nsjbList = yizhuBsjbRepService.findAllByBIdAndNum(id, i);
                tempyizhuTestBean.setBid(id);
                tempyizhuTestBean.setNum(i);
                tempyizhuTestBean.setChangeList(changeList);
                tempyizhuTestBean.setResultList(resultList);
                tempyizhuTestBean.setBsjbList(nsjbList);
                tempyizhuTestBean.setNum(i);
                params.put(i, tempyizhuTestBean);
            }
            paramsList.add(params);
        }
        wirte(response, paramsList);
    }

    /**
     * 分析疾病  得到数量
     *
     * @param response
     * @param map
     */
    @PostMapping("/analyzeDistinctBidByIllName")
    @ResponseBody
    public void analyzeDistinctBidByIllName(HttpServletResponse response, @RequestBody(required = false) String map) {
        JSONObject jsonObject = JSONObject.parseObject(map);
        String name = jsonObject.getString("name");
        List<String> distinctIllName = yizhuResultRepService.getDistinctBidByIllName(name);
        List<Map<Integer, YizhuTestBean>> paramsList = new ArrayList<>();
        //病历id

        for (String id : distinctIllName) {
            Map<Integer, YizhuTestBean> params = new HashMap<>();
            int num = yizhuResultRepService.getMaxBid(id);
            for (int i = 1; i <= num; i++) {
                YizhuTestBean tempyizhuTestBean = new YizhuTestBean();

                List<YizhuResult> resultList = yizhuResultRepService.findAllByBIdAndNum(id, i);
                List<YizhuChange> changeList = yizhuChangeRepService.findAllByBIdAndNum(id, i);
                List<YizhuBsjb> nsjbList = yizhuBsjbRepService.findAllByBIdAndNum(id, i);
                tempyizhuTestBean.setBid(id);
                tempyizhuTestBean.setNum(i);
                tempyizhuTestBean.setChangeList(changeList);
                tempyizhuTestBean.setResultList(resultList);
                tempyizhuTestBean.setBsjbList(nsjbList);
                tempyizhuTestBean.setNum(i);
                params.put(i, tempyizhuTestBean);
            }
            paramsList.add(params);
        }
        wirte(response, paramsList);
    }

    /**
     * 分析数据 集中治疗方案
     *
     * @param response
     * @param map
     */
        @PostMapping("/analyzeDistinctBidByIllNameAndManager")
    @ResponseBody
    public void analyzeDistinctBidByIllNameAndManager(HttpServletResponse response, @RequestBody(required = false) String map) {
        JSONObject jsonObject = JSONObject.parseObject(map);
        String name = jsonObject.getString("name");
        List<String> distinctIllName = yizhuResultRepService.getDistinctBidByIllName(name);
        List<List<YizhuResult>> list = new ArrayList<>();
        List<List<YizhuChange>> changelist = new ArrayList<>();
        List<List<YizhuBsjb>> bsjbList = new ArrayList<>();
        //病历id
        int i = 1;
        for (String id : distinctIllName) {
            //治疗方案 医嘱
            List<YizhuResult> yizhuResults = yizhuResultRepService.findAllByBIdAndNum(id, i);
            Collections.sort(yizhuResults, CompareUtil.createComparator(1, "purpose", "drug"));
            list.add(yizhuResults);
            //治疗方案 g增加或减少
            List<YizhuChange> changeList = yizhuChangeRepService.findAllByBIdAndNum(id, i);
            changelist.add(changeList);
            //治疗方案 伴随疾病
            List<YizhuBsjb> nsjbList = yizhuBsjbRepService.findAllByBIdAndNum(id, i);
            bsjbList.add(nsjbList);

        }
        Map<String,Object>paeams=new HashMap<>();
        List<Map.Entry<String, Integer>> entries = zlfaService.analyzeYizhuResult(list);
        paeams.put("yizhu",entries);
        List<Map.Entry<String, Integer>> analyzeYizhuChange = zlfaService.analyzeYizhuChange(changelist);
        paeams.put("change",analyzeYizhuChange);
        List<Map.Entry<String, Integer>> analyzeYizhuBsjb = zlfaService.analyzeYizhuBsjb(bsjbList);
        paeams.put("bsjb",analyzeYizhuBsjb);

        wirte(response, paeams);
    }
}