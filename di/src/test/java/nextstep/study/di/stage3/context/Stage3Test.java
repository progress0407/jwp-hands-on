package nextstep.study.di.stage3.context;

import java.util.Set;
import nextstep.study.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class Stage3Test {

    @Test
    void stage3() {
        final var user = new User(1L, "gugu");

        final DIContext diContext = createDIContext();
        final UserService userService = diContext.getBean(UserService.class);

        final User actual = userService.join(user);

        assertThat(actual.getAccount()).isEqualTo("gugu");
    }

    private static DIContext createDIContext() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(InMemoryUserDao.class);
        classes.add(UserService.class);
        classes.add(UserController.class);
        return new DIContext("nextstep.study.di.stage3.context", classes);
    }
}
