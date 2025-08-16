package com.thanlinardos.resource_server.misc.utils;

import jakarta.annotation.Nullable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AspectUtils {

    private AspectUtils() {
    }

    public static String getSimpleClassName(@Nullable Object result) {
        return result != null ? result.getClass().getSimpleName() : "";
    }

    public static <A extends Annotation> boolean hasAnnotation(ProceedingJoinPoint proceedingJoinPoint, Class<A> annotation) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method, annotation) != null ||
                AnnotationUtils.findAnnotation(method.getDeclaringClass(), annotation) != null;
    }

    public static <A extends Annotation> boolean hasAnnotationWithProperty(ProceedingJoinPoint proceedingJoinPoint, Class<A> annotation, String property, List<Object> values) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        if (annotationObject != null) {
            Object annotationValue = AnnotationUtils.getValue(annotationObject, property);
            return annotationValue != null && new HashSet<>(values).containsAll(Arrays.asList((Object[]) annotationValue));
        }
        return false;
    }

    public static <A extends Annotation, J extends JoinPoint> Object getAnnotationValue(J joinPoint, Class<A> annotation, String property) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        if (annotationObject != null) {
            Object value = AnnotationUtils.getValue(annotationObject, property);
            if (value instanceof Object[] values) {
                if (values.length > 0) {
                    return values[0];
                } else {
                    return null;
                }
            } else {
                return value;
            }
        }
        return null;
    }

    public static <A extends Annotation, J extends JoinPoint> List<Map.Entry<String, Object>> getAnnotationValues(J joinPoint, Class<A> annotation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        return annotationObject != null ? AnnotationUtils.getAnnotationAttributes(annotationObject).entrySet().stream()
                .filter(Objects::nonNull)
                .filter(entry -> !(entry.getValue() instanceof String value && value.isEmpty()))
                .toList()
                : Collections.emptyList();
    }
}
