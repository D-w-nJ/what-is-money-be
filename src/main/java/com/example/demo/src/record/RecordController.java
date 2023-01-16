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
}
