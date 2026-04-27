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
  const [tipoBusca, setTipoBusca] = useState('servicos');
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
      let dados;
      if (tipoBusca === 'impressoras') {
        dados = await searchPrinters(params);
      } else {
        dados = await searchServices(params);
      }
      setResultados(dados);
    } catch (error) {
      console.error('Erro ao buscar dados:', error);
      const mockServicos = [
        {
          id: 1,
          nome: "Impressão FDM de Alta Precisão",
          descricao: "Serviço ideal para protótipos e peças mecânicas em diversas cores.",
          tecnologia: "FDM",
          material: "PLA",
          maker: { nome: "Adriano Maker", latitude: -23.550520, longitude: -46.633308, telefone: "+55 11 99999-1111" }
        },
        {
          id: 2,
          nome: "Impressão em Resina SLA Premium",
          descricao: "Perfeito para miniaturas e objetos decorativos com alto detalhamento.",
          tecnologia: "SLA",
          material: "Resina",
          maker: { nome: "Lucas Maker", latitude: -23.561684, longitude: -46.655981, telefone: "+55 11 98888-2222" }
        }
      ];

      const mockImpressoras = [
        {
          id: 101,
          nome: "Ender 3 Pro - Adriano",
          modelo: "Ender 3",
          tecnologia: "FDM",
          material: "PLA, ABS, PETG",
          descricao: "Ótima para peças funcionais.",
          maker: { nome: "Adriano Maker", latitude: -23.550520, longitude: -46.633308, telefone: "+55 11 99999-1111" }
        }
      ];

      let dadosMock = tipoBusca === 'impressoras' ? mockImpressoras : mockServicos;

      let dadosFiltrados = dadosMock;
      if (params) {
        if (params.tecnologia && params.tecnologia !== '') {
          dadosFiltrados = dadosFiltrados.filter(s => s.tecnologia === params.tecnologia);
        }
        if (params.material && params.material !== '') {
          dadosFiltrados = dadosFiltrados.filter(s => s.material === params.material);
        }
        if (params.modelo && params.modelo !== '') {
          dadosFiltrados = dadosFiltrados.filter(s => s.modelo?.toLowerCase().includes(params.modelo.toLowerCase()));
        }
        if (params.buscaSimplificada) {
          const termo = params.buscaSimplificada.toLowerCase();
          if (termo.includes('peça pequena') || termo.includes('protótipo') || termo.includes('engrenagem')) {
            dadosFiltrados = dadosFiltrados.filter(s => s.id === 1 || s.id === 101);
          } else if (termo.includes('decorativo') || termo.includes('miniatura')) {
            dadosFiltrados = dadosFiltrados.filter(s => s.id === 2);
          } else {
            dadosFiltrados = [];
          }
        }
      }

      setResultados(dadosFiltrados);
    } finally {
      setCarregando(false);
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

      <AbasBusca abaAtiva={abaAtiva} setAbaAtiva={setAbaAtiva} />

      <div className="flex-center gap-4 mb-6">
        <button
          className={`btn me-2 ${tipoBusca === 'servicos' ? 'btn-primary' : 'btn-outline'}`}
          onClick={() => setTipoBusca('servicos')}
        >
          Buscar Serviços
        </button>
        <button
          className={`btn ${tipoBusca === 'impressoras' ? 'btn-primary' : 'btn-outline'}`}
          onClick={() => setTipoBusca('impressoras')}
        >
          Buscar Impressoras
        </button>
      </div>

      {abaAtiva === 'simplified' ? (
        <BuscaSimplificada onSearch={handleSearch} />
      ) : (
        <BuscaAvancada onSearch={handleSearch} />
      )}

      {jaBuscou && (
        <div className="results-container mt-4">
          <div className="results-header">
            <h2 style={{ fontSize: '1.5rem', margin: 0 }}>Resultados da Busca</h2>
          </div>

          <div className="results-layout">
            <div className="list-column">
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
