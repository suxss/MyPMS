package com.example.mypms.service;

import com.example.mypms.mapper.VendorMapper;
import com.example.mypms.model.Procurement;
import com.example.mypms.model.ProcurementDemand;
import com.example.mypms.model.Purchaser;
import com.example.mypms.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VendorService {
    @Autowired
    VendorMapper vendorMapper;

    /**
     * 获取所有采购需求
     *
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 采购需求列表
     */
    public ArrayList<ProcurementDemand> getAllDemands(int curr_page, int size) {
        int offset = (curr_page - 1) * size;
        return vendorMapper.getAllDemands(offset, size);
    }

    /**
     * 获取所有采购需求数量
     *
     * @return 采购需求数量
     */
    public int getAllDemandsCount() {
        return vendorMapper.getAllDemandsCount();
    }

    /**
     * 添加报价
     *
     * @param v_uid       供应商id
     * @param p_id        采购需求id
     * @param total_price 总价
     * @param remark      备注
     * @return 1:成功 0:失败
     */
    public int addQuote(String v_uid, int p_id, int amount, float total_price, String remark) {
        Quote quote = new Quote();
        quote.setV_uid(v_uid);
        quote.setP_id(p_id);
        quote.setAmount(amount);
        quote.setTotal_price(total_price);
        quote.setRemark(remark);
        return vendorMapper.addQuote(quote);
    }

    /**
     * 获取供应商的所有报价
     *
     * @param v_uid     供应商id
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 报价列表
     */
    public ArrayList<Quote> getQuotesByUid(String v_uid, int curr_page, int size) {
        int offset = (curr_page - 1) * size;
        return vendorMapper.getQuotesByUid(v_uid, offset, size);
    }

    /**
     * 获取供应商的报价数量
     *
     * @param v_uid 供应商id
     * @return 报价数量
     */
    public int getQuotesCountByUid(String v_uid) {
        return vendorMapper.getQuotesCountByUid(v_uid);
    }

    /**
     * 删除报价
     *
     * @param qid   报价id
     * @param v_uid 供应商id
     * @return 1:成功 0:失败
     */
    public int deleteQuote(int qid, String v_uid) {
        return vendorMapper.deleteQuote(qid, v_uid);
    }

    /**
     * 获取供应商的正在进行中的所有采购
     *
     * @param v_uid     供应商id
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 采购列表
     */
    public ArrayList<Procurement> getProcurementsByUid(String v_uid, int curr_page, int size) {
        int offset = (curr_page - 1) * size;
        return vendorMapper.getProcurementsByUid(v_uid, offset, size);
    }

    /**
     * 获取供应商的正在进行中的采购数量
     *
     * @param v_uid 供应商id
     * @return 采购数量
     */
    public int getProcurementsCountByUid(String v_uid) {
        return vendorMapper.getProcurementsCountByUid(v_uid);
    }

    /**
     * 依据采购员用户id获取采购员信息
     *
     * @param p_uid 采购员用户id
     * @return 采购员信息
     */
    public Purchaser getPurchaserByUid(String p_uid) {
        return vendorMapper.getPurchaserByUid(p_uid);
    }
}
