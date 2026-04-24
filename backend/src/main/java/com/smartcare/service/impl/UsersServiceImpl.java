package com.smartcare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcare.common.ResultCodeEnum;
import com.smartcare.common.exception.BusinessException;
import com.smartcare.dto.user.LoginDto;
import com.smartcare.dto.user.RegisterDto;
import com.smartcare.dto.user.ResetPasswordRequestDto;
import com.smartcare.entity.Users;
import com.smartcare.mapper.UsersMapper;
import com.smartcare.service.UsersService;
import com.smartcare.utils.HttpUtils;
import com.smartcare.utils.VerifyCodeUtil;

import com.smartcare.vo.user.LoginVo;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersMapper usersMapper;

    // true = 模拟发送，false = 真实短信
    private static final boolean MOCK_SMS = false;

    // 验证码有效期，单位：分钟
    private static final int CODE_EXPIRE_MINUTE = 1;

    // 不用 Redis，直接存在内存里
    private static final Map<String, CodeCache> CODE_MAP = new ConcurrentHashMap<>();

    @Override
    public LoginVo login(LoginDto dto) {
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getUsername, dto.getUsername());
        wrapper.eq(Users::getPassword, dto.getPassword());
        wrapper.eq(Users::getUserType, dto.getUserType());

        Users user = usersMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCodeEnum.LOGIN_ERROR);
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        LoginVo vo = new LoginVo();
        BeanUtils.copyProperties(user, vo);
        vo.setToken("token_" + user.getUserId());
        return vo;
    }

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

        // 不用 Redis，存入内存
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_MINUTE * 60 * 1000L;
        CODE_MAP.put(phone, new CodeCache(verifyCode, expireTime));

        System.out.println("验证码已保存到内存");
        System.out.println("手机号：" + phone);
        System.out.println("验证码：" + verifyCode);
        System.out.println("有效期：" + CODE_EXPIRE_MINUTE + "分钟");
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

        // 查用户名是否存在
        LambdaQueryWrapper<Users> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Users::getUsername, dto.getUsername());
        if (usersMapper.selectOne(wrapper1) != null) {
            throw new RuntimeException("账号已存在");
        }

        // 查手机号是否注册
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
        user.setStatus((byte) 1);

        usersMapper.insert(user);

        // 注册成功后删除验证码
        CODE_MAP.remove(dto.getPhone());
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

        // 重置成功后删除验证码
        CODE_MAP.remove(dto.getPhone());
    }

    // ================== 校验验证码 ==================
    private void checkCode(String phone, String code) {

        CodeCache cache = CODE_MAP.get(phone);

        if (cache == null) {
            throw new RuntimeException("验证码已过期或未发送");
        }

        if (System.currentTimeMillis() > cache.getExpireTime()) {
            CODE_MAP.remove(phone);
            throw new RuntimeException("验证码已过期");
        }

        if (!cache.getCode().equals(code)) {
            throw new RuntimeException("验证码错误");
        }
    }

    // ================== 短信发送 ==================
    private boolean sendMessage(String phone, String verifyCode) {

        if (MOCK_SMS) {
            System.out.println("========== 模拟短信发送 ==========");
            System.out.println("手机号: " + phone);
            System.out.println("验证码: " + verifyCode);
            System.out.println("有效期: " + CODE_EXPIRE_MINUTE + "分钟");
            System.out.println("================================");
            return true;
        }

        try {
            String host = "https://gyytz.market.alicloudapi.com";
            String path = "/sms/smsSend";
            String method = "POST";

            // 这里用你自己的 AppCode
            String appcode = "5dbc03c05611454090ecdc6b17d96d94";

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "APPCODE " + appcode);

            Map<String, String> querys = new HashMap<>();
            querys.put("mobile", phone);

            String paramStr = "**code**:" + verifyCode + ",**minute**:" + CODE_EXPIRE_MINUTE;
            querys.put("param", paramStr);

            querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
            querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");

            Map<String, String> bodys = new HashMap<>();

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");

            System.out.println("接口状态码 = " + statusCode);
            System.out.println("接口返回结果 = " + result);

            return statusCode == 200 && result.contains("\"code\":\"0\"");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== 内存验证码对象 ==================
    private static class CodeCache {
        private final String code;
        private final long expireTime;

        public CodeCache(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        public String getCode() {
            return code;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
}