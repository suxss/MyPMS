package com.example.mypms.mapper;


import com.example.mypms.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface PurchaserMapper {
    int addDemand(ProcurementDemand demand);

    ArrayList<ProcurementDemand> getDemandsByUid(String p_uid, int offset, int size);

    int getDemandsCountByUid(String p_uid);

    int deleteDemand(int pdid, String p_uid);

    ArrayList<Quote> getQuotesByPdid(int pdid, int offset, int size);

    int getQuotesCountByPdid(int pdid);

    int isDemandBelongToPurchaser(int pdid, String p_uid);

    int deleteOtherQuotesByPdidAndQid(int pdid, int qid);

    int invalidDemandByPdidAndUid(int pdid, String p_uid);

    ArrayList<Procurement> getProcurementsByUid(String p_uid, int offset, int size);

    int getProcurementsCountByUid(String p_uid);

    Vendor getVendorByUid(String v_uid);

    int addProcurement(Procurement procurement);

    int getPdidByQidAndUid(int qid, String uid);

    int deleteAllQuotesByPdid(int pdid);

    int getStatusByPidAndUid(int pid, String uid);

    int updateContract(int pid, String filename, String path, String expire_time, double file_size);

    Contract getContractPathAndName(int pid);

    int deleteProcurement(int pid);

    int nextStep(int pid, int current_status);

    int updateRate(String uid, double rate);

    String getVuidByPid(int pid);

    int finishProcurement(int pid);

    float getPurchaserRate(String uid);

    ArrayList<LineChartData> getChartData(String uid);

    int getAllQuotesCountByUid(String uid);

    StatusCount getStatusCount(String p_uid);

    ArrayList<Procurement> getNeedActionProcurement(String p_uid);

    ArrayList<ProcurementDemand> getLatestQuotesByPid(String p_uid);
}
