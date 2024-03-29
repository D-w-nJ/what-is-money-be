package com.example.demo.src.record.model;

import lombok.*;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor
public class PatchRecordReq {
    private Long userIdx;
    private Long recordIdx;
    private String date;
    private Long categoryIdx;
    private int amount;
}
