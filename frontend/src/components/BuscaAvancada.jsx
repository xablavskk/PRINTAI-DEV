import React, { useState, useEffect } from 'react';
import { Settings2 } from 'lucide-react';
import { buscaService } from '../services/buscaService';
import './SearchForms.css';

const BuscaAvancada = ({ onSearch }) => {
  const [filtros, setFiltros] = useState({
    tecnologia: '',
    material: '',
    modelo: '',
    volumeMaximo: '',
  });

  const [tipos, setTipos] = useState([]);
  const [materiais, setMateriais] = useState([]);

  useEffect(() => {
    buscaService.listarTipos().then(setTipos).catch(() => setTipos([]));
    buscaService.listarMateriais().then(setMateriais).catch(() => setMateriais([]));
  }, []);

  const tecnologiasDisponiveis = [...new Set(tipos.flatMap(t => t.tecnologias || []))];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFiltros(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = (e) => {
    e.preventDefault();
    const params = {};

    if (filtros.tecnologia) params.tecnologia = filtros.tecnologia;
    if (filtros.material) params.material = filtros.material;
    if (filtros.modelo) params.modelo = filtros.modelo;
    if (filtros.volumeMaximo !== '') params.volumeMaximo = Number(filtros.volumeMaximo);

    onSearch(params);
  };

  const handleLimpar = () => {
    setFiltros({ tecnologia: '', material: '', modelo: '', volumeMaximo: '' });
    onSearch({});
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
            {tecnologiasDisponiveis.map(tec => (
              <option key={tec} value={tec}>{tec}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Material</label>
          <select name="material" value={filtros.material} onChange={handleChange} className="search-select">
            <option value="">Qualquer Material</option>
            {materiais.map(m => (
              <option key={m.nome} value={m.nome} title={m.descricao}>
                {m.nome}
              </option>
            ))}
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

        <div className="advanced-form-actions">
          <button type="button" className="btn btn-outline" onClick={handleLimpar}>
            Limpar
          </button>
          <button type="submit" className="btn btn-primary flex-center gap-2">
            <Settings2 size={18} />
            Aplicar Filtros
          </button>
        </div>
      </form>
    </div>
  );
};

export default BuscaAvancada;
