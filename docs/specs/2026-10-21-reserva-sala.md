# Reserva de sala de reunião

Status: concluída (2026-10-21) — PR único entregue

## Objetivo
Permitir que qualquer usuário cadastrado reserve uma sala de reunião, sem conflito de horário.

## Critérios de aceite
- Ao subir a aplicação existem salas pré-cadastradas (seed), listadas em `GET /salas`.
- Um usuário cadastrado cria uma reserva informando sala, título, início e fim.
- Reserva sobreposta na mesma sala é rejeitada com 409; intervalos encostados (`[inicio, fim)`) são aceitos.
- Reserva no passado, com `fim <= inicio` ou duração acima de 4h é rejeitada com 400.
- Só quem criou a reserva consegue cancelá-la (outro usuário recebe 403).
- É possível listar as reservas de uma sala num dia e as reservas de um usuário.

## Escopo
- Entidades `Sala` e `Reserva`, repositories, services, `ReservaFacade`, controllers.
- Seed de 2 salas na inicialização (ex.: `ApplicationRunner` que insere só se a tabela estiver vazia).
- Erros pelo `GlobalExceptionHandler` existente, formato `{ "mensagem": "..." }`.
- Testes unitários (facade/service) e de integração dos endpoints.
- README com os novos endpoints e exemplos curl.

## Fora de escopo
- CRUD de salas, edição de reserva, participantes, descrição.
- Busca de salas livres, horário comercial, antecedência máxima, granularidade de horário.
- Autenticação e frontend.

## Modelo
- `Sala`: `id`, `nome` (único, obrigatório), `capacidade` (obrigatório, > 0), `localizacao` (opcional).
- `Reserva`: `id`, `sala` (ManyToOne), `responsavel` (ManyToOne `Usuario`), `titulo` (obrigatório), `inicio`, `fim` (`LocalDateTime`).

## Contrato da API

| Método | Rota | Corpo / Query | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/salas` | — | 200 `[SalaResponse]` | — |
| GET | `/salas/{id}/reservas` | `?data=YYYY-MM-DD` (obrigatório) | 200 `[ReservaResponse]` ordenado por `inicio` | 404 sala |
| GET | `/usuarios/{id}/reservas` | — | 200 `[ReservaResponse]` ordenado por `inicio` | 404 usuário |
| POST | `/reservas` | `ReservaRequest` | 201 `ReservaResponse` | 400 validação/regra, 404 sala ou usuário, 409 conflito |
| DELETE | `/reservas/{id}` | `?usuarioId=X` (obrigatório) | 204 | 403 não é o responsável, 404 reserva |

`GET /salas/{id}/reservas?data=D` retorna reservas que tocam o dia D (`inicio < D+1 00:00` e `fim > D 00:00`).

```json
// ReservaRequest
{ "salaId": 1, "usuarioId": 1, "titulo": "Daily", "inicio": "2026-10-22T10:00:00", "fim": "2026-10-22T11:00:00" }

// SalaResponse
{ "id": 1, "nome": "Sala Azul", "capacidade": 8, "localizacao": "2º andar" }

// ReservaResponse
{ "id": 1, "salaId": 1, "salaNome": "Sala Azul", "usuarioId": 1, "usuarioNome": "Ana", "titulo": "Daily",
  "inicio": "2026-10-22T10:00:00", "fim": "2026-10-22T11:00:00" }
```

Regras do POST, em ordem: campos obrigatórios (400) → sala e usuário existem (404) → `fim > inicio`, `inicio` no futuro, duração ≤ 4h (400) → sem sobreposição na sala (409). Sobreposição: existe reserva da sala com `inicio < novo.fim` e `fim > novo.inicio`.

## Tarefas
1. **Backend** — `dev-backend-java`: modelo, seed, repositories, services, `ReservaFacade`, controllers, DTOs, exceções novas (conflito 409, acesso negado 403) no `GlobalExceptionHandler`, README. Área: `src/main/java/.../rede_social/**`, `README.md`.
2. **Testes e validação** — `dev-tester` (depende de 1): testes unitários da `ReservaFacade`/services e de integração dos 5 endpoints cobrindo cada critério de aceite; rodar a suíte completa. Área: `src/test/java/**`.

## PRs
PR único (demanda pequena).
