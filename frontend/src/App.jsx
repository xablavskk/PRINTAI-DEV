import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SearchServices from './pages/BuscaServicos';
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
              <Link to="/login" className="btn btn-primary" style={{ textDecoration: 'none' }}>
                Entrar
              </Link>
            </nav>
          </div>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<SearchServices />} />
            {/* Outras rotas podem ser adicionadas aqui no futuro */}
            <Route path="/busca" element={<SearchServices />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
