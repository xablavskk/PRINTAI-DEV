import apiClient from '../api/client';

export const buscaService = {
  async listarServicos(params) {
    const { data } = await apiClient.get('/busca/servicos', { params });
    return data;
  },

  async listarImpressoras(params) {
    const { data } = await apiClient.get('/busca/impressoras', { params });
    return data;
  },

  async obterDetalhes(id) {
    const { data } = await apiClient.get(`/busca/detalhe/${id}`);
    return data;
  },

  async listarTipos() {
    const { data } = await apiClient.get('/tipos');
    return data;
  },

  async listarMateriais() {
    const { data } = await apiClient.get('/materiais');
    return data;
  },
};
