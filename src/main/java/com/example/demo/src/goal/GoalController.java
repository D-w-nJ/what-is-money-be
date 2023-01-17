package com.example.demo.src.goal;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.goal.model.GetGoalRes;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.goal.model.MakeGoalReq;
import com.example.demo.src.goal.model.MakeGoalRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goal")
public class GoalController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final GoalService goalService;

    public GoalController(JwtService jwtService, GoalService goalService) {
        this.jwtService = jwtService;
        this.goalService = goalService;
    }

    @ResponseBody
    @PostMapping("/createGoal/{category_id}/{userIdx}")
    public BaseResponse<MakeGoalRes> createGoal(@PathVariable("category_id") Long category_id, @PathVariable("userIdx") Long userIdx, @RequestBody MakeGoalReq makeGoalReq){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if(jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            goalService.createGoal(makeGoalReq, category_id, userIdx);
            return new BaseResponse<>();
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/getGoalList/{userIdx}")
    public BaseResponse<List<GoalEntity>> getGoalList(@PathVariable("userIdx") Long userIdx){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GoalEntity> getGoalRes = goalService.getGoalResList(userIdx);
            return new BaseResponse<>(getGoalRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
