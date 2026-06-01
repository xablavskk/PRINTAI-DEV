import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { makerService } from '../services/makerService';
import {
  Package, Wrench, Star, ChevronDown, ChevronUp,
  Plus, Pencil, Trash2, CheckCircle, XCircle, PlayCircle, Flag
} from 'lucide-react';
import './DashboardMaker.css';

const STATUS_LABEL = {
  AGUARDANDO_ANALISE: 'Aguardando Análise',
  APROVADO: 'Aprovado',
  EM_PRODUCAO: 'Em Produção',
  FINALIZADO: 'Finalizado',
  CANCELADO: 'Cancelado',
};

const STATUS_CLASS = {
  AGUARDANDO_ANALISE: 'badge-warning',
  APROVADO: 'badge-success',
  EM_PRODUCAO: 'badge-info',
  FINALIZADO: 'badge-neutral',
  CANCELADO: 'badge-danger',
};

const FILTROS_STATUS = ['TODOS', 'AGUARDANDO_ANALISE', 'APROVADO', 'EM_PRODUCAO', 'FINALIZADO', 'CANCELADO'];

const FORM_SERVICO_VAZIO = {
  nome: '', precoBase: '', descricao: '', condicoesServico: '',
  tipoId: '', tecnologia: '', material: '',
  suportaPecasPequenas: false, suportaDecorativos: false, suportaPrototipos: false,
};

export default function DashboardMaker() {
  const navigate = useNavigate();
  const [maker, setMaker] = useState(null);

  const [pedidos, setPedidos] = useState([]);
  const [servicos, setServicos] = useState([]);
  const [avaliacoes, setAvaliacoes] = useState([]);
  const [tipos, setTipos] = useState([]);
  const [materiais, setMateriais] = useState([]);

  const [abaAtiva, setAbaAtiva] = useState('pedidos');
  const [filtroStatus, setFiltroStatus] = useState('TODOS');
  const [pedidoExpandido, setPedidoExpandido] = useState(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');

  const [formServico, setFormServico] = useState(FORM_SERVICO_VAZIO);
  const [servicoEditandoId, setServicoEditandoId] = useState(null);
  const [formServicoAberto, setFormServicoAberto] = useState(false);
  const [salvandoServico, setSalvandoServico] = useState(false);

  useEffect(() => {
    const sessao = authService.obterSessao();
    if (!sessao || sessao.perfil !== 'MAKER') {
      navigate('/');
      return;
    }
    setMaker(sessao);
  }, [navigate]);

  const carregarDados = useCallback(async (makerId) => {
    setLoading(true);
    setErro('');
    try {
      const [p, s, a, t, m] = await Promise.all([
        makerService.listarPedidos(makerId),
        makerService.listarServicos(makerId),
        makerService.listarAvaliacoes(makerId),
        makerService.listarTipos(),
        makerService.listarMateriais(),
      ]);
      setPedidos(p);
      setServicos(s);
      setAvaliacoes(a);
      setTipos(t);
      setMateriais(m);
    } catch {
      setErro('Erro ao carregar dados. Verifique se o servidor está rodando.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (maker) carregarDados(maker.id);
  }, [maker, carregarDados]);

  // ── Pedidos ──────────────────────────────────────────────────────────────

  const pedidosFiltrados = filtroStatus === 'TODOS'
    ? pedidos
    : pedidos.filter(p => p.status === filtroStatus);

  const atualizarStatus = async (pedidoId, novoStatus) => {
    try {
      const atualizado = await makerService.atualizarStatusPedido(maker.id, pedidoId, novoStatus);
      setPedidos(prev => prev.map(p => p.id === pedidoId ? atualizado : p));
    } catch (e) {
      alert(e.response?.data?.erro || 'Erro ao atualizar status');
    }
  };

  // ── Serviços ─────────────────────────────────────────────────────────────

  const abrirFormServico = (servico = null) => {
    if (servico) {
      setFormServico({
        nome: servico.nome || '',
        precoBase: servico.precoBase || '',
        descricao: servico.descricao || '',
        condicoesServico: servico.condicoesServico || '',
        tipoId: servico.tipoId || '',
        tecnologia: servico.tecnologia || '',
        material: servico.material || '',
        suportaPecasPequenas: servico.suportaPecasPequenas || false,
        suportaDecorativos: servico.suportaDecorativos || false,
        suportaPrototipos: servico.suportaPrototipos || false,
      });
      setServicoEditandoId(servico.id);
    } else {
      setFormServico(FORM_SERVICO_VAZIO);
      setServicoEditandoId(null);
    }
    setFormServicoAberto(true);
  };

  const salvarServico = async (e) => {
    e.preventDefault();
    setSalvandoServico(true);
    const payload = {
      ...formServico,
      precoBase: parseFloat(formServico.precoBase),
      tipoId: formServico.tipoId ? Number(formServico.tipoId) : null,
    };
    try {
      if (servicoEditandoId) {
        const atualizado = await makerService.editarServico(maker.id, servicoEditandoId, payload);
        setServicos(prev => prev.map(s => s.id === servicoEditandoId ? atualizado : s));
      } else {
        const novo = await makerService.criarServico(maker.id, payload);
        setServicos(prev => [...prev, novo]);
      }
      setFormServicoAberto(false);
    } catch (e) {
      alert(e.response?.data?.erro || 'Erro ao salvar serviço');
    } finally {
      setSalvandoServico(false);
    }
  };

  const removerServico = async (servicoId) => {
    if (!confirm('Remover este serviço?')) return;
    try {
      await makerService.removerServico(maker.id, servicoId);
      setServicos(prev => prev.filter(s => s.id !== servicoId));
    } catch (e) {
      alert(e.response?.data?.erro || 'Erro ao remover serviço');
    }
  };

  // ── Render ────────────────────────────────────────────────────────────────

  if (!maker) return null;

  return (
    <div className="dashboard-maker">
      <div className="dashboard-header">
        <div>
          <h1>Dashboard do Maker</h1>
          <p className="dashboard-subtitle">Gerencie seus pedidos e serviços</p>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button className={`tab-btn ${abaAtiva === 'pedidos' ? 'active' : ''}`} onClick={() => setAbaAtiva('pedidos')}>
          <Package size={16} /> Pedidos
          {pedidos.filter(p => p.status === 'AGUARDANDO_ANALISE').length > 0 && (
            <span className="tab-badge">{pedidos.filter(p => p.status === 'AGUARDANDO_ANALISE').length}</span>
          )}
        </button>
        <button className={`tab-btn ${abaAtiva === 'servicos' ? 'active' : ''}`} onClick={() => setAbaAtiva('servicos')}>
          <Wrench size={16} /> Serviços <span className="tab-count">({servicos.length})</span>
        </button>
        <button className={`tab-btn ${abaAtiva === 'avaliacoes' ? 'active' : ''}`} onClick={() => setAbaAtiva('avaliacoes')}>
          <Star size={16} /> Avaliações <span className="tab-count">({avaliacoes.length})</span>
        </button>
      </div>

      <div className="dashboard-content">
        {loading && <div className="dashboard-loading">Carregando...</div>}
        {erro && <div className="dashboard-erro">{erro}</div>}

        {!loading && !erro && (
          <>
            {/* ── ABA PEDIDOS ─────────────────────────────────────────────── */}
            {abaAtiva === 'pedidos' && (
              <div>
                <div className="filtros-status">
                  {FILTROS_STATUS.map(f => (
                    <button
                      key={f}
                      className={`filtro-btn ${filtroStatus === f ? 'active' : ''}`}
                      onClick={() => setFiltroStatus(f)}
                    >
                      {f === 'TODOS' ? 'Todos' : STATUS_LABEL[f]}
                    </button>
                  ))}
                </div>

                {pedidosFiltrados.length === 0 && (
                  <div className="empty-state">Nenhum pedido encontrado.</div>
                )}

                {pedidosFiltrados.map(pedido => (
                  <div key={pedido.id} className="pedido-card">
                    <div className="pedido-header" onClick={() => setPedidoExpandido(pedidoExpandido === pedido.id ? null : pedido.id)}>
                      <div className="pedido-resumo">
                        <span className="pedido-id">Pedido #{pedido.id}</span>
                        <span className="pedido-servico">{pedido.servicoNome}</span>
                        <span className="pedido-cliente">{pedido.clienteNome}</span>
                        <span className={`status-badge ${STATUS_CLASS[pedido.status]}`}>
                          {STATUS_LABEL[pedido.status] || pedido.status}
                        </span>
                        <span className="pedido-tipo">{pedido.tipoPedido === 'COM_MODELO' ? 'Com Arquivo 3D' : 'Descrição de Projeto'}</span>
                      </div>
                      <button className="expand-btn">
                        {pedidoExpandido === pedido.id ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                      </button>
                    </div>

                    {pedidoExpandido === pedido.id && (
                      <div className="pedido-detalhes">
                        <div className="detalhe-grid">
                          <div><strong>Cliente</strong><p>{pedido.clienteNome}</p></div>
                          <div><strong>E-mail</strong><p>{pedido.clienteEmail}</p></div>
                          <div><strong>Telefone</strong><p>{pedido.clienteTelefone || '—'}</p></div>
                          <div><strong>Material</strong><p>{pedido.material || '—'}</p></div>
                          <div><strong>Quantidade</strong><p>{pedido.quantidade}</p></div>
                        </div>

                        {pedido.arquivo3D && (
                          <div className="detalhe-arquivo">
                            <strong>Arquivo 3D:</strong> <span>{pedido.arquivo3D}</span>
                          </div>
                        )}
                        {pedido.descricaoNecessidade && (
                          <div className="detalhe-descricao">
                            <strong>Descrição da necessidade:</strong>
                            <p>{pedido.descricaoNecessidade}</p>
                          </div>
                        )}
                        {pedido.observacoes && (
                          <div className="detalhe-descricao">
                            <strong>Observações:</strong>
                            <p>{pedido.observacoes}</p>
                          </div>
                        )}

                        <div className="pedido-acoes">
                          {pedido.status === 'AGUARDANDO_ANALISE' && (
                            <>
                              <button className="btn-acao btn-aprovar" onClick={() => atualizarStatus(pedido.id, 'APROVADO')}>
                                <CheckCircle size={14} /> Aprovar
                              </button>
                              <button className="btn-acao btn-cancelar" onClick={() => atualizarStatus(pedido.id, 'CANCELADO')}>
                                <XCircle size={14} /> Rejeitar
                              </button>
                            </>
                          )}
                          {pedido.status === 'APROVADO' && (
                            <>
                              <button className="btn-acao btn-producao" onClick={() => atualizarStatus(pedido.id, 'EM_PRODUCAO')}>
                                <PlayCircle size={14} /> Iniciar Produção
                              </button>
                              <button className="btn-acao btn-cancelar" onClick={() => atualizarStatus(pedido.id, 'CANCELADO')}>
                                <XCircle size={14} /> Cancelar
                              </button>
                            </>
                          )}
                          {pedido.status === 'EM_PRODUCAO' && (
                            <button className="btn-acao btn-finalizar" onClick={() => atualizarStatus(pedido.id, 'FINALIZADO')}>
                              <Flag size={14} /> Finalizar
                            </button>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* ── ABA SERVIÇOS ─────────────────────────────────────────────── */}
            {abaAtiva === 'servicos' && (
              <div>
                <div className="aba-actions">
                  <button className="btn btn-primary" onClick={() => abrirFormServico()}>
                    <Plus size={16} /> Novo Serviço
                  </button>
                </div>

                {formServicoAberto && (
                  <form className="form-card" onSubmit={salvarServico}>
                    <h3>{servicoEditandoId ? 'Editar Serviço' : 'Novo Serviço'}</h3>
                    <div className="form-grid">
                      <div className="form-group">
                        <label>Nome *</label>
                        <input required value={formServico.nome} onChange={e => setFormServico(f => ({ ...f, nome: e.target.value }))} />
                      </div>
                      <div className="form-group">
                        <label>Preço Base (R$) *</label>
                        <input required type="number" step="0.01" min="0.01" value={formServico.precoBase} onChange={e => setFormServico(f => ({ ...f, precoBase: e.target.value }))} />
                      </div>
                      <div className="form-group">
                        <label>Tipo de Impressão</label>
                        <select value={formServico.tipoId} onChange={e => setFormServico(f => ({ ...f, tipoId: e.target.value }))}>
                          <option value="">Selecione...</option>
                          {tipos.map(t => <option key={t.id} value={t.id}>{t.nome}</option>)}
                        </select>
                      </div>
                      <div className="form-group">
                        <label>Tecnologia</label>
                        <input value={formServico.tecnologia} onChange={e => setFormServico(f => ({ ...f, tecnologia: e.target.value }))} placeholder="Ex: FDM, SLA" />
                      </div>
                      <div className="form-group">
                        <label>Material</label>
                        <select value={formServico.material} onChange={e => setFormServico(f => ({ ...f, material: e.target.value }))}>
                          <option value="">Selecione...</option>
                          {materiais.map(m => <option key={m.nome} value={m.nome}>{m.nome}</option>)}
                        </select>
                      </div>
                    </div>
                    <div className="form-group">
                      <label>Descrição</label>
                      <textarea rows={3} value={formServico.descricao} onChange={e => setFormServico(f => ({ ...f, descricao: e.target.value }))} />
                    </div>
                    <div className="form-group">
                      <label>Condições do Serviço</label>
                      <textarea rows={2} value={formServico.condicoesServico} onChange={e => setFormServico(f => ({ ...f, condicoesServico: e.target.value }))} />
                    </div>
                    <div className="form-checks">
                      <label><input type="checkbox" checked={formServico.suportaPecasPequenas} onChange={e => setFormServico(f => ({ ...f, suportaPecasPequenas: e.target.checked }))} /> Peças Pequenas</label>
                      <label><input type="checkbox" checked={formServico.suportaDecorativos} onChange={e => setFormServico(f => ({ ...f, suportaDecorativos: e.target.checked }))} /> Decorativos</label>
                      <label><input type="checkbox" checked={formServico.suportaPrototipos} onChange={e => setFormServico(f => ({ ...f, suportaPrototipos: e.target.checked }))} /> Protótipos</label>
                    </div>
                    <div className="form-actions">
                      <button type="submit" className="btn btn-primary" disabled={salvandoServico}>{salvandoServico ? 'Salvando...' : 'Salvar'}</button>
                      <button type="button" className="btn btn-outline" onClick={() => setFormServicoAberto(false)}>Cancelar</button>
                    </div>
                  </form>
                )}

                {servicos.length === 0 && !formServicoAberto && (
                  <div className="empty-state">Nenhum serviço cadastrado ainda.</div>
                )}

                <div className="cards-grid">
                  {servicos.map(s => (
                    <div key={s.id} className="item-card">
                      <div className="item-card-header">
                        <h4>{s.nome}</h4>
                        <div className="item-card-actions">
                          <button className="icon-btn" onClick={() => abrirFormServico(s)}><Pencil size={15} /></button>
                          <button className="icon-btn danger" onClick={() => removerServico(s.id)}><Trash2 size={15} /></button>
                        </div>
                      </div>
                      <p className="item-preco">R$ {s.precoBase?.toFixed(2)}</p>
                      {s.tipoNome && <span className="tag">{s.tipoNome}</span>}
                      {s.tecnologia && <span className="tag">{s.tecnologia}</span>}
                      {s.material && <span className="tag">{s.material}</span>}
                      <div className="item-flags">
                        {s.suportaPecasPequenas && <span className="flag">Peças pequenas</span>}
                        {s.suportaDecorativos && <span className="flag">Decorativos</span>}
                        {s.suportaPrototipos && <span className="flag">Protótipos</span>}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
            {/* ── ABA AVALIAÇÕES ──────────────────────────────────────────── */}
            {abaAtiva === 'avaliacoes' && (
              <div>
                {avaliacoes.length === 0 ? (
                  <div className="empty-state">
                    <Star size={40} style={{ opacity: 0.3, marginBottom: '0.5rem' }} />
                    <p>Nenhuma avaliação recebida ainda.</p>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                      As avaliações aparecem aqui quando clientes finalizarem pedidos com você.
                    </span>
                  </div>
                ) : (
                  <>
                    <div className="avaliacoes-resumo">
                      <div className="resumo-nota">
                        <span className="nota-numero">
                          {(avaliacoes.reduce((sum, a) => sum + a.nota, 0) / avaliacoes.length).toFixed(1)}
                        </span>
                        <div className="resumo-stars">
                          {[1, 2, 3, 4, 5].map(n => {
                            const media = avaliacoes.reduce((sum, a) => sum + a.nota, 0) / avaliacoes.length;
                            return (
                              <Star key={n} size={18}
                                fill={n <= Math.round(media) ? '#f59e0b' : 'none'}
                                color={n <= Math.round(media) ? '#f59e0b' : 'var(--text-secondary)'}
                                strokeWidth={1.5}
                              />
                            );
                          })}
                        </div>
                        <span className="resumo-total">{avaliacoes.length} avaliação{avaliacoes.length !== 1 ? 'ões' : ''}</span>
                      </div>
                    </div>

                    <div className="avaliacoes-lista">
                      {avaliacoes.map(a => (
                        <div key={a.id} className="avaliacao-card">
                          <div className="avaliacao-topo">
                            <strong>{a.clienteNome}</strong>
                            <div className="avaliacao-stars">
                              {[1, 2, 3, 4, 5].map(n => (
                                <Star key={n} size={14}
                                  fill={n <= a.nota ? '#f59e0b' : 'none'}
                                  color={n <= a.nota ? '#f59e0b' : 'var(--text-secondary)'}
                                  strokeWidth={1.5}
                                />
                              ))}
                            </div>
                            {a.dataAvaliacao && (
                              <span className="avaliacao-data">
                                {new Date(a.dataAvaliacao).toLocaleDateString('pt-BR')}
                              </span>
                            )}
                          </div>
                          <p className="avaliacao-comentario">{a.comentario}</p>
                        </div>
                      ))}
                    </div>
                  </>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
