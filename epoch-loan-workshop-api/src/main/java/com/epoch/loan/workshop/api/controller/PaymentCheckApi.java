package com.epoch.loan.workshop.api.controller;

import com.epoch.loan.workshop.common.util.LogUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentCheckApi extends BaseController {
    @GetMapping("/web/v1/inpays/checkOrder/{poutId}")
    public Object checkOrder(@PathVariable("poutId") String poutId) {

        Map res = new HashMap();
        res.put("code", 200);
        res.put("msg", "success");
        boolean data = paymentCallbackService.checkOrder(poutId);
        res.put("data", data);

        return res;
    }
}
