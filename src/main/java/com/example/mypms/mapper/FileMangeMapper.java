package com.example.mypms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface FileMangeMapper {
    ArrayList<Integer> getExpireContractPid();

    String getContractNameByPid(int pid);

    int resetStatusByPid(int pid);
}
