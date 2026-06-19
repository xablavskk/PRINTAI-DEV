import { useState, useEffect } from 'react';
import { authService } from '../services/authService';

export function useAuth() {
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const sessao = authService.obterSessao();
    if (sessao) {
      setCliente(sessao);
    }
    setLoading(false);
  }, []);

  const login = async (email, senha) => {
    setLoading(true);
    try {
      const dados = await authService.login(email, senha);
      authService.salvarSessao(dados);
      setCliente(dados);
      return dados;
    } catch (err) {
      const msg = err.response?.data?.erro || 'Erro ao realizar login';
      throw new Error(msg);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.limparSessao();
    setCliente(null);
  };

  return {
    cliente,
    logado: !!cliente,
    loading,
    login,
    logout
  };
}
