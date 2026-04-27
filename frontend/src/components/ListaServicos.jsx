import React from 'react';
import CardServico from './CardServico';
import './ServiceList.css';

const ListaServicos = ({ resultados, carregando }) => {
  if (carregando) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p>Buscando as melhores impressoras 3D para você...</p>
      </div>
    );
  }

  if (!resultados || resultados.length === 0) {
    return (
      <div className="empty-state glass-panel">
        <img src="https://illustrations.popsy.co/amber/page-under-construction.svg" alt="Vazio" className="empty-img" />
        <h3>Nenhum resultado encontrado</h3>
        <p>Tente ajustar seus filtros ou use palavras diferentes na busca simplificada.</p>
      </div>
    );
  }

  return (
    <div className="service-grid">
      {resultados.map(resultado => (
        <CardServico key={resultado.id} resultado={resultado} />
      ))}
    </div>
  );
};

export default ListaServicos;
