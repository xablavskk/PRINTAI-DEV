import React from 'react';
import { Link } from 'react-router-dom';
import { User, CheckCircle, AlertCircle, Loader } from 'lucide-react';
import { useCadastroCliente } from '../hooks/useCadastroCliente';
import './CadastroMaker.css'; // reutiliza os mesmos estilos

const UFS = ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG',
             'PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'];

const CadastroCliente = () => {
  const { form, loading, erros, sucesso, erroGeral, handleChange, handleSubmit } = useCadastroCliente();

  if (sucesso) {
    return (
      <div className="cadastro-container">
        <div className="sucesso-card glass-panel">
          <CheckCircle size={56} color="var(--accent-color)" />
          <h2>Cadastro realizado!</h2>
          <p>{sucesso.mensagem}</p>
          <div className="sucesso-info">
            <span><strong>Nome:</strong> {sucesso.nome}</span>
            <span><strong>E-mail:</strong> {sucesso.email}</span>
            {sucesso.cidade && (
              <span><strong>Localização:</strong> {sucesso.cidade} - {sucesso.estado}</span>
            )}
          </div>
          <Link to="/" className="btn btn-primary">Buscar Makers</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="cadastro-container">
      <div className="cadastro-header">
        <User size={36} color="var(--accent-color)" />
        <h1 className="text-gradient">Criar Conta</h1>
        <p>Cadastre-se para solicitar serviços de impressão 3D.</p>
      </div>

      {erroGeral && (
        <div className="alerta-erro glass-panel">
          <AlertCircle size={18} />
          <span>{erroGeral}</span>
        </div>
      )}

      <form className="cadastro-form glass-panel" onSubmit={handleSubmit} noValidate>

        {/* ── Dados Pessoais ── */}
        <section className="form-section">
          <h3>Dados Pessoais</h3>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="nome">Nome completo *</label>
              <input id="nome" name="nome" type="text" value={form.nome}
                onChange={handleChange} placeholder="Seu nome completo"
                className={erros.nome ? 'input-erro' : ''} />
              {erros.nome && <span className="erro-msg">{erros.nome}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="email">E-mail *</label>
              <input id="email" name="email" type="email" value={form.email}
                onChange={handleChange} placeholder="seu@email.com"
                className={erros.email ? 'input-erro' : ''} />
              {erros.email && <span className="erro-msg">{erros.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="senha">Senha *</label>
              <input id="senha" name="senha" type="password" value={form.senha}
                onChange={handleChange} placeholder="Mínimo 6 caracteres"
                className={erros.senha ? 'input-erro' : ''} />
              {erros.senha && <span className="erro-msg">{erros.senha}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="telefone">Telefone / WhatsApp *</label>
              <input id="telefone" name="telefone" type="tel" value={form.telefone}
                onChange={handleChange} placeholder="(11) 99999-0000"
                className={erros.telefone ? 'input-erro' : ''} />
              {erros.telefone && <span className="erro-msg">{erros.telefone}</span>}
            </div>
          </div>
        </section>

        {/* ── Localização (opcional) ── */}
        <section className="form-section">
          <h3>Localização</h3>
          <p className="section-hint">
            📍 Opcional — informe sua cidade para ver a distância até os makers.
          </p>
          <div className="form-grid">
            <div className="form-group form-group--wide">
              <label htmlFor="logradouro">Logradouro</label>
              <input id="logradouro" name="logradouro" type="text" value={form.logradouro}
                onChange={handleChange} placeholder="Rua, Avenida..." />
            </div>

            <div className="form-group">
              <label htmlFor="numero">Número</label>
              <input id="numero" name="numero" type="text" value={form.numero}
                onChange={handleChange} placeholder="123" />
            </div>

            <div className="form-group">
              <label htmlFor="bairro">Bairro</label>
              <input id="bairro" name="bairro" type="text" value={form.bairro}
                onChange={handleChange} placeholder="Seu bairro" />
            </div>

            <div className="form-group">
              <label htmlFor="cidade">Cidade</label>
              <input id="cidade" name="cidade" type="text" value={form.cidade}
                onChange={handleChange} placeholder="São Paulo" />
            </div>

            <div className="form-group">
              <label htmlFor="estado">Estado</label>
              <select id="estado" name="estado" value={form.estado} onChange={handleChange}>
                <option value="">Selecione</option>
                {UFS.map(uf => <option key={uf} value={uf}>{uf}</option>)}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="cep">CEP</label>
              <input id="cep" name="cep" type="text" value={form.cep}
                onChange={handleChange} placeholder="00000-000" />
            </div>
          </div>
        </section>

        <div className="form-actions">
          <Link to="/" className="btn btn-outline">Cancelar</Link>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? <><Loader size={16} className="spin" /> Cadastrando...</> : 'Criar Conta'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CadastroCliente;
