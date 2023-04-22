package com.example.mypms.service;

import com.example.mypms.mapper.VendorMapper;
import com.example.mypms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class VendorService {
    @Value(value = "${file.save_path}")
    private String FILE_SAVE_PATH;
    @Value(value = "${file.expire_days}")
    private float EXPIRE_DAYS;
    @Autowired
    VendorMapper vendorMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        if (isQuoteBeAgreed(qid))
            return 0;
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

    /**
     * 获取采购状态
     *
     * @param pid 采购id
     * @param uid 用户id
     * @return 采购状态
     */
    public int getStatus(int pid, String uid) {
        return vendorMapper.getStatusByPidAndUid(pid, uid);
    }

    /**
     * 更新采购记录中的合同相应字段
     *
     * @param pid         采购id
     * @param filename    文件名
     * @param newFilename 新文件名
     * @return 1 - 更新成功, 0 - 更新失败
     */
    public int updateContract(int pid, String filename, String newFilename, double file_size) {
        String path = FILE_SAVE_PATH + newFilename;
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((long) (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRE_DAYS)));
        return vendorMapper.updateContract(pid, filename, path, expireTime, file_size);
    }

    /**
     * 获取合同存储路径与文件名
     *
     * @param pid 采购id
     * @param uid 采购员用户id
     * @return 合同存储路径与文件名
     */
    public Contract getContractPathAndName(int pid, String uid) {
        int status = vendorMapper.getStatusByPidAndUid(pid, uid);
        if (status == 2)
            return vendorMapper.getContractPathAndName(pid);
        return null;
    }

    public int deleteProcurement(int pid, String uid) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        return vendorMapper.deleteProcurement(pid);
    }

    private boolean isProcurementBelongToVendor(int pid, String uid) {
        return vendorMapper.getStatusByPidAndUid(pid, uid) > 0;
    }

    public int confirmSend(int pid, String uid) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        return vendorMapper.nextStep(pid, 4);
    }

    public int updateRate(int pid, String uid, double rate) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        int status = vendorMapper.getStatusByPidAndUid(pid, uid);
        if (status != 7)
            return 0;
        String p_uid = vendorMapper.getPuidByPid(pid);
        int r = vendorMapper.nextStep(pid, status);
        if (r == 0)
            return 0;
        return vendorMapper.updateRate(p_uid, rate);
    }

    public BasicInfo getBasicInfo(String uid) {
        int demands_count = getAllDemandsCount();
        int quotes_count = getQuotesCountByUid(uid);
        int processing_count = getProcurementsCountByUid(uid);
        float current_rate = vendorMapper.getVendorRate(uid);
        ArrayList<LineChartData> chartData = vendorMapper.getChartData(uid);
        BasicInfo basicInfo = new BasicInfo();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        for (LineChartData chartDataItem :
                chartData) {
            labels.add(chartDataItem.getLabel());
            counts.add(chartDataItem.getCount());
        }
        basicInfo.setLabels(labels);
        basicInfo.setCounts(counts);
        basicInfo.setDemands_count(demands_count);
        basicInfo.setQuotes_count(quotes_count);
        basicInfo.setProcessing_count(processing_count);
        basicInfo.setCurrent_rate(current_rate);
        return basicInfo;
    }

    public boolean isQuoteBeAgreed(int qid) {
        int r = vendorMapper.isQuoteBeAgreed(qid);
        return r > 0;
    }

    public StatusCount getStatusCount(String v_uid) {
        return vendorMapper.getStatusCount(v_uid);
    }

    public ArrayList<Procurement> getNeedActionProcurement(String v_uid) {
        return vendorMapper.getNeedActionProcurement(v_uid);
    }

    public ArrayList<ProcurementDemand> getLatestDemands() {
        return vendorMapper.getLatestDemands();
    }
}
