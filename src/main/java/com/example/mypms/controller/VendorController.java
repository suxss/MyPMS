package com.example.mypms.controller;

import com.example.mypms.model.*;
import com.example.mypms.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class VendorController {
    @Autowired
    VendorService vendorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/v/query/demands", method = RequestMethod.GET)
    public DatasJson getAllDemands(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getAllDemands >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        try {
            curr = Integer.parseInt((String) params.get("curr"));
            nums = Integer.parseInt((String) params.get("nums"));
        } catch (NumberFormatException e) {
            logger.error("getAllDemands >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        } catch (NullPointerException e) {
            logger.error("getAllDemands >> curr or nums is null");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量不能为空");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        datasJson.setCode(0);
        datasJson.setMsg("查询成功");
        datasJson.setTotal(vendorService.getAllDemandsCount());
        ArrayList<ProcurementDemand> demands = vendorService.getAllDemands(curr, nums);
        if (demands == null) {
            demands = new ArrayList<>();
        }
        datasJson.setData(demands);
        return datasJson;
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
            demand_id = Integer.parseInt((String) quote.get("demand_id"));
            amount = Integer.parseInt((String) quote.get("amount"));
            total_price = Float.parseFloat((String) quote.get("total_price"));
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
        String remark = (String) quote.get("remark");
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
    public DatasJson getAllQuotes(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getAllQuotes >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            return datasJson;
        }
        try {
            curr = Integer.parseInt((String) params.get("curr"));
            nums = Integer.parseInt((String) params.get("nums"));
        } catch (NumberFormatException e) {
            logger.error("getAllQuotes >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        } catch (NullPointerException e) {
            logger.error("getAllQuotes >> curr or nums is null");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量不能为空");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        datasJson.setCode(0);
        datasJson.setMsg("查询成功");
        datasJson.setTotal(vendorService.getQuotesCountByUid(user.getUid()));
        ArrayList<Quote> quotes = vendorService.getQuotesByUid(user.getUid(), curr, nums);
        if (quotes == null) {
            quotes = new ArrayList<>();
        }
        datasJson.setData(quotes);
        return datasJson;
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
            qid = Integer.parseInt((String) params.get("qid"));
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
    public DatasJson getProcurements(HttpServletRequest request, @RequestParam Map<String, Object> params) {
        DatasJson datasJson = new DatasJson();
        User user = (User) request.getSession().getAttribute("user");
        int curr, nums;
        if (user == null) {
            logger.error("getProcurement >> user is null");
            datasJson.setCode(-1);
            datasJson.setMsg("请先登录");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        try {
            curr = Integer.parseInt((String) params.get("curr"));
            nums = Integer.parseInt((String) params.get("nums"));
        } catch (NumberFormatException e) {
            logger.error("getProcurement >> curr or nums is not a number");
            datasJson.setCode(-1);
            datasJson.setMsg("页码或数量必须为数字");
            datasJson.setTotal(0);
            datasJson.setData(new ArrayList<>());
            return datasJson;
        }
        datasJson.setCode(0);
        datasJson.setMsg("查询成功");
        datasJson.setTotal(vendorService.getProcurementsCountByUid(user.getUid()));
        ArrayList<Procurement> procurements = vendorService.getProcurementsByUid(user.getUid(), curr, nums);
        if (procurements == null) {
            procurements = new ArrayList<>();
        }
        datasJson.setData(procurements);
        return datasJson;
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
        String p_uid = (String) params.get("pid");
        if (p_uid == null) {
            logger.error("getPurchaserInfo >> p_uid is null");
            resultJson.setCode(-1);
            resultJson.setMsg("采购商编号不能为空");
            resultJson.setData(new Purchaser());
            return resultJson;
        }
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
}
