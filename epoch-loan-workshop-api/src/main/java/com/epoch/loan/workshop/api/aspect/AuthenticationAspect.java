package com.epoch.loan.workshop.api.aspect;

import com.alibaba.fastjson.JSONObject;
import com.epoch.loan.workshop.api.annotated.Authentication;
import com.epoch.loan.workshop.common.authentication.TokenManager;
import com.epoch.loan.workshop.common.constant.Field;
import com.epoch.loan.workshop.common.constant.ResultEnum;
import com.epoch.loan.workshop.common.dao.mysql.LoanAppControlDao;
import com.epoch.loan.workshop.common.entity.mysql.LoanAppControlEntity;
import com.epoch.loan.workshop.common.params.User;
import com.epoch.loan.workshop.common.params.params.result.Result;
import com.epoch.loan.workshop.common.util.LogUtil;
import com.epoch.loan.workshop.common.util.ThrowableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author : Duke
 * @packageName : com.epoch.loan.workshop.api.aspect
 * @className : AuthenticationAspect
 * @createTime : 2022/4/18 15:17
 * @description : 鉴权
 */
@Aspect
@Component
@Order(1)
public class AuthenticationAspect {

    /**
     * Token工具类
     */
    @Autowired
    private TokenManager tokenManager;

    /**
     * app版本
     */
    @Autowired
    public LoanAppControlDao loanAppControlDao;


    /**
     * 定义注解@Authentication 为切入点
     */
    @Pointcut("@annotation(com.epoch.loan.workshop.api.annotated.Authentication)")
    public void authentication() {

    }

    /**
     * 校验Session
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("authentication()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 结果集
        Result result = new Result();

        try {
            // 获取注解配置
            Authentication authentication = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(Authentication.class);

            // 判断是否需要权限验证
            if (!authentication.auth()) {
                // 调用下一级
                return point.proceed();
            }

            // 获取request对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

            // Token
            String token = request.getHeader(Field.TOKEN);
            LogUtil.sysInfo("token : {}", token);
            if (StringUtils.isEmpty(token)) {
                result.setReturnCode(ResultEnum.NO_LOGIN.code());
                result.setMessage(ResultEnum.NO_LOGIN.message());
                return result;
            }

            // 判断用户是否在线
            if (!this.tokenManager.userOnline(token)) {
                result.setReturnCode(ResultEnum.NO_LOGIN.code());
                result.setMessage(ResultEnum.NO_LOGIN.message());
                return result;
            }

            // 判断是否需要强更
            String appName = request.getHeader(Field.APP_NAME);
            String appVersion = request.getHeader(Field.APP_VERSION);
            LoanAppControlEntity loanAppControlEntity = loanAppControlDao.findByAppNameAndAppVersion(appName, appVersion);
            if (ObjectUtils.isEmpty(loanAppControlEntity) || loanAppControlEntity.getStatus() != 1) {
                result.setReturnCode(ResultEnum.VERSION_ERROR.code());
                result.setMessage(ResultEnum.VERSION_ERROR.message());
                return result;
            }


            // 获取用户缓存
            User user = this.tokenManager.getUserCache(token);
            request.setAttribute(Field.USER, user);

            // 调用下一级
            return point.proceed();
        } catch (Exception e) {
            LogUtil.sysError("[AuthenticationAspect around]", e);

            // 异常返回结果
            result.setEx(ThrowableUtils.throwableToString(e));
            result.setReturnCode(ResultEnum.SYSTEM_ERROR.code());
            result.setMessage(ResultEnum.SYSTEM_ERROR.message());
            return result;
        }
    }
}
