package com.example.mypms.service;

import com.example.mypms.mapper.PurchaserMapper;
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
public class PurchaserService {
    @Value(value = "${file.save_path}")
    private String FILE_SAVE_PATH;
    @Value(value = "${file.expire_days}")
    private float EXPIRE_DAYS;
    @Autowired
    PurchaserMapper purchaserMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        procurement.setStatus(1);
        procurement.setStart_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return purchaserMapper.addProcurement(procurement);
    }

    private int getPdidByQidAndUid(int qid, String uid) {
        return purchaserMapper.getPdidByQidAndUid(qid, uid);
    }

    /**
     * 依据 采购id 与 用户id 查询采购员是否能够上传合同
     *
     * @param pid 采购id
     * @param uid 用户id
     * @return true - 能, false - 否
     */
    public boolean canUploadContract(int pid, String uid) {
        return getStatus(pid, uid) == 1;
    }

    /**
     * 获取采购状态
     *
     * @param pid 采购id
     * @param uid 用户id
     * @return 采购状态
     */
    public int getStatus(int pid, String uid) {
        return purchaserMapper.getStatusByPidAndUid(pid, uid);
    }

    /**
     * 更新采购记录中的合同相应字段
     *
     * @param pid         采购id
     * @param filename    文件名
     * @param newFilename 新文件名
     * @return 1 - 更新成功, 0 - 更新失败
     */
    public int updateContract(int pid, String filename, String newFilename) {
        String path = FILE_SAVE_PATH + newFilename;
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((long) (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRE_DAYS)));
        return purchaserMapper.updateContract(pid, filename, path, expireTime);
    }

    /**
     * 获取合同存储路径与文件名
     *
     * @param pid 采购id
     * @param uid 采购员用户id
     * @return 合同存储路径与文件名
     */
    public Contract getContractPathAndName(int pid, String uid) {
        int status = purchaserMapper.getStatusByPidAndUid(pid, uid);
        if (status == 3)
            return purchaserMapper.getContractPathAndName(pid);
        return null;
    }

    public int deleteProcurement(int pid, String uid) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        return purchaserMapper.deleteProcurement(pid);
    }

    private boolean isProcurementBelongToVendor(int pid, String uid) {
        return purchaserMapper.getStatusByPidAndUid(pid, uid) > 0;
    }

    public int agreeContract(int pid, String uid) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        return purchaserMapper.nextStep(pid, 3);
    }

    public int confirmDelivery(int pid, String uid) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        return purchaserMapper.nextStep(pid, 5);
    }

    public int updateRate(int pid, String uid, double rate) {
        if (!isProcurementBelongToVendor(pid, uid))
            return 0;
        int status = purchaserMapper.getStatusByPidAndUid(pid, uid);
        if (status != 6)
            return 0;
        String v_uid = purchaserMapper.getVuidByPid(pid);
        int r = purchaserMapper.nextStep(pid, status);
        if (r == 0)
            return 0;
        return purchaserMapper.updateRate(v_uid, rate);
    }

    public BasicInfo getBasicInfo(String uid) {
        int demands_count = getDemandsCountByUid(uid);
        int quotes_count = purchaserMapper.getAllQuotesCountByUid(uid);
        int processing_count = getProcurementsCountByUid(uid);
        float current_rate = purchaserMapper.getPurchaserRate(uid);
        ArrayList<LineChartData> chartData = purchaserMapper.getChartData(uid);
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
}
