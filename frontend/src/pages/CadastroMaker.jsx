import React from 'react';
import { Link } from 'react-router-dom';
import { Printer, CheckCircle, AlertCircle, Loader, Plus, Trash2 } from 'lucide-react';
import { useCadastroMaker } from '../hooks/useCadastroMaker';
import './CadastroMaker.css';

const MATERIAIS = ['PLA','ABS','PETG','RESINA','TPU','NYLON','ASA','PEEK','CARBON_FIBER'];

const CadastroMaker = () => {
  const {
    form, servicos, tipos,
    loading, erros, sucesso, erroGeral,
    handleChange, handleServicoChange,
    adicionarServico, removerServico,
    handleSubmit,
  } = useCadastroMaker();

  if (sucesso) {
    return (
      <div className="cadastro-container">
        <div className="sucesso-card glass-panel">
          <CheckCircle size={56} color="var(--accent-color)" />
          <h2>Solicitação enviada!</h2>
          <p>{sucesso.mensagem}</p>
          <div className="sucesso-info">
            <span><strong>Nome:</strong> {sucesso.nome}</span>
            <span><strong>E-mail:</strong> {sucesso.email}</span>
            {sucesso.cidade && (
              <span><strong>Localização:</strong> {sucesso.cidade} - {sucesso.estado}</span>
            )}
            {sucesso.totalServicos > 0 && (
              <span><strong>Serviços cadastrados:</strong> {sucesso.totalServicos}</span>
            )}
            {sucesso.latitude && (
              <span className="coordenadas">
                📍 Coordenadas: {sucesso.latitude.toFixed(4)}, {sucesso.longitude.toFixed(4)}
              </span>
            )}
          </div>
          <Link to="/" className="btn btn-primary">Voltar para a busca</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="cadastro-container">
      <div className="cadastro-header">
        <Printer size={36} color="var(--accent-color)" />
        <h1 className="text-gradient">Seja um Maker</h1>
        <p>Cadastre-se como criador e ofereça seus serviços de impressão 3D na plataforma.</p>
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

            <div className="form-group">
              <label htmlFor="documentoCpfCnpj">CPF ou CNPJ *</label>
              <input id="documentoCpfCnpj" name="documentoCpfCnpj" type="text"
                value={form.documentoCpfCnpj} onChange={handleChange}
                placeholder="000.000.000-00 ou 00.000.000/0001-00"
                className={erros.documentoCpfCnpj ? 'input-erro' : ''} />
              {erros.documentoCpfCnpj && <span className="erro-msg">{erros.documentoCpfCnpj}</span>}
            </div>
          </div>
        </section>

        {/* ── Endereço ── */}
        <section className="form-section">
          <h3>Endereço</h3>
          <p className="section-hint">
            📍 Seu endereço será convertido em coordenadas para aparecer no mapa da plataforma.
          </p>
          <div className="form-grid">
            <div className="form-group form-group--wide">
              <label htmlFor="logradouro">Logradouro *</label>
              <input id="logradouro" name="logradouro" type="text" value={form.logradouro}
                onChange={handleChange} placeholder="Rua, Avenida, Travessa..."
                className={erros.logradouro ? 'input-erro' : ''} />
              {erros.logradouro && <span className="erro-msg">{erros.logradouro}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="numero">Número</label>
              <input id="numero" name="numero" type="text" value={form.numero}
                onChange={handleChange} placeholder="123" />
            </div>

            <div className="form-group">
              <label htmlFor="complemento">Complemento</label>
              <input id="complemento" name="complemento" type="text" value={form.complemento}
                onChange={handleChange} placeholder="Apto, Sala..." />
            </div>

            <div className="form-group">
              <label htmlFor="bairro">Bairro</label>
              <input id="bairro" name="bairro" type="text" value={form.bairro}
                onChange={handleChange} placeholder="Seu bairro" />
            </div>

            <div className="form-group">
              <label htmlFor="cidade">Cidade *</label>
              <input id="cidade" name="cidade" type="text" value={form.cidade}
                onChange={handleChange} placeholder="São Paulo"
                className={erros.cidade ? 'input-erro' : ''} />
              {erros.cidade && <span className="erro-msg">{erros.cidade}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="estado">Estado *</label>
              <select id="estado" name="estado" value={form.estado} onChange={handleChange}
                className={erros.estado ? 'input-erro' : ''}>
                <option value="">Selecione</option>
                {['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG',
                  'PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'].map(uf => (
                  <option key={uf} value={uf}>{uf}</option>
                ))}
              </select>
              {erros.estado && <span className="erro-msg">{erros.estado}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="cep">CEP *</label>
              <input id="cep" name="cep" type="text" value={form.cep}
                onChange={handleChange} placeholder="00000-000"
                className={erros.cep ? 'input-erro' : ''} />
              {erros.cep && <span className="erro-msg">{erros.cep}</span>}
            </div>
          </div>
        </section>

        {/* ── Serviços de Impressão (opcional) ── */}
        <section className="form-section">
          <div className="section-header-row">
            <div>
              <h3>Serviços de Impressão</h3>
              <p className="section-hint">Opcional — adicione os serviços que você oferece.</p>
            </div>
            <button type="button" className="btn-add-servico" onClick={adicionarServico}>
              <Plus size={15} /> Adicionar serviço
            </button>
          </div>

          {servicos.length === 0 && (
            <p className="servicos-vazio">Nenhum serviço adicionado ainda.</p>
          )}

          {servicos.map((servico, index) => (
            <div key={index} className="servico-card">
              <div className="servico-card-header">
                <span>Serviço #{index + 1}</span>
                <button type="button" className="btn-remover" onClick={() => removerServico(index)}
                  aria-label="Remover serviço">
                  <Trash2 size={15} />
                </button>
              </div>

              <div className="form-grid">
                <div className="form-group form-group--wide">
                  <label>Nome do serviço *</label>
                  <input name="nome" type="text" value={servico.nome}
                    onChange={(e) => handleServicoChange(index, e)}
                    placeholder="Ex: Impressão FDM de Alta Precisão" />
                </div>

                <div className="form-group">
                  <label>Tipo de impressão</label>
                  <select name="tipoId" value={servico.tipoId}
                    onChange={(e) => handleServicoChange(index, e)}>
                    <option value="">Selecione</option>
                    {tipos.map((t) => (
                      <option key={t.id} value={t.id}>{t.nome}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Tecnologia</label>
                  <select name="tecnologia" value={servico.tecnologia}
                    onChange={(e) => handleServicoChange(index, e)}>
                    <option value="">Selecione</option>
                    {servico.tipoId
                      ? (tipos.find(t => String(t.id) === String(servico.tipoId))?.tecnologias || [])
                          .map(tec => <option key={tec} value={tec}>{tec}</option>)
                      : ['FDM','SLA','DLP','SLS','MJF','BINDER_JETTING']
                          .map(tec => <option key={tec} value={tec}>{tec}</option>)
                    }
                  </select>
                </div>

                <div className="form-group">
                  <label>Material</label>
                  <select name="material" value={servico.material}
                    onChange={(e) => handleServicoChange(index, e)}>
                    <option value="">Selecione</option>
                    {MATERIAIS.map(m => <option key={m} value={m}>{m}</option>)}
                  </select>
                </div>

                <div className="form-group">
                  <label>Preço base (R$)</label>
                  <input name="precoBase" type="number" min="0" step="0.01"
                    value={servico.precoBase}
                    onChange={(e) => handleServicoChange(index, e)}
                    placeholder="0,00" />
                </div>

                <div className="form-group form-group--wide">
                  <label>Descrição</label>
                  <textarea name="descricao" rows={2} value={servico.descricao}
                    onChange={(e) => handleServicoChange(index, e)}
                    placeholder="Descreva seu serviço..." />
                </div>

                <div className="form-group form-group--wide">
                  <label>Condições do serviço</label>
                  <textarea name="condicoesServico" rows={2} value={servico.condicoesServico}
                    onChange={(e) => handleServicoChange(index, e)}
                    placeholder="Prazo, requisitos, observações..." />
                </div>

                <div className="form-group form-group--wide">
                  <label>Capacidades</label>
                  <div className="checkboxes-row">
                    <label className="checkbox-label">
                      <input type="checkbox" name="suportaPecasPequenas"
                        checked={servico.suportaPecasPequenas}
                        onChange={(e) => handleServicoChange(index, e)} />
                      Peças pequenas
                    </label>
                    <label className="checkbox-label">
                      <input type="checkbox" name="suportaDecorativos"
                        checked={servico.suportaDecorativos}
                        onChange={(e) => handleServicoChange(index, e)} />
                      Decorativos
                    </label>
                    <label className="checkbox-label">
                      <input type="checkbox" name="suportaPrototipos"
                        checked={servico.suportaPrototipos}
                        onChange={(e) => handleServicoChange(index, e)} />
                      Protótipos
                    </label>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </section>

        {/* ── Ações ── */}
        <div className="form-actions">
          <Link to="/" className="btn btn-outline">Cancelar</Link>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? (
              <><Loader size={16} className="spin" /> Enviando...</>
            ) : (
              'Solicitar Cadastro'
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CadastroMaker;
