import axios from "axios"

// const api_url = axios.create({baseURL: "/api/v1", withCredentials: true,});

const api_url = axios.create({baseURL: "http://localhost:9090/api/v1", withCredentials: true,});

export default api_url