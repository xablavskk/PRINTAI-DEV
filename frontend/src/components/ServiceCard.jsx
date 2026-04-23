import React from 'react';
import { Printer, User, Wrench, Package } from 'lucide-react';
import './ServiceList.css';

const ServiceCard = ({ service }) => {
  return (
    <div className="service-card glass-panel animate-fade-in">
      <div className="service-card-header">
        <h3 className="service-title">{service.name}</h3>
        <span className="tech-badge">{service.technology}</span>
      </div>
      
      <p className="service-description">{service.description}</p>
      
      <div className="service-details">
        <div className="detail-item">
          <Package size={16} className="detail-icon" />
          <span>Material: <strong>{service.material}</strong></span>
        </div>
        <div className="detail-item">
          <User size={16} className="detail-icon" />
          <span>Maker: <strong>{service.maker?.name}</strong></span>
        </div>
      </div>
      
      <div className="service-actions">
        <button className="btn btn-primary w-full">Ver Detalhes do Serviço</button>
      </div>
    </div>
  );
};

export default ServiceCard;
