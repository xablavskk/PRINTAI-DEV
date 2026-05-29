import React, { useState, useEffect } from 'react';
import AbasBusca from '../components/AbasBusca';
import BuscaSimplificada from '../components/BuscaSimplificada';
import BuscaAvancada from '../components/BuscaAvancada';
import ListaServicos from '../components/ListaServicos';
import MapaServicos from '../components/MapaServicos';
import { useBusca } from '../hooks/useBusca';
import './BuscaServicos.css';

const BuscaServicos = ({ cliente, setLoginAberto }) => {
  const [abaAtiva, setAbaAtiva] = useState('simplified');
  const [jaBuscou, setJaBuscou] = useState(false);
  const { resultados, loading, buscar } = useBusca();

  useEffect(() => {
    handleSearch({});
  }, []);

  const handleSearch = async (params) => {
    setJaBuscou(true);
    try {
      await buscar(params);
    } catch (error) {
      console.error('Erro ao buscar dados:', error);
    }
  };

  return (
    <div className="container">
      <div className="hero-section">
        <h1 className="text-gradient">Encontre seu Maker</h1>
        <p>Conecte-se com especialistas em impressão 3D perto de você.</p>
      </div>

      <div className="search-box glass-panel">
        <AbasBusca abaAtiva={abaAtiva} setAbaAtiva={setAbaAtiva} />

        <div className="search-interface">
          {abaAtiva === 'simplified' ? (
            <BuscaSimplificada onSearch={handleSearch} />
          ) : (
            <BuscaAvancada onSearch={handleSearch} />
          )}
        </div>
      </div>

      {jaBuscou && (
        <div className="results-container">
          <div className="results-layout">
            <div className="list-column">
              <div className="results-info">
                <span>{resultados.length} Makers encontrados</span>
              </div>
              <ListaServicos resultados={resultados} carregando={loading} cliente={cliente} setLoginAberto={setLoginAberto} />
            </div>
            <div className="map-column">
              <MapaServicos resultados={resultados} cliente={cliente} setLoginAberto={setLoginAberto} />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};


export default BuscaServicos;
