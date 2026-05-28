/**
 * Utilitários de máscara e formatação de campos de formulário.
 * Todas as funções de mascarar recebem o valor bruto e retornam o valor formatado.
 * limparMascara remove todos os caracteres não numéricos.
 */

export const limparMascara = (valor) => (valor ? valor.replace(/\D/g, '') : '');

export const mascararTelefone = (valor) => {
  const d = limparMascara(valor).slice(0, 11);
  if (d.length <= 10) return d.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
  return d.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
};

export const mascararCep = (valor) => {
  const d = limparMascara(valor).slice(0, 8);
  return d.replace(/(\d{5})(\d)/, '$1-$2');
};

export const mascararDocumento = (valor) => {
  const d = limparMascara(valor).slice(0, 14);
  if (d.length <= 11) {
    return d
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  }
  return d
    .replace(/(\d{2})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1/$2')
    .replace(/(\d{4})(\d{1,2})$/, '$1-$2');
};

/**
 * Formata um telefone já salvo (sem máscara) para exibição.
 * Ex: "11999990000" → "(11) 99999-0000"
 */
export const formatarTelefone = (tel) => {
  if (!tel) return 'Não informado';
  const d = limparMascara(tel);
  if (d.length === 11) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
  if (d.length === 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
  return tel;
};
