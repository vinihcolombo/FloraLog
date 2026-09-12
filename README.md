# FloraLog — Biblioteca Digital de Plantas

Aplicação web que centraliza informações sobre espécies vegetais (iluminação, rega,
temperatura, dificuldade de cultivo e orientações de cuidado) em um único lugar,
com CRUD completo das plantas cadastradas.

Projeto desenvolvido como PoC da AEP.

---

## 1. Problema

Quem deseja cultivar plantas encontra dificuldade em obter informações confiáveis e
organizadas sobre as necessidades de cada espécie. Dados sobre iluminação, irrigação,
temperatura, características e cuidados estão espalhados em diferentes fontes, o que
dificulta o acesso e o acompanhamento adequado.

Essa falta de informação leva a cuidados inadequados, prejudica o desenvolvimento das
plantas e contribui para a perda de espécies vegetais que poderiam ser preservadas.

## 3. ODS — Objetivo de Desenvolvimento Sustentável

**ODS 15 — Vida Terrestre**, relacionado à proteção, recuperação e promoção do uso
sustentável dos ecossistemas terrestres e à preservação da biodiversidade.

A relação com a solução está na disseminação de conhecimento sobre espécies vegetais e
seus cuidados, promovendo maior conscientização sobre a importância da preservação da
vida vegetal. Ao facilitar o acesso a informações que incentivam práticas adequadas de
cultivo, a plataforma contribui para a conscientização ambiental e para a preservação da
biodiversidade.

## 4. Tecnologias

**Backend**
- Java 21
- Spring Boot 4.1.1 (Spring Web MVC)
- Spring Data MongoDB (banco NoSQL)
- Maven (via Maven Wrapper)

**Frontend**
- HTML, CSS e JavaScript (vanilla), servidos como conteúdo estático pelo Spring Boot
- Bootstrap 5.3 e Bootstrap Icons

**Testes e qualidade**
- JUnit 5 + Mockito
- MockMvc para os testes de controller
- JaCoCo com cobertura mínima de **70%** exigida no build

**Versionamento**
- Git e GitHub

## 5. Como executar

### Pré-requisitos

- **JDK 21** ou superior
- **MongoDB** rodando localmente na porta `27017`
  (a aplicação usa o banco `flora_log`, criado automaticamente no primeiro cadastro)

A conexão é configurada em [application.properties](src/main/resources/application.properties):

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/flora_log
```

Para usar outro MongoDB (por exemplo, Atlas), basta alterar essa URI.

### Rodando a aplicação

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicação sobe em **http://localhost:8080**.

- Página inicial: http://localhost:8080/index.html
- Biblioteca / gerenciamento: http://localhost:8080/app.html

### Rodando os testes

```bash
./mvnw test          # executa os testes e gera o relatório JaCoCo
./mvnw verify        # executa os testes e valida a cobertura mínima de 70%
```

Relatório de cobertura: `target/site/jacoco/index.html`

### Gerando o build

```bash
./mvnw clean package
java -jar target/aep-0.0.1-SNAPSHOT.jar
```

## 6. API

Base: `/api/plantas`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/plantas` | Lista todas as plantas |
| `GET` | `/api/plantas/{id}` | Busca uma planta pelo id |
| `POST` | `/api/plantas` | Cadastra uma planta |
| `POST` | `/api/plantas/lote` | Cadastra várias plantas de uma vez |
| `PUT` | `/api/plantas/{id}` | Atualiza uma planta existente |
| `DELETE` | `/api/plantas/{id}` | Exclui uma planta |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/plantas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Espada-de-são-jorge",
    "nomeCientifico": "Dracaena trifasciata",
    "categoria": "Folhagem",
    "descricao": "Planta resistente, tolera bem ambientes internos.",
    "nivelDificuldade": "Fácil",
    "iluminacao": "Meia-sombra a sol pleno",
    "rega": "A cada 15 dias",
    "temperatura": "18°C a 30°C"
  }'
```

Todos os campos são obrigatórios, exceto `descricao`. Requisições inválidas retornam
`400 Bad Request` e ids inexistentes retornam `404 Not Found`.

## 7. Estrutura do projeto

```
src/
├── main/
│   ├── java/FloraLog/aep/
│   │   ├── AepApplication.java          # classe principal
│   │   ├── controllers/                 # camada REST
│   │   ├── services/                    # regras de negócio e validações
│   │   ├── repositories/                # acesso ao MongoDB
│   │   ├── models/                      # PlantaModel
│   │   └── exception/                   # tratamento global de erros
│   └── resources/
│       ├── application.properties
│       └── static/                      # frontend (HTML, CSS, JS)
└── test/java/FloraLog/aep/              # testes de service e controller
```
