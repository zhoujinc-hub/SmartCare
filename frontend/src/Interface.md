 *login 
 1. 用户登录接口
 * 接口地址：/api/user/login
 * 请求方式：POST
 * 请求参数：username + password + user_type
 * 返回格式：{ code: 200, data: { token, userId, ... }, message: '成功' }
  
   * 2. 获取验证码接口（忘记密码）
 * 接口地址：/api/user/send-code
 * 请求方式：POST
 * 请求参数：phone（用户手机号）
 * 返回格式：{ code: 200, message: '验证码发送成功' }

  * 3. 重置密码接口
 * 接口地址：/api/user/reset-password
 * 请求方式：POST
 * 请求参数：phone + code + new_password
 * 返回格式：{ code: 200, message: '密码重置成功' }

Alerts
 * 1. 获取告警列表（分页+筛选）
 * 接口地址：/api/fall-events/list
 * 请求方式：POST
 * 请求参数：分页参数 + 筛选参数
 * 返回格式：{ code: 200, data: { list: [], total: 0 }, message: '成功' }

  * 2. 标记告警为已处理
 * 接口地址：/api/fall-events/handle/{eventId}
 * 请求方式：PUT
 * 请求参数：eventId + 处理人信息
 * 返回格式：{ code: 200, message: '成功' }

  * 3. 保存处理备注
 * 接口地址：/api/fall-events/notes/{eventId}
 * 请求方式：PUT
 * 请求参数：eventId + processNotes
 * 返回格式：{ code: 200, message: '成功' }

 Elders
  * 1. 获取老人列表（分页+筛选）
 * 接口地址：/api/elder/list
 * 请求方式：GET
 * 请求参数：name、pageNum、pageSize（URL参数）
 * 返回格式：PageResponse<ElderItem>

  * 2. 获取家属列表（关联users表，user_type=2）
 * 接口地址：/api/user/relative/list
 * 请求方式：GET
 * 返回格式：BaseResponse & { data: UserItem[] }

  * 3. 新增老人信息
 * 接口地址：/api/elder/add
 * 请求方式：POST
 * 请求参数：elders表字段 + relativeIds（关联家属ID列表）
 * 返回格式：BaseResponse

  * 4. 编辑老人信息
 * 接口地址：/api/elder/update
 * 请求方式：PUT
 * 请求参数：elders表所有字段 + elder_id + relativeIds
 * 返回格式：BaseResponse

  * 5. 删除老人信息
 * 接口地址：/api/elder/delete/{elder_id}
 * 请求方式：DELETE
 * 请求参数：elder_id（路径参数）
 * 返回格式：BaseResponse
 * 说明：会级联删除relations表中的关联记录（数据库已配置ON DELETE CASCADE）

  * 6. 获取老人详情
 * 接口地址：/api/elder/detail/{elder_id}
 * 请求方式：GET
 * 请求参数：elder_id（路径参数）
 * 返回格式：BaseResponse & { data: DetailItem }

  * 7. 获取老人关联的家属列表
 * 接口地址：/api/relation/list/{elder_id}
 * 请求方式：GET
 * 返回格式：BaseResponse & { data: RelationItem[] }

Households
 * 1. 获取社区列表
 * 接口地址：/api/communities
 * 请求方式：GET
 * 返回格式：{ code: 200, data: [], message: '成功' }

  * 2. 获取家庭列表（分页+筛选）
 * 接口地址：/api/households/list
 * 请求方式：GET
 * 请求参数：分页参数 + 筛选参数
 * 返回格式：{ code: 200, data: { records: [], total: 0 }, message: '成功' }

  * 3. 获取家庭详情
 * 接口地址：/api/households/{householdId}
 * 请求方式：GET
 * 请求参数：householdId
 * 返回格式：{ code: 200, data: {}, message: '成功' }

  * 4. 新增家庭
 * 接口地址：/api/households
 * 请求方式：POST
 * 请求参数：家庭表单数据
 * 返回格式：{ code: 200, message: '成功' }

  * 5. 编辑家庭
 * 接口地址：/api/households/{householdId}
 * 请求方式：PUT
 * 请求参数：householdId + 表单数据
 * 返回格式：{ code: 200, message: '成功' }

  * 6. 删除家庭
 * 接口地址：/api/households/{householdId}
 * 请求方式：DELETE
 * 请求参数：householdId
 * 返回格式：{ code: 200, message: '成功' }

  * 7. 批量更新报警阈值
 * 接口地址：/api/households/batch-update-threshold
 * 请求方式：POST
 * 请求参数：householdIds + alertThresholdSeconds
 * 返回格式：{ code: 200, message: '成功' }

Cameras
 * 1. 获取摄像头列表（分页+筛选）
 * 接口地址：/api/camera/list
 * 请求方式：GET
 * 请求参数：cameraName、status、pageNum、pageSize（URL参数）
 * 返回格式：PageResponse<CameraItem>

  * 2. 获取家庭列表（用于关联选择）
 * 接口地址：/api/household/list
 * 请求方式：GET
 * 返回格式：BaseResponse & { data: HouseholdItem[] }

  * 3. 新增摄像头信息
 * 接口地址：/api/camera/add
 * 请求方式：POST
 * 请求参数：cameras表字段 + householdId（扩展字段）
 * 返回格式：BaseResponse

  * 4. 编辑摄像头信息
 * 接口地址：/api/camera/update
 * 请求方式：PUT
 * 请求参数：cameras表所有字段 + camera_id + householdId
 * 返回格式：BaseResponse

  * 5. 删除摄像头信息
 * 接口地址：/api/camera/delete/{camera_id}
 * 请求方式：DELETE
 * 请求参数：camera_id（路径参数）
 * 返回格式：BaseResponse

  * 6. 获取摄像头详情
 * 接口地址：/api/camera/detail/{camera_id}
 * 请求方式：GET
 * 请求参数：camera_id（路径参数）
 * 返回格式：BaseResponse & { data: CameraItem }

  * 7. 刷新摄像头状态
 * 接口地址：/api/camera/refreshStatus
 * 请求方式：POST
 * 返回格式：BaseResponse

 





