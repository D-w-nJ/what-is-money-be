package com.example.demo.src.goal;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.goal.model.MakeGoalReq;
import com.example.demo.src.goal.model.MakeGoalRes;
import com.example.demo.src.user.UserService;
import com.example.demo.utils.JwtService;
import jdk.internal.net.http.common.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/createGoal/{category_id}")
    public BaseResponse<MakeGoalRes> createGoal(@PathVariable("category_id") Long category_id, @RequestBody MakeGoalReq makeGoalReq){
        try{
            goalService.createGoal(makeGoalReq, category_id);
            return new BaseResponse<>();
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
