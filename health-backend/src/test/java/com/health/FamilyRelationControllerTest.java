package com.health;

import com.health.common.Result;
import com.health.controller.FamilyRelationController;
import com.health.dto.FamilyRelationCreateRequest;
import com.health.mapper.FamilyRelationMapper;
import com.health.vo.FamilyRelationVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FamilyRelationControllerTest {

    @Autowired
    private FamilyRelationController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FamilyRelationMapper mapper;

    @AfterEach
    void cleanUp() {
        mapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.health.entity.FamilyRelation>()
                .eq("owner_user_id", 2L).eq("member_user_id", 3L));
        mapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.health.entity.FamilyRelation>()
                .eq("owner_user_id", 3L).eq("member_user_id", 2L));
        SecurityContextHolder.clearContext();
    }

    @Test
    void getInviteCodeReturnsCurrentUsersCode() {
        setUser(2L);

        Result<Map<String, String>> result = controller.getInviteCode();

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).containsKey("inviteCode");
    }

    @Test
    void createWithInvalidCodeReturnsBusinessError() {
        setUser(2L);
        FamilyRelationCreateRequest request = new FamilyRelationCreateRequest();
        request.setInviteCode("INVALID-CODE");
        request.setRelationship("父亲");

        Result<?> result = controller.create(request);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMsg()).contains("关联码");
    }

    @Test
    void createReturnsMemberSummary() {
        setUser(2L);
        FamilyRelationCreateRequest request = new FamilyRelationCreateRequest();
        request.setInviteCode("ZH6B9D97C87F");
        request.setRelationship("父亲");

        Result<FamilyRelationVO> result = controller.create(request);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getMemberUserId()).isEqualTo(3L);
        assertThat(result.getData().getRelationship()).isEqualTo("父亲");
        assertThat(result.getData().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void unauthenticatedInviteCodeRequestIsRejected() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/family/invite-code"))
                .andExpect(status().isForbidden());
    }

    private void setUser(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        authentication.setDetails("testuser:user");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
