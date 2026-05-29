import { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { divIcon } from 'leaflet';
import { Phone, ShieldCheck } from 'lucide-react';
import ModalDetalhes from './ModalDetalhes';
import ModalSolicitarPedido from './ModalSolicitarPedido';
import { formatarTelefone } from '../utils/mascaras';
import 'leaflet/dist/leaflet.css';
import './ServiceMap.css';

const createCustomIcon = () => divIcon({
  className: 'custom-marker',
  html: `<div class="marker-pin"></div><div class="marker-pulse"></div>`,
  iconSize: [24, 24],
  iconAnchor: [12, 12],
});

const MapaServicos = ({ resultados = [], cliente, setLoginAberto }) => {
  const [servicoSelecionado, setServicoSelecionado] = useState(null);
  const [modalAberto, setModalAberto] = useState(false);
  const [pedidoAberto, setPedidoAberto] = useState(false);
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

  const handleSolicitar = (s) => {
    setServicoSelecionado(s);
    if (!cliente) {
      setLoginAberto(true);
    } else {
      setPedidoAberto(true);
    }
  };

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
                        {s.tecnologias?.length > 0 ? [...new Set(s.tecnologias)].join(', ') : s.tipoNome || 'N/A'}
                        {s.material ? ` — ${s.material}` : ''}
                      </span>
                      <div style={{ display: 'flex', gap: '4px', marginTop: '0.4rem' }}>
                        <button
                          className="btn btn-outline"
                          style={{ flex: 1, padding: '0.3rem', fontSize: '0.75rem' }}
                          onClick={() => { setServicoSelecionado(s); setModalAberto(true); }}
                        >
                          Detalhes
                        </button>
                        <button
                          className="btn btn-primary"
                          style={{ flex: 1, padding: '0.3rem', fontSize: '0.75rem' }}
                          onClick={() => handleSolicitar(s)}
                        >
                          Solicitar Pedido
                        </button>
                      </div>
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

      <ModalSolicitarPedido
        isOpen={pedidoAberto}
        onClose={() => setPedidoAberto(false)}
        servico={servicoSelecionado}
        cliente={cliente}
      />
    </>
  );
};

export default MapaServicos;

