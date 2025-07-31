# Paystore 🏥

Sistema de gerenciamento de centros comunitários com controle de recursos e trocas entre centros.

## 📚 Descrição

O **Paystore** é uma API RESTFul desenvolvida com **Spring Boot** e **MongoDB** que permite:

## 📦 Funcionalidades

### ✅ Centros Comunitários
- Cadastrar e listar centros comunitários
- Gerenciar recursos (como médicos, alimentos, remédios etc.) de cada centro
- Gerenciar percentual de ocupação de cada centro.
- Verifica a quantidade média de cada tipo de recurso cadastrado no sistema
- Notificar (via event logs) quando ocupação ultrapassa 90%.

### 🔁 Troca de Recursos
- Troca permitida apenas se os **pontos dos recursos** forem equivalentes.
- Caso a **ocupação seja > 90%**, é permitido trocar por menos pontos.
- Todas as trocas são armazenadas em um **histórico de negociações**.
- Quantidade do recurso não pode ser menor ou igual a zero após troca.

#### 🧮 Tabela de Pontos dos Recursos

| Recurso                    | Pontuação |
|----------------------------|-----------|
| Médico                     | 4         |
| Voluntário                 | 3         |
| Kit de suprimentos médicos | 7         |
| Veículo de transporte      | 5         |
| Cesta básica               | 2         |

### 📊 Relatórios

- Centros com **ocupação > 90%**.
- Quantidade média de cada recurso por centro.
- Histórico de negociações com filtros:
  - Por centro específico (obrigatório)
  - Por período de tempo (opcional)

---

## ⚡ Tecnologias Utilizadas

- Java 21
- Spring Boot
- MongoDB
- Lombok
- Maven

## ⚙ Requisitos

- Java 21+
- Maven
- MongoDB em execução local ou via Atlas

## 🚀 Como Executar

1. Clone o repositório:

```bash
git clone https://github.com/pedrogles/paystore.git
cd paystore
```

2. Compile e execute:

```bash
./mvnw spring-boot:run
```

3. Acesse a API:

```
http://localhost:8080
```

## 🔧 Endpoints Principais

- `POST /community-centers` - Cadastrar centro comunitário
- `GET /community-centers` - Listar todos os centros
- `POST /trade-history/exchange` - Realizar troca de recursos
- `GET /trade-history/all` - Ver histórico de trocas

A documentação da API pode ser consultada via Swagger em: 
```
http://localhost:8080](http://localhost:8080/swagger-ui.html
```

## 📁 Estrutura do Projeto

```bash
src/
├── main/
│   ├── java/
│   │   └── com.phoebuspaystore.paystore/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com.phoebuspaystore.paystore/
            └── service/
```
