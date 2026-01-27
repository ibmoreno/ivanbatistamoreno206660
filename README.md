# EDITAL DE PROCESSO SELETIVO 001/2026/SEPLAG/SEFAZ/SEDUC/SESP/PJC/PMMT/CBMMT/DETRAN/POLITEC/SEJUS/SEMA/SE

>NÚMERO DE INSCRIÇÃO: 16310</br>
>CARGO: ANALISTA DE TECNOLOGIA DA INFORMAÇÃO</br>
>PERFIL: ENGENHEIRO DA COMPUTAÇÃO - SÊNIOR</br>


## Anexo II - A - Projeto Desenvolvedor Backend

API REST para disponibilizar dados referentes à artistas e álbuns.

### Pre-requisitos

- [Java 21 (openjdk-21.0.2)](https://jdk.java.net/archive/)
- [Maven (3.9.11)](https://maven.apache.org/docs/3.9.11/release-notes.html)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Mise](https://mise.jdx.dev/dev-tools/)

### Execução em docker compose

Para executar a API, execute o seguinte comando:

```bash
docker compose up -d
```

Para verificar os logs dos serviços levantados.
```bash
docker compose logs
```

### Variáveis de Ambiente

As variáveis de ambiente necessárias para a execução da API estão no arquivo .env

| Variável | Descrição                         |
| --- |-----------------------------------|
| JDBC_POSTGRES_URL| URL de conexão com banco de dados |
| JDBC_POSTGRES_USER| Usuário de conexão com banco de dados |
| JDBC_POSTGRES_PASSWORD| Senha de conexão com banco de dados |
| HIBERNATE_SHOW_SQL| Mostra as queries SQL no console |
| HIBERNATE_FORMAT_SQL| Formata as queries SQL no console |
| SPRINGDOC_API_DOCS_ENABLED| Habilita a documentação da API |
| SPRINGDOC_SWAGGER_UI_ENABLED| Habilita a interface de documentação da API |
| POSTGRES_DB| Nome do banco de dados |
| POSTGRES_USER| Usuário de conexão com banco de dados |
| POSTGRES_PASSWORD| Senha de conexão com banco de dados |


## Estrutura do Projeto

Estrutura de dados proposta:

![DER (Diagrama Entidade-Relacionamento) ](./assets/DER.png)

Relacionamento Artista X Álbum é **N:N**, Exemplos comuns no mundo real:
- Artista convidado em um álbum
- Álbuns colaborativos
- Bandas + artista solo no mesmo álbum
- Participação especial

Logo um artista pode participar de vários álbuns e um álbum pode ter vários artistas.</br>
Para resolver a redundância de dados e inconsistência na sua relação e representação foi criado 
uma entidade associativa com nome **artista_album**.

## Notificação via websocket

Notificação de eventos quando um novo album é cadastrado: [acompanhe aqui](http://localhost:8080).

![WEBSOCKET](./assets/web-socket-notification.png)

## Documentação dos Recursos

Veja os recursos da API disponíveis sobre artistas e álbuns na documentação.

* [Open API Definition - JSON Format](http://localhost:8080/v3/api-docs)
* [Open API Definition - Swagger UI](http://localhost:8080/swagger-ui/index.html)


## Recursos de Monitoramento

Recursos disponíveis para monitoramento da aplicação.

* [Health Checks Endpoint](http://localhost:8080/actuator/health)
* [Liveness Endpoint](http://localhost:8080/actuator/health/liveness)
* [Readiness Endpoint](http://localhost:8080/actuator/health/readiness)

## Referências e Documentações

Para referência adicional do framework utilizado para desenvolvimento, considere as seguintes seções:

* [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#using.devtools)
* [Spring Boot Actuator Web API](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/)
* [Validation](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#io.validation)
* [OpenAPI Specification](https://swagger.io/specification/)
* [Flyway migration](https://documentation.red-gate.com/home)

## Histórico da Versão

Foi implementado todos os requisitos descritos no edital, em destaque:

- Implementação de tratamento de erros personalizado.
- Swagger para documentação da API.
- Health Checks para monitoramento da aplicação, Liveness e Readiness.
- Uso de Flyway para controle de versão do banco de dados.
- Testes unitários com testcontainer para testar a persistência do banco de dados.

