# Demo Front-End Multipart Upload

Projeto exemplo em Node.js puro para testar o upload multipart de video direto no MinIO usando os endpoints do backend Spring.

## Como executar

```bash
cd frontend/minio-multipart-demo
npm start
```

Abra:

```text
http://localhost:3000
```

## O que a tela faz

- autentica no endpoint `/api/v1/auth/login`
- inicia o multipart upload em `/api/v1/album/{id}/video/multipart`
- envia cada parte diretamente para o MinIO com `PUT` nas URLs assinadas
- conclui o upload em `/api/v1/album/{id}/video/multipart/complete`
- permite abortar o upload em `/api/v1/album/{id}/video/multipart`

## Configuração padrão usada pela tela

- API Base URL: `http://localhost:8080`
- usuário: `user`
- senha: `user123`
- chunk padrão: `10 MB`

## Observações importantes

- O MinIO precisa ter CORS liberado para o front conseguir ler o header `ETag`.
- Em multipart upload compatível com S3, cada parte deve ter no mínimo `5 MB`, exceto a última.
- Se a API estiver protegida por JWT, o login da tela já guarda o `accessToken` em memória.
