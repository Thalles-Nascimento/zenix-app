import axios from "axios"

const api_url = axios.create({ baseURL: '/api/v1' })

api_url.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

export default api_url