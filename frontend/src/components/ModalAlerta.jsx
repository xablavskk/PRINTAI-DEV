import React from 'react';
import { X, ShieldAlert, CheckCircle, XCircle, Info } from 'lucide-react';
import './ModalAlerta.css';

export default function ModalAlerta({ isOpen, onClose, titulo, mensagem, tipo = 'warning' }) {
  if (!isOpen) return null;

  const renderIcon = () => {
    switch (tipo) {
      case 'success':
        return <CheckCircle size={48} className="modal-alerta-icon success" />;
      case 'error':
        return <XCircle size={48} className="modal-alerta-icon error" />;
      case 'info':
        return <Info size={48} className="modal-alerta-icon info" />;
      case 'warning':
      default:
        return <ShieldAlert size={48} className="modal-alerta-icon warning" />;
    }
  };

  return (
    <div className="modal-alerta-overlay animate-fade-in-bg" onClick={onClose}>
      <div className="modal-alerta-container glass-panel animate-scale-up" onClick={(e) => e.stopPropagation()}>
        <button className="modal-alerta-close" onClick={onClose} aria-label="Fechar">
          <X size={18} />
        </button>
        <div className="modal-alerta-content">
          <div className="modal-alerta-icon-wrapper">
            {renderIcon()}
          </div>
          <h3 className="modal-alerta-titulo">{titulo}</h3>
          <p className="modal-alerta-mensagem">{mensagem}</p>
          <button className="btn btn-primary btn-alerta-confirm" onClick={onClose}>
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
}
