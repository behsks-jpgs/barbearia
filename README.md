# Barbearia — Login Seguro com Spring Boot, Thymeleaf e MongoDB Atlas

Sistema web simples para uma barbearia com cadastro, login e logout de usuários, controle de acesso por perfil e interface com temas configuráveis. Usuários e sessões ficam armazenados no MongoDB Atlas.

Autor: Bruno Dante Nogueira Alves — bruno.dante.nogue@gmail.com

## Funcionalidades

- Cadastro de clientes com validação de nome, e-mail, telefone e senha forte.
- Senhas armazenadas com hash BCrypt (fator de custo 12). A senha em texto puro nunca é gravada.
- Login e logout com Spring Security, proteção CSRF, cookie de sessão `HttpOnly` e troca do ID de sessão no login.
- Três perfis de acesso: **CLIENTE**, **BARBEIRO** e **ADMIN**.
- Sessões HTTP persistidas no MongoDB Atlas (coleção `sessoes`) via Spring Session.
- Interface Thymeleaf com layouts e temas trocáveis por configuração, sem alterar código Java.

## Tecnologias

| Tecnologia | Versão |
| --- | --- |
| Java | 17 ou superior |
| Spring Boot | 3.3.5 |
| Spring Security | 6.3 (gerenciado pelo Spring Boot) |
| Spring Data MongoDB e Spring Session MongoDB | gerenciados pelo Spring Boot |
| Thymeleaf + Layout Dialect + Extras Spring Security 6 | gerenciados pelo Spring Boot |
| Maven | 3.9 ou superior |

## Perfis e rotas

| Rota | Acesso | Conteúdo |
| --- | --- | --- |
| `/`, `/login`, `/cadastro` | Público | Página inicial, login e cadastro |
| `/painel` | Autenticado | Redireciona para a área do perfil |
| `/cliente` | CLIENTE, ADMIN | Dados do cliente e serviços |
| `/barbeiro` | BARBEIRO, ADMIN | Agenda e tabela de serviços |
| `/admin` | ADMIN | Gestão de usuários (perfil e ativação) |

Todo cadastro público cria um **CLIENTE**. O administrador promove contas a **BARBEIRO** ou **ADMIN** pela tela `/admin`. A mudança de perfil vale a partir do próximo login do usuário afetado.

## Estrutura do projeto

```
src/main/java/br/com/barbearia
├── BarbeariaApplication.java
├── config        SecurityConfig, SessionConfig, MongoConfig, AdminInicializador
├── controller    Home, Auth, Cliente, Barbeiro, Admin
├── dto           CadastroForm (validação do formulário)
├── model         Usuario, Perfil, Servico
├── repository    UsuarioRepository
├── service       UsuarioService, UsuarioDetailsService, CatalogoServicos
└── tema          TemaProperties, TemaAtivo

src/main/resources
├── application.properties        configuração lida de variáveis de ambiente
├── application-prod.properties   ajustes de produção (cookie seguro, cache)
├── static/css/base.css           estrutura visual, sem cores fixas
├── static/temas/<nome>/tema.css  cores, fontes e raios de cada tema
└── templates
    ├── layouts/   padrao.html e lateral.html
    ├── fragments/ navegação, rodapé, mensagens e lista de serviços
    ├── auth/      login e cadastro
    ├── cliente/ barbeiro/ admin/
    └── error/     páginas 403 e 404
```

## 1. Configurar o MongoDB Atlas

1. Crie uma conta em <https://www.mongodb.com/atlas> e um cluster gratuito **M0** (sugestão: AWS, região São Paulo).
2. Em **Database Access**, crie um usuário de banco com senha e permissão *Read and write to any database*.
3. Em **Network Access**, adicione o seu IP atual. Evite `0.0.0.0/0`.
4. Em **Connect → Drivers → Java**, copie a connection string. Ela começa com `mongodb+srv://`, o que já ativa TLS.

O banco `barbearia` e as coleções `usuarios` e `sessoes` são criados automaticamente na primeira execução, junto com o índice único de e-mail.

## 2. Configurar o ambiente

```bash
git clone https://github.com/behsks-jpgs/barbearia.git
cd barbearia
cp .env.example .env
```

No Windows (PowerShell), use `Copy-Item .env.example .env`.

Edite o `.env`:

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `MONGODB_URI` | Sim | Connection string do Atlas, com usuário e senha |
| `MONGODB_DATABASE` | Não | Nome do banco (padrão `barbearia`) |
| `ADMIN_EMAIL` / `ADMIN_SENHA` | Na 1ª execução | Cria o primeiro administrador se nenhum existir |
| `TEMA_NOME` | Não | `classico` ou `moderno` |
| `TEMA_LAYOUT` | Não | `padrao` ou `lateral` |
| `BARBEARIA_NOME` / `BARBEARIA_SLOGAN` | Não | Textos exibidos no layout |
| `COOKIE_SEGURO` | Não | `true` quando rodar atrás de HTTPS |

O arquivo `.env` está no `.gitignore` e **nunca** deve ir para o repositório. A aplicação o lê por meio de `spring.config.import=optional:file:.env[.properties]`; variáveis de ambiente do sistema também funcionam e têm prioridade.

Se a senha do banco tiver `@`, `:`, `/` ou `%`, codifique esses caracteres na URI (por exemplo, `@` vira `%40`).

## 3. Executar localmente

```bash
mvn spring-boot:run
```

Acesse <http://localhost:8080>. Entre com o `ADMIN_EMAIL` e a `ADMIN_SENHA` definidos no `.env`, ou crie uma conta de cliente em **Criar conta**.

Para gerar o pacote e rodar como JAR:

```bash
mvn clean package
java -jar target/barbearia-1.0.0.jar
```

Em produção, ative o perfil `prod` (cookie `Secure`, `SameSite=Strict` e cache de templates):

```bash
java -jar target/barbearia-1.0.0.jar --spring.profiles.active=prod
```

## 4. Executar os testes

```bash
mvn test
```

Os testes não precisam do Atlas: validam o formulário de cadastro, o hash de senha no serviço e as regras de acesso de cada perfil com MockMvc.

## Como funciona a integração com o MongoDB Atlas

- **Conexão:** o Spring Boot lê `spring.data.mongodb.uri` da variável `MONGODB_URI`. O esquema `mongodb+srv` resolve os nós do cluster por DNS e usa TLS por padrão.
- **Usuários:** a classe `Usuario` é um `@Document` da coleção `usuarios`. O campo `email` tem índice único, criado automaticamente (`spring.data.mongodb.auto-index-creation=true`). A data de criação é preenchida por auditoria (`@EnableMongoAuditing`).
- **Sessões:** `@EnableMongoHttpSession` substitui a sessão em memória do Tomcat por documentos na coleção `sessoes`, com expiração de 30 minutos de inatividade. O cookie `SESSION` guarda apenas o ID. Assim, a sessão sobrevive a reinícios da aplicação e funciona com mais de uma instância.
- **Autenticação:** `UsuarioDetailsService` busca o usuário pelo e-mail no Atlas e entrega ao Spring Security o hash e o perfil. A comparação da senha é feita pelo `BCryptPasswordEncoder`.

## Temas e layouts

A aparência é separada da lógica em três camadas:

1. **Layout** (`templates/layouts/*.html`): estrutura da página. Todas as páginas usam `layout:decorate="~{${@tema.layout}}"`, então o layout ativo vem da configuração.
2. **Estrutura visual** (`static/css/base.css`): espaçamentos, grades e componentes, sempre usando variáveis CSS.
3. **Tema** (`static/temas/<nome>/tema.css`): define somente as variáveis (cores, fontes, raios).

Para criar um tema novo:

1. Copie `static/temas/moderno` para `static/temas/meutema`.
2. Ajuste as variáveis em `tema.css`.
3. Defina `TEMA_NOME=meutema` no `.env` e reinicie.

Para criar um layout novo, copie `templates/layouts/padrao.html` para `templates/layouts/meulayout.html`, mantenha o `layout:fragment="conteudo"` e defina `TEMA_LAYOUT=meulayout`. Nomes de tema e layout aceitam apenas letras minúsculas, números e hífen.

## Gitflow

| Branch | Uso |
| --- | --- |
| `main` | Somente versões publicadas, marcadas com tags (`v1.0.0`) |
| `develop` | Integração das funcionalidades prontas |
| `feature/*` | Uma funcionalidade por branch, criada a partir de `develop` |
| `release/*` | Preparação de versão, mesclada em `main` e `develop` |
| `hotfix/*` | Correção urgente a partir de `main` |

```bash
git flow init -d
git flow feature start nome-da-funcionalidade
git flow feature finish nome-da-funcionalidade
git flow release start 1.1.0
git flow release finish 1.1.0
git push origin main develop --tags
```