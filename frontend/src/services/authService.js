import api from './api';

export const authService = {
  login: async (email, senha) => {
    const response = await api.post('/auth/login', { email, senha });
    return response.data;
  },

  salvarSessao: (dados) => {
    localStorage.setItem('printai_cliente', JSON.stringify(dados));
  },

  obterSessao: () => {
    const sessao = localStorage.getItem('printai_cliente');
    return sessao ? JSON.parse(sessao) : null;
  },

  limparSessao: () => {
    localStorage.removeItem('printai_cliente');
  },

  estaLogado: () => {
    return !!localStorage.getItem('printai_cliente');
  }
};
