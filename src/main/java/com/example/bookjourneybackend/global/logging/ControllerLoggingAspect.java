package com.example.bookjourneybackend.global.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용 ObjectMapper
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public ControllerLoggingAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Pointcut("execution(* com.example.bookjourneybackend.domain..controller..*(..))")
    private void controller() {}

    @Before("controller()")
    public void requestLog(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        String httpMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        log.info("=============================== API 요청: [{}] {} ===============================", httpMethod, requestUrl);

        Method method = getMethod(joinPoint);
        log.info("Handler Method: {}", method.getName());

        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        if (args.length == 0) {
            log.info("No parameters in request.");
        } else {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String paramName = paramNames[i];
                if (arg == null) {
                    log.info("Parameter: {} = null", paramName);
                } else if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof BindingResult) {
                    log.info("Skipping system object: {}", arg.getClass().getSimpleName());
                } else if (isPrimitiveOrWrapper(arg.getClass()) || arg instanceof String) {
                    log.info("Parameter: {} = {}", paramName, arg);
                } else {
                    logJson("Request", arg);
                }
            }
        }
    }

    @AfterReturning(value = "controller()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
        long elapsedTime = System.currentTimeMillis() - startTime.get();
        String httpMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        log.info("=============================== API 응답: [{}] {} ===============================", httpMethod, requestUrl);
        log.info("응답 소요시간: {} ms", elapsedTime);

        Method method = getMethod(joinPoint);
        log.info("Handler Method: {}", method.getName());

        if (returnObj != null) {
            log.info("Return Type: {}", returnObj.getClass().getSimpleName());

            if (isPrimitiveOrWrapper(returnObj.getClass()) || returnObj instanceof String) {
                log.info("Return Value: {}", returnObj);
            } else if (returnObj instanceof Collection) {
                log.info("Collection Size: {}", ((Collection<?>) returnObj).size());
                ((Collection<?>) returnObj).forEach(item -> logJson("Item", item));
            } else if (returnObj instanceof Map) {
                log.info("Map Size: {}", ((Map<?, ?>) returnObj).size());
                ((Map<?, ?>) returnObj).forEach((key, value) -> logJson("Key: " + key, value));
            } else {
                logJson("Response", returnObj);
            }
        } else {
            log.info("Return Value: null");
        }
    }

    // JSON 변환 및 로깅
    private void logJson(String label, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.info("{} JSON: {}", label, json);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert {} to JSON: {}", label, e.getMessage());
            log.info("{} Fields: {}", label, extractDtoFields(obj));
        }
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private Map<String, Object> extractDtoFields(Object dto) {
        if (dto.getClass().getName().startsWith("org.springframework.security")) {
            return Map.of("Skipped", "Security-related object");
        }

        return Arrays.stream(dto.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        return field.get(dto);
                    } catch (IllegalAccessException e) {
                        return "Access Error";
                    }
                }));
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Character.class);
    }
}