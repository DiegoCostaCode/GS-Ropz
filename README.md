# ROPZ - Rede de Observação de Zonas Quentes

### Integrantes do grupo:
- Diego Costa Silva | RM552648
- Lucas Minozzo Bronzeri | RM553745
- Thais Ribeiro Asfur | RM553870

## Vídeos

- **[Pitch](https://youtu.be/O1Hr3DbmBR4)**
- **[Vídeo teste](https://youtu.be/J8OLpV8tQps)**

---

## Problema
Com o avanço das mudanças climáticas, eventos de calor extremo
tornaram-se mais frequentes e intensos, impactando diretamente a
saúde pública. Grupos vulneráveis, como crianças, idosos e pessoas
com doenças crônicas, são especialmente suscetíveis a condições
como desidratação, insolação e agravamento de doenças
respiratórias.

Entretanto, a maioria das pessoas não recebe alertas preventivos
eficazes, nem possui informações suficientes sobre como agir
adequadamente para reduzir os riscos associados a temperaturas
elevadas.

## Nossa solução se propõe a...
Entregar ao usuário informações sobre a temperatura de sua região sem formulários gigantes,
apenas dados de contato afim de receber alertas de temperatura e dicas de cuidados com a saúde.
Uma interface fácil de lidar, informações fáceis para leitura, e dicas para cuidados eficientes.

Utiliza-se modelo de IA [Mistral](https://ollama.com/library/mistral) para analisar os dados de temperatura, como:

```java
  temperaturaResponseDTO.temperatura(),
  temperaturaResponseDTO.sensacaoTermica(),
  temperaturaResponseDTO.temperaturaMinima(),
  temperaturaResponseDTO.temperaturaMaxima(),
  temperaturaResponseDTO.umidade(),
  temperaturaResponseDTO.id()
```

E ele promove, em uma linguagem simples, cuidados para previnir problemas de saúde.

---

## Identidade

![frame-app-design](https://github.com/user-attachments/assets/c8a6ace8-08f0-4f96-8cc1-b16a5af8345a)

---
## Modelo banco de dados

[modelo-banco.pdf](docs/modelo-banco.pdf)

---
## Dependências

```gradle
    dependencies {
	implementation 'dev.langchain4j:langchain4j:0.36.2'
	implementation 'dev.langchain4j:langchain4j-ollama:0.36.2'
	implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
	runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
	runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity6'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-amqp'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	runtimeOnly 'com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
	testImplementation 'org.seleniumhq.selenium:selenium-java:4.21.0'
	testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
	testImplementation 'io.github.bonigarcia:webdrivermanager:5.7.0'
}
```
---

## Configuração do Banco de Dados

### 1. Escolha o banco e adicione a dependencia no `build.gradle`:

```gradle
// H2 (memória)

runtimeOnly 'com.h2database:h2'

// SQL Server

runtimeOnly 'com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11'

// Oracle

runtimeOnly 'com.oracle.database.jdbc:ojdbc11:21.7.0.0'
```

### 2. Configure `application.properties`

#### Para SQL Server:

```properties
spring.datasource.url=jdbc:sqlserver://sqlserver-java.database.windows.net:1433;database=javadb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
spring.datasource.username=${dbUser}
spring.datasource.password=${dbPass}
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

#### Para Oracle:

```properties
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
spring.datasource.username=${dbUser}
spring.datasource.password=${dbPass}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

## Executando a Aplicação

### Passos:

1. Clone o repositório:

```sh
git clone https://github.com/DiegoCostaCode/GS-Ropz
```

2. Acesse o diretório:

```sh
cd GS-Ropz
```

3. Configure o `application.properties` com dados do banco, RabbitMQ, uma Api Key do OpenWeather, e 
ip do servidor onde o Ollama está rodando:

```properties

#JPA Config
spring.jpa.hibernate.ddl-auto=create-drop

# DataSource Config

spring.datasource.url=jdbc_url_aqui
spring.datasource.username=username_aqui
spring.datasource.password=senha_aqui
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Open Weather API Config

openweather.api.key=sua_api_key_aqui

# RabbitMq

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

app.rabbitmq.queue=temperatura.queue
app.rabbitmq.exchange=temperatura.exchange
app.rabbitmq.routingkey=temperatura.routingkey

# MistralService

mistral.ip.server=localhost

```

4. Rode o RabbitMQ (mensageria):

```sh
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```

5. Instale e inicie o [Ollama](https://ollama.com)

```sh
ollama pull mistral
```

6. Compile e execute:

```sh
./gradlew build
./gradlew bootRun
```

7. Acesse: [http://localhost:8080](http://localhost:8080)









