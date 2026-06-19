import { useState, useCallback } from 'react';
import { adminService } from '../services/adminService';

export const useAdmin = () => {
  const [makers, setMakers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');
  const [filtro, setFiltro] = useState('pendentes'); // 'pendentes' | 'todos'

  const carregar = useCallback(async (adminId) => {
    setLoading(true);
    setErro('');
    try {
      const data = filtro === 'pendentes'
        ? await adminService.listarPendentes(adminId)
        : await adminService.listarTodos(adminId);
      setMakers(data);
    } catch (err) {
      setErro(err.response?.data?.erro || 'Erro ao carregar solicitações.');
    } finally {
      setLoading(false);
    }
  }, [filtro]);

  const processar = async (adminId, makerId, aprovado, motivoRejeicao = '') => {
    setErro('');
    try {
      const atualizado = await adminService.processarSolicitacao(adminId, makerId, aprovado, motivoRejeicao);
      setMakers(prev =>
        filtro === 'pendentes'
          ? prev.filter(m => m.id !== makerId)
          : prev.map(m => m.id === makerId ? atualizado : m)
      );
      return atualizado;
    } catch (err) {
      const msg = err.response?.data?.erro || 'Erro ao processar solicitação.';
      setErro(msg);
      throw new Error(msg);
    }
  };

  return { makers, loading, erro, filtro, setFiltro, carregar, processar };
};
