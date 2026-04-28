# CLAUDE.md — PrintAI (Contexto Completo)

> Arquivo de contexto para IAs assistentes (Claude Code, Cursor, Copilot, etc.) e membros da equipe.
> Leia este arquivo antes de qualquer tarefa no projeto.

---

## 🧠 Visão Geral do Projeto

**PrintAI** é um marketplace web de serviços de impressão 3D.  
A plataforma conecta **Clientes** (quem precisa imprimir) com **Makers** (quem possui impressoras 3D disponíveis), permitindo busca por localização, envio de arquivos 3D e avaliação de prestadores.

O sistema **não processa pagamentos** — a negociação e pagamento ocorrem fora da plataforma (WhatsApp, email, etc.).

---

## 👥 Atores do Sistema

| Ator | Descrição |
|---|---|
| **Cliente** | Busca serviços de impressão, envia arquivos 3D, avalia Makers |
| **Maker (Criador)** | Cadastra impressoras, oferece serviços, atende pedidos |
| **Administrador** | Aprova cadastros de Makers, monitora a plataforma |

---

## 📋 Casos de Uso

### Cliente
- Realizar Cadastro
- Buscar impressoras 3D (por localização e filtros)
- Visualizar detalhes de um Maker/impressora
- Solicitar pedido de impressão 3D (envia arquivo STL/OBJ)
- Avaliar Maker após o serviço

### Maker
- Solicitar Cadastro (aguarda aprovação do Administrador)
- Manter Serviços de Impressão 3D (cadastrar/editar/remover)
- Consultar avaliações recebidas
- Buscar impressoras 3D

### Administrador
- Aprovar Makers
- Buscar impressoras 3D

> `<<extend>>` de "Solicitar pedido de impressão 3D" e "Visualizar detalhes" sobre "Manter Serviços de Impressão 3D"

---

## 🗂️ Glossário

| Termo | Definição |
|---|---|
| **Maker** | Usuário que possui impressora 3D e oferece serviços |
| **Cliente** | Usuário que deseja contratar impressão 3D |
| **STL** | Formato de arquivo 3D baseado em geometria |
| **OBJ** | Formato de arquivo 3D com suporte a textura e materiais |
| **Impressão 3D** | Criação de objetos físicos a partir de modelos digitais |

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.3+**
  - Spring Web (REST API)
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **MySQL 8.4** (Atualmente usando **H2** para testes rápidos de MVP)

### Frontend
- **React** (com **Vite** como bundler)
- Comunicação via API REST

### Arquitetura
- Padrão **MVC** no backend
- API REST (JSON) entre frontend e backend
- Upload de arquivos 3D (STL e OBJ)
- Integração com API de geolocalização/mapas (Leaflet)

---

## 🏗️ Padrão Arquitetural — MVC (Backend)

```
src/
└── main/
    └── java/
        └── com/printai/
            ├── controller/     # Camada Controller — recebe requisições HTTP
            ├── service/        # Camada de negócio (regras e lógica)
            ├── repository/     # Camada de acesso ao banco (Spring Data JPA)
            ├── model/          # Entidades JPA (mapeamento do banco)
            ├── dto/            # Data Transfer Objects (request/response)
            ├── config/         # Configurações (Security, CORS, etc.)
            └── exception/      # Tratamento de exceções global
```

---

## ⚠️ Restrições do Projeto

- Aplicação **exclusivamente web** (navegadores modernos)
- **Sem pagamentos internos** — comunicação entre usuários é externa
- Upload permitido apenas para arquivos **STL** e **OBJ**
- Dados devem ser armazenados com segurança
- Comunicação frontend ↔ backend **somente via API REST**
- Prazo limitado (projeto acadêmico)

---

## 👨‍💻 Equipe

| Papel | Nome |
|---|---|
| Product Owner | Adriano Camocardi |
| Scrum Master | Mario Henrique M A Pedrao |
| Desenvolvedor | Diogo Acioli |
| Desenvolvedor | Lucas Gabriel |
| Desenvolvedor | Mario Henrique M A Pedrao |
| Desenvolvedor | Adriano Camocardi |

---

## 🗓️ Sprints e Entregas

| Sprint | Entregáveis | Horas |
|---|---|---|
| Sprint 1 | Visão, Modelo de Casos de Uso e Plano | 40h |
| Sprint 2 | UC Realizar Cadastro, UC Solicitar Cadastro, UC Aprovar Makers, UC Manter Serviços de Impressão 3D | 60h |
| Sprint 3 | UC Buscar Impressoras 3D, UC Visualizar Detalhes, UC Solicitar Pedido de Impressão 3D | 50h |
| Sprint 4 | UC Consultar Avaliações e UC Avaliar Maker | 40h |
| Sprint 5 | Testes Finais, Correções e Apresentação | 50h |

**Esforço total estimado:** 240h  
**Capacidade do time por sprint:** 72h (4 devs × 9h/semana × 2 semanas)

---

## 📊 Priorização dos Casos de Uso (VRDC)

| UC | Prioridade |
|---|---|
| UC Visualizar Detalhes | 30 ⭐ mais alta |
| UC Solicitar Pedido de Impressão 3D | 28 |
| UC Avaliar Maker | 26 |
| UC Buscar Impressoras 3D | 25 |
| UC Solicitar Cadastro | 25 |
| UC Realizar Cadastro | 24 |
| UC Manter Serviços de Impressão 3D | 24 |
| UC Aprovar Makers | 23 |
| UC Consultar Avaliações | 19 |

> A IA deve priorizar implementação nessa ordem ao sugerir o que desenvolver primeiro.

---

## 📌 Regras para a IA ao trabalhar neste projeto

1. **Sempre seguir o padrão MVC** — não misturar responsabilidades entre camadas
2. **DTOs obrigatórios** — nunca expor entidades JPA diretamente nas respostas da API
3. **Validações** na camada de `service`, anotações `@Valid` no `controller`
4. **Nomes em português** para classes, métodos, variáveis, tabelas e endpoints.
5. **Três perfis de usuário** bem separados: `CLIENTE`, `MAKER`, `ADMIN`
6. Ao criar endpoints, seguir REST semântico: `GET /api/servicos`, `POST /api/pedidos`, etc.
7. Não implementar lógica de pagamento — fora do escopo

---
---

# 🚀 STATUS ATUAL DA IMPLEMENTAÇÃO (O QUE JÁ FOI FEITO)

Foi finalizado o setup inicial (scaffold) e o **UC Buscar Impressoras 3D (com mapa)**.

### Backend (`api/`)
- Entidades `Usuario` (e subclasses `Cliente`, `Maker`, `Administrador`) e `ServicoImpressao` criadas com suporte a Geolocalização (`latitude`, `longitude`) e `telefone`.
- Repositórios e Serviços mapeando as buscas (Simplificada via `buscaSimplificada` e Avançada via parâmetros técnicos).
- **Banco H2 + Data Initializer**: A API sobe populada com dados mockados (Makers no centro de SP) para facilitar testes de frontend. Para migrar para a stack oficial, basta alterar `application.properties` para MySQL 8.4 e deletar o Initializer.
- Como rodar: `./mvnw spring-boot:run` (porta 8080).

### Frontend (`frontend/`)
- Design Premium (Glassmorphism e Vanilla CSS) com ícones do `lucide-react`.
- **Busca Simplificada:** O usuário dita o que quer ("peça pequena", "decorativo") e o sistema mapeia isso para ids/tipos usando fallback ou chamada à API.
- **Integração de Mapa (Leaflet):** Layout de Dashboard dividindo a tela. Lista de serviços na esquerda, Mapa geolocalizado interativo na direita (`react-leaflet`). Ao clicar nos marcadores, abre um popup estilizado com as informações da máquina e botão de "Ver Detalhes" do Maker.
- **Fallback de Interface:** Caso a API (`http://localhost:8080`) esteja offline na máquina do dev, o componente `BuscaServicos.jsx` intercepta a falha e utiliza um Mock que **simula a filtragem perfeitamente**. O layout front-end pode ser trabalhado de forma totalmente independente.
- Como rodar: `npm install` e `npm run dev` (porta 5173).
