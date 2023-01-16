package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.record.model.PostRecordReq;
import com.example.demo.src.record.model.PostRecordRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class RecordController {

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final RecordService recordService;

    public RecordController(JwtService jwtService, RecordService recordService) {
        this.jwtService = jwtService;
        this.recordService = recordService;
    }

    /**
     * 기록 추가 API
     * [POST] /records
     */
    @ResponseBody
    @PostMapping("/records")
    public BaseResponse<PostRecordRes> createRecord(@RequestBody PostRecordReq postRecordReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (postRecordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            PostRecordRes postRecordRes = recordService.createRecord(postRecordReq);
            return new BaseResponse<>(postRecordRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
