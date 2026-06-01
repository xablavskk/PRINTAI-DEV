import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SearchServices from './pages/BuscaServicos';
import CadastroMaker from './pages/CadastroMaker';
import CadastroCliente from './pages/CadastroCliente';
import DashboardMaker from './pages/DashboardMaker';
import ModalLogin from './components/ModalLogin';
import ModalMeusPedidos from './components/ModalMeusPedidos';
import { Printer, LogOut, User, ClipboardList, LayoutDashboard } from 'lucide-react';
import './App.css';

function App() {
  const [cliente, setCliente] = useState(null);
  const [loginAberto, setLoginAberto] = useState(false);
  const [pedidosAberto, setPedidosAberto] = useState(false);

  useEffect(() => {
    const sessao = localStorage.getItem('printai_cliente');
    if (sessao) {
      setCliente(JSON.parse(sessao));
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('printai_cliente');
    setCliente(null);
    window.location.reload();
  };

  return (
    <Router>
      <div className="app-container">
        <header>
          <div className="container">
            <Link to="/" className="logo" style={{ textDecoration: 'none' }}>
              <Printer color="var(--accent-color)" size={28} />
              <span className="text-gradient">PrintAI</span>
            </Link>
            <nav style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              {cliente ? (
                <>
                  <button
                    onClick={() => setPedidosAberto(true)}
                    className="btn btn-outline"
                    style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
                  >
                    <ClipboardList size={16} />
                    Meus Pedidos
                  </button>
                  {cliente.perfil === 'MAKER' ? (
                    <Link to="/maker/dashboard" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '6px', textDecoration: 'none' }}>
                      <LayoutDashboard size={16} />
                      Meu Painel
                    </Link>
                  ) : (
                    <Link to="/maker/cadastro" className="btn btn-outline" style={{ textDecoration: 'none' }}>
                      Seja um Maker
                    </Link>
                  )}
                  <div className="user-menu" style={{ display: 'flex', alignItems: 'center', gap: '0.8rem', paddingLeft: '0.5rem', borderLeft: '1px solid var(--glass-border)' }}>
                    <div className="user-badge" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-primary)', fontSize: '0.9rem' }}>
                      <User size={16} color="var(--accent-color)" />
                      <span>Olá, {cliente.nome.split(' ')[0]}</span>
                    </div>
                    <button onClick={handleLogout} className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '0.4rem 0.8rem' }}>
                      <LogOut size={16} />
                      Sair
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <button onClick={() => setLoginAberto(true)} className="btn btn-link" style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', fontWeight: '600', padding: '0.5rem 1rem', transition: 'color 0.2s' }}>
                    Entrar
                  </button>
                  <Link to="/cliente/cadastro" className="btn btn-outline" style={{ textDecoration: 'none' }}>
                    Cadastre-se
                  </Link>
                  <Link to="/maker/cadastro" className="btn btn-primary" style={{ textDecoration: 'none' }}>
                    Seja um Maker
                  </Link>
                </>
              )}
            </nav>
          </div>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<SearchServices cliente={cliente} setLoginAberto={setLoginAberto} />} />
            <Route path="/busca" element={<SearchServices cliente={cliente} setLoginAberto={setLoginAberto} />} />
            <Route path="/maker/cadastro" element={<CadastroMaker />} />
            <Route path="/maker/dashboard" element={<DashboardMaker />} />
            <Route path="/cliente/cadastro" element={<CadastroCliente />} />
          </Routes>
        </main>
      </div>

      <ModalLogin
        isOpen={loginAberto}
        onClose={() => setLoginAberto(false)}
        onLoginSuccess={(dados) => {
          setCliente(dados);
          window.location.reload();
        }}
      />

      <ModalMeusPedidos
        isOpen={pedidosAberto}
        onClose={() => setPedidosAberto(false)}
        cliente={cliente}
      />
    </Router>
  );
}

export default App;

