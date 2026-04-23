import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { divIcon } from 'leaflet';
import { Phone, Package, ShieldCheck } from 'lucide-react';
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

const ServiceMap = ({ services }) => {
  // Center of SP by default
  const defaultCenter = [-23.550520, -46.633308];
  
  // Find center based on first valid service, or use default
  const validServices = services.filter(s => s.maker?.latitude && s.maker?.longitude);
  const center = validServices.length > 0 
    ? [validServices[0].maker.latitude, validServices[0].maker.longitude] 
    : defaultCenter;

  const customMarkerIcon = createCustomIcon();

  return (
    <div className="map-wrapper glass-panel">
      <MapContainer center={center} zoom={13} style={{ height: '100%', width: '100%', borderRadius: '1rem' }}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />
        
        {validServices.map(service => (
          <Marker 
            key={service.id} 
            position={[service.maker.latitude, service.maker.longitude]}
            icon={customMarkerIcon}
          >
            <Popup className="premium-popup">
              <div className="popup-content">
                <h3>{service.name}</h3>
                <span className="popup-tech">{service.technology} - {service.material}</span>
                <p>{service.description}</p>
                <div className="popup-contact">
                  <Phone size={14} />
                  <span>{service.maker?.phone || 'Não informado'}</span>
                </div>
                <div className="popup-maker">
                  <ShieldCheck size={14} color="var(--accent-color)" />
                  <span>Maker: <strong>{service.maker?.name}</strong></span>
                </div>
                <button className="btn btn-primary" style={{width: '100%', marginTop: '10px', padding: '0.4rem'}}>Ver Perfil Completo</button>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
};

export default ServiceMap;
