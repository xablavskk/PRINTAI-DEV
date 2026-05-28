use printai;
-- =============================================================
-- PrintAI — Seed de dados de referência
-- Tipos de impressão, Tecnologias e Materiais
--
-- Executar uma única vez após a criação do schema.
-- Usa INSERT IGNORE para ser idempotente (pode rodar novamente
-- sem duplicar registros).
-- =============================================================

-- -------------------------------------------------------------
-- TIPOS DE IMPRESSÃO
-- -------------------------------------------------------------
INSERT IGNORE INTO tipos (nome, descricao) VALUES
  ('Filamento',
   'Impressão por deposição de material fundido (FFF/FDM). Tecnologia mais acessível e versátil. Ideal para protótipos funcionais, peças mecânicas e objetos de uso geral.'),
  ('Resina',
   'Impressão por fotopolimerização de resina líquida (SLA/DLP/MSLA). Alta resolução e acabamento superficial superior. Indicada para miniaturas, joias, odontologia e peças com detalhes finos.'),
  ('Pó',
   'Impressão por sinterização ou aglutinação de pó (SLS/MJF/Binder Jetting). Não requer suportes, permite geometrias complexas e alta resistência mecânica. Usada em aplicações industriais e aeroespaciais.');

-- -------------------------------------------------------------
-- TECNOLOGIAS
-- Vinculadas aos tipos pelo nome (subquery)
-- Valores devem corresponder exatamente ao enum TecnologiaTipo:
--   FDM, SLA, SLS, DLP, MJF, BINDER_JETTING
-- -------------------------------------------------------------

-- Filamento
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('FDM', (SELECT id FROM tipos WHERE nome = 'Filamento'));

-- Resina
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('SLA', (SELECT id FROM tipos WHERE nome = 'Resina')),
  ('DLP', (SELECT id FROM tipos WHERE nome = 'Resina'));

-- Pó (MJF e BINDER_JETTING são tecnologias de pó, não de filamento)
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('SLS',            (SELECT id FROM tipos WHERE nome = 'Pó')),
  ('MJF',            (SELECT id FROM tipos WHERE nome = 'Pó')),
  ('BINDER_JETTING', (SELECT id FROM tipos WHERE nome = 'Pó'));

-- -------------------------------------------------------------
-- MATERIAIS
-- Valores devem corresponder exatamente ao enum MaterialTipo:
--   PLA, ABS, PETG, RESINA, TPU, NYLON, ASA, PEEK, CARBON_FIBER
-- -------------------------------------------------------------
INSERT IGNORE INTO materiais (nome, descricao) VALUES
  ('PLA',
   'Ácido Polilático. Material mais popular para FDM. Biodegradável, fácil de imprimir, baixa temperatura de extrusão. Ideal para protótipos, decoração e peças de baixo esforço mecânico.'),
  ('ABS',
   'Acrilonitrila Butadieno Estireno. Alta resistência mecânica e térmica (até ~100°C). Requer mesa aquecida e ambiente fechado. Usado em peças automotivas, caixas e componentes técnicos.'),
  ('PETG',
   'Politereftalato de Etileno com Glicol. Combina a facilidade do PLA com a resistência do ABS. Resistente à umidade, translúcido, boa adesão entre camadas. Ótimo para peças funcionais e recipientes.'),
  ('RESINA',
   'Resina fotopolimerizável (padrão, ABS-like, flexível ou castable). Usada em impressoras SLA, DLP e MSLA. Alta resolução e acabamento liso. Indicada para miniaturas, joias, odontologia e moldes.'),
  ('TPU',
   'Poliuretano Termoplástico. Material flexível e elástico, resistente a abrasão e impacto. Usado em capas de celular, solas, juntas e peças que exigem flexibilidade.'),
  ('NYLON',
   'Poliamida (PA). Alta resistência ao impacto, desgaste e fadiga. Levemente higroscópico (absorve umidade). Indicado para engrenagens, rolamentos, peças estruturais e aplicações industriais.'),
  ('ASA',
   'Acrilonitrila Estireno Acrilato. Similar ao ABS, porém com excelente resistência a UV e intempéries. Indicado para peças de uso externo, sinalização e componentes expostos ao sol.'),
  ('PEEK',
   'Poliéter Éter Cetona. Polímero de alta performance com resistência química, mecânica e térmica extremas (até ~250°C). Usado em aeroespacial, médico e industrial. Requer impressora de alta temperatura.'),
  ('CARBON_FIBER',
   'Fibra de Carbono (compósito). Filamento com fibras de carbono picadas ou contínuas. Altíssima rigidez e leveza. Usado em drones, peças estruturais e aplicações de engenharia avançada.');

-- =============================================================
-- DADOS DE EXEMPLO (desenvolvimento)
-- Descomente o bloco abaixo para popular o banco com makers e
-- serviços de exemplo. NÃO executar em produção.
-- =============================================================

INSERT IGNORE INTO usuarios (nome, email, senha, telefone, documento_cpf_cnpj,
  cidade, estado, latitude, longitude,
  logradouro, numero, bairro, endereco_cidade, endereco_estado, endereco_cep, endereco_pais,
  perfil, status_aprovacao)
VALUES
  ('Ana Maker', 'ana@printai.com', 'senha123', '11999990001', '11122233344',
   'São Paulo', 'SP', -23.5505, -46.6333,
   'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', '01310100', 'Brasil',
   'MAKER', true),
  ('Bruno Maker', 'bruno@printai.com', 'senha123', '21988880002', '22233344455',
   'Rio de Janeiro', 'RJ', -22.9068, -43.1729,
   'Rua das Impressoras', '42', 'Centro', 'Rio de Janeiro', 'RJ', '20040020', 'Brasil',
   'MAKER', true);

-- -- Serviços vinculados aos makers de exemplo
-- -- (requer que os makers acima tenham sido inseridos)
INSERT IGNORE INTO servicos_impressao
  (nome, preco_base, descricao, condicoes_servico, tipo_id, material_id,
   suporta_pecas_pequenas, suporta_decorativos, suporta_prototipos, maker_id)
VALUES
  ('Impressão FDM - PLA', 50.00,
   'Serviço de impressão FDM com PLA. Ideal para protótipos e peças funcionais.',
   'Orçamento final após envio do arquivo STL. Prazo: 3 a 5 dias úteis.',
   (SELECT id FROM tipos WHERE nome = 'Filamento'),
   (SELECT id FROM materiais WHERE nome = 'PLA'),
   true, true, true,
   (SELECT id FROM usuarios WHERE email = 'ana@printai.com')),
  ('Impressão Resina - SLA', 120.00,
   'Impressão em resina SLA com alta resolução. Perfeita para miniaturas e peças com detalhes finos.',
   'Arquivo STL obrigatório. Peças grandes podem ter custo adicional.',
   (SELECT id FROM tipos WHERE nome = 'Resina'),
   (SELECT id FROM materiais WHERE nome = 'RESINA'),
   true, false, false,
   (SELECT id FROM usuarios WHERE email = 'bruno@printai.com'));

-- -- Impressoras vinculadas aos makers de exemplo
INSERT IGNORE INTO impressoras_3d
  (modelo, tipo_id, material_id, volume_impressao, descricao, disponibilidade, maker_id)
VALUES
  ('Ender 3 Pro',
   (SELECT id FROM tipos WHERE nome = 'Filamento'),
   (SELECT id FROM materiais WHERE nome = 'PLA'),
   '220x220x250mm', 'Impressora FDM confiável para uso geral.', true,
   (SELECT id FROM usuarios WHERE email = 'ana@printai.com')),
  ('Anycubic Photon Mono',
   (SELECT id FROM tipos WHERE nome = 'Resina'),
   (SELECT id FROM materiais WHERE nome = 'RESINA'),
   '130x80x165mm', 'Impressora de resina com alta precisão.', true,
   (SELECT id FROM usuarios WHERE email = 'bruno@printai.com'));
