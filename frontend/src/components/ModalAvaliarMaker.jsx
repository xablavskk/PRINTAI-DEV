import React, { useState } from 'react';
import { Star, X } from 'lucide-react';
import { avaliacaoService } from '../services/avaliacaoService';
import './ModalAvaliarMaker.css';

export default function ModalAvaliarMaker({ isOpen, onClose, pedido, cliente, onSucesso }) {
  const [nota, setNota] = useState(0);
  const [hover, setHover] = useState(0);
  const [comentario, setComentario] = useState('');
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState('');

  if (!isOpen || !pedido) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (nota === 0) {
      setErro('Selecione uma nota de 1 a 5 estrelas.');
      return;
    }
    setErro('');
    setLoading(true);
    try {
      await avaliacaoService.avaliarMaker(cliente.id, {
        makerId: pedido.makerId,
        nota,
        comentario
      });
      onSucesso(pedido.makerId);
      onClose();
    } catch (err) {
      setErro(err.response?.data?.erro || 'Erro ao enviar avaliação.');
    } finally {
      setLoading(false);
    }
  };

  const labelNota = ['', 'Ruim', 'Regular', 'Bom', 'Muito bom', 'Excelente'];

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container aval-modal animate-scale-up" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>
          <X size={24} />
        </button>

        <div className="aval-header">
          <h2>Avaliar Maker</h2>
          <p>Como foi sua experiência com <strong>{pedido.makerNome}</strong>?</p>
          <span className="aval-pedido-ref">Pedido #{pedido.id} — {pedido.servicoNome}</span>
        </div>

        {erro && <div className="aval-erro">{erro}</div>}

        <form onSubmit={handleSubmit} className="aval-form">
          <div className="stars-group">
            <label>Nota</label>
            <div className="stars-row">
              {[1, 2, 3, 4, 5].map((n) => (
                <button
                  key={n}
                  type="button"
                  className="star-btn"
                  onClick={() => setNota(n)}
                  onMouseEnter={() => setHover(n)}
                  onMouseLeave={() => setHover(0)}
                  aria-label={`${n} estrela${n > 1 ? 's' : ''}`}
                >
                  <Star
                    size={36}
                    fill={(hover || nota) >= n ? 'var(--accent-color)' : 'none'}
                    color={(hover || nota) >= n ? 'var(--accent-color)' : 'var(--text-secondary)'}
                    strokeWidth={1.5}
                  />
                </button>
              ))}
            </div>
            {(hover || nota) > 0 && (
              <span className="nota-label">{labelNota[hover || nota]}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="aval-comentario">Comentário *</label>
            <textarea
              id="aval-comentario"
              rows="4"
              placeholder="Conte como foi o atendimento, qualidade da impressão, prazo de entrega..."
              value={comentario}
              onChange={(e) => setComentario(e.target.value)}
              maxLength={500}
              required
            />
            <span className="char-count">{comentario.length}/500</span>
          </div>

          <div className="aval-actions">
            <button type="button" className="btn-cancelar" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn-enviar" disabled={loading}>
              {loading ? <div className="spinner-small"></div> : 'Enviar Avaliação'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
