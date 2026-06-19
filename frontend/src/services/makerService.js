import apiClient from '../api/client';

const headers = (makerId) => ({ 'X-Maker-Id': makerId });

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

  // Pedidos
  async listarPedidos(makerId, status = null) {
    const params = status ? { status } : {};
    const { data } = await apiClient.get('/maker/pedidos', { headers: headers(makerId), params });
    return data;
  },

  async atualizarStatusPedido(makerId, pedidoId, status, impressoraId = null) {
    const { data } = await apiClient.patch(
      `/maker/pedidos/${pedidoId}/status`,
      { status, impressoraId },
      { headers: headers(makerId) }
    );
    return data;
  },

  // Serviços
  async listarServicos(makerId) {
    const { data } = await apiClient.get('/maker/servicos', { headers: headers(makerId) });
    return data;
  },

  async criarServico(makerId, dados) {
    const { data } = await apiClient.post('/maker/servicos', dados, { headers: headers(makerId) });
    return data;
  },

  async editarServico(makerId, servicoId, dados) {
    const { data } = await apiClient.put(`/maker/servicos/${servicoId}`, dados, { headers: headers(makerId) });
    return data;
  },

  async removerServico(makerId, servicoId) {
    await apiClient.delete(`/maker/servicos/${servicoId}`, { headers: headers(makerId) });
  },

  // Avaliações
  async listarAvaliacoes(makerId) {
    const { data } = await apiClient.get('/avaliacoes/maker', { headers: headers(makerId) });
    return data;
  },

};
