package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import nextstep.study.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Stage4Test {

    @Test
    void qwewqe() throws Exception {
        final Constructor<UserService> constructor = UserService.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final UserService userService = constructor.newInstance();
        final InMemoryUserDao inMemoryUserDao = new InMemoryUserDao();
        for (final Field field : userService.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            field.set(userService, inMemoryUserDao);
        }
    }

    @Test
    void stage4() {
        final var user = new User(1L, "gugu");

        final DIContext diContext = createDIContext();
        final UserService userService = diContext.getPeanut(UserService.class);

        final User actual = userService.join(user);

        assertThat(actual.getAccount()).isEqualTo("gugu");
    }

    private static DIContext createDIContext() {
        final var rootPackageName = Stage4Test.class.getPackage().getName();
        return DIContext.createContextForPackage(rootPackageName);
    }
}
