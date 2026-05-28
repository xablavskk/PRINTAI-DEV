import apiClient from '../api/client';

export const makerService = {
  async solicitarCadastro(dados) {
    const { data } = await apiClient.post('/maker/cadastro', dados);
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
