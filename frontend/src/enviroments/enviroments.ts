import axios from "axios"

const api_url = axios.create({
    baseURL: "/api/v1",
    withCredentials: true,
});

export default api_url