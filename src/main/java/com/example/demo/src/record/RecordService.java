package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.goal.GoalRepository;
import com.example.demo.src.record.model.*;
import com.example.demo.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.config.BaseResponseStatus.*;

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
                    userRepository.findById(postRecordReq.getUserIdx()).orElse(null),
                    goalRepository.findById(postRecordReq.getGoalIdx()).orElse(null),
                    categoryRepository.findById(postRecordReq.getCategory()).orElse(null));
            recordRepository.save(record);
            return record.toPostRecordRes();
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteRecord(DeleteRecordReq deleteRecordReq) throws BaseException {
        try {
            recordRepository.deleteById(deleteRecordReq.getRecordIdx());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetRecordRes getRecords(GetRecordReq getRecordReq) throws BaseException {
        try {
            String date = getRecordReq.getDate().substring(0, getRecordReq.getDate().indexOf(" "));
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dayStart = LocalDateTime.parse(date+" 00:00:00",format);
            LocalDateTime dayEnd = LocalDateTime.parse(date+" 23:59:59",format);
            List<RecordEntity> records = recordRepository.findAllByDateBetweenAndUserAndGoal(
                    dayStart, dayEnd,
                    userRepository.findById(getRecordReq.getUserIdx()).orElse(null),
                    goalRepository.findById(getRecordReq.getGoalIdx()).orElse(null)
            );
            List<RecordByDate> collect = records.stream()
                    .map(m->new RecordByDate(m.getId(),m.isFlag(),m.getCategory().getCategory_name(),m.getAmount()))
                    .collect(Collectors.toList());
            return new GetRecordRes(date, collect);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
