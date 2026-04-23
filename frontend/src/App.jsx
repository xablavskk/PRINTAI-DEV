import React from 'react';
import SearchServices from './pages/SearchServices';
import { Printer } from 'lucide-react';
import './App.css';

function App() {
  return (
    <div className="app-container">
      <header>
        <div className="container">
          <div className="logo">
            <Printer color="var(--accent-color)" size={28} />
            <span className="text-gradient">PrintAI</span>
          </div>
          <nav>
            <button className="btn btn-outline" style={{ marginRight: '1rem' }}>Seja um Maker</button>
            <button className="btn btn-primary">Entrar</button>
          </nav>
        </div>
      </header>

      <main>
        <SearchServices />
      </main>
    </div>
  );
}

export default App;
