import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, LogOut, CheckCircle, XCircle, Clock, Users, Loader } from 'lucide-react';
import { adminService } from '../services/adminService';
import { authService } from '../services/authService';
import { useAdmin } from '../hooks/useAdmin';
import ModalAlerta from '../components/ModalAlerta';
import './AdminPanel.css';

// ─── Sub-componente: login ────────────────────────────────────────────────────
function AdminLogin({ onLogin }) {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setLoading(true);
    try {
      const dados = await adminService.login(email, senha);
      adminService.salvarSessao(dados);
      onLogin(dados);
    } catch (err) {
      setErro(err.response?.data?.erro || 'Credenciais inválidas.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-login-wrapper">
      <div className="admin-login-card glass-panel">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.5rem' }}>
          <ShieldCheck size={28} color="var(--accent-color)" />
          <h2>Painel Administrativo</h2>
        </div>
        <p>Acesso exclusivo para administradores PrintAI.</p>

        {erro && <div className="admin-login-erro">{erro}</div>}

        <form className="admin-login-form" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="adm-email">E-mail</label>
            <input
              id="adm-email"
              type="email"
              placeholder="admin@printai.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="adm-senha">Senha</label>
            <input
              id="adm-senha"
              type="password"
              placeholder="Sua senha"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}
            style={{ marginTop: '0.5rem', height: '44px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
            {loading ? <Loader size={16} className="spin" /> : <ShieldCheck size={16} />}
            {loading ? 'Entrando...' : 'Entrar como Administrador'}
          </button>
        </form>
      </div>
    </div>
  );
}

// ─── Sub-componente: card de maker ───────────────────────────────────────────
function MakerCard({ maker, adminId, onProcessar }) {
  const [mostrarRejeicao, setMostrarRejeicao] = useState(false);
  const [motivo, setMotivo] = useState('');
  const [processando, setProcessando] = useState(false);
  const [feedback, setFeedback] = useState('');

  const statusLabel = () => {
    if (maker.statusAprovacao === null || maker.statusAprovacao === undefined)
      return <span className="status-badge pendente"><Clock size={12} /> Pendente</span>;
    if (maker.statusAprovacao === true)
      return <span className="status-badge aprovado"><CheckCircle size={12} /> Aprovado</span>;
    return <span className="status-badge rejeitado"><XCircle size={12} /> Rejeitado</span>;
  };

  const isPendente = maker.statusAprovacao === null || maker.statusAprovacao === undefined;

  const handleAprovar = async () => {
    setProcessando(true);
    setFeedback('');
    try {
      await onProcessar(adminId, maker.id, true, '');
      setFeedback('Maker aprovado com sucesso.');
    } catch (err) {
      setFeedback(err.message);
    } finally {
      setProcessando(false);
    }
  };

  const handleRejeitar = async () => {
    if (!motivo.trim()) return;
    setProcessando(true);
    setFeedback('');
    try {
      await onProcessar(adminId, maker.id, false, motivo);
      setFeedback('Solicitação rejeitada.');
      setMostrarRejeicao(false);
    } catch (err) {
      setFeedback(err.message);
    } finally {
      setProcessando(false);
    }
  };

  return (
    <div className="admin-card glass-panel">
      <div className="admin-card-header">
        <div className="admin-card-info">
          <h3>{maker.nome}</h3>
          <p>{maker.email} · {maker.telefone}</p>
          <p>CPF/CNPJ: {maker.documentoCpfCnpj}</p>
          <p>{maker.cidade} — {maker.estado}
            {maker.latitude && maker.longitude
              ? ` · ${maker.latitude.toFixed(4)}, ${maker.longitude.toFixed(4)}`
              : ' · Sem coordenadas'}
          </p>
        </div>
        {statusLabel()}
      </div>

      {maker.servicos && maker.servicos.length > 0 && (
        <div>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
            {maker.totalServicos} serviço(s) cadastrado(s):
          </p>
          <div className="admin-servicos">
            {maker.servicos.map((s) => (
              <span key={s.id} className="servico-tag">
                {s.nome} {s.tipoNome ? `· ${s.tipoNome}` : ''} {s.material ? `· ${s.material}` : ''}
              </span>
            ))}
          </div>
        </div>
      )}

      {feedback && (
        <p style={{ fontSize: '0.85rem', color: feedback.includes('sucesso') || feedback.includes('rejeit') ? '#10b981' : '#f87171', marginTop: '0.5rem' }}>
          {feedback}
        </p>
      )}

      {isPendente && (
        <div className="admin-acoes">
          <button className="btn-aprovar" onClick={handleAprovar} disabled={processando}>
            {processando ? <Loader size={14} /> : <CheckCircle size={14} />}
            Aprovar
          </button>
          <button className="btn-rejeitar" onClick={() => setMostrarRejeicao(v => !v)} disabled={processando}>
            <XCircle size={14} />
            Rejeitar
          </button>
        </div>
      )}

      {isPendente && mostrarRejeicao && (
        <div className="rejeicao-form">
          <textarea
            placeholder="Informe o motivo da rejeição (obrigatório)..."
            value={motivo}
            onChange={(e) => setMotivo(e.target.value)}
          />
          <div className="rejeicao-acoes">
            <button className="btn-rejeitar" onClick={handleRejeitar} disabled={!motivo.trim() || processando}>
              {processando ? <Loader size={14} /> : <XCircle size={14} />}
              Confirmar Rejeição
            </button>
            <button className="btn btn-outline" onClick={() => setMostrarRejeicao(false)}
              style={{ padding: '0.4rem 0.8rem', fontSize: '0.875rem' }}>
              Cancelar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

// ─── Painel principal ─────────────────────────────────────────────────────────
export default function AdminPanel() {
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(() => adminService.obterSessao());
  const { makers, loading, erro, filtro, setFiltro, carregar, processar } = useAdmin();
  const [alerta, setAlerta] = useState({
    isOpen: false,
    titulo: '',
    mensagem: '',
    tipo: 'warning',
    onConfirm: null
  });

  useEffect(() => {
    const clienteSessao = authService.obterSessao();
    if (clienteSessao) {
      setAlerta({
        isOpen: true,
        titulo: 'Acesso Negado',
        mensagem: 'Usuários logados não possuem permissão de administrador.',
        tipo: 'warning',
        onConfirm: () => {
          if (clienteSessao.perfil === 'MAKER') {
            navigate('/maker/dashboard');
          } else {
            navigate('/');
          }
        }
      });
    }
  }, [navigate]);

  useEffect(() => {
    if (admin) carregar(admin.id);
  }, [admin, filtro, carregar]);

  const handleLogout = () => {
    adminService.limparSessao();
    setAdmin(null);
  };

  if (!admin) {
    return (
      <>
        <AdminLogin onLogin={setAdmin} />
        <ModalAlerta
          isOpen={alerta.isOpen}
          onClose={() => {
            setAlerta(prev => ({ ...prev, isOpen: false }));
            if (alerta.onConfirm) alerta.onConfirm();
          }}
          titulo={alerta.titulo}
          mensagem={alerta.mensagem}
          tipo={alerta.tipo}
        />
      </>
    );
  }

  const pendentes = makers.filter(m => m.statusAprovacao === null || m.statusAprovacao === undefined).length;

  return (
    <div className="admin-container">
      <div className="admin-header">
        <div>
          <h1>Painel Administrativo</h1>
          <p className="admin-meta">
            Olá, {admin.nome} · {pendentes} solicitação(ões) pendente(s)
          </p>
        </div>
        <button className="btn btn-outline" onClick={handleLogout}
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <LogOut size={16} />
          Sair
        </button>
      </div>

      <div className="admin-filtros">
        <button className={filtro === 'pendentes' ? 'ativo' : ''} onClick={() => setFiltro('pendentes')}>
          <Clock size={14} style={{ marginRight: 4 }} />
          Pendentes
        </button>
        <button className={filtro === 'todos' ? 'ativo' : ''} onClick={() => setFiltro('todos')}>
          <Users size={14} style={{ marginRight: 4 }} />
          Todos os Makers
        </button>
      </div>

      {erro && <div className="admin-erro">{erro}</div>}

      {loading ? (
        <div className="admin-vazio">
          <Loader size={32} style={{ animation: 'spin 1s linear infinite' }} />
          <p>Carregando...</p>
        </div>
      ) : makers.length === 0 ? (
        <div className="admin-vazio">
          <ShieldCheck size={48} style={{ opacity: 0.3, marginBottom: '1rem' }} />
          <p>{filtro === 'pendentes' ? 'Nenhuma solicitação pendente.' : 'Nenhum maker cadastrado.'}</p>
        </div>
      ) : (
        <div className="admin-lista">
          {makers.map((maker) => (
            <MakerCard
              key={maker.id}
              maker={maker}
              adminId={admin.id}
              onProcessar={processar}
            />
          ))}
        </div>
      )}
      <ModalAlerta
        isOpen={alerta.isOpen}
        onClose={() => {
          setAlerta(prev => ({ ...prev, isOpen: false }));
          if (alerta.onConfirm) alerta.onConfirm();
        }}
        titulo={alerta.titulo}
        mensagem={alerta.mensagem}
        tipo={alerta.tipo}
      />
    </div>
  );
}
