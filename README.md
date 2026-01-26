# EDITAL DE PROCESSO SELETIVO 001/2026/SEPLAG/SEFAZ/SEDUC/SESP/PJC/PMMT/CBMMT/DETRAN/POLITEC/SEJUS/SEMA/SE

>NÚMERO DE INSCRIÇÃO: 16310</br>
>CARGO: ANALISTA DE TECNOLOGIA DA INFORMAÇÃO</br>
>PERFIL: ENGENHEIRO DA COMPUTAÇÃO - SÊNIOR</br>


## Anexo II - A - Projeto Desenvolvedor Backend

API REST para disponibilizar dados referentes à artistas e álbuns.

### Execução em docker compose

Para executar a API, execute o seguinte comando:

```bash
docker compose up -d
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

## Documentação dos Recursos

Veja os recursos da API disponíveis sobre artistas e álbuns na documentação.

* [Open API Definition - JSON Format](http://localhost:8080/v3/api-docs)
* [Open API Definition - Swagger UI](http://localhost:8080/swagger-ui/index.html)

### Exemplos de Requisições:

- Listar Albuns

```shell
curl --location 'http://localhost:8080/api/v1/album?size=10&page=0&sort=titulo%2CDESC'
```

## Recursos de Monitoramento

Recursos disponíveis para monitoramento da aplicação.

* [Health Checks Endpoint](http://localhost:8080/actuator/health)
* [Liveness Endpoint](http://localhost:8080/actuator/health/liveness)
* [Readiness Endpoint](http://localhost:8080/actuator/health/readiness)

