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
-- -------------------------------------------------------------

-- Filamento
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('FDM',  (SELECT id FROM tipos WHERE nome = 'Filamento')),
  ('MJF',  (SELECT id FROM tipos WHERE nome = 'Filamento'));

-- Resina
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('SLA',  (SELECT id FROM tipos WHERE nome = 'Resina')),
  ('DLP',  (SELECT id FROM tipos WHERE nome = 'Resina'));

-- Pó
INSERT IGNORE INTO tecnologias (nome, tipo_id) VALUES
  ('SLS',            (SELECT id FROM tipos WHERE nome = 'Pó')),
  ('BINDER_JETTING', (SELECT id FROM tipos WHERE nome = 'Pó'));

-- -------------------------------------------------------------
-- MATERIAIS
-- -------------------------------------------------------------
INSERT IGNORE INTO materiais (nome, descricao) VALUES
  ('PLA',
   'Ácido Polilático. Material mais popular para FDM. Biodegradável, fácil de imprimir, baixa temperatura de extrusão. Ideal para protótipos, decoração e peças de baixo esforço mecânico.'),
  ('ABS',
   'Acrilonitrila Butadieno Estireno. Alta resistência mecânica e térmica (até ~100°C). Requer mesa aquecida e ambiente fechado. Usado em peças automotivas, caixas e componentes técnicos.'),
  ('PETG',
   'Politereftalato de Etileno com Glicol. Combina a facilidade do PLA com a resistência do ABS. Resistente à umidade, translúcido, boa adesão entre camadas. Ótimo para peças funcionais e recipientes.'),
  ('TPU',
   'Poliuretano Termoplástico. Material flexível e elástico, resistente a abrasão e impacto. Usado em capas de celular, solas, juntas e peças que exigem flexibilidade.'),
  ('NYLON',
   'Poliamida (PA). Alta resistência ao impacto, desgaste e fadiga. Levemente higroscópico (absorve umidade). Indicado para engrenagens, rolamentos, peças estruturais e aplicações industriais.'),
  ('ASA',
   'Acrilonitrila Estireno Acrilato. Similar ao ABS, porém com excelente resistência a UV e intempéries. Indicado para peças de uso externo, sinalização e componentes expostos ao sol.'),
  ('PEEK',
   'Poliéter Éter Cetona. Polímero de alta performance com resistência química, mecânica e térmica extremas (até ~250°C). Usado em aeroespacial, médico e industrial. Requer impressora de alta temperatura.'),
  ('CARBON_FIBER',
   'Fibra de Carbono (compósito). Filamento com fibras de carbono picadas ou contínuas. Altíssima rigidez e leveza. Usado em drones, peças estruturais e aplicações de engenharia avançada.'),
  ('RESINA',
   'Resina fotopolimerizável (padrão, ABS-like, flexível ou castable). Usada em impressoras SLA, DLP e MSLA. Alta resolução e acabamento liso. Indicada para miniaturas, joias, odontologia e moldes.');

-- -------------------------------------------------------------
-- USUÁRIOS DE TESTE (Clientes e Makers)
-- -------------------------------------------------------------

-- Cliente de Teste
-- Senha em texto plano: senha123
INSERT IGNORE INTO usuarios (id, nome, email, senha, telefone, cidade, estado, perfil, status_aprovacao, documento_cpf_cnpj) VALUES
  (10, 'João Cliente', 'cliente@printai.com', 'senha123', '19988880000', 'Indaiatuba', 'SP', 'CLIENTE', true, '11122233344');

-- Makers de Teste
-- Senhas em texto plano: senha123
INSERT IGNORE INTO usuarios (id, nome, email, senha, telefone, cidade, estado, perfil, status_aprovacao, documento_cpf_cnpj, latitude, longitude) VALUES
  (11, 'Mario Henrique Maker', 'maker@printai.com', 'senha123', '19977770000', 'Indaiatuba', 'SP', 'MAKER', true, '22233344455', -23.0913052, -47.2180265),
  (12, 'Ana Resinas Pro', 'ana@printai.com', 'senha123', '19966660000', 'Campinas', 'SP', 'MAKER', true, '33344455566', -22.9099384, -47.0626332);

-- -------------------------------------------------------------
-- SERVIÇOS DE IMPRESSÃO DE TESTE
-- -------------------------------------------------------------
INSERT IGNORE INTO servicos_impressao (id, nome, preco_base, descricao, condicoes_servico, tipo_id, tecnologia, material_id, maker_id, suporta_pecas_pequenas, suporta_decorativos, suporta_prototipos) VALUES
  (10, 
   'Impressão FDM de Alta Precisão', 
   15.50, 
   'Ofereço serviços de impressão 3D por filamento (FDM) com excelente qualidade. Ótimo custo-benefício para protótipos rápidos, suportes, organizadores e peças mecânicas em geral.', 
   'Preço calculado por hora de impressão + gramas de filamento. Envie seu arquivo STL ou OBJ para orçamento.',
   (SELECT id FROM tipos WHERE nome = 'Filamento'),
   'FDM',
   (SELECT id FROM materiais WHERE nome = 'PLA'),
   11, true, true, true),

  (11, 
   'Impressão em Resina SLA Alta Resolução', 
   25.00, 
   'Serviço especializado em peças ricas em detalhes utilizando impressora 3D de resina. Altíssima fidelidade e qualidade de acabamento para miniaturas de RPG, próteses dentárias, joias e peças decorativas complexas.', 
   'O valor inicial cobre a configuração do lote. O valor final varia pelo volume da peça em ml. Necessário pós-cura UV.',
   (SELECT id FROM tipos WHERE nome = 'Resina'),
   'SLA',
   (SELECT id FROM materiais WHERE nome = 'RESINA'),
   12, true, true, false);
