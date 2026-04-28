import React, { useState } from 'react';
import { Settings2 } from 'lucide-react';
import './SearchForms.css';

const BuscaAvancada = ({ onSearch }) => {
  const [filtros, setFiltros] = useState({
    tecnologia: '',
    material: '',
    modelo: '',
    volumeMaximo: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFiltros(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    const params = { ...filtros };
    if (params.volumeMaximo !== '') {
      params.volumeMaximo = Number(params.volumeMaximo);
    } else {
      delete params.volumeMaximo;
    }
    onSearch(params);
  };

  return (
    <div className="search-form-container animate-fade-in">
      <h2 className="form-title">Filtros Técnicos</h2>
      <p className="form-subtitle">Selecione as especificações exatas para sua impressão.</p>
      
      <form onSubmit={handleSearch} className="advanced-form">
        <div className="form-group">
          <label>Tecnologia</label>
          <select name="tecnologia" value={filtros.tecnologia} onChange={handleChange} className="search-select">
            <option value="">Qualquer Tecnologia</option>
            <option value="FDM">FDM (Filamento)</option>
            <option value="SLA">SLA (Resina)</option>
            <option value="SLS">SLS (Pó)</option>
          </select>
        </div>

        <div className="form-group">
          <label>Material</label>
          <select name="material" value={filtros.material} onChange={handleChange} className="search-select">
            <option value="">Qualquer Material</option>
            <option value="PLA">PLA</option>
            <option value="ABS">ABS</option>
            <option value="PETG">PETG</option>
            <option value="Resina">Resina</option>
          </select>
        </div>

        <div className="form-group">
          <label>Modelo da Impressora</label>
          <input 
            type="text" 
            name="modelo" 
            value={filtros.modelo} 
            onChange={handleChange} 
            placeholder="Ex: Ender 3..." 
            className="search-input"
            style={{ marginTop: '0.5rem' }}
          />
        </div>

        <div className="form-group">
          <label>Volume Máximo (cm³)</label>
          <input 
            type="number" 
            name="volumeMaximo" 
            value={filtros.volumeMaximo} 
            onChange={handleChange} 
            placeholder="Ex: 20" 
            min="1"
            className="search-input"
            style={{ marginTop: '0.5rem' }}
          />
        </div>

        <button type="submit" className="btn btn-primary flex-center gap-2 mt-4">
          <Settings2 size={18} />
          Aplicar Filtros
        </button>
      </form>
    </div>
  );
};

export default BuscaAvancada;
