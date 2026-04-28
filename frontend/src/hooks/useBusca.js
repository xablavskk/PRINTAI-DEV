import { useState, useCallback, useEffect } from 'react';
import { buscaService } from '../services/buscaService';

export const useBusca = () => {
  const [resultados, setResultados] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const buscar = useCallback(async (params) => {
    setLoading(true);
    setError(null);
    try {
      const temFiltroImpressora = params.tecnologia || params.material || params.modelo || params.volumeMaximo;
      const data = temFiltroImpressora
        ? await buscaService.listarImpressoras(params)
        : await buscaService.listarServicos(params);
      setResultados(data);
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { resultados, loading, error, buscar };
};

export const useDetalhesServico = (id) => {
  const [detalhes, setDetalhes] = useState(null);
  const [loading, setLoading] = useState(false);

  const carregar = useCallback(async () => {
    if (!id || id === undefined) return;
    
    setLoading(true);
    try {
      const data = await buscaService.obterDetalhes(id);
      setDetalhes(data);
    } catch (err) {
      if (err.response?.status !== 404) {
        console.error('Erro ao carregar detalhes:', err);
      }
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    setDetalhes(null);
  }, [id]);

  return { detalhes, loading, carregar };
};
