import axios from "axios"

const api_url = axios.create({ baseURL: 'http://localhost:9090/api/v1' })

export default api_url