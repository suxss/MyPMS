package com.example.mypms.controller;

import com.example.mypms.model.*;
import com.example.mypms.service.FileManageService;
import com.example.mypms.service.PurchaserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

@RestController
public class PurchaserController {
    @Autowired
    PurchaserService purchaserService;
    @Autowired
    FileManageService fileManageService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/p/add/demand", method = RequestMethod.POST)
    public ResultJson addDemand(HttpServletRequest request, @RequestBody Map<String, Object> demand) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            logger.error("addDemand >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        String product_name = (String) demand.get("product_name");
        int amount;
        try {
            amount = parseInt((String) demand.get("amount"));
        } catch (NumberFormatException e) {
            logger.error("addDemand >> amount is not a number");
            resultJson.setCode(-1);
            resultJson.setMsg("数量必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            logger.error("addDemand >> amount is null");
            resultJson.setCode(-1);
            resultJson.setMsg("数量不能为空");
            return resultJson;
        }
        String expire_date = (String) demand.get("expire_date");
        Date expire;
        try {
            expire = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expire_date);
        } catch (Exception e) {
            logger.error("addDemand >> expire_date is not a date");
            resultJson.setCode(-1);
            resultJson.setMsg("过期日期必须为日期");
            return resultJson;
        }
        if (expire.before(new Date())) {
            logger.error("addDemand >> expire_date is before today");
            resultJson.setCode(-1);
            resultJson.setMsg("过期日期必须在今天之后");
            return resultJson;
        }
        String remark = (String) demand.get("remark");
        logger.info("addDemand >> product_name:" + product_name + ", amount: " + amount + ", expire_date: " + expire_date + ", remark: " + remark);
        int r = purchaserService.addDemand(user.getUid(), product_name, amount, expire_date, remark);
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("添加成功");
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("添加失败");
        }
        return resultJson;
    }

    @RequestMapping(value = "/p/query/demands", method = RequestMethod.GET)
    public DatasJson getDemandList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        try {
            curr = parseInt((String) params.get("curr"));
            nums = parseInt((String) params.get("nums"));
        } catch (NumberFormatException e) {
            logger.error("getDemandList >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        } catch (NullPointerException e) {
            logger.error("getDemandList >> curr or nums is null");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量不能为空");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        if (user == null) {
            logger.error("getDemandList >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        logger.info("getDemandList >> curr:" + curr + ", nums: " + nums);
        ArrayList<ProcurementDemand> demands = purchaserService.getDemandsByUid(user.getUid(), curr, nums);
        if (demands == null) {
            demands = new ArrayList<>();
        }
        datasJson.setData(demands);
        datasJson.setCode(0);
        datasJson.setMsg("获取成功");
        datasJson.setTotal(purchaserService.getDemandsCountByUid(user.getUid()));
        return datasJson;
    }

    @RequestMapping(value = "/p/query/quote", method = RequestMethod.GET)
    public DatasJson getQuoteList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums, pdid;
        try {
            curr = parseInt((String) params.get("curr"));
            nums = parseInt((String) params.get("nums"));
            pdid = parseInt((String) params.get("id"));
        } catch (NumberFormatException e) {
            logger.error("getQuoteList >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(null);
            return datasJson;
        } catch (NullPointerException e) {
            logger.error("getQuoteList >> curr or nums is null");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量不能为空");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        if (user == null) {
            logger.error("getQuoteList >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        logger.info("getQuoteList >> curr:" + curr + ", nums: " + nums);
        ArrayList<Quote> quotes = purchaserService.getQuotesByPdid(pdid, user.getUid(), curr, nums);
        if (quotes == null) {
            quotes = new ArrayList<>();
        }
        datasJson.setData(quotes);
        datasJson.setCode(0);
        datasJson.setMsg("获取成功");
        datasJson.setTotal(purchaserService.getQuotesCountByPdid(pdid, user.getUid()));
        return datasJson;
    }

    @RequestMapping(value = "/p/query/processing", method = RequestMethod.GET)
    public DatasJson getProcurementList(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        try {
            curr = parseInt((String) params.get("curr"));
            nums = parseInt((String) params.get("nums"));
        } catch (NumberFormatException e) {
            logger.error("getProcumentList >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        } catch (NullPointerException e) {
            logger.error("getProcumentList >> curr or nums is null");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量不能为空");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        if (user == null) {
            logger.error("getProcumentList >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        logger.info("getProcumentList >> curr:" + curr + ", nums: " + nums);
        ArrayList<Procurement> procurements = purchaserService.getProcurementsByUid(user.getUid(), curr, nums);
        if (procurements == null) {
            procurements = new ArrayList<>();
        }
        datasJson.setData(procurements);
        datasJson.setCode(0);
        datasJson.setMsg("获取成功");
        datasJson.setTotal(purchaserService.getProcurementsCountByUid(user.getUid()));
        return datasJson;
    }

    @RequestMapping(value = "/p/basic_info", method = RequestMethod.GET)
    public ResultJson getUserInfo(HttpServletRequest request) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            logger.error("getUserInfo >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            resultJson.setData(new User());
            return resultJson;
        }
        logger.info("getUserInfo >> user: " + user);
        resultJson.setCode(0);
        resultJson.setMsg("获取成功");
        user.setPwd("");
        resultJson.setData(user);
        return resultJson;
    }

    @RequestMapping(value = "/p/query/v_info", method = RequestMethod.POST)
    public ResultJson getVendorInfo(@RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        String v_uid = (String) params.get("vid");
        if (v_uid == null) {
            logger.error("getVendorInfo >> v_uid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("参数错误");
            resultJson.setData(new Vendor());
            return resultJson;
        }
        Vendor vendor = purchaserService.getVendorInfoByUid(v_uid);
        if (vendor == null) {
            logger.error("getVendorInfo >> vendor is null");
            resultJson.setCode(-1);
            resultJson.setMsg("供应商不存在");
            resultJson.setData(new Vendor());
            return resultJson;
        }
        resultJson.setCode(0);
        resultJson.setMsg("获取成功");
        resultJson.setData(vendor);
        return resultJson;
    }

    @RequestMapping(value = "/p/agree/quote", method = RequestMethod.POST)
    public ResultJson agreeQuote(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int qid;
        try {
            qid = parseInt((String) params.get("qid"));
        } catch (NumberFormatException e) {
            logger.error("agreeQuote >> qid is not a number");
            resultJson.setCode(-1);
            resultJson.setMsg("报价id必须为数字");
            resultJson.setData(null);
            return resultJson;
        } catch (NullPointerException e) {
            logger.error("agreeQuote >> qid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("报价id不能为空");
            resultJson.setData(null);
            return resultJson;
        }
        if (user == null) {
            logger.error("agreeQuote >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            resultJson.setData(null);
            return resultJson;
        }
        logger.info("agreeQuote >> qid:" + qid);
        int r = purchaserService.agreeQuote(qid, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("操作成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("操作失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/p/delete/demand", method = RequestMethod.POST)
    public ResultJson deleteDemand(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int pdid;
        try {
            pdid = parseInt((String) params.get("pdid"));
        } catch (NumberFormatException e) {
            logger.error("deleteDemand >> pdid is not a number");
            resultJson.setCode(-1);
            resultJson.setMsg("需求id必须为数字");
            resultJson.setData(null);
            return resultJson;
        } catch (NullPointerException e) {
            logger.error("deleteDemand >> pdid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("需求id不能为空");
            resultJson.setData(null);
            return resultJson;
        }
        if (user == null) {
            logger.error("deleteDemand >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            resultJson.setData(null);
            return resultJson;
        }
        logger.info("deleteDemand >> pdid:" + pdid);
        int r = purchaserService.deleteDemand(pdid, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("操作成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("操作失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/p/upload/contract", method = RequestMethod.POST)
    public ResultJson uploadContract(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("id") int id) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        if (purchaserService.getStatus(id, user.getUid()) != 1) {
            resultJson.setCode(-1);
            resultJson.setMsg("当前流程无需上传合同");
            return resultJson;
        }
        String newFilename = fileManageService.getNewFileName(file);
        try {
            fileManageService.fileUpload(file.getBytes(), newFilename);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            resultJson.setCode(-1);
            resultJson.setMsg("文件上传出错");
            return resultJson;
        }
        int r = purchaserService.updateContract(id, file.getOriginalFilename(), newFilename);
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("上传成功");
        }
        logger.info("upload contract >> filename: " + file.getOriginalFilename() + ", id: " + id + ", uid:" + user.getUid());
        return resultJson;
    }

    @RequestMapping(value = "/p/download/contract", method = RequestMethod.GET)
    public void downloadContract(HttpServletRequest request, @RequestParam("id") int id, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return;
        }
        Contract contract = purchaserService.getContractPathAndName(id, user.getUid());
        if (contract == null) {
            return;
        }
        try {
            InputStream inputStream = new FileInputStream(contract.getPath());// 文件的存放路径
            response.reset();
            response.setContentType("application/octet-stream");
            String filename = contract.getName();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/p/query/status", method = RequestMethod.GET)
    public ResultJson getStatus(HttpServletRequest request, @RequestParam("id") int id) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
        }
        int status = purchaserService.getStatus(id, user.getUid());
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        resultJson.setData(hashMap);
        return resultJson;
    }

    @RequestMapping(value = "/p/delete/processing", method = RequestMethod.POST)
    public ResultJson deleteProcurement(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int id;
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        try {
            id = parseInt((String) params.get("id"));
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id不能为空");
            return resultJson;
        }
        int r = purchaserService.deleteProcurement(id, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("删除成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("删除失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/p/agree/contract", method = RequestMethod.POST)
    public ResultJson agreeContract(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int id;
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        try {
            id = parseInt((String) params.get("id"));
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id不能为空");
            return resultJson;
        }
        int r = purchaserService.agreeContract(id, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("操作成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("操作失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/p/confirm/delivery", method = RequestMethod.POST)
    public ResultJson confirmDelivery(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int id;
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        try {
            id = parseInt((String) params.get("id"));
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id不能为空");
            return resultJson;
        }
        int r = purchaserService.confirmDelivery(id, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("操作成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("操作失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/p/update/rate", method = RequestMethod.POST)
    public ResultJson updateRate(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int id;
        double rate;
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        try {
            id = parseInt(params.get("id").toString());
            rate = Double.parseDouble((params.get("rate").toString()));
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id, rate 必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id, rate 不能为空");
            return resultJson;
        }
        if (purchaserService.getStatus(id, user.getUid()) != 6) {
            resultJson.setCode(-1);
            resultJson.setMsg("当前流程无需评价");
            return resultJson;
        }
        int r = purchaserService.updateRate(id, user.getUid(), rate);
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("修改成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("修改失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "p/query/info", method = RequestMethod.GET)
    public ResultJson getBasicInfo(HttpServletRequest request) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        BasicInfo basicInfo = purchaserService.getBasicInfo(user.getUid());
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(basicInfo);
        return resultJson;
    }
}
