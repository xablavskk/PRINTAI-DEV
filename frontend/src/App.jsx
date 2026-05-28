import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SearchServices from './pages/BuscaServicos';
import CadastroMaker from './pages/CadastroMaker';
import CadastroCliente from './pages/CadastroCliente';
import { Printer } from 'lucide-react';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app-container">
        <header>
          <div className="container">
            <Link to="/" className="logo" style={{ textDecoration: 'none' }}>
              <Printer color="var(--accent-color)" size={28} />
              <span className="text-gradient">PrintAI</span>
            </Link>
            <nav>
              <Link to="/maker/cadastro" className="btn btn-outline" style={{ marginRight: '1rem', textDecoration: 'none' }}>
                Seja um Maker
              </Link>
              <Link to="/cliente/cadastro" className="btn btn-primary" style={{ textDecoration: 'none' }}>
                Criar Conta
              </Link>
            </nav>
          </div>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<SearchServices />} />
            <Route path="/busca" element={<SearchServices />} />
            <Route path="/maker/cadastro" element={<CadastroMaker />} />
            <Route path="/cliente/cadastro" element={<CadastroCliente />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
