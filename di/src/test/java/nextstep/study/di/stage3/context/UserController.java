package nextstep.study.di.stage3.context;

public class UserController {

    private UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }
}
