import React, { useState } from 'react';
import './ModalSolicitarPedido.css';
import { pedidoService } from '../services/pedidoService';

export default function ModalSolicitarPedido({ isOpen, onClose, servico, cliente }) {
  const [tipoPedido, setTipoPedido] = useState('COM_MODELO'); // COM_MODELO ou SEM_MODELO
  const [arquivo3D, setArquivo3D] = useState('');
  const [descricaoNecessidade, setDescricaoNecessidade] = useState('');
  const [material, setMaterial] = useState('');
  const [quantidade, setQuantidade] = useState(1);
  const [observacoes, setObservacoes] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [sucesso, setSucesso] = useState(null);
  const [erro, setErro] = useState('');

  if (!isOpen || !servico) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setLoading(true);

    const payload = {
      servicoId: servico.id,
      tipoPedido,
      quantidade,
      observacoes,
      material: material || servico.material || undefined
    };

    if (tipoPedido === 'COM_MODELO') {
      if (!arquivo3D.trim()) {
        setErro('Por favor, informe o nome ou link do arquivo 3D.');
        setLoading(false);
        return;
      }
      payload.arquivo3D = arquivo3D;
    } else {
      if (!descricaoNecessidade.trim()) {
        setErro('Por favor, descreva detalhadamente sua necessidade.');
        setLoading(false);
        return;
      }
      payload.descricaoNecessidade = descricaoNecessidade;
    }

    try {
      const data = await pedidoService.solicitarPedido(cliente.id, payload);
      setSucesso(data);
    } catch (err) {
      setErro(err.response?.data?.erro || err.message || 'Erro ao processar solicitação.');
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

  if (sucesso) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-container request-modal success animate-scale-up" onClick={(e) => e.stopPropagation()}>
          <div className="success-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#10b981" strokeWidth="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <h2>Pedido Solicitado!</h2>
          <p className="success-msg">{sucesso.mensagem}</p>
          
          <div className="summary-box">
            <div><span>Número do Pedido:</span> <strong>#{sucesso.id}</strong></div>
            <div><span>Serviço:</span> <strong>{sucesso.servicoNome}</strong></div>
            <div><span>Maker Parceiro:</span> <strong>{sucesso.makerNome}</strong></div>
            <div><span>Status Atual:</span> <strong className="status-badge">{formatarStatus(sucesso.status)}</strong></div>
          </div>

          <button className="btn-success-close" onClick={onClose}>
            Fechar e Voltar ao Mapa
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container request-modal animate-scale-up" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>

        <div className="request-header">
          <h2>Solicitar Projeto 3D</h2>
          <p>Você está solicitando para o serviço de <strong>{servico.nome}</strong> de <strong>{servico.maker?.nome}</strong>.</p>
        </div>

        {erro && <div className="request-error">{erro}</div>}

        <form onSubmit={handleSubmit} className="request-form">
          <div className="type-selector">
            <button
              type="button"
              className={tipoPedido === 'COM_MODELO' ? 'active' : ''}
              onClick={() => setTipoPedido('COM_MODELO')}
            >
              🚀 Tenho modelo 3D pronto
            </button>
            <button
              type="button"
              className={tipoPedido === 'SEM_MODELO' ? 'active' : ''}
              onClick={() => setTipoPedido('SEM_MODELO')}
            >
              💡 Não tenho modelo pronto
            </button>
          </div>

          {tipoPedido === 'COM_MODELO' ? (
            <div className="form-group animate-fade">
              <label htmlFor="req-arquivo">Arquivo Digital (Nome/Link STL ou OBJ) *</label>
              <input
                id="req-arquivo"
                type="text"
                placeholder="Ex: vasinho_decorativo.stl ou link de download"
                value={arquivo3D}
                onChange={(e) => setArquivo3D(e.target.value)}
                required
              />
              <span className="input-tip">Informe o nome do arquivo ou link externo temporário do seu modelo.</span>
            </div>
          ) : (
            <div className="form-group animate-fade">
              <label htmlFor="req-descricao">Descrição da sua Necessidade *</label>
              <textarea
                id="req-descricao"
                rows="4"
                placeholder="Ex: Preciso de uma engrenagem de reposição para meu liquidificador de X cm de diâmetro..."
                value={descricaoNecessidade}
                onChange={(e) => setDescricaoNecessidade(e.target.value)}
                required
              ></textarea>
              <span className="input-tip">Forneça o máximo de detalhes possível para que o Maker elabore um orçamento.</span>
            </div>
          )}

          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="req-material">Material Desejado</label>
              <input
                id="req-material"
                type="text"
                placeholder={`Ex: PLA, ABS, PETG (Padrão: ${servico.material || 'PLA'})`}
                value={material}
                onChange={(e) => setMaterial(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="req-qtd">Quantidade</label>
              <input
                id="req-qtd"
                type="number"
                min="1"
                value={quantidade}
                onChange={(e) => setQuantidade(parseInt(e.target.value) || 1)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="req-obs">Observações Adicionais</label>
            <textarea
              id="req-obs"
              rows="2"
              placeholder="Alguma restrição de cor, embalagem ou acabamento específico?"
              value={observacoes}
              onChange={(e) => setObservacoes(e.target.value)}
            ></textarea>
          </div>

          <div className="request-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? <div className="spinner-small"></div> : 'Confirmar Solicitação'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
