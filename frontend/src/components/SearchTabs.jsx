import React from 'react';
import './SearchTabs.css';

const SearchTabs = ({ activeTab, setActiveTab }) => {
  return (
    <div className="search-tabs">
      <button 
        className={`tab-btn ${activeTab === 'simplified' ? 'active' : ''}`}
        onClick={() => setActiveTab('simplified')}
      >
        Busca Simplificada
      </button>
      <button 
        className={`tab-btn ${activeTab === 'advanced' ? 'active' : ''}`}
        onClick={() => setActiveTab('advanced')}
      >
        Busca Avançada
      </button>
    </div>
  );
};

export default SearchTabs;
