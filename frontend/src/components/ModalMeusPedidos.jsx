import React, { useEffect, useState } from 'react';
import { X, Clock, FileText, User } from 'lucide-react';
import { pedidoService } from '../services/pedidoService';
import './ModalMeusPedidos.css';

export default function ModalMeusPedidos({ isOpen, onClose, cliente }) {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen && cliente) {
      carregarPedidos();
    }
  }, [isOpen, cliente]);

  const carregarPedidos = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await pedidoService.listarPedidos(cliente.id);
      setPedidos(data.sort((a, b) => b.id - a.id));
    } catch (err) {
      setError('Não foi possível carregar seus pedidos.');
    } finally {
      setLoading(false);
    }
  };

  const formatarStatus = (statusOriginal) => {
    const statusMap = {
      'AGUARDANDO_ANALISE': 'Aguardando Análise',
      'APROVADO': 'Aprovado',
      'EM_PRODUCAO': 'Em Produção',
      'FINALIZADO': 'Finalizado',
      'CANCELADO': 'Cancelado'
    };
    return statusMap[statusOriginal] || statusOriginal;
  };

  const formatarData = (dataStr) => {
    if (!dataStr) return '';
    const d = new Date(dataStr);
    return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container pedidos-modal animate-scale-up" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>
          <X size={24} />
        </button>

        <div className="pedidos-header">
          <h2>Meus Pedidos</h2>
          <p>Acompanhe o status e atualizações dos seus projetos solicitados.</p>
        </div>

        {error && <div className="pedidos-error">{error}</div>}

        <div className="pedidos-body">
          {loading ? (
            <div className="loading-center">
              <div className="spinner"></div>
            </div>
          ) : pedidos.length === 0 ? (
            <div className="pedidos-empty">
              <FileText size={48} color="var(--text-secondary)" style={{ opacity: 0.5 }} />
              <h3>Nenhum pedido solicitado</h3>
              <p>Os serviços que você solicitar no mapa aparecerão aqui.</p>
            </div>
          ) : (
            <div className="pedidos-list">
              {pedidos.map((p) => (
                <div key={p.id} className="pedido-item glass-panel">
                  <div className="pedido-row-main">
                    <div>
                      <span className="pedido-num">Pedido #{p.id}</span>
                      <span className="pedido-date">{formatarData(p.dataPedido)}</span>
                    </div>
                    <span className={`status-badge-pill ${p.status.toLowerCase()}`}>
                      {formatarStatus(p.status)}
                    </span>
                  </div>

                  <div className="pedido-details">
                    <div>
                      <span>Serviço:</span>
                      <strong>{p.servicoNome}</strong>
                    </div>
                    <div>
                      <span>Maker:</span>
                      <strong>{p.makerNome}</strong>
                    </div>
                    <div>
                      <span>Tipo de Pedido:</span>
                      <strong>{p.tipoPedido === 'COM_MODELO' ? 'Modelo STL/OBJ' : 'Sob Demanda'}</strong>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
