package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcare.dto.user.RegisterDto;
import com.smartcare.dto.user.ResetPasswordRequestDto;
import com.smartcare.entity.Users;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.UsersService;
import com.smartcare.utils.VerifyCodeUtil;
import com.atguigu.lease.web.app.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersMapper usersMapper;
    private final StringRedisTemplate stringRedisTemplate;

    // true=模拟发送，false=真实短信
    private static final boolean MOCK_SMS = true;

    private static final int CODE_EXPIRE_MINUTE = 1;

    private static final String CODE_PREFIX = "user:code:";

    // ================== 发送验证码 ==================
    @Override
    public void sendCode(String phone) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }

        // 生成验证码
        String verifyCode = VerifyCodeUtil.getVerifyCode(6);

        // 发送短信
        boolean success = sendMessage(phone, verifyCode);
        if (!success) {
            throw new RuntimeException("短信发送失败");
        }

        // 存入Redis
        stringRedisTemplate.opsForValue().set(
                CODE_PREFIX + phone,
                verifyCode,
                CODE_EXPIRE_MINUTE,
                TimeUnit.MINUTES
        );
    }

    // ================== 注册 ==================
    @Override
    public void register(RegisterDto dto) {

        if (dto == null) {
            throw new RuntimeException("参数不能为空");
        }

        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new RuntimeException("账号不能为空");
        }

        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }

        if (dto.getUserType() == null) {
            throw new RuntimeException("用户类型不能为空");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }

        // 校验验证码
        checkCode(dto.getPhone(), dto.getCode());

        // 查用户名
        LambdaQueryWrapper<Users> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Users::getUsername, dto.getUsername());
        if (usersMapper.selectOne(wrapper1) != null) {
            throw new RuntimeException("账号已存在");
        }

        // 查手机号
        LambdaQueryWrapper<Users> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Users::getPhone, dto.getPhone());
        if (usersMapper.selectOne(wrapper2) != null) {
            throw new RuntimeException("手机号已注册");
        }

        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setUserType(dto.getUserType());
        user.setPassword(dto.getPassword());

        // ✅ 修复这里
        user.setStatus((byte) 1);

        usersMapper.insert(user);

        stringRedisTemplate.delete(CODE_PREFIX + dto.getPhone());
    }

    // ================== 重置密码 ==================
    @Override
    public void resetPasswordByPhone(ResetPasswordRequestDto dto) {

        if (dto == null) {
            throw new RuntimeException("参数不能为空");
        }

        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }

        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }

        // 校验验证码
        checkCode(dto.getPhone(), dto.getCode());

        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getPhone, dto.getPhone());

        Users user = usersMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(dto.getNewPassword());
        usersMapper.updateById(user);

        stringRedisTemplate.delete(CODE_PREFIX + dto.getPhone());
    }

    // ================== 校验验证码 ==================
    private void checkCode(String phone, String code) {

        String redisCode = stringRedisTemplate.opsForValue().get(CODE_PREFIX + phone);

        if (redisCode == null) {
            throw new RuntimeException("验证码已过期");
        }

        if (!redisCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }
    }

    // ================== 短信发送 ==================
    private boolean sendMessage(String phone, String verifyCode) {

        if (MOCK_SMS) {
            System.out.println("========== 模拟短信发送 ==========");
            System.out.println("手机号: " + phone);
            System.out.println("验证码: " + verifyCode);
            System.out.println("================================");
            return true;
        }

        try {
            String host = "https://gyytz.market.alicloudapi.com";
            String path = "/sms/smsSend";
            String method = "POST";
            String appcode = "你的AppCode";

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "APPCODE " + appcode);

            Map<String, String> querys = new HashMap<>();
            querys.put("mobile", phone);

            String paramStr = "**code**:" + verifyCode + ",**minute**:" + CODE_EXPIRE_MINUTE;
            querys.put("param", paramStr);

            querys.put("smsSignId", "你的smsSignId");
            querys.put("templateId", "你的templateId");

            Map<String, String> bodys = new HashMap<>();

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");

            return statusCode == 200 && result.contains("\"code\":\"0\"");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}