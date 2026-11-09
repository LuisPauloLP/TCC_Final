# GED – Sistema de Gestão Eletrônica de Documentos

Este repositório contém o projeto que desenvolvi como Trabalho de Conclusão de Curso (TCC), com o objetivo de avaliar o impacto de um Sistema de Gestão Eletrônica de Documentos (GED) na redução do tempo de atendimento em serviços de pagamento presenciais.

O sistema foi desenvolvido para facilitar a digitalização, o armazenamento e a organização de boletos bancários, utilizando tecnologias de reconhecimento óptico de caracteres (OCR) e QR Code.

## Estrutura do projeto

Organizei o projeto em duas pastas principais:

```text
TCC_Final/
│
├── Sistema GED/
│   └── Aplicativo Android
│
├── Servidor Web/
│   └── API REST
│
├── README.md
└── .gitignore
```

## Funcionalidades desenvolvidas

- Digitalização de documentos pelo aplicativo.
- Importação de arquivos PDF e imagens.
- Extração de informações dos boletos utilizando Tesseract OCR.
- Identificação automática de beneficiário, vencimento, valor e linha digitável.
- Armazenamento local de documentos utilizando SQLite.
- Organização, pesquisa e filtragem de documentos.
- Seleção de múltiplos documentos.
- Consolidação de documentos selecionados.
- Geração de QR Code para acesso ao documento consolidado.
- Integração do aplicativo Android com o servidor Web.

## Tecnologias utilizadas

**Aplicativo Android (Sistema GED)**
- Kotlin
- Android Studio
- SQLite
- Tesseract OCR
- ZXing

**Servidor Web**
- Java
- Spring Boot
- Maven
- Spring Data JPA
- PostgreSQL

---

# Como executar o projeto

Para executar o sistema, é necessário configurar e iniciar o servidor Web e, posteriormente, executar o aplicativo Android.

## 1. Requisitos

As ferramentas utilizadas no desenvolvimento e necessárias para executar o projeto são:

- Android Studio – para abrir e executar o aplicativo.
- IntelliJ IDEA – para abrir e executar o servidor Web.
- JDK – para execução do Spring Boot.
- PostgreSQL – para o banco de dados do servidor.
- Git – para baixar o repositório.

Também é necessário possuir um dispositivo Android ou utilizar um emulador.

## 2. Baixar o projeto

Primeiramente, clone este repositório utilizando o comando:

```bash
git clone https://github.com/LuisPauloLP/TCC_Final.git
```

Após o download, entre na pasta do projeto:

```bash
cd TCC_Final
```

# Servidor Web

## 3. Configurar o banco de dados

Antes de iniciar o servidor, é necessário instalar e executar o PostgreSQL.

Crie o banco de dados utilizado pelo projeto, conforme definido nas configurações do servidor.

Exemplo:

```sql
CREATE DATABASE ged_db;
```

Também é necessário configurar as credenciais de acesso ao PostgreSQL.

No servidor, configure as seguintes variáveis de ambiente:

```text
spring.datasource.url=jdbc:postgresql://localhost:5432/ged_db
spring.datasource.username=postgres
spring.datasource.password=SENHA_DO_BANCO
```

## 4. Abrir e executar o servidor

Para executar o servidor, utilizo o IntelliJ IDEA.

1. Abra o IntelliJ IDEA.
2. Clique em **Open**.
3. Selecione a pasta `Servidor Web`.
4. Aguarde o carregamento do projeto Maven e das dependências.
5. Configure as variáveis de ambiente necessárias.
6. Acesse o Prompt de Comando.
7. Digite "ipconfig", e localize o endereço Ipv4 da máquina, copiando-o.
8. No Intellij, localize a classe "ConsolidadoService", dentro da pasta "service".
9. Na linha 95, troque o endereço IP no código para o endereço IP da rede que a máquina está logada.
10. Localize a classe principal do Spring Boot, identificada pela anotação `@BackendApplication`.
11. Execute essa classe utilizando o botão **Run**.

Após a inicialização, o servidor estará disponível, por padrão, no endereço:

```text
http://IP_DA_REDE:8080
```

# Sistema GED

## 5. Abrir o aplicativo Android

Para executar o aplicativo, utilizo o Android Studio.

1. Abra o Android Studio.
2. Clique em **Open**.
3. Selecione a pasta `Sistema GED`.
4. Aguarde a sincronização do Gradle.
5. Verifique se todas as dependências foram carregadas corretamente.

## 6. Configurar a conexão com o servidor

Para que o aplicativo consiga se comunicar com o servidor Web, é necessário configurar o endereço IP da máquina em que o Spring Boot está sendo executado.

No projeto Android, localize o arquivo "RetrofitClient" e altere o endereço-base:

```kotlin
private const val BASE_URL = "http://IP-DO-SERVIDOR:8080/"
```

Substitua `IP-DO-SERVIDOR` pelo endereço IP local do computador que está executando o servidor.

Exemplo:

```kotlin
private const val BASE_URL = "http://192.168.0.100:8080/"
```

**Algumas observações:**

- O computador e o celular devem estar conectados à mesma rede Wi-Fi.
- O endereço `localhost` não deve ser utilizado quando o aplicativo estiver sendo executado em um celular físico.
- O firewall do computador deve permitir conexões na porta utilizada pelo servidor.
- Caso o endereço IP ou a porta sejam alterados, será necessário atualizar a configuração no aplicativo.

## 7. Executar o aplicativo

Depois de configurar a conexão:

1. Verifique se o servidor Web está em execução no IntelliJ IDEA.
2. No Android Studio, selecione um dispositivo físico ou emulador.
3. Clique em **Run**.
4. Aguarde a instalação e inicialização do aplicativo.

Com o servidor e o aplicativo em execução, o sistema estará pronto para utilização.

---

## Observações

- O aplicativo utiliza SQLite para armazenar os documentos localmente.
- O servidor Web utiliza PostgreSQL para armazenar os dados gerenciados pela API.
- O arquivo de treinamento `por.traineddata` do Tesseract OCR deve estar disponível no projeto Android para o reconhecimento de documentos em português.
- As configurações de banco de dados, endereço IP e autenticação devem ser ajustadas de acordo com o ambiente em que o projeto será executado.

## Autor

**Luís Augusto Paulo**

Projeto desenvolvido como Trabalho de Conclusão de Curso (TCC), Universidade do Extremo Sul Catarinense (UNESC).

[GitHub – TCC_Final](https://github.com/LuisPauloLP/TCC_Final)
