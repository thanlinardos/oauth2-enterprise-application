package com.thanlinardos.resource_server.misc.utils;

import com.thanlinardos.resource_server.TestUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class AspectUtilsTest {

    @Controller
    static class TestController {

        @RequestMapping(method = RequestMethod.GET)
        public ResponseEntity<String> get() {
            return ResponseEntity.ok("Hello");
        }

        @GetMapping
        public ResponseEntity<String> getMapping() {
            return ResponseEntity.ok("Hello");
        }

        @RequestMapping(method = RequestMethod.POST)
        public ResponseEntity<String> post() {
            return ResponseEntity.ok("Hello");
        }

        @RequestMapping(method = RequestMethod.PUT)
        public ResponseEntity<String> put() {
            return ResponseEntity.ok("Hello");
        }

        @RequestMapping(method = {RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST})
        public ResponseEntity<String> getPostPut() {
            return ResponseEntity.ok("Hello");
        }

        @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.TRACE})
        public ResponseEntity<String> all() {
            return ResponseEntity.ok("Hello");
        }
    }

    public static Stream<Arguments> getAnnotationValueParams() {
        return Stream.of(
                Arguments.argumentSet("RequestMapping GET", RequestMapping.class, "get", RequestMethod.GET),
                Arguments.argumentSet("RequestMapping POST", RequestMapping.class, "post", RequestMethod.POST),
                Arguments.argumentSet("RequestMapping PUT", RequestMapping.class, "put", RequestMethod.PUT)
        );
    }

    @ParameterizedTest
    @MethodSource("getAnnotationValueParams")
    void getAnnotationValue(Class<RequestMapping> annotation, String methodName, RequestMethod expected) throws NoSuchMethodException {
        JoinPoint joinPoint = TestUtils.createDummyJoinPointForMethod(TestController.class.getDeclaredMethod(methodName));
        RequestMethod actual = (RequestMethod) AspectUtils.getAnnotationValue(joinPoint, annotation, "method");
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> getSimpleClassNameParams() {
        return Stream.of(
                Arguments.argumentSet("Null object", null, ""),
                Arguments.argumentSet("RequestMethod object", RequestMethod.GET, "RequestMethod")
        );
    }

    @ParameterizedTest
    @MethodSource("getSimpleClassNameParams")
    void getSimpleClassName(Object classObject, String expected) {
        String actual = AspectUtils.getSimpleClassName(classObject);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> hasAnnotationParams() {
        return Stream.of(
                Arguments.argumentSet("GetMapping", GetMapping.class, "getMapping"),
                Arguments.argumentSet("RequestMapping", RequestMapping.class, "get")
        );
    }

    @ParameterizedTest
    @MethodSource("hasAnnotationParams")
    void hasAnnotation(Class<Annotation> annotation, String methodName) throws NoSuchMethodException {
        JoinPoint joinPoint = TestUtils.createDummyJoinPointForMethod(TestController.class.getDeclaredMethod(methodName));
        boolean actual = AspectUtils.hasAnnotation((ProceedingJoinPoint) joinPoint, annotation);
        Assertions.assertTrue(actual);
    }

    public static Stream<Arguments> hasAnnotationWithPropertyParams() {
        return Stream.of(
                Arguments.of("Has RequestMapping annotation with method property and GET value", RequestMapping.class, "get", "method", List.of(RequestMethod.GET)),
                Arguments.of("Has RequestMapping annotation with method property and GET,POST,PUT values", RequestMapping.class, "getPostPut", "method", List.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT))
        );
    }

    @ParameterizedTest(name = "{0}: annotation {1} exists in  TestController#{2}")
    @MethodSource("hasAnnotationWithPropertyParams")
    void hasAnnotationWithProperty(String description, Class<Annotation> annotation, String methodName, String propertyName, List<Object> values) throws NoSuchMethodException {
        JoinPoint joinPoint = TestUtils.createDummyJoinPointForMethod(TestController.class.getDeclaredMethod(methodName));
        boolean actual = AspectUtils.hasAnnotationWithProperty((ProceedingJoinPoint) joinPoint, annotation, propertyName, values);
        Assertions.assertTrue(actual);
    }

    public static Stream<Arguments> getAnnotationValuesParams() {
        return Stream.of(
                Arguments.argumentSet("RequestMapping GET",
                        RequestMapping.class, "get",
                        createAnnotationProperties(standardAnnotationProperties(), Map.of("method", new RequestMethod[]{RequestMethod.GET}))),
                Arguments.argumentSet("RequestMapping ALL",
                        RequestMapping.class, "all",
                        createAnnotationProperties(standardAnnotationProperties(),
                                Map.of("method", new RequestMethod[]{RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.TRACE})))
        );
    }

    @ParameterizedTest
    @MethodSource("getAnnotationValuesParams")
    void getAnnotationValues(Class<RequestMapping> annotation, String methodName, Map<String, Object> expected) throws NoSuchMethodException {
        JoinPoint joinPoint = TestUtils.createDummyJoinPointForMethod(TestController.class.getDeclaredMethod(methodName));
        Map<String, Object> actual = AspectUtils.getAnnotationValues(joinPoint, annotation).stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
        Assertions.assertEquals(expected.keySet(), actual.keySet());
        expected.forEach((k, v) -> Assertions.assertArrayEquals((Object[]) v, (Object[]) actual.get(k)));
    }

    private static Map<String, Object> createAnnotationProperties(Map<String, Object> defaultProps, Map<String, Object[]> props) {
        Map<String, Object> defaultPropsMap = new HashMap<>(defaultProps);
        defaultPropsMap.putAll(props);
        return defaultPropsMap;
    }

    private static Map<String, Object> standardAnnotationProperties() {
        return Map.of("value", new String[0],
                "path", new String[0],
                "params", new String[0],
                "headers", new String[0],
                "consumes", new String[0],
                "produces", new String[0]);
    }
}