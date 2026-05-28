import { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X, Phone, MessageCircle, MapPin, Printer, ShieldCheck, Star, MessageSquare } from 'lucide-react';
import { useDetalhesServico } from '../hooks/useBusca';
import { formatarTelefone } from '../utils/mascaras';
import './ModalDetalhes.css';

const ModalDetalhes = ({ aberto, fechar, dados }) => {
  const { detalhes, loading, carregar } = useDetalhesServico(dados?.id);

  useEffect(() => {
    if (aberto) {
      carregar();
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => { document.body.style.overflow = 'unset'; };
  }, [aberto, carregar]);

  if (!aberto || !dados) return null;

  const info = detalhes || dados;

  const handleWhatsApp = () => {
    const telefone = info.maker?.telefone?.replace(/\D/g, '');
    window.open(`https://wa.me/${telefone}`, '_blank');
  };

  const modalContent = (
    <div className="modal-overlay" onClick={fechar}>
      <div className="modal-container glass-panel animate-scale-up" onClick={e => e.stopPropagation()}>
        <button className="modal-close" onClick={fechar}>
          <X size={24} />
        </button>

        <div className="modal-header">
          <div className="maker-info-large">
            <div className="maker-avatar-large">
              {info.maker?.nome?.charAt(0) || 'M'}
            </div>
            <div>
              <h2>{info.maker?.nome}</h2>
              <div className="maker-badges">
                <span className="badge-verified"><ShieldCheck size={14} /> Maker Verificado</span>
                {info.totalAvaliacoes > 0 ? (
                  <span className="badge-rating">
                    <Star size={14} fill="currentColor" />
                    {info.mediaAvaliacao?.toFixed(1)} ({info.totalAvaliacoes} avaliações)
                  </span>
                ) : (
                  <span className="badge-rating none">
                    <Star size={14} /> Sem avaliações
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="modal-body">
          {loading ? (
            <div className="loading-center"><div className="spinner"></div></div>
          ) : (
            <>
              <section className="modal-section">
                <h3><MapPin size={18} /> Localização e Contato</h3>
                <p className="description-text">
                  <strong>{info.maker?.nome}</strong> está em {info.maker?.cidade || 'São Paulo'}, {info.maker?.estado || 'SP'}.
                  {info.distanciaKm != null && (
                    <>
                      <br />
                      <span style={{ fontSize: '0.9rem', color: 'var(--accent-color)' }}>
                        Aproximadamente {info.distanciaKm.toFixed(1)}km de você.
                      </span>
                    </>
                  )}
                </p>
              </section>

              <section className="modal-section">
                <h3><MessageSquare size={18} /> Descrição do Serviço</h3>
                <p className="description-text">{info.descricao}</p>
              </section>

              <section className="modal-section">
                <h3><Printer size={18} /> Detalhes Técnicos e Área de Impressão</h3>
                <div className="tech-grid">
                  <div className="tech-item">
                    <span>Modelo da Impressora:</span>
                    <strong>{info.modelo || 'Não informado'}</strong>
                  </div>
                  <div className="tech-item">
                    <span>Tecnologia:</span>
                    <strong>
                      {info.tecnologias?.length > 0
                        ? info.tecnologias.join(', ')
                        : info.tecnologia || 'Não informado'}
                    </strong>
                  </div>
                  {info.tipoNome && (
                    <div className="tech-item">
                      <span>Tipo de Impressão:</span>
                      <strong title={info.tipoDescricao || ''}>{info.tipoNome}</strong>
                    </div>
                  )}
                  <div className="tech-item">
                    <span>Materiais Suportados:</span>
                    <strong>{info.material || 'Não informado'}</strong>
                  </div>
                  <div className="tech-item">
                    <span>Área Máxima (Volume):</span>
                    <strong>{info.volumeImpressao || 'Padrão'}</strong>
                  </div>
                </div>
              </section>

              <section className="modal-section">
                <h3><Star size={18} /> Faixa de Preço e Condições</h3>
                <div className="price-box">
                  <div className="price-main">
                    <span>A partir de:</span>
                    <strong>R$ {info.precoBase?.toFixed(2)}</strong>
                  </div>
                  <div className="price-conditions">
                    <span>Condições:</span>
                    <p>{info.condicoesServico || 'Orçamento final após envio do arquivo 3D (STL/OBJ).'}</p>
                  </div>
                </div>
              </section>

              {info.avaliacoes && info.avaliacoes.length > 0 && (
                <section className="modal-section">
                  <h3><MessageSquare size={18} /> Avaliações</h3>
                  <div className="evaluations-list">
                    {info.avaliacoes.map(aval => (
                      <div key={aval.id} className="evaluation-card">
                        <div className="evaluation-header">
                          <strong>{aval.clienteNome}</strong>
                          <div className="evaluation-stars">
                            {[...Array(5)].map((_, i) => (
                              <Star key={i} size={12}
                                fill={i < aval.nota ? 'var(--accent-color)' : 'none'}
                                color={i < aval.nota ? 'var(--accent-color)' : 'var(--text-secondary)'} />
                            ))}
                          </div>
                        </div>
                        <p>{aval.comentario}</p>
                      </div>
                    ))}
                  </div>
                </section>
              )}

              <div className="contact-actions">
                <button className="btn btn-whatsapp" onClick={handleWhatsApp}>
                  <MessageCircle size={20} />
                  Conversar no WhatsApp
                </button>
                <button className="btn btn-outline" onClick={() => window.location.href = `tel:${info.maker?.telefone}`}>
                  <Phone size={20} />
                  Ligar: {formatarTelefone(info.maker?.telefone)}
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
};

export default ModalDetalhes;
