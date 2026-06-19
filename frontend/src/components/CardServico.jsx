import React, { useState } from 'react';
import { Printer, User, Wrench, Package } from 'lucide-react';
import ModalDetalhes from './ModalDetalhes';
import ModalSolicitarPedido from './ModalSolicitarPedido';
import './ServiceList.css';

const CardServico = ({ resultado, cliente, setLoginAberto }) => {
  const [modalAberto, setModalAberto] = useState(false);
  const [pedidoAberto, setPedidoAberto] = useState(false);

  const isMeuServico = cliente && cliente.id === resultado.maker?.id;

  const handleSolicitar = (e) => {
    e.stopPropagation();
    if (!cliente) {
      setLoginAberto(true);
    } else {
      setPedidoAberto(true);
    }
  };

  return (
    <>
      <div className="service-card glass-panel animate-fade-in">
        <div className="service-card-header" onClick={() => setModalAberto(true)} style={{ cursor: 'pointer' }}>
          <div>
            <h3 className="service-title">{resultado.maker?.nome || resultado.nome}</h3>
            <p className="service-location" style={{ fontSize: '0.8rem', color: 'var(--accent-color)' }}>
              {resultado.maker?.cidade && resultado.maker?.estado
                ? `${resultado.maker.cidade}, ${resultado.maker.estado}`
                : 'Localização não informada'}
              {resultado.distanciaKm != null ? ` • ${resultado.distanciaKm.toFixed(1)}km` : ''}
            </p>
          </div>
          <div className="maker-avatar">
            <User size={20} />
          </div>
        </div>
        
        <p className="service-description" onClick={() => setModalAberto(true)} style={{ cursor: 'pointer' }}>
          {resultado.descricao}
        </p>
        
        <div className="capabilities-group">
          <div className="capability-tag">
            <Wrench size={14} />
            <span>{resultado.tecnologias?.length > 0 ? [...new Set(resultado.tecnologias)].join(', ') : resultado.tipoNome || 'N/A'}</span>
          </div>
          <div className="capability-tag">
            <Package size={14} />
            <span>{resultado.material}</span>
          </div>
          {resultado.modelo && (
            <div className="capability-tag">
              <Printer size={14} />
              <span>{resultado.modelo}</span>
            </div>
          )}
        </div>
        
        <div className="service-actions" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
          <button 
            className="btn btn-outline"
            onClick={() => setModalAberto(true)}
          >
            Ver Perfil
          </button>
          {isMeuServico ? (
            <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.8rem', color: 'var(--accent-color)', fontWeight: '600', border: '1px solid var(--accent-color)', borderRadius: '8px', padding: '0.4rem' }}>
              Meu serviço
            </span>
          ) : (
            <button
              className="btn btn-primary"
              onClick={handleSolicitar}
            >
              Solicitar Pedido
            </button>
          )}
        </div>
      </div>

      <ModalDetalhes 
        aberto={modalAberto} 
        fechar={() => setModalAberto(false)} 
        dados={resultado} 
      />

      <ModalSolicitarPedido
        isOpen={pedidoAberto}
        onClose={() => setPedidoAberto(false)}
        servico={resultado}
        cliente={cliente}
      />
    </>
  );
};

export default CardServico;

