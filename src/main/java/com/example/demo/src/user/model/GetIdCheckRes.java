package com.example.demo.src.user.model;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetIdCheckRes {
    private String id_str;
    private boolean duplicate;

    public GetIdCheckRes(String id_str, boolean duplicate){
        this.id_str = id_str;
        this.duplicate = duplicate;
    }

}

