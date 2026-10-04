# Peel Web

Peel Web é uma biblioteca web leve e minimalista para Java. O nome representa Portable, Embedded, Express e Layer.

O repositório também contém uma aplicação demonstrativa que usa a biblioteca. Essa aplicação é o artefato executável, dockerizado e preparado para publicação no Render.

## Estrutura

```text
peel-web
├── peel-web-core
├── peel-web-demo
├── .github/workflows/ci.yml
├── Dockerfile
├── render.yaml
└── pom.xml
```

- `peel-web-core`: biblioteca reutilizável.
- `peel-web-demo`: aplicação web de demonstração.
- `ci.yml`: pipeline de testes, SonarQube, integração e Docker.
- `render.yaml`: Blueprint de publicação da demonstração.

## Requisitos

- Java 17
- Maven 3.8 ou superior
- Docker

## Build e testes

```bash
mvn clean verify
```

Somente testes unitários:

```bash
mvn clean test
```

Somente testes de integração:

```bash
mvn clean verify -DskipUnitTests=true
```

## Execução local

```bash
mvn clean package
java -jar peel-web-demo/target/peel-web-demo.jar
```

A porta padrão é `8080`. A variável `PORT` permite escolher outra porta.

```bash
PORT=9090 java -jar peel-web-demo/target/peel-web-demo.jar
```

## Endpoints da demonstração

| Método | Endpoint | Resultado |
| --- | --- | --- |
| GET | `/peel/health` | Estado da aplicação |
| GET | `/peel/demo/hello` | Resposta JSON de exemplo |
| GET | `/peel/demo/static` | Página estática empacotada |

## Docker

```bash
docker build -t peel-web-demo .
docker run --rm -p 8080:8080 peel-web-demo
```

O projeto possui uma biblioteca e uma única aplicação executável. Por isso, a imagem da demonstração é suficiente e não há múltiplos serviços de aplicação que exijam Docker Compose.

## CI/CD

O GitHub Actions executa os estágios na seguinte ordem:

1. testes unitários com JUnit 5;
2. análise estática em uma instância SonarQube Community temporária;
3. testes de integração com servidor HTTP real;
4. build e teste da imagem Docker.

A instância SonarQube nasce dentro do próprio runner. O pipeline cria um token temporário durante a execução e não depende de secrets cadastrados no repositório.

## Git Flow

- `main`: releases estáveis.
- `develop`: integração das próximas alterações.
- `feature/*`: novas funcionalidades.
- `bugfix/*`: correções isoladas.
- `release/*`: preparação de versões.

Cada branch de feature, bugfix ou release é incorporada por Pull Request.

## Publicação no Render

O `render.yaml` cria um Web Service Docker a partir da branch `main`. O serviço usa `/peel/health` como health check e recebe a variável `PORT` automaticamente do Render.

## Uso da biblioteca

```java
PeelApp app = PeelAppBuilder.run(builder -> builder
        .context("/api")
        .port(8080)
        .staticContentPath("static")
        .addController(new MyController())
);

app.start();
```

## Licença

Este projeto é distribuído sob a licença presente em `LICENSE`.
