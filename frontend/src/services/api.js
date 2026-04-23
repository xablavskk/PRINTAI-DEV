import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const searchServices = async (params) => {
  const response = await api.get('/services/search', { params });
  return response.data;
};

export default api;
