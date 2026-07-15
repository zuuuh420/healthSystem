package com.health;

import com.health.service.AvatarStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvatarStorageServiceTest {

    @Test
    void rejectsUnsupportedAvatarType() {
        AvatarStorageService service = new AvatarStorageService("target/test-avatars");
        MockMultipartFile file = new MockMultipartFile("file", "avatar.svg", "image/svg+xml", "<svg/>".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .hasMessageContaining("只支持PNG、JPG或WebP");
    }

    @Test
    void rejectsAvatarLargerThanTwoMegabytes() {
        AvatarStorageService service = new AvatarStorageService("target/test-avatars");
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[(2 * 1024 * 1024) + 1]);

        assertThatThrownBy(() -> service.store(file))
                .hasMessageContaining("不能超过2MB");
    }
}
