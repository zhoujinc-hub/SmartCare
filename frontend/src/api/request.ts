//Axios配置,其余的都是接口


import axios from "axios"

const request = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 5000
})

export default request