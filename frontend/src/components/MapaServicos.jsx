import { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { divIcon } from 'leaflet';
import { Phone, ShieldCheck } from 'lucide-react';
import ModalDetalhes from './ModalDetalhes';
import { formatarTelefone } from '../utils/mascaras';
import 'leaflet/dist/leaflet.css';
import './ServiceMap.css';

const createCustomIcon = () => divIcon({
  className: 'custom-marker',
  html: `<div class="marker-pin"></div><div class="marker-pulse"></div>`,
  iconSize: [24, 24],
  iconAnchor: [12, 12],
});

const MapaServicos = ({ resultados = [] }) => {
  const [servicoSelecionado, setServicoSelecionado] = useState(null);
  const [modalAberto, setModalAberto] = useState(false);
  const defaultCenter = [ -23.0913052, -47.2180265];

  const makerMap = {};
  resultados
    .filter(r => r.maker?.latitude && r.maker?.longitude)
    .forEach(r => {
      const key = r.maker.id ?? `${r.maker.latitude}_${r.maker.longitude}`;
      if (!makerMap[key]) makerMap[key] = { maker: r.maker, servicos: [] };
      makerMap[key].servicos.push(r);
    });
  const grupos = Object.values(makerMap);

  const center = grupos.length > 0
    ? [grupos[0].maker.latitude, grupos[0].maker.longitude]
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

          {grupos.map(({ maker, servicos }) => (
            <Marker
              key={maker.id ?? `${maker.latitude}_${maker.longitude}`}
              position={[maker.latitude, maker.longitude]}
              icon={customMarkerIcon}
            >
              <Popup className="premium-popup">
                <div className="popup-content">
                  <div className="popup-maker">
                    <ShieldCheck size={14} color="var(--accent-color)" />
                    <strong>{maker.nome}</strong>
                  </div>
                  <div className="popup-contact">
                    <Phone size={14} />
                    <span>{formatarTelefone(maker.telefone)}</span>
                  </div>

                  {servicos.map(s => (
                    <div key={s.id} style={{ borderTop: '1px solid var(--border-color)', paddingTop: '0.4rem', marginTop: '0.4rem' }}>
                      <div style={{ fontWeight: 'bold', fontSize: '0.85rem' }}>{s.nome}</div>
                      <span className="popup-tech">
                        {s.tecnologias?.length > 0 ? s.tecnologias.join(', ') : s.tipoNome || 'N/A'}
                        {s.material ? ` — ${s.material}` : ''}
                      </span>
                      <button
                        className="btn btn-primary"
                        style={{ width: '100%', marginTop: '0.4rem', padding: '0.3rem' }}
                        onClick={() => { setServicoSelecionado(s); setModalAberto(true); }}
                      >
                        Ver Detalhes
                      </button>
                    </div>
                  ))}
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
