/** 登录表单数据类型（对齐 users 表） */
export interface LoginForm {
  username: string       // 对应 users.username（唯一）
  password: string       // 对应 users.password
  userType: number       // 对应 users.user_type：1=管理员 2=家属
  rememberMe: boolean    // 记住密码（前端本地存储）
}

/** 登录接口返回数据类型 */
export interface LoginResponse {
  code: number
  message: string
  data: {
    token: string                // 登录令牌
    userId?: number              // 对应 users.user_id
    username?: string            // 对应 users.username
    realName?: string            // 对应 users.real_name
    phone?: string               // 对应 users.phone
    userType?: number            // 对应 users.user_type
    status?: number              // 对应 users.status（0=禁用 1=启用）
    expires?: number             // token过期时间
  }
}

/** 忘记密码表单类型（对齐 users 表 phone 字段） */
export interface ForgotForm {
  phone: string            // 对应 users.phone
  code: string             // 验证码
  newPassword: string      // 新密码（对应 users.password）
  confirmPassword: string  // 确认新密码
}

/** 验证码接口返回类型 */
export interface CodeResponse {
  code: number
  message: string
  data?: {
    code?: string          // 测试用，生产环境不返回
  }
}

/** 重置密码接口返回类型 */
export interface ResetPwdResponse {
  code: number
  message: string
}

/** 存储在本地的用户信息类型 */
export interface LocalUserInfo {
  userId?: number
  username?: string
  realName?: string
  phone?: string
  userType?: number
  status?: number
}

/**
 * users表结构（用户表：管理员/家属）
 */
export interface UserItem {
  user_id: number; // 用户ID
  username: string; // 用户名
  real_name: string; // 真实姓名
  phone: string; // 手机号
  user_type: number; // 1:管理员 2:家属
  status: number; // 0:禁用 1:启用
}

/**
 * relations表结构（老人-家属关联表）
 */
export interface RelationItem {
  relation_id: number;
  elder_id: number;
  user_id: number;
  relationship: string; // 关系描述
  created_at: string;
}

/** 基础接口返回类型 */
export interface BaseResponse {
  code: number;
  message: string;
  data?: any;
}

/** 分页接口返回类型 */
export interface PageResponse<T> {
  code: number;
  message: string;
  data: {
    list: T[];
    total: number;
    pageNum: number;
    pageSize: number;
  };
}