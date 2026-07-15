package com.health.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.entity.FamilyRelation;
import com.health.entity.User;
import com.health.mapper.FamilyRelationMapper;
import com.health.mapper.UserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Keeps the built-in demo account useful without changing real users' relationships. */
@Component
public class DemoFamilyDataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final FamilyRelationMapper relationMapper;

    public DemoFamilyDataInitializer(UserMapper userMapper, FamilyRelationMapper relationMapper) {
        this.userMapper = userMapper;
        this.relationMapper = relationMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        User demo = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "demo2026"));
        if (demo == null) return;
        ensureRelation(demo.getId(), "zhangsan", demoName(0x7238), "父亲");
        ensureRelation(demo.getId(), "lisi", demoName(0x5988), "母亲");
    }

    private String demoName(int codePoint) {
        return new String(new char[]{(char) codePoint, (char) codePoint});
    }

    private void ensureRelation(Long ownerId, String username, String displayName, String relationship) {
        User member = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (member == null || ownerId.equals(member.getId())) return;
        FamilyRelation existing = relationMapper.selectOne(new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getOwnerUserId, ownerId)
                .eq(FamilyRelation::getMemberUserId, member.getId()));
        if (existing != null) {
            if (existing.getDisplayName() == null || existing.getDisplayName().trim().isEmpty() || existing.getDisplayName().matches("\\?+")) {
                existing.setDisplayName(displayName);
                relationMapper.updateById(existing);
            }
            return;
        }
        FamilyRelation relation = new FamilyRelation();
        relation.setOwnerUserId(ownerId);
        relation.setMemberUserId(member.getId());
        relation.setDisplayName(displayName);
        relation.setRelationship(relationship);
        relation.setStatus("ACTIVE");
        relationMapper.insert(relation);
    }
}
