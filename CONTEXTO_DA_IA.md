# Contexto de Implementação: Buscar Impressoras 3D

Este documento foi gerado automaticamente pela Inteligência Artificial para manter os desenvolvedores do projeto alinhados com as decisões técnicas e a base de código estruturada durante a implementação do caso de uso "Buscar Impressoras 3D".

## O que foi implementado

Foi feito o setup inicial (scaffold) da arquitetura MVC usando **Java 21 + Spring Boot 3.3.4** e o frontend usando **React + Vite**. 

A funcionalidade entregue abrange:
- **Busca Simplificada:** Baseada em palavras-chave que mapeiam para capacidades específicas das impressoras (ex: "peça pequena" verifica o boolean `isSmallPieceCapable`).
- **Busca Avançada:** Filtros técnicos diretos no banco por `technology` e `material`.
- **Integração de Mapa (Leaflet):** O frontend exibe os resultados da busca tanto em lista quanto renderizados em um mapa interativo, aproveitando as tags de `latitude` e `longitude`.

## Decisões Técnicas & Mocks

### Backend (`api/`)
- **Banco de dados temporário:** Para fins de teste rápido de MVP, a aplicação Spring Boot está utilizando o banco de dados em memória **H2**.
- **Data Initializer:** Há uma classe `DataInitializer.java` que injeta dados *mock* de teste toda vez que a API inicia. Isso é útil para simular os Makers no mapa.
- **Próximos Passos (Backend):** Quando a equipe estiver pronta, basta atualizar o `application.properties` com as credenciais do **MySQL 8.4** e remover ou adaptar o `DataInitializer`.

### Frontend (`frontend/`)
- **Bibliotecas usadas:** `lucide-react` para ícones premium, `axios` para requisições http, `leaflet` e `react-leaflet` para o mapa interativo. O design premium foi construído **apenas com Vanilla CSS** para máxima performance e controle visual.
- **Fallback de Interface:** Caso a API (`http://localhost:8080`) não responda, o componente `SearchServices.jsx` possui uma camada de *fallback* (mock frontend). Ele intercepta falhas de rede (`catch`) e retorna dados estáticos. Essa camada simula inclusive a filtragem das impressoras, permitindo que os desenvolvedores do front trabalhem no layout de "Busca" independentemente do backend estar online.

## Como Executar
1. **Backend:** Acesse a pasta `api/` e rode `.\mvnw spring-boot:run`. A API sobe na porta `8080`.
2. **Frontend:** Acesse a pasta `frontend/`, instale os pacotes com `npm install` e rode com `npm run dev`. O frontend sobe na porta `5173`.

> Esta documentação reflete o estado atual da sprint. Bom trabalho para a equipe!
