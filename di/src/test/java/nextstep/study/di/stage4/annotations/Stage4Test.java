package nextstep.study.di.stage4.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.study.User;
import org.junit.jupiter.api.Test;

class Stage4Test {

    @Test
    void stage4() {
        final var user = new User(1L, "gugu");

        final DIContext diContext = createDIContext();
        final UserService userService = diContext.getPeanut(UserService.class);

        final User actual = userService.join(user);

        assertThat(actual.getAccount()).isEqualTo("gugu");
    }

    private static DIContext createDIContext() {
        final String rootPackageName = Stage4Test.class.getPackage().getName();
        return DIContext.createContextForPackage(rootPackageName);
    }
}
