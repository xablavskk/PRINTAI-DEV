import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const searchServices = async (params) => {
  const response = await api.get('/busca/servicos', { params });
  return response.data;
};

export const searchPrinters = async (params) => {
  const response = await api.get('/busca/impressoras', { params });
  return response.data;
};

export default api;
