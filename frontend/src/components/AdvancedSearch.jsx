import React, { useState } from 'react';
import { Settings2 } from 'lucide-react';
import './SearchForms.css';

const AdvancedSearch = ({ onSearch }) => {
  const [filters, setFilters] = useState({
    technology: '',
    material: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(filters);
  };

  return (
    <div className="search-form-container animate-fade-in">
      <h2 className="form-title">Filtros Técnicos</h2>
      <p className="form-subtitle">Selecione as especificações exatas para sua impressão.</p>
      
      <form onSubmit={handleSearch} className="advanced-form">
        <div className="form-group">
          <label>Tecnologia</label>
          <select name="technology" value={filters.technology} onChange={handleChange} className="search-select">
            <option value="">Qualquer Tecnologia</option>
            <option value="FDM">FDM (Filamento)</option>
            <option value="SLA">SLA (Resina)</option>
            <option value="SLS">SLS (Pó)</option>
          </select>
        </div>

        <div className="form-group">
          <label>Material</label>
          <select name="material" value={filters.material} onChange={handleChange} className="search-select">
            <option value="">Qualquer Material</option>
            <option value="PLA">PLA</option>
            <option value="ABS">ABS</option>
            <option value="PETG">PETG</option>
            <option value="Resin">Resina</option>
          </select>
        </div>

        <button type="submit" className="btn btn-primary flex-center gap-2 mt-4">
          <Settings2 size={18} />
          Aplicar Filtros
        </button>
      </form>
    </div>
  );
};

export default AdvancedSearch;
