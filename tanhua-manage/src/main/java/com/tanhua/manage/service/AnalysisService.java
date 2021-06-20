package com.tanhua.manage.service;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.mapper.AnalysisByDayMapper;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.utils.ComputeUtil;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import com.tanhua.manage.vo.AnalysisUsersVo;
import com.tanhua.manage.vo.DataPointVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService extends ServiceImpl<AnalysisByDayMapper, AnalysisByDay> {
    @Autowired
    private LogMapper logMapper;
    public AnalysisSummaryVo summary(){
        DateTime date = DateUtil.date(new Date());
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
        analysisSummaryVo.setCumulativeUsers(queryNumRegistered());
        analysisSummaryVo.setActivePassMonth(queryUserCount(date,-30,"num_active",false));
        analysisSummaryVo.setActivePassWeek(queryUserCount(date,-7,"num_active",false));
        Long now = queryUserCount(date, 0, "num_active", false);
        Long old = queryUserCount(date, -1, "num_active", true);
        analysisSummaryVo.setActiveUsersToday(now);
        analysisSummaryVo.setActiveUsersTodayRate(ComputeUtil.computeRate(now,old));

        Long now1 = queryUserCount(date, 0, "num_login", false);
        Long old1 = queryUserCount(date, -1, "num_login", true);
        analysisSummaryVo.setLoginTimesToday(now1);
        analysisSummaryVo.setLoginTimesTodayRate(ComputeUtil.computeRate(now1,old1));

        Long now2 = queryUserCount(date, 0, "num_registered", false);
        Long old2 = queryUserCount(date, -1, "num_registered", true);
        analysisSummaryVo.setNewUsersToday(now2);
        analysisSummaryVo.setNewUsersTodayRate(ComputeUtil.computeRate(now2,old2));

        return analysisSummaryVo;
    }
    public Long queryNumRegistered(){
        AnalysisByDay analysisByDay = getOne(Wrappers.<AnalysisByDay>query()
                .select("SUM(num_registered) as numRegistered"));
        return Long.valueOf(analysisByDay.getNumRegistered());
    }
    private Long queryUserCount(DateTime today,int offset,String column,boolean flag){
        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum("+column+") as numRegistered");
        if (flag){
            queryWrapper.le("record_date",DateUtil.offsetDay(today,offset).toDateStr());
        }else {
            queryWrapper.le("record_date",today.toDateStr());
        }
        queryWrapper.ge("record_date",DateUtil.offsetDay(today,offset).toDateStr());
        AnalysisByDay analysisByDay = getOne(queryWrapper);
        return Long.parseLong(analysisByDay.getNumRegistered().toString());
    }
    public AnalysisUsersVo queryAnalysisUserVo(Long sd,Long ed,Integer type){
        DateTime startDate = DateUtil.date(sd);
        DateTime endDate = DateUtil.date(ed);
        AnalysisUsersVo analysisUsersVo = new AnalysisUsersVo();
        analysisUsersVo.setLastYear(this.queryDataPointVos(
                DateUtil.offset(startDate, DateField.YEAR, -1),
                DateUtil.offset(endDate, DateField.YEAR, -1), type)
        );

        return analysisUsersVo;
    }
    private List<DataPointVo> queryDataPointVos(DateTime sd,DateTime ed,Integer type){
        String startDate = sd.toDateStr();
        String endDate = ed.toDateStr();
        String column=null;
        switch (type){
            case 101:
                column="num_registered";
                break;
            case 102:
                column = "num_active";
                break;
            case 103:
                column = "num_retention1d";
                break;
            default:
                column = "num_active";
                break;
        }
        List<AnalysisByDay> analysisByDayList = super.list(Wrappers.<AnalysisByDay>query().
                select("record_date," + column + "as num_active").
                ge("record_date", startDate).
                le("record_date", endDate));
        return analysisByDayList.stream().map(analysisByDay ->
                new DataPointVo(DateUtil.date(analysisByDay.getRecordDate()).toDateStr()
                ,analysisByDay.getNumActive().longValue())).collect(Collectors.toList());
    }
    public void analysis(){
        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper<>();
        String todayStr = DateUtil.formatDate(new Date());
        Date today = DateUtil.parse(todayStr);
        String yestodayStr = ComputeUtil.offsetDay(new Date(), -1);
        queryWrapper.eq("record_date",today);
        AnalysisByDay analysisByDay = getOne(queryWrapper);
        Integer numByType = logMapper.findNumByType(todayStr, "0102");
        Integer numByType1 = logMapper.findNumByType(todayStr, "0101");
        Integer numsByDate = logMapper.findNumsByDate(todayStr);
        Integer retention1d = logMapper.findRetention1d(todayStr, yestodayStr);
        AnalysisByDay analysis = new AnalysisByDay();
        analysis.setNumRegistered(numByType);
        analysis.setNumActive(numsByDate);
        analysis.setNumLogin(numByType1);
        analysis.setNumRetention1d(retention1d);
        analysis.setRecordDate(today);
        analysis.setCreated(new Date());
        analysis.setUpdated(new Date());
        if (analysisByDay==null){
             save(analysis);
        }else {
            QueryWrapper<AnalysisByDay> analysisByDayQueryWrapper = new QueryWrapper<>();
            analysisByDayQueryWrapper.eq("record_date",today);
            update(analysis,analysisByDayQueryWrapper);
        }
    }
}
