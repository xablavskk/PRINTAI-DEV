import React from 'react';
import { Printer, User, Wrench, Package } from 'lucide-react';
import './ServiceList.css';

const CardServico = ({ resultado }) => {
  return (
    <div className="service-card glass-panel animate-fade-in">
      <div className="service-card-header">
        <h3 className="service-title">{resultado.nome}</h3>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '0.2rem' }}>
          <span className="tech-badge">{resultado.tecnologia}</span>
          {resultado.modelo && <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)' }}>Mod: {resultado.modelo}</span>}
        </div>
      </div>
      
      <p className="service-description">{resultado.descricao}</p>
      
      <div className="service-details">
        <div className="detail-item">
          <Package size={16} className="detail-icon" />
          <span>Material: <strong>{resultado.material}</strong></span>
        </div>
        <div className="detail-item">
          <User size={16} className="detail-icon" />
          <span>Maker: <strong>{resultado.maker?.nome}</strong></span>
        </div>
      </div>
      
      <div className="service-actions">
        <button className="btn btn-primary w-full">Ver Detalhes</button>
      </div>
    </div>
  );
};

export default CardServico;
