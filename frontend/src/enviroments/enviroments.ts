import axios from "axios"

const api_url = axios.create({ baseURL: 'https://app.zenixapp.cloud/api/v1/' })

export default api_url