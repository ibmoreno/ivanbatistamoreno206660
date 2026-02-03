# 📌 PROCESSO SELETIVO SEPLAG-MT 2026

**NÚMERO DE INSCRIÇÃO:** 16310</br>
**CARGO:** ANALISTA DE TECNOLOGIA DA INFORMAÇÃO</br>
**PERFIL:** ENGENHEIRO DA COMPUTAÇÃO - SÊNIOR</br>

## 📦 Descrição do Projeto 

Este projeto é uma **API REST desenvolvida em Java com Spring Boot** que provê endpoints para gerenciamento e consultas de recursos relacionados a artistas e álbums.

O projeto foi desenvolvido seguindo boas práticas de arquitetura, organização de código e facilidade de execução, incluindo suporte a Docker.

---

## 🔧 Tecnologias Utilizadas

- Java SDK
- Maven
- Lombok
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Actuator
- PostgreSQL
- MinIO
- Docker e Docker Compose

---

## 🧱 Pré‑requisitos

Para executar o projeto localmente, é necessário ter instalado:

- [Java 21 (openjdk-21.0.2)](https://jdk.java.net/archive/)
- [Maven (3.9.11)](https://maven.apache.org/docs/3.9.11/release-notes.html)
- [Docker Desktop](https://www.docker.com/)
- [Mise tools (opcional)](https://mise.jdx.dev/dev-tools/)

---

## ▶️ Executando a Aplicação Localmente

Clone o repositório:

```bash
git clone https://github.com/ibmoreno/ivanbatistamoreno206660.git
cd ivanbatistamoreno206660
```

Certifique‑se de que o serviço Docker esteja em execução e inicie as imagens dos serviços dependentes da aplicação configuradas no docker-compose.yml:
```bash
docker compose up -d minio postgres
```
> ***NOTA: Os serviços, Banco de Dados PostreSQL e MinIO devem estar rodando.***

Compile e execute a aplicação:

```bash
mvn clean install
mvn spring-boot:run
```
A aplicação ficará disponível em:

```
http://localhost:8080
```

---

## 🧪 Executando os Testes

Para executar os testes automatizados:

```bash
mvn test
```

## 🐳 Executando com Docker Compose

O projeto já possui configuração para execução via Docker.

### Passos:

1. Certifique‑se de que o Docker esteja em execução.
2. No diretório do projeto, execute:

```bash
docker compose up --build
```
Este comando irá:

- Criar a imagem da aplicação;
- Subir o container do PostgreSQL e MinIO;
- Iniciar a API automaticamente.

A aplicação estará disponível em:

```
http://localhost:8080
```

Para verificar os logs dos serviços levantados.
```bash
docker compose logs
```

Para parar a execução dos containers:

```bash
docker compose down
```

---

## ⚙️ Variáveis de Ambiente

O projeto utiliza variáveis de ambiente definidas no arquivo `.env`.

Principais variáveis:

| Variável                     | Descrição                                         |
|------------------------------|---------------------------------------------------|
| JDBC_POSTGRES_URL            | URL de conexão com banco de dados                 |
| JDBC_POSTGRES_USER           | Usuário de conexão com banco de dados             |
| JDBC_POSTGRES_PASSWORD       | Senha de conexão com banco de dados               |
| POSTGRES_URL                 | URL conexão com o banco de dados para docker-compose |
| POSTGRES_DB                  | Nome do banco de dados para docker-compose        |
| POSTGRES_USER                | Usuário de conexão com banco de dados para docker-compose |
| POSTGRES_PASSWORD            | Senha de conexão com banco de dados para docker-compose |
| MINIO_USER                   | Usuário de acesso ao console do MinIO             |
| MINIO_PASSWORD               | Senha de acesso ao console do MinIO               |
| MINIO_ENDPOINT               | Endereço do MinIO                                 |
| MINIO_BUCKET_NAME            | Nome do bucket do MinIO                           |
| MINIO_ACCESS_KEY             | Chave de acesso do MinIO                          |
| MINIO_SECRET_KEY             | Chave secreta do MinIO                            |


---

## 🔐 Segurança e Autenticação

A aplicação implementa segurança baseada em tokens (JWT) para proteger endpoints sensíveis e garantir acesso apenas a usuários autenticados.

Alguns endpoints exigem que o cliente esteja autenticado e envie um token de acesso (Access Token) válido no header da requisição.

---

## 🔑 Processo de Autenticação

Para autenticar um usuário, é necessário realizar uma chamada ao endpoint de login informando as credenciais válidas.

### Credenciais de exemplo:
- **Username:** "user"
- **Senha:** "user123"

### Endpoint de autenticação
```shel
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user",
    "password": "user123"
}'
```
### Resposta de sucesso
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdmFuYmF0aXN0YW1vcmVubzIwNjY2MC1hcGkiLCJzdWIiOiJ1c2VyIiwiZXhwIjoxNzcwMDgxNjYwLCJpYXQiOjE3NzAwODEzNjAsInVzZXJJZCI6IjEiLCJyb2xlcyI6WyJST0xFX1VTRVJfUk9MRSJdfQ.269EGnibbQ9M3d4dT7qR5G1YSozl4Xr1spXoeVhgc-4",
  "refreshToken": "0b953971-802f-4445-a2cd-4ffd5ebc8d17"
}
```

A autenticação retorna dois tipos de tokens, cada um com uma finalidade específica:

### 🔹 Access Token (JWT)
- Utilizado para acessar os endpoints protegidos da API
- Possui tempo de expiração de 5 minutos
- Deve ser enviado no header Authorization em todas as chamadas protegidas

**Exemplo de uso:**
```http
Authorization: Bearer <accessToken>
```

### 🔹 Refresh Token
- Utilizado para renovar o Access Token quando ele expirar
- Possui tempo de expiração de 30 minutos
- Evita a necessidade de o usuário se autenticar novamente com username e senha

O Refresh Token pode ser enviado para um endpoint específico de renovação de token (ex: ***/api/v1/auth/refresh***) 
para obtenção de um novo Access Token válido.

---

## 🛡️ Endpoints Protegidos
- Endpoints protegidos exigem um Access Token válido
- Caso o token esteja expirado ou inválido, a API retornará erro 401 – Unauthorized
- Endpoints públicos (como autenticação e health checks) não exigem token

---

## 📮 Collections do Postman

Coleções do Postman disponíveis para importação, na pasta `collections/`, permitindo testar e validar os endpoints da API de forma prática.

### Como utilizar:

1. Abra o Postman
2. Clique em **Import**
3. Selecione os arquivos da pasta `collections`

As coleções permitem testar:

- Autenticação (Login)
- Refresh Token
- Criação de álbuns com artistas
- Upload de capa do álbum
- Consulta de álbuns
- Consulta de artistas
- Importar Regionais

--- 

## 🧭 Swagger – OpenAPI Definition

Documentação interativa da API baseada em OpenAPI (Swagger), permitindo visualizar e testar os endpoints diretamente pela interface.

* [Open API Definition - JSON Format](http://localhost:8080/v3/api-docs)
* [Open API Definition - Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

## 🔔 Notificação via websocket

Notificação por **websocket** quando um novo album é cadastrado. Para testar inicie a aplicação, abra o navegador e acesse o endereço
[http://localhost:8080](http://localhost:8080), em seguida inclusa um novo registro de album.

 
### Exemplo da requisição:
```shell
curl --location 'http://localhost:8080/api/v1/album' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <accessToken>' \
--data '{
    "titulo": "Black Album",
    "artistas": [
        {
            "nome": "Metallica"
        }
    ]
}'
```
> **NOTA: Você pode utilizar as collections do POSTMAN.**

### Página da Notificação onde receberá os dados do novo Album cadastrado:
![WEBSOCKET](./assets/web-socket-notification.png)

---

## 🗃️ Integração com MinIO

A aplicação integra-se ao MinIO para armazenamento de objetos. O console administrativo pode ser acessado em [http://localhost:9001](http://localhost:9001), utilizando **usuário** e **senha** configurados no ambiente.

---
## 📈 Recursos de Monitoramento

O **Spring Boot Actuator** serve para fornecer recursos prontos para produção, permitindo monitorar, gerenciar e interagir
com aplicações Java em execução através de endpoints HTTP. Estes endpoints permitem a coleta de métricas, saúde (health),
e informações detalhadas do ambiente, sendo essencial para microsserviços e integração com ferramentas como Prometheus,
Grafana e Kubernetes:

* [Health Checks Endpoint](http://localhost:8080/actuator/health)
* [Liveness Endpoint](http://localhost:8080/actuator/health/liveness)
* [Readiness Endpoint](http://localhost:8080/actuator/health/readiness)

---

## ✅ Histórico da Versão 1.0

### 🛠️ Foram implementados todos os requisitos descritos

- Endpoints POST, PUT e GET para album/artista.
- Consultas Paginadas para Album e Artista
- Endpoint para upload de imagem do album.
- Endpoints para importação e sincronização de regionais.
- Health Checks, Liveness e Readiness.
- Swagger para documentação da API.
- Uso de Flyway para controle de versão do banco de dados.
- Upload de capa do álbum e Armazenamentos da imagem no MinIO.
- Testes unitários
- Notificação via websocket.
- Autenticação via JWT.
- Refresh Token para autenticação.
- Rate Limiting para limitar o número de requisições por usuário.

### 🛠️ Melhorias adicionais
- Implementação de tratamento de erros personalizado.
- Uso de testcontainer para testes de persistência com banco de dados PostgreSQL.

## 🧱 Estrutura do projeto (/src)

A estrutura do projeto a partir da pasta /src foi organizada seguindo alguns dos princípios da **Clean Architecture**,
com foco em **separação de responsabilidades, baixo acoplamento e facilidade de manutenção e testes**.

O código foi dividido de forma que **cada classe possua uma única responsabilidade**, principalmente na camada de use 
cases, onde cada caso de uso representa uma ação clara do domínio.

### 📂 Visão Geral da Estrutura

```
src/
├── main/
│   ├── java/
│   │   └── br.com....
│   │       ├── application/
│   │       │   └── usecase/
│   │       ├── domain/
│   │       ├── infra/
│   │       └── presentation/ (API)
│   └── resources/
└── test/
```
>A organização acima reflete uma separação clara entre **camadas de negócio, infraestrutura e interfaces de entrada**.

### 🧠 Camada de Casos de Uso (application/usecase)

A camada de **use cases** implementa os **casos de uso da aplicação**, seguindo fortemente o princípio da 
**Responsabilidade Única (SRP)**.

- Cada classe nesta camada:
- Representa uma única ação do sistema
- Orquestra regras de negócio
- Coordena entidades e repositórios

### 🌐 Camada de Interface / API (api)

Esta camada é responsável pela **exposição da aplicação ao mundo externo**, geralmente via REST.

Responsabilidades:

- Controllers REST
- Mapeamento de requisições HTTP
- Validação de dados de entrada
- Delegação da lógica para os use cases

> ⚠️ Importante:
> Os controllers não implementam regras de negócio, apenas orquestram chamadas para os casos de uso.

### 🏗️ Camada de Infraestrutura (infra)

A camada de infraestrutura contém as implementações técnicas necessárias para a aplicação funcionar.

Inclui, por exemplo:

- Implementações de repositórios (JPA/Hibernate)
- Configurações de banco de dados
- Configurações de segurança
- Integrações externas

### 🧪 Camada de Testes (/src/test)

A pasta de testes segue a mesma organização do código principal, permitindo:

- Testes unitários de use cases
- Testes de integração para controllers e infraestrutura

### 🛢️ Banco de Dados

A aplicação utiliza **PostgreSQL** como banco de dados relacional para persistência das informações, configurado via Docker Compose.


Estrutura de schema de dados proposta:

![DER (Diagrama Entidade-Relacionamento) ](./assets/DER.png)

Relacionamento Artista X Álbum é **N:N**, Exemplos comuns no mundo real:
- Artista convidado em um álbum
- Álbuns colaborativos
- Bandas + artista solo no mesmo álbum
- Participação especial

Logo um artista pode participar de vários álbuns e um álbum pode ter vários artistas.</br>


---

## 💡 Referências e Documentações

Para referência adicional do framework utilizado para desenvolvimento, considere as seguintes seções:

* [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#using.devtools)
* [Spring Boot Actuator Web API](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/)
* [Validation](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#io.validation)
* [OpenAPI Specification](https://swagger.io/specification/)
* [Flyway migration](https://documentation.red-gate.com/home)
