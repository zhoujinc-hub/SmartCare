/**
 * 摄像头表实体类
 */
export interface CameraItem {
    cameraId: number
    cameraType: number
    cameraName: string
    deviceSerial: string
    streamUrl: string
    locationDesc: string
    latitude: number
    longitude: number
    status: number
}

/**
 * 新增摄像头接口入参（不含 cameraId）
 */
export interface CameraAddDto {
    cameraType?: number;
    cameraName?: string;
    deviceSerial?: string;
    streamUrl?: string;
    locationDesc?: string;
    latitude?: number;
    longitude?: number;
    status?: number;
}

/**
 * 更新摄像头接口入参（必须包含 cameraId）
 */
export interface CameraUpdateDto extends CameraAddDto {
    cameraId: number;
}

/**
 * 分页查询参数
 */
export interface CameraQueryParams {
    cameraName?: string;
    status?: number;
    cameraType?: number;
    pageNum?: number;
    pageSize?: number;
}

export interface CameraPageResult {
    total: number;
    pages: number;
    pageNum: number;
    pageSize: number;
    records: CameraItem[];
}

export interface CameraPageResponse {
    code: number;
    message: string;
    data: CameraPageResult;
}

export interface CameraDetailResponse {
    code: number;
    message: string;
    data: CameraItem;
}

export interface CameraBaseResponse {
    code: number;
    message: string;
    data?: any;
}