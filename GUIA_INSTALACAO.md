# Guia de Instalação — PrintAI

Este guia explica como rodar o projeto **PrintAI** em uma máquina nova, do zero.

---

## 1. Pré-requisitos

Instale antes de começar:

1. **Docker Desktop** — é o único requisito. Banco de dados, backend e frontend rodam todos em containers.
   - https://www.docker.com/products/docker-desktop/
   - Após instalar, abra o Docker Desktop e deixe ele rodando em segundo plano.

> Para conferir se está tudo instalado corretamente, abra um terminal e rode:
> ```
> docker --version
> ```
> O comando deve retornar uma versão (não pode dar erro de "comando não encontrado").

---

## 2. Baixando o projeto

[⬇️ Baixar PrintAI (.zip)](https://github.com/xablavskk/PRINTAI-DEV/archive/refs/heads/main.zip)

Extraia o arquivo em uma pasta de sua preferência.

---

## 3. Rodando o projeto

### Windows

Dentro da pasta do projeto, dê **dois cliques** no arquivo `start.bat`.

### Mac / Linux

Abra um terminal na pasta do projeto e rode:

```bash
./start.sh
```

(Se der erro de permissão, rode antes: `chmod +x start.sh`)

Em ambos os casos, o script vai construir e subir 3 containers via Docker: banco de dados (MySQL), backend (Spring Boot, porta `8080`) e frontend (React, porta `5173`).

---

## 4. Acessando a aplicação

Depois que tudo subir (pode levar 1-2 minutos na primeira vez, pois o Docker precisa construir as imagens), acesse no navegador:

👉 **http://localhost:5173**

---

## 5. Usuários de teste

O banco já vem populado com usuários de teste (definidos em `api/src/main/resources/db/seed.sql`):

| Perfil | Email | Senha |
|---|---|---|
| Administrador | admin@printai.com | admin123 |
| Cliente | cliente@printai.com | senha123 |
| Maker | maker@printai.com | senha123 |
| Maker | ana@printai.com | senha123 |

---

## 6. Encerrando

Para parar todos os containers (banco, backend e frontend):

```bash
docker compose down
```

---

## 7. Problemas comuns

- **"docker: command not found" / Docker Desktop não abre:** verifique se o Docker Desktop foi instalado e está em execução (ícone na bandeja do sistema).
- **Porta 3306, 8080 ou 5173 já em uso:** feche qualquer outro programa que esteja usando essas portas (outro MySQL, XAMPP, etc.) e tente novamente.
- **Quer ver o que está acontecendo nos containers:** rode `docker compose logs -f` na pasta do projeto.
