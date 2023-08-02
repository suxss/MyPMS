package com.example.mypms.controller;

import com.example.mypms.model.*;
import com.example.mypms.service.FileManageService;
import com.example.mypms.service.MailService;
import com.example.mypms.service.UserService;
import com.example.mypms.service.VendorService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VendorController {
    @Autowired
    VendorService vendorService;
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;
    @Autowired
    FileManageService fileManageService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/v/query/demands", method = RequestMethod.GET)
    public DataJson getAllDemands(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DataJson dataJson = new DataJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getAllDemands >> user is null");
            dataJson.setCode(-1);
            dataJson.setMsg("请先登录");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        }
        try {
            curr = Integer.parseInt(params.get("curr").toString());
            nums = Integer.parseInt(params.get("nums").toString());
        } catch (NumberFormatException e) {
            logger.error("getAllDemands >> curr or nums is not a number");
            dataJson.setCode(-1);
            dataJson.setMsg("页码或数量必须为数字");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        } catch (NullPointerException e) {
            logger.error("getAllDemands >> curr or nums is null");
            dataJson.setCode(-1);
            dataJson.setMsg("页码或数量不能为空");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        }
        dataJson.setCode(0);
        dataJson.setMsg("查询成功");
        dataJson.setTotal(vendorService.getAllDemandsCount());
        ArrayList<ProcurementDemand> demands = vendorService.getAllDemands(curr, nums);
        if (demands == null) {
            demands = new ArrayList<>();
        }
        dataJson.setData(demands);
        return dataJson;
    }

    @RequestMapping(value = "/v/add/quote", method = RequestMethod.POST)
    public ResultJson addQuote(HttpServletRequest request, @RequestBody Map<String, Object> quote) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            logger.error("addQuote >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        int demand_id, amount;
        float total_price;
        try {
            demand_id = Integer.parseInt(quote.get("demand_id").toString());
            amount = Integer.parseInt(quote.get("amount").toString());
            total_price = Float.parseFloat(quote.get("total_price").toString());
        } catch (NumberFormatException e) {
            logger.error("addQuote >> demand_id, amount or total_price is not a number");
            resultJson.setCode(-1);
            resultJson.setMsg("需求编号、数量或总价必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            logger.error("addQuote >> demand_id, amount or total_price is null");
            resultJson.setCode(-1);
            resultJson.setMsg("需求编号、数量或总价不能为空");
            return resultJson;
        }
        String remark;
        try {
            remark = quote.get("remark").toString();
        } catch (NullPointerException e) {
            remark = "";
        }
        if (vendorService.addQuote(user.getUid(), demand_id, amount, total_price, remark) > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("添加成功");
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("添加报价失败");
        }
        return resultJson;
    }

    @RequestMapping(value = "/v/query/quotes", method = RequestMethod.GET)
    public DataJson getAllQuotes(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DataJson dataJson = new DataJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getAllQuotes >> user is null");
            dataJson.setCode(-1);
            dataJson.setMsg("请先登录");
            return dataJson;
        }
        try {
            curr = Integer.parseInt(params.get("curr").toString());
            nums = Integer.parseInt(params.get("nums").toString());
        } catch (NumberFormatException e) {
            logger.error("getAllQuotes >> curr or nums is not a number");
            dataJson.setCode(-1);
            dataJson.setMsg("页码或数量必须为数字");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        } catch (NullPointerException e) {
            logger.error("getAllQuotes >> curr or nums is null");
            dataJson.setCode(-1);
            dataJson.setMsg("页码或数量不能为空");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        }
        dataJson.setCode(0);
        dataJson.setMsg("查询成功");
        dataJson.setTotal(vendorService.getQuotesCountByUid(user.getUid()));
        ArrayList<Quote> quotes = vendorService.getQuotesByUid(user.getUid(), curr, nums);
        if (quotes == null) {
            quotes = new ArrayList<>();
        }
        dataJson.setData(quotes);
        return dataJson;
    }

    @RequestMapping(value = "/v/delete/quote", method = RequestMethod.POST)
    public ResultJson deleteQuote(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            logger.error("deleteQuote >> user is null");
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        int qid;
        try {
            qid = Integer.parseInt(params.get("qid").toString());
        } catch (NumberFormatException e) {
            logger.error("deleteQuote >> qid is not a number");
            resultJson.setCode(-1);
            resultJson.setMsg("报价编号必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            logger.error("deleteQuote >> qid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("报价编号不能为空");
            return resultJson;
        }
        if (vendorService.deleteQuote(qid, user.getUid()) > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("删除成功");
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("删除报价失败");
        }
        return resultJson;
    }

    @RequestMapping(value = "/v/query/processing", method = RequestMethod.GET)
    public DataJson getProcurements(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DataJson dataJson = new DataJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getProcurement >> user is null");
            dataJson.setCode(-1);
            dataJson.setMsg("请先登录");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        }
        try {
            curr = Integer.parseInt(params.get("curr").toString());
            nums = Integer.parseInt(params.get("nums").toString());
        } catch (NumberFormatException e) {
            logger.error("getProcurement >> curr or nums is not a number");
            dataJson.setCode(-1);
            dataJson.setMsg("页码或数量必须为数字");
            dataJson.setTotal(0);
            dataJson.setData(new ArrayList<>());
            return dataJson;
        }
        dataJson.setCode(0);
        dataJson.setMsg("查询成功");
        dataJson.setTotal(vendorService.getProcurementsCountByUid(user.getUid()));
        ArrayList<Procurement> procurements = vendorService.getProcurementsByUid(user.getUid(), curr, nums);
        if (procurements == null) {
            procurements = new ArrayList<>();
        }
        dataJson.setData(procurements);
        return dataJson;
    }

    @RequestMapping(value = "/v/basic_info", method = RequestMethod.GET)
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
        resultJson.setMsg("查询成功");
        user.setPwd("");
        resultJson.setData(user);
        return resultJson;
    }

    @RequestMapping(value = "/v/query/p_info", method = RequestMethod.POST)
    public ResultJson getPurchaserInfo(@RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        if (params.get("pid") == null) {
            logger.error("getPurchaserInfo >> p_uid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("采购商编号不能为空");
            resultJson.setData(new Purchaser());
            return resultJson;
        }
        String p_uid = params.get("pid").toString();
        Purchaser purchaser = vendorService.getPurchaserByUid(p_uid);
        if (purchaser == null) {
            logger.error("getPurchaserInfo >> purchaser is null");
            resultJson.setCode(-1);
            resultJson.setMsg("采购商不存在");
            resultJson.setData(new Purchaser());
            return resultJson;
        }
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(purchaser);
        return resultJson;
    }

    @RequestMapping(value = "/v/download/contract", method = RequestMethod.GET)
    public void downloadContract(HttpServletRequest request, @RequestParam("id") int id, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return;
        }
        Contract contract = vendorService.getContractPathAndName(id, user.getUid());
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

    @RequestMapping(value = "/v/upload/contract", method = RequestMethod.POST)
    public ResultJson uploadContract(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("id") int id) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        if (vendorService.getStatus(id, user.getUid()) != 2) {
            resultJson.setCode(-1);
            resultJson.setMsg("当前流程无需上传合同");
            return resultJson;
        }
        String oldFilename = fileManageService.getContractNameByPid(id);
        fileManageService.deleteFile(oldFilename);
        String newFilename = fileManageService.getNewFileName(file);
        double file_size = file.getSize() / 1024.0 / 1024.0;
        try {
            fileManageService.fileUpload(file.getBytes(), newFilename);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            resultJson.setCode(-1);
            resultJson.setMsg("文件上传出错");
            return resultJson;
        }
        int r = vendorService.updateContract(id, file.getOriginalFilename(), newFilename, file_size);
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("上传成功");
        }
        logger.info("upload contract >> filename: " + file.getOriginalFilename() + ", id: " + id + ", uid:" + user.getUid());
        return resultJson;
    }

    @RequestMapping(value = "/v/query/status", method = RequestMethod.GET)
    public ResultJson getStatus(HttpServletRequest request, @RequestParam("id") int id) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
        }
        int status = vendorService.getStatus(id, user.getUid());
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        resultJson.setData(hashMap);
        return resultJson;
    }

    @RequestMapping(value = "/v/delete/processing", method = RequestMethod.POST)
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
            id = Integer.parseInt(params.get("id").toString());
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id不能为空");
            return resultJson;
        }
        Procurement r = vendorService.deleteProcurement(id, user.getUid());
        if (r != null) {
            String p_uid = r.getP_uid();
            String p_email_address = userService.getEmailByUid(p_uid);
            logger.info("find email >> uid: " + p_uid + ",  email: " + p_email_address);
            String notice_content = r.getP_name() + "(email: " + user.getEmail() + ")" + "已取消采购(id: " + r.getPid() + ", 产品名:" + r.getProduct_name() + ", 数量: " + r.getAmount() + ")";
            mailService.sendMailNotice(r.getV_name(), p_email_address, notice_content);
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

    @RequestMapping(value = "/v/confirm/send", method = RequestMethod.POST)
    public ResultJson confirmSend(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        int id;
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        try {
            id = Integer.parseInt(params.get("id").toString());
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id不能为空");
            return resultJson;
        }
        int r = vendorService.confirmSend(id, user.getUid());
        if (r > 0) {
            resultJson.setCode(0);
            resultJson.setMsg("确认成功");
            resultJson.setData(null);
            return resultJson;
        } else {
            resultJson.setCode(-1);
            resultJson.setMsg("确认失败");
            resultJson.setData(null);
            return resultJson;
        }
    }

    @RequestMapping(value = "/v/update/rate", method = RequestMethod.POST)
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
            id = Integer.parseInt(params.get("id").toString());
            rate = Double.parseDouble(params.get("rate").toString());
        } catch (NumberFormatException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id, rate 必须为数字");
            return resultJson;
        } catch (NullPointerException e) {
            resultJson.setCode(-1);
            resultJson.setMsg("id, rate 不能为空");
            return resultJson;
        }
        String oldFilename = fileManageService.getContractNameByPid(id);
        fileManageService.deleteFile(oldFilename);
        if (vendorService.getStatus(id, user.getUid()) != 7) {
            resultJson.setCode(-1);
            resultJson.setMsg("当前流程无需评价");
            return resultJson;
        }
        int r = vendorService.updateRate(id, user.getUid(), rate);
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

    @RequestMapping(value = "v/query/info", method = RequestMethod.GET)
    public ResultJson getBasicInfo(HttpServletRequest request) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        BasicInfo basicInfo = vendorService.getBasicInfo(user.getUid());
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(basicInfo);
        return resultJson;
    }

    @RequestMapping(value = "/v/query/status_count", method = RequestMethod.GET)
    public ResultJson getStatusCount(HttpServletRequest request) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setCode(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        StatusCount statusCount = vendorService.getStatusCount(user.getUid());
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(statusCount);
        return resultJson;
    }

    @RequestMapping(value = "/v/query/need_action", method = RequestMethod.GET)
    public ResultJson getNeedActionProcurement(HttpServletRequest request) {
        ResultJson resultJson = new ResultJson();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultJson.setData(-1);
            resultJson.setMsg("请先登录");
            return resultJson;
        }
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(vendorService.getNeedActionProcurement(user.getUid()));
        return resultJson;
    }

    @RequestMapping(value = "/v/query/latest_demands", method = RequestMethod.GET)
    public ResultJson getLatestDemands() {
        ResultJson resultJson = new ResultJson();
        resultJson.setCode(0);
        resultJson.setMsg("查询成功");
        resultJson.setData(vendorService.getLatestDemands());
        return resultJson;
    }
}
