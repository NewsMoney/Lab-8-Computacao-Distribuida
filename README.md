# Lab-8-Computacao-Distribuida

## Disciplina

Computação Distribuída

## Aluno(s)

* Nome Completo: Milton Almeida Leôncio
* RA: 10416764

---

# Descrição do Projeto

Este projeto implementa um Sistema de Gerenciamento de Biblioteca Digital utilizando gRPC e Protocol Buffers em Java.

O sistema é composto por um servidor gRPC responsável pelo gerenciamento de livros e empréstimos, além de fornecer um canal de comunicação em tempo real entre usuários e um bibliotecário virtual.

Foram implementados os quatro tipos de comunicação suportados pelo gRPC:

1. Unary RPC
2. Server Streaming RPC
3. Client Streaming RPC
4. Bidirectional Streaming RPC

Todos os dados são armazenados em memória, sem utilização de banco de dados.

---

# Tecnologias Utilizadas

* Java 17
* Maven
* gRPC Java 1.68.x
* Protocol Buffers 3

---

# Estrutura do Projeto

```text
biblioteca-grpc/
│
├── pom.xml
│
└── src/
    └── main/
        ├── java/
        │   └── br/
        │       └── mackenzie/
        │           └── biblioteca/
        │               ├── server/
        │               ├── client/
        │               └── model/
        │
        └── proto/
            └── biblioteca.proto
```

---

# Funcionalidades

## 1. Cadastrar Livro (Unary RPC)

Permite cadastrar um livro no acervo informando:

* Título
* Autor
* Ano
* ISBN

O sistema gera automaticamente um identificador único para cada livro.

Também é realizado o controle de ISBN duplicado.

---

## 2. Listar Livros por Autor (Server Streaming RPC)

Recebe o nome de um autor e retorna uma sequência de livros cadastrados para esse autor.

Caso o autor não exista, o servidor retorna erro `NOT_FOUND`.

---

## 3. Registrar Empréstimos (Client Streaming RPC)

Recebe uma sequência de empréstimos enviados pelo cliente.

Ao final do envio, o servidor retorna:

* Quantidade total de empréstimos registrados;
* Tempo total de processamento.

---

## 4. Chat Bibliotecário (Bidirectional Streaming RPC)

Permite comunicação em tempo real entre cliente e servidor.

Para cada mensagem enviada pelo usuário, o servidor retorna uma sugestão de livro relacionada ao assunto informado.

---

# Como Compilar

Executar na raiz do projeto:

```bash
mvn clean compile
```

ou

```bash
mvn clean package
```

---

# Como Executar

## Iniciar o Servidor

```bash
mvn exec:java -Dexec.mainClass="br.mackenzie.biblioteca.server.ServidorBiblioteca"
```

---

## Executar o Cliente

```bash
mvn exec:java -Dexec.mainClass="br.mackenzie.biblioteca.client.ClienteBiblioteca"
```

---

# Testes Realizados

## Cadastro de Livros

Foram cadastrados três livros:

* Clean Code
* Clean Architecture
* Effective Java

Resultado esperado:

```text
Livro cadastrado com sucesso
```

---

## ISBN Duplicado

Tentativa de cadastro utilizando ISBN já existente.

Resultado esperado:

```text
ALREADY_EXISTS
ISBN já cadastrado
```

---

## Listagem por Autor

Consulta realizada para:

```text
Robert Martin
```

Resultado esperado:

```text
Clean Code
Clean Architecture
```

---

## Autor Inexistente

Consulta realizada para:

```text
Autor Inexistente
```

Resultado esperado:

```text
NOT_FOUND
Autor não encontrado
```

---

## Registro de Empréstimos

Foram enviados cinco empréstimos consecutivos.

Resultado esperado:

```text
Total de empréstimos: 5
Tempo de processamento: XX ms
```

---

## Chat Bibliotecário

Mensagens enviadas:

```text
java
algoritmos
ia
```

Respostas esperadas:

```text
Effective Java
Introduction to Algorithms
Artificial Intelligence: A Modern Approach
```

---

# Tratamento de Erros

O sistema utiliza os códigos de status do gRPC para tratamento adequado das falhas:

* INVALID_ARGUMENT
* NOT_FOUND
* ALREADY_EXISTS
* INTERNAL

---

# Logs do Servidor

Todas as chamadas recebidas pelo servidor são registradas no console, incluindo:

* Método invocado
* Parâmetros recebidos
* Resultado da operação

---

# Execução

execuções no GitHub Actions
