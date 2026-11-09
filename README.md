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

## Requisitos necessários

Antes de executar o projeto, instale:

| Ferramenta | Finalidade |
|---|---|
| Android Studio | Abrir, configurar e executar o aplicativo GED |
| IntelliJ IDEA | Abrir, configurar e executar o servidor Web |
| JDK | Execução do servidor Spring Boot |
| PostgreSQL | Banco de dados do servidor |
| Git | Clonagem do repositório |

Também será necessário um dispositivo Android físico ou emulador.

---

# Configuração do Servidor Web

## 1. Configurar o PostgreSQL

Instale e inicie o PostgreSQL.

Crie o banco de dados utilizado pelo servidor, conforme o nome definido na configuração do projeto.

Configure as credenciais de acesso no arquivo de configuração local do Spring Boot.

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seguranca
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

**Importante:** substitua os valores conforme a configuração do seu ambiente. Não compartilhe senhas ou segredos JWT.

## 2. Abrir o servidor no IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Selecione **Open**.
3. Localize a pasta `Servidor Web` do repositório.
4. Selecione o arquivo `pom.xml` para importar o projeto Maven.
5. Aguarde o IntelliJ baixar as dependências.
6. Configure as variáveis de ambiente necessárias para o funcionamento do servidor, incluindo as credenciais do PostgreSQL e o segredo JWT, caso utilizado.
7. Localize a classe principal do Spring Boot, identificada pela anotação `@SpringBootApplication`.
8. Clique no botão **Run** para iniciar o servidor.

Por padrão, o servidor poderá ser acessado em:

```text
http://localhost:8080
```

---

# Configuração do Sistema GED

## 3. Abrir o aplicativo no Android Studio

1. Abra o Android Studio.
2. Selecione **Open**.
3. Localize a pasta `Sistema GED` do repositório.
4. Aguarde a sincronização do Gradle.
5. Verifique se as dependências foram instaladas corretamente.

## 4. Configurar o endereço IP do servidor

Para permitir a comunicação entre o aplicativo Android e o servidor Spring Boot, é necessário configurar o endereço IP da máquina que hospeda o servidor.

Localize no projeto Android o arquivo Kotlin responsável pela configuração da URL da API.

Altere o endereço-base:

```kotlin
private const val BASE_URL = "http://IP-DO-SERVIDOR:8080/"
```

Substitua `IP-DO-SERVIDOR` pelo endereço IP local do computador que está executando o servidor.

Exemplo:

```kotlin
private const val BASE_URL = "http://192.168.0.100:8080/"
```

**Observações:**

- O computador e o dispositivo Android devem estar conectados à mesma rede Wi-Fi.
- Não utilize `localhost` no aplicativo quando estiver utilizando um celular físico.
- Verifique se o firewall permite conexões na porta 8080.
- Caso a porta do servidor seja alterada, atualize também o endereço no aplicativo.

## 5. Executar o aplicativo

1. Certifique-se de que o servidor Spring Boot está em execução no IntelliJ IDEA.
2. No Android Studio, selecione um dispositivo físico ou emulador.
3. Clique em **Run**.
4. Aguarde a instalação e inicialização do aplicativo.

O aplicativo estará pronto para utilização após a inicialização do servidor e a configuração correta da conexão com a API.
