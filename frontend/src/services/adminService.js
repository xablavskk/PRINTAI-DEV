import apiClient from '../api/client';

const adminHeaders = (adminId) => ({ 'X-Admin-Id': adminId });

export const adminService = {

  login: async (email, senha) => {
    const { data } = await apiClient.post('/admin/login', { email, senha });
    return data;
  },

  listarPendentes: async (adminId) => {
    const { data } = await apiClient.get('/admin/makers/pendentes', {
      headers: adminHeaders(adminId),
    });
    return data;
  },

  listarTodos: async (adminId) => {
    const { data } = await apiClient.get('/admin/makers', {
      headers: adminHeaders(adminId),
    });
    return data;
  },

  buscarMaker: async (adminId, makerId) => {
    const { data } = await apiClient.get(`/admin/makers/${makerId}`, {
      headers: adminHeaders(adminId),
    });
    return data;
  },

  processarSolicitacao: async (adminId, makerId, aprovado, motivoRejeicao = '') => {
    const { data } = await apiClient.patch(
      `/admin/makers/${makerId}/processar`,
      { aprovado, motivoRejeicao },
      { headers: adminHeaders(adminId) }
    );
    return data;
  },

  salvarSessao: (dados) => {
    localStorage.removeItem('printai_cliente');
    localStorage.setItem('printai_admin', JSON.stringify(dados));
  },

  obterSessao: () => {
    const sessao = localStorage.getItem('printai_admin');
    return sessao ? JSON.parse(sessao) : null;
  },

  limparSessao: () => {
    localStorage.removeItem('printai_admin');
  },
};
