import apiClient from '../api/client';

export const pedidoService = {
  solicitarPedido: async (clienteId, dados) => {
    const response = await apiClient.post('/pedidos', dados, {
      headers: {
        'X-Cliente-Id': clienteId
      }
    });
    return response.data;
  },

  listarPedidos: async (clienteId) => {
    const response = await apiClient.get('/pedidos', {
      headers: {
        'X-Cliente-Id': clienteId
      }
    });
    return response.data;
  }
};
