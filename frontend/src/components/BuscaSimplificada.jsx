import React, { useState } from 'react';
import { Search } from 'lucide-react';
import './SearchForms.css';

const tags = ["Peça pequena", "Objeto decorativo", "Protótipo", "Engrenagem", "Miniatura"];

const BuscaSimplificada = ({ onSearch }) => {
  const [busca, setBusca] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch({ buscaSimplificada: busca });
  };

  const handleTagClick = (tag) => {
    setBusca(tag);
    onSearch({ buscaSimplificada: tag });
  };

  return (
    <div className="search-form-container animate-fade-in mt-4">
      <h2 className="form-title">O que você deseja imprimir?</h2>
      <p className="form-subtitle">Descreva em poucas palavras ou escolha uma sugestão.</p>

      <form onSubmit={handleSearch} className="search-input-group">
        <div className="input-wrapper">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            placeholder="Ex: peça pequena para drone..."
            className="search-input"
          />
        </div>
        <button type="submit" className="btn btn-primary">Buscar</button>
      </form>

      <div className="tags-container">
        <p className="tags-label">Sugestões:</p>
        <div className="tags-list">
          {tags.map(tag => (
            <span
              key={tag}
              className="search-tag"
              onClick={() => handleTagClick(tag)}
            >
              {tag}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default BuscaSimplificada;
