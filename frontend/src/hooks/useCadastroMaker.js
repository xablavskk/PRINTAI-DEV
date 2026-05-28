import { useState, useEffect } from 'react';
import { makerService } from '../services/makerService';

const estadoInicial = {
  nome: '',
  email: '',
  senha: '',
  telefone: '',
  documentoCpfCnpj: '',
  logradouro: '',
  numero: '',
  complemento: '',
  bairro: '',
  cidade: '',
  estado: '',
  cep: '',
  pais: 'Brasil',
};

const servicoVazio = () => ({
  nome: '',
  precoBase: '',
  descricao: '',
  condicoesServico: '',
  tipoId: '',
  materialId: '',      
  suportaPecasPequenas: false,
  suportaDecorativos: false,
  suportaPrototipos: false,
});

const mascararTelefone = (valor) => {
  const digits = valor.replace(/\D/g, '').slice(0, 11);
  if (digits.length <= 10) {
    return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
  }
  return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
};

const mascararDocumento = (valor) => {
  const digits = valor.replace(/\D/g, '').slice(0, 14);
  if (digits.length <= 11) {
    return digits
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  }
  return digits
    .replace(/(\d{2})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1/$2')
    .replace(/(\d{4})(\d{1,2})$/, '$1-$2');
};

const mascararCep = (valor) => {
  const digits = valor.replace(/\D/g, '').slice(0, 8);
  return digits.replace(/(\d{5})(\d)/, '$1-$2');
};

const mascaras = {
  telefone: mascararTelefone,
  documentoCpfCnpj: mascararDocumento,
  cep: mascararCep,
};

const limparMascara = (valor) => valor.replace(/\D/g, '');

export const useCadastroMaker = () => {
  const [form, setForm] = useState(estadoInicial);
  const [servicos, setServicos] = useState([]);
  const [tipos, setTipos] = useState([]);
  const [materiais, setMateriais] = useState([]);
  const [loading, setLoading] = useState(false);
  const [erros, setErros] = useState({});
  const [sucesso, setSucesso] = useState(null);
  const [erroGeral, setErroGeral] = useState(null);

  useEffect(() => {
    makerService.listarTipos().then(setTipos).catch(() => setTipos([]));
    makerService.listarMateriais().then(setMateriais).catch(() => setMateriais([]));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const valorFormatado = mascaras[name] ? mascaras[name](value) : value;
    setForm((prev) => ({ ...prev, [name]: valorFormatado }));
    if (erros[name]) setErros((prev) => ({ ...prev, [name]: undefined }));
  };

  const adicionarServico = () => setServicos((prev) => [...prev, servicoVazio()]);

  const removerServico = (index) =>
    setServicos((prev) => prev.filter((_, i) => i !== index));

  const handleServicoChange = (index, e) => {
    const { name, value, type, checked } = e.target;
    setServicos((prev) =>
      prev.map((s, i) =>
        i === index ? { ...s, [name]: type === 'checkbox' ? checked : value } : s
      )
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErros({});
    setErroGeral(null);
    setSucesso(null);

    try {
      const payload = {
        ...form,
        telefone: limparMascara(form.telefone),
        documentoCpfCnpj: limparMascara(form.documentoCpfCnpj),
        cep: limparMascara(form.cep),
        servicos: servicos.length > 0
          ? servicos.map((s) => ({
              nome: s.nome,
              precoBase: s.precoBase ? Number(s.precoBase) : 0,
              descricao: s.descricao,
              condicoesServico: s.condicoesServico,
              tipoId: s.tipoId ? Number(s.tipoId) : null,
              materialId: s.materialId ? Number(s.materialId) : null,
              suportaPecasPequenas: s.suportaPecasPequenas,
              suportaDecorativos: s.suportaDecorativos,
              suportaPrototipos: s.suportaPrototipos,
            }))
          : [],
      };

      const resposta = await makerService.solicitarCadastro(payload);
      setSucesso(resposta);
      setForm(estadoInicial);
      setServicos([]);
    } catch (err) {
      const status = err.response?.status;
      const data = err.response?.data;
      if (status === 400 && typeof data === 'object') {
        setErros(data);
      } else if (status === 409) {
        setErroGeral(data?.erro || 'E-mail já cadastrado.');
      } else {
        setErroGeral('Erro ao enviar cadastro. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return {
    form, servicos, tipos, materiais,
    loading, erros, sucesso, erroGeral,
    handleChange, handleServicoChange,
    adicionarServico, removerServico,
    handleSubmit,
  };
};
