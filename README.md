# GED — Sistema de Gestão Eletrônica de Documentos

Sistema de Gestão Eletrônica de Documentos (GED) desenvolvido como Trabalho de Conclusão de Curso (TCC), com o objetivo de facilitar a digitalização, organização e gerenciamento de documentos, especialmente boletos bancários.

A aplicação permite digitalizar ou importar documentos, extrair automaticamente informações por meio de OCR e organizar os arquivos para posterior consolidação e acesso por QR Code.

---

## Estrutura do projeto

O repositório está dividido em duas partes principais:

```text
TCC_Final/
│
├── Sistema GED/
│   └── Aplicativo Android
│
├── Servidor Web/
│   └── API REST Spring Boot
│
├── README.md
└── .gitignore
```

## Funcionalidades

- Digitalização de documentos.
- Importação de arquivos PDF e imagens.
- Extração automática de informações utilizando Tesseract OCR.
- Identificação de beneficiário, vencimento, valor e linha digitável.
- Armazenamento local de documentos utilizando SQLite.
- Organização, pesquisa e filtragem de documentos.
- Seleção de múltiplos documentos.
- Geração de documentos consolidados.
- Geração de QR Code para acesso ao documento consolidado.
- Comunicação entre o aplicativo Android e o servidor web.

## Tecnologias utilizadas

### Sistema GED — Aplicativo Android

- Kotlin
- Android Studio
- SQLite
- Tesseract OCR
- ZXing
- Retrofit

### Servidor Web

- Java
- Spring Boot
- Maven
- Spring Data JPA
- PostgreSQL
- API REST

---

# Instalação e configuração

## 1. Requisitos necessários

Antes de executar o projeto, instale:

| Ferramenta | Finalidade |
|---|---|
| Android Studio | Desenvolvimento e execução do aplicativo |
| JDK | Execução do servidor Spring Boot |
| PostgreSQL | Banco de dados do servidor |
| Git | Clonagem do repositório |
| Maven | Gerenciamento e execução do servidor, caso não utilize Maven Wrapper |

Também será necessário um dispositivo Android ou emulador para executar o aplicativo.

## 2. Clonar o repositório

Abra o terminal e execute:

```bash
git clone https://github.com/LuisPauloLP/TCC_Final.git
```

Entre na pasta:

```bash
cd TCC_Final
```

# Configuração do Servidor Web

## 3. Configurar o PostgreSQL

Instale e inicie o PostgreSQL.

Crie o banco de dados utilizado pelo servidor:

```sql
CREATE DATABASE ged;
```

Configure as credenciais de conexão conforme o arquivo de configuração do Spring Boot.

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ged
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

**Importante:** os nomes do banco, usuário e senha devem corresponder à configuração utilizada pelo projeto.

Se o projeto utilizar Flyway, verifique também as migrações responsáveis pela criação das tabelas.

## 4. Configurar o servidor Spring Boot

Abra a pasta:

```text
Servidor Web/
```

Configure as variáveis de ambiente necessárias, incluindo:

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET (caso utilizado)

Não compartilhe credenciais reais ou segredos JWT.

Abra o terminal dentro da pasta `Servidor Web` e execute:

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

**Linux:**

```bash
./mvnw spring-boot:run
```

Caso não exista Maven Wrapper:

```bash
mvn spring-boot:run
```

Por padrão, o servidor Spring Boot utiliza a porta 8080.

Endereço local:

```text
http://localhost:8080
```

# Configuração do Sistema GED

## 5. Abrir o aplicativo Android

Abra o Android Studio e selecione a pasta:

```text
Sistema GED/
```

Aguarde a sincronização do Gradle e a instalação das dependências necessárias.

## 6. Configurar o endereço IP do servidor

Para permitir a comunicação entre o aplicativo Android e o servidor Spring Boot, é necessário configurar o endereço IP da máquina que está executando o servidor.

Localize no projeto Android o arquivo responsável pela configuração da URL da API.

Altere o endereço-base para:

```kotlin
private const val BASE_URL = "http://IP-DO-SERVIDOR:8080/"
```

Substitua `IP-DO-SERVIDOR` pelo endereço IP local do computador que hospeda o servidor.

Exemplo:

```kotlin
private const val BASE_URL = "http://192.168.0.100:8080/"
```

**Observações:**

- O computador e o dispositivo Android devem estar conectados à mesma rede Wi-Fi.
- Não utilize `localhost` no aplicativo quando estiver executando em um celular físico.
- Verifique se o firewall permite conexões na porta 8080.
- Caso a porta do servidor seja alterada, atualize também o endereço no aplicativo.
- Para utilização em produção, recomenda-se configurar HTTPS.

## 7. Executar o aplicativo

No Android Studio:

1. Abra o projeto localizado em `Sistema GED/`.
2. Aguarde a sincronização do Gradle.
3. Selecione um dispositivo Android ou emulador.
4. Certifique-se de que o servidor Spring Boot está em execução.
5. Clique em **Run** para instalar e iniciar o aplicativo.

---

# Observações importantes

- O aplicativo Android utiliza SQLite para armazenamento local.
- O servidor web utiliza PostgreSQL para persistência dos dados disponibilizados pela API.
- O servidor precisa estar em execução para que as funcionalidades que dependem da API funcionem.
- Os arquivos de treinamento do Tesseract OCR, especialmente `por.traineddata`, precisam estar disponíveis no aplicativo.
- As configurações locais e credenciais devem ser preenchidas individualmente por cada pessoa que instalar o projeto.
- Os dados utilizados para testes devem ser fictícios, evitando a exposição de informações pessoais.

---

## Autor

**Luís Augusto Paulo**

Projeto desenvolvido no contexto do Trabalho de Conclusão de Curso da Universidade do Extremo Sul Catarinense (UNESC).

## Repositório

[GitHub — TCC_Final](https://github.com/LuisPauloLP/TCC_Final)

## Licença

Projeto acadêmico. A reutilização e distribuição do código devem respeitar os direitos autorais de seus componentes e dependências.
