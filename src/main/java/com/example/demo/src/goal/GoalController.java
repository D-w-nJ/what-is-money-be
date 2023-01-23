package com.example.demo.src.goal;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.goal.model.*;
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
    @PostMapping("/createGoal/{categoryIdx}/{userIdx}")
    public BaseResponse<MakeGoalRes> createGoal(@PathVariable("userIdx") Long userIdx, @RequestBody MakeGoalReq makeGoalReq) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            goalService.createGoal(makeGoalReq, userIdx);
            return new BaseResponse<>();
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/getGoalList/{userIdx}")
    public BaseResponse<List<GetGoalRes>> getGoalList(@PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetGoalRes> getGoalRes = goalService.getGoalResList(userIdx);
            return new BaseResponse<>(getGoalRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/deleteGoal/{goalIdx}/{userIdx}")
    public BaseResponse deleteGoal(@PathVariable("goalIdx") Long goalIdx, @PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            goalService.deleteGoal(goalIdx);
            return new BaseResponse();
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("/sortGoalByAsc/{userIdx}")
    public BaseResponse<List<GetGoalRes>> sortGoalByAsc(@PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetGoalRes> getGoalRes = goalService.getGoalResListAsc(userIdx);
            return new BaseResponse<>(getGoalRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/sortGoalByDesc/{userIdx}")
    public BaseResponse<List<GetGoalRes>> sortGoalByDesc(@PathVariable("userIdx") Long userIdx) {
        try {
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            List<GetGoalRes> getGoalRes = goalService.getGoalResListDesc(userIdx);
            return new BaseResponse<>(getGoalRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/getGoal/{goalIdx}/{userIdx}")
    public BaseResponse<GetGoalRes> getGoal(@PathVariable("goalIdx") Long goalIdx, @PathVariable("userIdx") Long userIdx){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if (jwtServiceUserIdx != userIdx) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            GetGoalRes getGoalRes = goalService.getGoalRes(userIdx, goalIdx);
            return new BaseResponse<>(getGoalRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/modifyGoal/{goalIdx}/{userIdx}")
    public BaseResponse modifyGoal(@PathVariable("goalIdx") Long goalIdx, @PathVariable("userIdx") Long userIdx, @RequestBody ModifyGoalReq modifyGoalReq){
        try{
            int jwtServiceUserIdx = jwtService.getUserIdx();
            if(jwtServiceUserIdx != userIdx){
                return new BaseResponse(BaseResponseStatus.INVALID_USER_JWT);
            }
            goalService.modifyGoal(goalIdx, userIdx, modifyGoalReq);
            return new BaseResponse();
        } catch (BaseException exception){
            return new BaseResponse(exception.getStatus());
        }
    }
}
