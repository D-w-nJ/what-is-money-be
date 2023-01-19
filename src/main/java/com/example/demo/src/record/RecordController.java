package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.record.model.*;
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

    /**
     * 기록 삭제 API
     * [DELETE] /records
     */
    @ResponseBody
    @DeleteMapping("/records")
    public BaseResponse<Long> deleteRecord(@RequestBody DeleteRecordReq deleteRecordReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (deleteRecordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            recordService.deleteRecord(deleteRecordReq);
            return new BaseResponse<>(deleteRecordReq.getUserIdx());
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 기록 수정 API
     * [PATCH] /records
     */
    @ResponseBody
    @PatchMapping("/records")
    public BaseResponse<Long> patchRecord(@RequestBody PatchRecordReq patchRecordReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (patchRecordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            recordService.updateRecord(patchRecordReq);
            return new BaseResponse<>(patchRecordReq.getRecordIdx());
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 날짜별 기록조회 API
     * [GET] /records
     */
    @ResponseBody
    @GetMapping("/records")
    public BaseResponse<GetRecordRes> getRecords(@RequestBody GetRecordReq getRecordReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (getRecordReq.getUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            GetRecordRes getRecordRes = recordService.getRecords(getRecordReq);
            return new BaseResponse<>(getRecordRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
