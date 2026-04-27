import React, { useState, useEffect } from 'react';
import { searchServices, searchPrinters } from '../services/api';
import AbasBusca from '../components/AbasBusca';
import BuscaSimplificada from '../components/BuscaSimplificada';
import BuscaAvancada from '../components/BuscaAvancada';
import ListaServicos from '../components/ListaServicos';
import MapaServicos from '../components/MapaServicos';
import './BuscaServicos.css';

const BuscaServicos = () => {
  const [abaAtiva, setAbaAtiva] = useState('simplified');
  const [resultados, setResultados] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [jaBuscou, setJaBuscou] = useState(false);

  useEffect(() => {
    handleSearch({});
  }, []);

  const handleSearch = async (params) => {
    setCarregando(true);
    setJaBuscou(true);
    try {
      // Agora buscamos de forma unificada (o backend filtrará em ambos)
      const dados = await searchServices(params);
      setResultados(dados);
    } catch (error) {
      console.error('Erro ao buscar dados:', error);
      // Fallback com dados unificados para demonstração
      const mockResultados = [
        {
          id: 1,
          nome: "Oficina do Adriano",
          descricao: "Especialista em peças técnicas e prototipagem rápida.",
          tecnologia: "FDM, SLA",
          material: "PLA, ABS, Resina",
          modelo: "Ender 3, Halot One",
          maker: { nome: "Adriano Silva", latitude: -23.550520, longitude: -46.633308, telefone: "+55 11 99999-1111" }
        },
        {
          id: 2,
          nome: "Lucas 3D Print",
          descricao: "Foco em miniaturas de RPG e objetos de decoração.",
          tecnologia: "SLA",
          material: "Resina Premium",
          modelo: "Anycubic Photon",
          maker: { nome: "Lucas Oliveira", latitude: -23.561684, longitude: -46.655981, telefone: "+55 11 98888-2222" }
        }
      ];
      setResultados(mockResultados);
    } finally {
      setCarregando(false);
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
              <ListaServicos resultados={resultados} carregando={carregando} />
            </div>
            <div className="map-column">
              <MapaServicos resultados={resultados} />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default BuscaServicos;
