import React, { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { divIcon } from 'leaflet';
import { Phone, ShieldCheck } from 'lucide-react';
import ModalDetalhes from './ModalDetalhes';
import 'leaflet/dist/leaflet.css';
import './ServiceMap.css';

const createCustomIcon = () => {
  return divIcon({
    className: 'custom-marker',
    html: `<div class="marker-pin"></div><div class="marker-pulse"></div>`,
    iconSize: [24, 24],
    iconAnchor: [12, 12]
  });
};

const MapaServicos = ({ resultados = [] }) => {
  const [servicoSelecionado, setServicoSelecionado] = useState(null);
  const [modalAberto, setModalAberto] = useState(false);
  const defaultCenter = [-23.550520, -46.633308];

  const abrirDetalhes = (servico) => {
    setServicoSelecionado(servico);
    setModalAberto(true);
  };

  const resultadosValidos = resultados.filter(r => r.maker?.latitude && r.maker?.longitude);
  const center = resultadosValidos.length > 0
    ? [resultadosValidos[0].maker.latitude, resultadosValidos[0].maker.longitude]
    : defaultCenter;

  const customMarkerIcon = createCustomIcon();

  return (
    <>
      <div className="map-wrapper glass-panel">
        <MapContainer center={center} zoom={13} style={{ height: '100%', width: '100%', borderRadius: '1rem' }}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
          />

          {resultadosValidos.map(resultado => (
            <Marker
              key={resultado.id}
              position={[resultado.maker.latitude, resultado.maker.longitude]}
              icon={customMarkerIcon}
            >
              <Popup className="premium-popup">
                <div className="popup-content">
                  <h3>{resultado.nome}</h3>
                  <span className="popup-tech">{resultado.tecnologia} - {resultado.material}</span>
                  {resultado.modelo && <div style={{ fontSize: '0.8rem', color: 'var(--accent-color)', fontWeight: 'bold' }}>Modelo: {resultado.modelo}</div>}
                  <p>{resultado.descricao}</p>
                  <div className="popup-contact">
                    <Phone size={14} />
                    <span>{resultado.maker?.telefone || 'Não informado'}</span>
                  </div>
                  <div className="popup-maker">
                    <ShieldCheck size={14} color="var(--accent-color)" />
                    <span>Maker: <strong>{resultado.maker?.nome}</strong></span>
                  </div>
                  <button 
                    className="btn btn-primary" 
                    style={{ width: '100%', marginTop: '10px', padding: '0.4rem' }}
                    onClick={() => abrirDetalhes(resultado)}
                  >
                    Ver Perfil Completo
                  </button>
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>

      <ModalDetalhes 
        aberto={modalAberto} 
        fechar={() => setModalAberto(false)} 
        dados={servicoSelecionado} 
      />
    </>
  );
};

export default MapaServicos;
