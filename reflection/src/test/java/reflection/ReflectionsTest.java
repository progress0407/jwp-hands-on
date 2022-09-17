package reflection;

import static java.lang.System.out;

import annotation.Controller;
import annotation.Repository;
import annotation.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.QueryFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionsTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsTest.class);

    @Test
    void showAnnotationClass() throws Exception {
        Reflections reflections = new Reflections("examples");

        // TODO 클래스 레벨에 @Controller, @Service, @Repository 애노테이션이 설정되어 모든 클래스 찾아 로그로 출력한다.
        final List<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class)
                .stream()
                .collect(Collectors.toUnmodifiableList());

        final List<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class)
                .stream()
                .collect(Collectors.toUnmodifiableList());

        final List<Class<?>> repositories = reflections.getTypesAnnotatedWith(Repository.class)
                .stream()
                .collect(Collectors.toUnmodifiableList());

        log.info("controllers = {}", controllers);
        log.info("services = {}", services);
        log.info("repositories = {}", repositories);
    }
}
