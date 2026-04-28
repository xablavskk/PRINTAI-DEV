import React from 'react';
import { Printer, User, Wrench, Package } from 'lucide-react';
import './ServiceList.css';

const CardServico = ({ resultado }) => {
  return (
    <div className="service-card glass-panel animate-fade-in">
      <div className="service-card-header">
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
      
      <p className="service-description">{resultado.descricao}</p>
      
      <div className="capabilities-group">
        <div className="capability-tag">
          <Wrench size={14} />
          <span>{resultado.tecnologia}</span>
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
        <button className="btn btn-primary w-full">Ver Perfil e Orçamento</button>
      </div>
    </div>
  );
};

export default CardServico;
