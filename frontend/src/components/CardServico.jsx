import React, { useState } from 'react';
import { Printer, User, Wrench, Package } from 'lucide-react';
import ModalDetalhes from './ModalDetalhes';
import './ServiceList.css';

const CardServico = ({ resultado }) => {
  const [modalAberto, setModalAberto] = useState(false);

  return (
    <>
      <div className="service-card glass-panel animate-fade-in">
        <div className="service-card-header" onClick={() => setModalAberto(true)} style={{ cursor: 'pointer' }}>
          <div>
            <h3 className="service-title">{resultado.maker?.nome || resultado.nome}</h3>
            <p className="service-location" style={{ fontSize: '0.8rem', color: 'var(--accent-color)' }}>
              São Paulo, SP • 2.5km
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
            <span>{resultado.tecnologias?.length > 0 ? resultado.tecnologias.join(', ') : resultado.tipoNome || 'N/A'}</span>
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
        
        <div className="service-actions">
          <button 
            className="btn btn-primary w-full"
            onClick={() => setModalAberto(true)}
          >
            Ver Perfil e Orçamento
          </button>
        </div>
      </div>

      <ModalDetalhes 
        aberto={modalAberto} 
        fechar={() => setModalAberto(false)} 
        dados={resultado} 
      />
    </>
  );
};

export default CardServico;
