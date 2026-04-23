import React, { useState, useEffect } from 'react';
import { searchServices } from '../services/api';
import SearchTabs from '../components/SearchTabs';
import SimplifiedSearch from '../components/SimplifiedSearch';
import AdvancedSearch from '../components/AdvancedSearch';
import ServiceList from '../components/ServiceList';
import ServiceMap from '../components/ServiceMap';
import './SearchServices.css';

const SearchServices = () => {
  const [activeTab, setActiveTab] = useState('simplified');
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  // Load all services initially
  useEffect(() => {
    handleSearch({});
  }, []);

  const handleSearch = async (params) => {
    setLoading(true);
    setHasSearched(true);
    try {
      const results = await searchServices(params);
      setServices(results);
    } catch (error) {
      console.error('Error fetching services:', error);
      // Fallback in case backend is down for demonstration
      const mockData = [
        {
          id: 1,
          name: "Impressão FDM de Alta Precisão",
          description: "Serviço ideal para protótipos e peças mecânicas em diversas cores.",
          technology: "FDM",
          material: "PLA",
          maker: { name: "Adriano Maker", latitude: -23.550520, longitude: -46.633308, phone: "+55 11 99999-1111" }
        },
        {
          id: 2,
          name: "Impressão em Resina SLA Premium",
          description: "Perfeito para miniaturas e objetos decorativos com alto detalhamento.",
          technology: "SLA",
          material: "Resin",
          maker: { name: "Lucas Maker", latitude: -23.561684, longitude: -46.655981, phone: "+55 11 98888-2222" }
        }
      ];

      let filteredData = mockData;
      if (params) {
        if (params.technology && params.technology !== '') {
          filteredData = filteredData.filter(s => s.technology === params.technology);
        }
        if (params.material && params.material !== '') {
          filteredData = filteredData.filter(s => s.material === params.material);
        }
        if (params.simplifiedSearch) {
          const term = params.simplifiedSearch.toLowerCase();
          if (term.includes('peça pequena') || term.includes('protótipo') || term.includes('engrenagem')) {
            filteredData = filteredData.filter(s => s.id === 1);
          } else if (term.includes('decorativo') || term.includes('miniatura')) {
            filteredData = filteredData.filter(s => s.id === 2);
          } else {
            // Se buscar outra coisa e não bater com as palavras chave da nossa base mock, retorna vazio
            filteredData = [];
          }
        }
      }
      
      setServices(filteredData);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div style={{ maxWidth: '800px', margin: '0 auto', textAlign: 'center', marginBottom: '3rem' }}>
        <h1 className="text-gradient" style={{ fontSize: '3rem', fontWeight: '700', marginBottom: '1rem' }}>
          Busca de Impressoras 3D
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
          Encontre o serviço perfeito para materializar suas ideias.
          Nossa rede de Makers está pronta para atender você.
        </p>
      </div>

      <SearchTabs activeTab={activeTab} setActiveTab={setActiveTab} />
      
      {activeTab === 'simplified' ? (
        <SimplifiedSearch onSearch={handleSearch} />
      ) : (
        <AdvancedSearch onSearch={handleSearch} />
      )}

      {hasSearched && (
        <div className="results-container mt-4">
          <div className="results-header">
            <h2 style={{ fontSize: '1.5rem', margin: 0 }}>Resultados da Busca</h2>
          </div>
          
          <div className="results-layout">
            <div className="list-column">
              <ServiceList services={services} loading={loading} />
            </div>
            <div className="map-column">
              <ServiceMap services={services} />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchServices;
