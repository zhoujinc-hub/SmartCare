import { defineStore } from 'pinia'
import { ref } from 'vue'

interface UserInfo {
    userId: number
    username: string
    realName?: string
    phone?: string
    userType?: number
    token: string
}

export const useUserStore = defineStore('user', () => {
    const userInfo = ref<UserInfo>({
        userId: 0,
        username: '',
        realName: '',
        phone: '',
        userType: 0,
        token: ''
    })

    const setUserInfo = (info: Partial<UserInfo>) => {
        console.log('【setUserInfo】- 传入信息:', info)
        userInfo.value = { ...userInfo.value, ...info }
        console.log('【setUserInfo】- 更新后用户信息:', userInfo.value)
    }

    const clearUserInfo = () => {
        userInfo.value = {
            userId: 0,
            username: '',
            realName: '',
            phone: '',
            userType: 0,
            token: ''
        }
    }

    return {
        userInfo,
        setUserInfo,
        clearUserInfo
    }
})