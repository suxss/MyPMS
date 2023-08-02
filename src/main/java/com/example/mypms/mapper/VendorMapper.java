package com.example.mypms.mapper;

import com.example.mypms.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface VendorMapper {
    ArrayList<ProcurementDemand> getAllDemands(int offset, int size);

    int getAllDemandsCount();

    int addQuote(Quote quote);

    ArrayList<Quote> getQuotesByUid(String v_uid, int offset, int size);

    int getQuotesCountByUid(String v_uid);

    int deleteQuote(int qid, String v_uid);

    ArrayList<Procurement> getProcurementsByUid(String v_uid, int offset, int size);

    int getProcurementsCountByUid(String v_uid);

    Purchaser getPurchaserByUid(String p_uid);

    int getStatusByPidAndUid(int pid, String uid);

    int updateContract(int pid, String filename, String path, String expire_time, double file_size);

    Contract getContractPathAndName(int pid);

    int deleteProcurement(int pid);

    int nextStep(int pid, int current_status);

    int updateRate(String uid, double rate);

    String getPuidByPid(int pid);

    int finishProcurement(int pid);

    float getVendorRate(String uid);

    ArrayList<LineChartData> getChartData(String uid);

    int isQuoteBeAgreed(int qid);

    StatusCount getStatusCount(String v_uid);

    ArrayList<Procurement> getNeedActionProcurement(String v_uid);

    ArrayList<ProcurementDemand> getLatestDemands();

    Procurement getProcurementByPid(String v_uid, int pid);
}
