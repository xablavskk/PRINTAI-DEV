import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import './ModalLogin.css';

export default function ModalLogin({ isOpen, onClose, onLoginSuccess }) {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro('');
    setLoading(true);

    try {
      const dados = await authService.login(email, senha);
      authService.salvarSessao(dados);
      onLoginSuccess(dados);
      onClose();
    } catch (err) {
      setErro(err.response?.data?.erro || err.message || 'Erro ao realizar login');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container auth-modal animate-scale-up" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>

        <div className="auth-header">
          <h2>Bem-vindo à PrintAI</h2>
          <p>Faça login como cliente para solicitar seu projeto 3D.</p>
        </div>

        {erro && <div className="auth-error">{erro}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="login-email">E-mail</label>
            <input
              id="login-email"
              type="email"
              placeholder="exemplo@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="login-senha">Senha</label>
            <input
              id="login-senha"
              type="password"
              placeholder="Sua senha secreta"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-auth-submit" disabled={loading}>
            {loading ? <div className="spinner-small"></div> : 'Entrar como Cliente'}
          </button>
        </form>

        <div className="auth-footer">
          <span>Não tem uma conta de cliente? </span>
          <button
            type="button"
            className="link-btn"
            onClick={() => { onClose(); navigate('/cliente/cadastro'); }}
          >
            Cadastre-se aqui
          </button>
        </div>
      </div>
    </div>
  );
}
