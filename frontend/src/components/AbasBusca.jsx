import React from 'react';
import './SearchTabs.css';

const AbasBusca = ({ abaAtiva, setAbaAtiva }) => {
  return (
    <div className="search-tabs">
      <button 
        className={`tab-btn ${abaAtiva === 'simplified' ? 'active' : ''}`}
        onClick={() => setAbaAtiva('simplified')}
      >
        Busca Simplificada
      </button>
      <button 
        className={`tab-btn ${abaAtiva === 'advanced' ? 'active' : ''}`}
        onClick={() => setAbaAtiva('advanced')}
      >
        Busca Avançada
      </button>
    </div>
  );
};

export default AbasBusca;
