package com.health;

import com.health.entity.User;
import com.health.mapper.UserMapper;
import com.health.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserInviteCodeTest {

    private static final String USERNAME = "invite-code-test-user";

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @AfterEach
    void cleanUp() {
        User user = userService.findByUsername(USERNAME);
        if (user != null) userMapper.deleteById(user.getId());
    }

    @Test
    void registerGeneratesUniqueInviteCode() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword("password123");
        user.setNickname("关联码测试用户");

        assertThat(userService.register(user)).isTrue();

        User saved = userService.findByUsername(USERNAME);
        assertThat(saved.getInviteCode()).matches("ZH[A-Z0-9]{10}");
    }
}
