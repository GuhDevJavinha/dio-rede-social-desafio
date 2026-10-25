# Rede Social

API REST básica de rede social em Spring Boot, feita como desafio final da DIO para demonstrar os padrões de projeto **Singleton**, **Strategy** e **Facade**.

Funcionalidades: cadastro e listagem de usuários, criação de posts, feed ordenado por recentes ou curtidas, curtidas (um usuário curte cada post uma única vez) e reserva de salas de reunião sem conflito de horário.

## Tecnologias

- Java 21
- Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Validation)
- H2 (banco em memória)
- springdoc-openapi (Swagger UI)
- Lombok
- JUnit 5 e Mockito

## Como rodar

Pré-requisito: JDK 21.

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`.

Endereços após subir a aplicação (porta padrão 8080):

- Swagger UI: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:rede-social`, usuário `sa`, senha em branco)

## Padrões de projeto

| Padrão | Onde está | Como funciona |
|---|---|---|
| **Singleton** | Todos os `@Service`, `@Component` e `@RestController` | O contêiner do Spring cria uma única instância de cada bean e a injeta onde for necessária. |
| **Strategy** | `strategy/OrdenacaoFeedStrategy`, `OrdenacaoPorRecentes`, `OrdenacaoPorCurtidas` | Cada estratégia define uma forma de ordenar o feed. A `RedeSocialFacade` recebe todas as estratégias e escolhe a correta pelo parâmetro `ordem`. |
| **Facade** | `facade/RedeSocialFacade` | Única porta de entrada dos controllers. Orquestra `UsuarioService`, `PostService` e `CurtidaService`. A `ReservaFacade` faz o mesmo para salas e reservas. |

## Endpoints

| Método | Rota | Corpo | Sucesso | Erros |
|---|---|---|---|---|
| POST | `/usuarios` | `{"nome", "email"}` | 201 usuário | 400 validação, 409 e-mail duplicado |
| GET | `/usuarios` | - | 200 lista de usuários | - |
| POST | `/posts` | `{"usuarioId", "conteudo"}` | 201 post | 400 validação, 404 usuário não encontrado |
| GET | `/posts?ordem=recentes\|curtidas` | - | 200 feed com `totalCurtidas` | 400 ordem inválida |
| POST | `/posts/{id}/curtidas` | `{"usuarioId"}` | 201 | 404 post ou usuário, 409 já curtiu |

### Salas e reservas

Ao subir a aplicação são criadas 2 salas (`Sala Azul` e `Sala Verde`) se a tabela estiver vazia.

| Método | Rota | Corpo / Query | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/salas` | - | 200 lista de salas | - |
| GET | `/salas/{id}/reservas?data=YYYY-MM-DD` | `data` obrigatório | 200 reservas que tocam o dia, ordenadas por `inicio` | 400 `data` ausente ou inválida, 404 sala |
| GET | `/usuarios/{id}/reservas` | - | 200 reservas do usuário, ordenadas por `inicio` | 404 usuário |
| POST | `/reservas` | `{"salaId", "usuarioId", "titulo", "inicio", "fim"}` | 201 reserva com `Location` | 400 validação ou regra, 404 sala ou usuário, 409 conflito de horário |
| DELETE | `/reservas/{id}?usuarioId=X` | `usuarioId` obrigatório | 204 | 400 `usuarioId` ausente, 403 não é o responsável, 404 reserva |

Regras do `POST /reservas`, nesta ordem: campos obrigatórios (400), sala e usuário existentes (404), `fim` posterior a `inicio`, `inicio` no futuro e duração de até 4 horas (400) e ausência de sobreposição na mesma sala (409). Os intervalos são `[inicio, fim)`, então reservas encostadas são aceitas.

Os erros retornam JSON no formato `{"mensagem": "..."}`, tratados pelo `GlobalExceptionHandler`. Se `ordem` não for informada, o feed usa `recentes`.

### Exemplos

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana", "email": "ana@email.com"}'
```

```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "conteudo": "Olá, rede social!"}'
```

```bash
curl "http://localhost:8080/posts?ordem=curtidas"
```

```bash
curl http://localhost:8080/salas
```

```bash
curl -X POST http://localhost:8080/reservas   -H "Content-Type: application/json"   -d '{"salaId": 1, "usuarioId": 1, "titulo": "Daily", "inicio": "2026-10-22T10:00:00", "fim": "2026-10-22T11:00:00"}'
```

```bash
curl "http://localhost:8080/salas/1/reservas?data=2026-10-22"
```

```bash
curl http://localhost:8080/usuarios/1/reservas
```

```bash
curl -X DELETE "http://localhost:8080/reservas/1?usuarioId=1"
```

## Testes

```bash
./mvnw test
```

Os testes unitários cobrem as estratégias de ordenação (`src/test/java/.../strategy`), a `RedeSocialFacade` e a `ReservaFacade` (`src/test/java/.../facade`) e o `ReservaService` (`src/test/java/.../service`), com `Clock` fixo.

Os testes de integração dos endpoints de salas e reservas ficam em `src/test/java/.../integration` (`@SpringBootTest` + MockMvc, H2 em memória, sem Docker). Cada teste cria seu próprio usuário e sala e usa datas distintas em 2040, então não dependem de ordem de execução.
