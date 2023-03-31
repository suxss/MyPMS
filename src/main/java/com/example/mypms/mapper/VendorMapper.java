package com.example.mypms.mapper;

import com.example.mypms.model.Procurement;
import com.example.mypms.model.ProcurementDemand;
import com.example.mypms.model.Purchaser;
import com.example.mypms.model.Quote;
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
}
