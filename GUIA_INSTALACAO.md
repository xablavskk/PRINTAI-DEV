# Guia de Instalação — PrintAI

Este guia explica como rodar o projeto **PrintAI** em uma máquina nova, do zero.

---

## 1. Pré-requisitos

Instale antes de começar:

1. **Docker Desktop** — usado para subir o banco de dados MySQL.
   - https://www.docker.com/products/docker-desktop/
   - Após instalar, abra o Docker Desktop e deixe ele rodando em segundo plano.

2. **JDK 21** (Java Development Kit).
   - https://adoptium.net/ (escolha a versão 21 - LTS)

3. **Node.js 18 ou superior** (inclui o `npm`).
   - https://nodejs.org/ (recomendado baixar a versão LTS)

> Para conferir se está tudo instalado corretamente, abra um terminal e rode:
> ```
> docker --version
> java --version
> node --version
> npm --version
> ```
> Os 4 comandos devem retornar uma versão (não pode dar erro de "comando não encontrado").

---

## 2. Baixando o projeto

Baixe o arquivo `.zip` do projeto (disponível na seção **Releases** do repositório no GitHub) e extraia em uma pasta de sua preferência.

---

## 3. Rodando o projeto

### Windows

Dentro da pasta do projeto, dê **dois cliques** no arquivo `start.bat`.

Ele vai:
1. Subir o banco de dados MySQL (Docker).
2. Abrir uma janela rodando o backend (Spring Boot) na porta `8080`.
3. Abrir uma janela rodando o frontend (Vite/React) na porta `5173`.

### Mac / Linux

Abra um terminal na pasta do projeto e rode:

```bash
./start.sh
```

(Se der erro de permissão, rode antes: `chmod +x start.sh`)

---

## 4. Acessando a aplicação

Depois que tudo subir (pode levar 1-2 minutos na primeira vez, pois o Docker baixa a imagem do MySQL e o `npm install` instala as dependências do frontend), acesse no navegador:

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

- **Windows:** feche as janelas do backend e do frontend.
- **Mac/Linux:** pressione `Ctrl + C` no terminal onde rodou o `start.sh`.

O banco de dados (Docker) continua rodando em segundo plano. Para pará-lo:

```bash
docker compose down
```

---

## 7. Problemas comuns

- **"docker: command not found" / Docker Desktop não abre:** verifique se o Docker Desktop foi instalado e está em execução (ícone na bandeja do sistema).
- **Porta 3306, 8080 ou 5173 já em uso:** feche qualquer outro programa que esteja usando essas portas (outro MySQL, XAMPP, etc.) e tente novamente.
- **Erro ao rodar `mvnw`/`mvnw.cmd`:** confirme que o `JDK 21` está instalado e configurado no PATH (`java --version`).
