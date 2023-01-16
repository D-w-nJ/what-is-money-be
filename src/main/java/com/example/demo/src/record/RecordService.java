package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.record.model.PostRecordReq;
import com.example.demo.src.record.model.PostRecordRes;
import com.example.demo.src.record.model.RecordEntity;
import com.example.demo.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final CategoryRepository categoryRepository;

    public PostRecordRes createRecord(PostRecordReq postRecordReq) throws BaseException {
        try {
            RecordEntity record = postRecordReq.toEntity(
                    userRepository.findById(postRecordReq.getUserIdx()),
                    goalRepository.findById(postRecordReq.getGoalIdx()),
                    categoryRepository.findById(postRecordReq.getCategory()));
            recordRepository.save(record);
            return record.toPostRecordRes();
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR)
        }
    }
}
