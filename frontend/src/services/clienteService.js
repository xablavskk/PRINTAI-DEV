import apiClient from '../api/client';

export const clienteService = {
  async realizarCadastro(dados) {
    const { data } = await apiClient.post('/cliente/cadastro', dados);
    return data;
  },
};
