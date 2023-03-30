package com.example.mypms.mapper;


import com.example.mypms.model.RequestLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LogMapper {
    public int insertLog(RequestLog requestLog);

    public int deleteLog(String time);
    public int deleteLog();
}
