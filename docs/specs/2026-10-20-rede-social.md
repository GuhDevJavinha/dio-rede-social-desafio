# Rede Social: desafio de Padrões de Projeto

Status: aprovada, implementação concluída (tarefas 1 a 5 escritas); testes ainda não executados

## Objetivo
API REST básica de rede social em Spring Boot que demonstra os padrões Singleton, Strategy e Facade, para portfólio (desafio final DIO).

## Critérios de aceite
- Cadastrar e listar usuários.
- Criar posts e listar o feed ordenado por `recentes` (padrão) ou `curtidas`.
- Curtir um post; o mesmo usuário não curte o mesmo post duas vezes.
- Cada padrão está implementado e o README diz onde.
- Swagger UI acessível e README com como rodar.
- Testes unitários da Strategy e da Facade passando.

## Escopo
Usuários, posts, curtidas, H2 + JPA, Swagger/OpenAPI, README, testes unitários.

## Fora de escopo
Autenticação/JWT, seguidores, comentários, Observer, frontend, outros bancos.

## Padrões
- **Singleton:** beans Spring (`@Service`, `@Component`); o README explica que o contêiner garante instância única.
- **Strategy:** `OrdenacaoFeedStrategy` com `OrdenacaoPorRecentes` e `OrdenacaoPorCurtidas`; a escolha é feita pelo parâmetro `ordem`.
- **Facade:** `RedeSocialFacade` é a única porta de entrada dos controllers e orquestra `UsuarioService`, `PostService` e `CurtidaService`.

## Contrato da API
| Método | Rota | Corpo | Sucesso | Erros |
|---|---|---|---|---|
| POST | `/usuarios` | `{nome, email}` | 201 usuário | 400 validação, 409 e-mail duplicado |
| GET | `/usuarios` | - | 200 lista | - |
| POST | `/posts` | `{usuarioId, conteudo}` | 201 post | 400 validação, 404 usuário |
| GET | `/posts?ordem=recentes\|curtidas` | - | 200 lista com `totalCurtidas` | 400 ordem inválida |
| POST | `/posts/{id}/curtidas` | `{usuarioId}` | 201 | 404 post/usuário, 409 já curtiu |

Erros retornam JSON `{mensagem}` via `@RestControllerAdvice`.

## Tarefas
Pacote base: `com.gustavo.dio_desafio.rede_social`. Tarefas em sequência, todas com `dev-backend-java`, exceto a 4.

1. **Base e domínio** (`pom.xml`, `model/`, `repository/`, `service/`): adicionar `spring-boot-starter-validation` e springdoc-openapi compatível com Boot 4.1; entidades `Usuario`, `Post`, `Curtida` (unique usuário+post); services.
2. **Strategy e Facade** (`strategy/`, `facade/`): depende da 1.
3. **API** (`controller/`, `dto/`, `exception/`, `application.yaml` com H2 console): depende da 2.
4. **Testes** (`dev-tester`, `src/test/`): unitários da Strategy e da Facade; depende da 3.
5. **Documentação** (`README.md`): padrões, endpoints, como rodar; depende da 3.

## Divisão em PRs
PR único, pois o projeto é pequeno. Branch: `feat/rede-social` (a confirmar).
