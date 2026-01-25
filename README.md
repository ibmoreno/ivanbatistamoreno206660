# EDITAL DE PROCESSO SELETIVO 001/2026/SEPLAG/SEFAZ/SEDUC/SESP/PJC/PMMT/CBMMT/DETRAN/POLITEC/SEJUS/SEMA/SE

>NÚMERO DE INSCRIÇÃO: 16310</br>
>CARGO: ANALISTA DE TECNOLOGIA DA INFORMAÇÃO</br>
>PERFIL: ENGENHEIRO DA COMPUTAÇÃO - SÊNIOR</br>


## Anexo II - A - Projeto Desenvolvedor Backend

API REST para disponibilizar dados referentes à artistas e álbuns.

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

### Documentação dos Recursos

Veja os recursos da API disponíveis sobre artistas e álbuns na documentação.

* [Open API Definition - JSON Format](http://localhost:8080/v3/api-docs)
* [Open API Definition - Swagger UI](http://localhost:8080/swagger-ui/index.html)

### Recursos de Monitoramento

Recursos disponíveis para monitoramento da aplicação.

* [Health Checks Endpoint](http://localhost:8080/actuator/health)
* [Liveness Endpoint](http://localhost:8080/actuator/health/liveness)
* [Readiness Endpoint](http://localhost:8080/actuator/health/readiness)

