package com.example.demo.src.record;

import com.example.demo.config.BaseException;
import com.example.demo.src.category.CategoryRepository;
import com.example.demo.src.goal.GoalRepository;
import com.example.demo.src.goal.model.GoalEntity;
import com.example.demo.src.record.model.*;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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

    final int SHOWN_SIZE = 3;
    final int LAST_INDEX_OF_DATE_FORMAT = 10;
    final int LAST_INDEX_OF_DATETIME = 19;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public void updateRecord(PatchRecordReq patchRecordReq) throws BaseException {
        try {
            String date = patchRecordReq.getDate().substring(0, LAST_INDEX_OF_DATETIME);
            RecordEntity record = recordRepository.findById(patchRecordReq.getRecordIdx()).orElse(null);
            record.update(patchRecordReq.getAmount(),
                    categoryRepository.findById(patchRecordReq.getCategoryIdx()).orElse(null),
                    LocalDateTime.parse(date, format));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetRecordRes getDailyRecords(GetRecordReq getRecordReq, boolean part) throws BaseException {
        try {
            String date = getRecordReq.getDate().substring(0, LAST_INDEX_OF_DATE_FORMAT);
            LocalDateTime dayStart = LocalDateTime.parse(date + " 00:00:00", format);
            LocalDateTime dayEnd = LocalDateTime.parse(date + " 23:59:59", format);

            Sort.Direction order = (part) ? Sort.Direction.DESC : Sort.Direction.ASC;
            List<RecordEntity> records = recordRepository.findAllByDateBetweenAndUserAndGoal(
                    dayStart, dayEnd,
                    userRepository.findById(getRecordReq.getUserIdx()).orElse(null),
                    goalRepository.findById(getRecordReq.getGoalIdx()).orElse(null),
                    Sort.by(order, "date")
            );

            List<RecordByDate> collect = records.stream()
                    .map(m -> new RecordByDate(m.getId(), m.getCategory().getFlag(), m.getCategory().getCategory_name(), m.getAmount()))
                    .collect(Collectors.toList());

            if (!part) {
                return new GetRecordRes(date, collect);
            } else {
                if (collect.size() < SHOWN_SIZE) {
                    return new GetRecordRes(date, collect);
                } else {
                    return new GetRecordRes(date, new ArrayList<>(collect.subList(0, SHOWN_SIZE)));
                }
            }
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRecordRes> getRecords(Long userIdx, Long goalIdx, boolean type) throws BaseException {
        try {
            List<String> dates = getDates(userRepository.findById(userIdx).orElse(null),
                    goalRepository.findById(goalIdx).orElse(null), type);
            List<GetRecordRes> getRecordRes = new ArrayList<>();
            for (String date : dates)
                getRecordRes.add(getDailyRecords(new GetRecordReq(userIdx, goalIdx, date), true));
            return getRecordRes;
        } catch (BaseException e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<String> getDates(UserEntity user, GoalEntity goal, boolean type) throws BaseException {
        try {
            List<LocalDateTime> dates = recordRepository.findDateByUserAndGoal(user, goal);
            List<String> strDates = new ArrayList<>();
            for (LocalDateTime date : dates)
                strDates.add(date.toLocalDate().toString());
            HashSet<String> setDates = new HashSet<>(strDates);
            ArrayList<String> reDates = new ArrayList<>(setDates);
            if (type)
                reDates.sort(Comparator.comparing(String::toString));
            else
                reDates.sort(Comparator.comparing(String::toString).reversed());
            return reDates;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetRecord getRecordOne(Long recordIdx) throws BaseException {
        try {
            RecordEntity record = recordRepository.findById(recordIdx).orElse(null);
            GetRecord getRecord = new GetRecord(record.getCategory().getFlag(), record.getCategory().getCategory_name(), record.getAmount());
            return getRecord;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}