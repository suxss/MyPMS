package com.example.mypms.service;

import com.example.mypms.model.Procurement;
import com.example.mypms.model.Quote;
import com.example.mypms.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mypms.mapper.PurchaserMapper;
import com.example.mypms.model.ProcurementDemand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class PurchaserService {
    @Autowired
    PurchaserMapper purchaserMapper;

    /**
     * 添加采购需求
     *
     * @param p_uid        采购员id
     * @param product_name 产品名称
     * @param amount       采购数量
     * @param expire_time  过期时间
     * @param remark       备注
     * @return 1:成功 0:失败
     */
    public int addDemand(String p_uid, String product_name, int amount, String expire_time, String remark) {
        String start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ProcurementDemand demand = new ProcurementDemand();
        demand.setP_uid(p_uid);
        demand.setProduct_name(product_name);
        demand.setAmount(String.valueOf(amount));
        demand.setExpire_time(expire_time);
        demand.setStart_time(start_time);
        demand.setRemark(remark);
        return purchaserMapper.addDemand(demand);
    }

    /**
     * 获取采购商的所有采购需求
     *
     * @param p_uid     采购员id
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 采购需求列表
     */
    public ArrayList<ProcurementDemand> getDemandsByUid(String p_uid, int curr_page, int size) {
        int offset = (curr_page - 1) * size;
        return purchaserMapper.getDemandsByUid(p_uid, offset, size);
    }

    /**
     * 获取采购商的采购需求数量
     *
     * @param p_uid 采购员id
     * @return 采购需求数量
     */
    public int getDemandsCountByUid(String p_uid) {
        return purchaserMapper.getDemandsCountByUid(p_uid);
    }

    /**
     * 删除采购需求
     * 报价表(quote)将依照级联删除策略自动删除对应报价
     *
     * @param pdid  采购需求id
     * @param p_uid 采购员id
     * @return 1:成功 0:失败
     */
    public int deleteDemand(int pdid, String p_uid) {
        return purchaserMapper.deleteDemand(pdid, p_uid);
    }

    /**
     * 获取采购需求的所有报价
     *
     * @param pdid      采购需求id
     * @param p_uid     采购员id
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 报价列表
     */
    public ArrayList<Quote> getQuotesByPdid(int pdid, String p_uid, int curr_page, int size) {
        if (!isDemandBelongToPurchaser(pdid, p_uid)) {
            return null;
        }
        int offset = (curr_page - 1) * size;
        return purchaserMapper.getQuotesByPdid(pdid, offset, size);
    }

    /**
     * 获取采购需求的报价数量
     *
     * @param pdid  采购需求id
     * @param p_uid 采购员id
     * @return 报价数量
     */
    public int getQuotesCountByPdid(int pdid, String p_uid) {
        if (!isDemandBelongToPurchaser(pdid, p_uid)) {
            return 0;
        }
        return purchaserMapper.getQuotesCountByPdid(pdid);
    }

    /**
     * 采购需求是否属于采购商
     *
     * @param pdid  采购需求id
     * @param p_uid 采购员id
     * @return true:属于 false:不属于
     */
    private Boolean isDemandBelongToPurchaser(int pdid, String p_uid) {
        return purchaserMapper.isDemandBelongToPurchaser(pdid, p_uid) > 0;
    }

    /**
     * 使采购需求失效
     *
     * @param pdid  采购需求id
     * @param p_uid 采购员id
     * @return 1:成功 0:失败
     */
    private int inValidDemandByPdidAndUid(int pdid, String p_uid) {
        return purchaserMapper.invalidDemandByPdidAndUid(pdid, p_uid);
    }

    /**
     * 获取采购商的所有正在进行中的采购
     *
     * @param p_uid     采购员id
     * @param curr_page 当前页
     * @param size      每页大小
     * @return 采购列表
     */
    public ArrayList<Procurement> getProcurementsByUid(String p_uid, int curr_page, int size) {
        int offset = (curr_page - 1) * size;
        return purchaserMapper.getProcurementsByUid(p_uid, offset, size);
    }

    /**
     * 获取采购商的正在进行中的采购数量
     *
     * @param p_uid 采购员id
     * @return 采购数量
     */
    public int getProcurementsCountByUid(String p_uid) {
        return purchaserMapper.getProcurementsCountByUid(p_uid);
    }

    /**
     * 依据供应商用户id获取供应商基本信息
     *
     * @param v_uid 供应商用户id
     * @return 供应商基本信息
     */
    public Vendor getVendorInfoByUid(String v_uid) {
        return purchaserMapper.getVendorByUid(v_uid);
    }

    /**
     * 依据采购员用户id与报价id，同意该报价
     * 先依据用户id与报价id获取采购需求id，再使该采购需求失效，最后创建新采购
     *
     * @param qid 报价id
     * @param uid 采购员id
     * @return 0:失败 1:成功
     */
    public int agreeQuote(int qid, String uid) {
        int pdid = getPdidByQidAndUid(qid, uid);
        if (pdid == 0) {
            return 0;
        }
        int result = inValidDemandByPdidAndUid(pdid, uid);
        if (result == 0) {
            return 0;
        }
        Procurement procurement = new Procurement();
        procurement.setQid(qid);
        procurement.setStatus(0);
        procurement.setStart_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return purchaserMapper.addProcurement(procurement);
    }

    private int getPdidByQidAndUid(int qid, String uid) {
        return purchaserMapper.getPdidByQidAndUid(qid, uid);
    }
}
