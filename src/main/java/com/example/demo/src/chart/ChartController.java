package com.example.demo.src.chart;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.chart.model.GetChartRes;
import com.example.demo.utils.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chart")
public class ChartController {

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final ChartService chartService;

    public ChartController(JwtService jwtService, ChartService chartService) {
        this.jwtService = jwtService;
        this.chartService = chartService;
    }

    @GetMapping("{userIdx}")
    public BaseResponse<List<GetChartRes>> getChartList(@PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetChartRes> getChartResList = chartService.getChartList(userIdx);
            return new BaseResponse<>(getChartResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("last/{userIdx}")
    public BaseResponse<List<GetChartRes>> getChartListByLast(@PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetChartRes> getChartResList = chartService.getChartListByLast(userIdx);
            return new BaseResponse<>(getChartResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
