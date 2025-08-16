package com.thanlinardos.resource_server.aspect;

import org.aspectj.lang.annotation.Pointcut;

abstract class PointCutDefinitions {

    @Pointcut("execution(public * com.thanlinardos.resource_server.service.*.*(..))"
            + "&& !execution(public com.thanlinardos.resource_server.model.entity.* *.*(..))"
            + "&& !execution(public java.util.List<com.thanlinardos.resource_server.model.entity.*> *.*(..))"
            + "&& !execution(public java.util.Optional<com.thanlinardos.resource_server.model.entity.*> *.*(..))")
    protected void forServicePackageAndNotEntityReturned() {
    }

    @Pointcut("execution(public * com.thanlinardos.resource_server.controller.*.*.*(..)) && "
            + "(@annotation(org.springframework.web.bind.annotation.RequestMapping) "
                + "|| @annotation(org.springframework.web.bind.annotation.GetMapping) "
                + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
                + "|| @annotation(org.springframework.web.bind.annotation.PostMapping) "
                + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) "
                + "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)"
            + ")")
    protected void forControllerPackage() {
    }

    @Pointcut("execution(public * com.thanlinardos.resource_server.batch.*EventTask.*()) && @annotation(org.springframework.scheduling.annotation.Scheduled)")
    protected void forBatchEventTasks() {
    }
}
