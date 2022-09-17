package reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit3Test> clazz = Junit3Test.class;

        // TODO Junit3Test에서 test로 시작하는 메소드 실행

        // only public
        for (final Method method : clazz.getMethods()) {
            if (method.getName().startsWith("test")) {
                method.invoke(new Junit3Test());
            }
        }

        // all
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith("test")) {
                method.setAccessible(true);
                method.invoke(new Junit3Test());
            }
        }

    }
}
