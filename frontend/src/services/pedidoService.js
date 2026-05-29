import api from './api';

export const pedidoService = {
  solicitarPedido: async (clienteId, dados) => {
    const response = await api.post('/pedidos', dados, {
      headers: {
        'X-Cliente-Id': clienteId
      }
    });
    return response.data;
  },

  listarPedidos: async (clienteId) => {
    const response = await api.get('/pedidos', {
      headers: {
        'X-Cliente-Id': clienteId
      }
    });
    return response.data;
  }
};
