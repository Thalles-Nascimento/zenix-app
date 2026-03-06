import axios from "axios"

const api_url = axios.create({ baseURL: '/api/v1' })

export default api_url