package com.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FamilyRelationSchemaTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void familyRelationTableAcceptsOneRelation() {
        jdbcTemplate.update("DELETE FROM family_relation WHERE owner_user_id = 2 AND member_user_id = 3");
        jdbcTemplate.update("INSERT INTO family_relation (owner_user_id, member_user_id, relationship, status) VALUES (2, 3, '父亲', 'ACTIVE')");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM family_relation WHERE owner_user_id = 2 AND member_user_id = 3",
                Integer.class);

        assertThat(count).isEqualTo(1);
        jdbcTemplate.update("DELETE FROM family_relation WHERE owner_user_id = 2 AND member_user_id = 3");
    }
}
