import React from 'react';
import ServiceCard from './ServiceCard';
import './ServiceList.css';

const ServiceList = ({ services, loading }) => {
  if (loading) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p>Buscando as melhores impressoras 3D para você...</p>
      </div>
    );
  }

  if (!services || services.length === 0) {
    return (
      <div className="empty-state glass-panel">
        <img src="https://illustrations.popsy.co/amber/page-under-construction.svg" alt="Empty" className="empty-img" />
        <h3>Nenhum serviço encontrado</h3>
        <p>Tente ajustar seus filtros ou use palavras diferentes na busca simplificada.</p>
      </div>
    );
  }

  return (
    <div className="service-grid">
      {services.map(service => (
        <ServiceCard key={service.id} service={service} />
      ))}
    </div>
  );
};

export default ServiceList;
