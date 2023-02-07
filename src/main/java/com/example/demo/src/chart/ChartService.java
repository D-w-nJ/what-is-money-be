package com.example.demo.src.chart;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.chart.model.GetChartRes;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartService {

    @Autowired
    private final ChartRepository chartRepository;

    @Transactional
    public List<GetChartRes> getChartList(Long userIdx) throws BaseException {
        try {
            List<GetChartRes> getChartResList = chartRepository.getChartResList(userIdx);
            return getChartResList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }

    @Transactional
    public List<GetChartRes> getChartListByLast(Long userIdx) throws BaseException {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            Calendar cal = Calendar.getInstance();
            cal.add(cal.MONTH, -1);
            String lastM = df.format(cal.getTime());
            List<GetChartRes> getChartResList = chartRepository.getChartListByLast(userIdx, lastM);
            return getChartResList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SERVER_ERROR);
        }
    }
}
