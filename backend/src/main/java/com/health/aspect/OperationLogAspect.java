package com.health.aspect;

import com.health.annotation.OperationLog;
import com.health.service.OperationLogService;
import com.health.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志切面
 *
 * @author health-team
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationLogService operationLogService;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            recordLog(operationLog.value(), 1, null);
            return result;
        } catch (Throwable throwable) {
            recordLog(operationLog.value(), 0, throwable.getMessage());
            throw throwable;
        }
    }

    private void recordLog(String operation, int success, String errorMessage) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        com.health.entity.OperationLog logEntity = new com.health.entity.OperationLog();
        logEntity.setUserId(UserHolder.getUserId());
        logEntity.setUsername(UserHolder.getUsername());
        logEntity.setOperation(operation);
        logEntity.setMethod(request.getMethod());
        logEntity.setPath(request.getRequestURI());
        logEntity.setIp(getClientIp(request));
        logEntity.setSuccess(success);
        logEntity.setErrorMessage(errorMessage == null ? null : errorMessage.substring(0, Math.min(errorMessage.length(), 500)));
        operationLogService.record(logEntity);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
