package com.health.controller;

import com.health.common.JwtUtil;
import com.health.common.Result;
import com.health.entity.User;
import com.health.common.SecurityUtil;
import com.health.dto.UserProfileUpdateRequest;
import com.health.service.AvatarStorageService;
import com.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AvatarStorageService avatarStorageService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid User user) {
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功", null);
        }
        return Result.error("用户名已存在");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        String token = userService.login(username, password);
        if (token != null) {
            User user = userService.findByUsername(username);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");

            // 获取用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("role", user.getRole());
            userInfo.put("inviteCode", user.getInviteCode());
            userInfo.put("createTime", user.getCreateTime());
            data.put("userInfo", userInfo);

            return Result.success("登录成功", data);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getById(userId);

            if (user == null) {
                return Result.error(401, "用户不存在");
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("role", user.getRole());
            userInfo.put("inviteCode", user.getInviteCode());
            userInfo.put("createTime", user.getCreateTime());

            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(401, "Token无效");
        }
    }

    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) return Result.error(401, "请先登录");
            if (!userService.updateProfile(userId, request.getNickname(), request.getEmail(), request.getPhone())) {
                return Result.error(400, "资料更新失败");
            }
            return Result.success("资料已更新", profileView(userService.getById(userId)));
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestPart("file") MultipartFile file) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) return Result.error(401, "请先登录");
            String avatarUrl = avatarStorageService.store(file);
            if (!userService.updateAvatar(userId, avatarUrl)) return Result.error(400, "头像更新失败");
            Map<String, Object> data = new HashMap<>();
            data.put("avatar", avatarUrl);
            return Result.success("头像已更新", data);
        } catch (RuntimeException exception) {
            return Result.error(400, exception.getMessage());
        }
    }

    private Map<String, Object> profileView(User user) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", user.getId());
        view.put("username", user.getUsername());
        view.put("nickname", user.getNickname());
        view.put("email", user.getEmail());
        view.put("phone", user.getPhone());
        view.put("avatar", user.getAvatar());
        view.put("role", user.getRole());
        view.put("inviteCode", user.getInviteCode());
        view.put("createTime", user.getCreateTime());
        return view;
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> passwordForm) {
        try {
            String token = authorization.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);

            String oldPassword = passwordForm.get("oldPassword");
            String newPassword = passwordForm.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return Result.error("旧密码和新密码不能为空");
            }

            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功", null);
            }
            return Result.error("旧密码错误");
        } catch (Exception e) {
            return Result.error(401, "Token无效");
        }
    }
}
