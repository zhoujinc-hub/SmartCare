/**
 * 老人信息类型
 */
export interface Elder {
    elderId?: number;
    userId?: number;        // 绑定的家属ID
    realName: string;         // 老人姓名
    gender: 0 | 1;            // 0女 1男
    age: number;              // 年龄
    phone: string;            // 联系电话
    idCard: string;           // 身份证
    address: string;          // 家庭住址
    status: 0 | 1;            // 0禁用 1正常
    createdAt?: string;
}

/**
 * 摔倒事件（家属可见）
 */
export interface ElderFallEvent {
    elderId?: number;
    userId?: number;
    elderName: string;
    fallTime: string;
    detectTime: string;
    locationDesc?: string;
    status: 1 | 2 | 3;        // 1待处理 2已处理 3误报
    screenshotPath?: string;
    videoPath?: string;
}

/**
 * 分页查询参数
 */
export interface PageQuery {
    pageNum: number;
    pageSize: number;
}