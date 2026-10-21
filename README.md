# Rede Social

API REST básica de rede social em Spring Boot, feita como desafio final da DIO para demonstrar os padrões de projeto **Singleton**, **Strategy** e **Facade**.

Funcionalidades: cadastro e listagem de usuários, criação de posts, feed ordenado por recentes ou curtidas e curtidas (um usuário curte cada post uma única vez).

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
| **Facade** | `facade/RedeSocialFacade` | Única porta de entrada dos controllers. Orquestra `UsuarioService`, `PostService` e `CurtidaService`. |

## Endpoints

| Método | Rota | Corpo | Sucesso | Erros |
|---|---|---|---|---|
| POST | `/usuarios` | `{"nome", "email"}` | 201 usuário | 400 validação, 409 e-mail duplicado |
| GET | `/usuarios` | - | 200 lista de usuários | - |
| POST | `/posts` | `{"usuarioId", "conteudo"}` | 201 post | 400 validação, 404 usuário não encontrado |
| GET | `/posts?ordem=recentes\|curtidas` | - | 200 feed com `totalCurtidas` | 400 ordem inválida |
| POST | `/posts/{id}/curtidas` | `{"usuarioId"}` | 201 | 404 post ou usuário, 409 já curtiu |

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

## Testes

```bash
./mvnw test
```

Os testes unitários cobrem as estratégias de ordenação (`src/test/java/.../strategy`) e a `RedeSocialFacade` (`src/test/java/.../facade`).
