import apiClient from '../api/client';

export const avaliacaoService = {
  avaliarMaker: async (clienteId, dados) => {
    const response = await apiClient.post('/avaliacoes', dados, {
      headers: { 'X-Cliente-Id': clienteId }
    });
    return response.data;
  }
};
