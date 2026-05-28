import { useState } from 'react';
import { clienteService } from '../services/clienteService';
import { mascararTelefone, mascararCep, limparMascara } from '../utils/mascaras';

const estadoInicial = {
  nome: '',
  email: '',
  senha: '',
  telefone: '',
  logradouro: '',
  numero: '',
  complemento: '',
  bairro: '',
  cidade: '',
  estado: '',
  cep: '',
  pais: 'Brasil',
};

const mascaras = { telefone: mascararTelefone, cep: mascararCep };

export const useCadastroCliente = () => {
  const [form, setForm] = useState(estadoInicial);
  const [loading, setLoading] = useState(false);
  const [erros, setErros] = useState({});
  const [sucesso, setSucesso] = useState(null);
  const [erroGeral, setErroGeral] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const v = mascaras[name] ? mascaras[name](value) : value;
    setForm(prev => ({ ...prev, [name]: v }));
    if (erros[name]) setErros(prev => ({ ...prev, [name]: undefined }));
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
        cep: limparMascara(form.cep),
      };
      const resposta = await clienteService.realizarCadastro(payload);
      setSucesso(resposta);
      setForm(estadoInicial);
    } catch (err) {
      const status = err.response?.status;
      const data = err.response?.data;
      if (status === 400 && typeof data === 'object') setErros(data);
      else if (status === 409) setErroGeral(data?.erro || 'E-mail já cadastrado.');
      else setErroGeral('Erro ao enviar cadastro. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, erros, sucesso, erroGeral, handleChange, handleSubmit };
};
