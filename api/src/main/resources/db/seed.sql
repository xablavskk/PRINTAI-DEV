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
