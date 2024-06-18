# Sistema de Biblioteca com Sockets em Java

Este projeto é uma aplicação cliente-servidor em Java que utiliza sockets para gerenciar um registro de livros de uma biblioteca. O servidor é responsável por manter um arquivo JSON com informações sobre os livros, enquanto o cliente se comunica com o servidor para realizar operações como listagem, cadastro, aluguel e devolução de livros.

## Estrutura do Projeto

- **Livro.java**: Classe que representa um livro na biblioteca. Possui os atributos `autor`, `nome`, `genero` e `exemplares`.
- **Cliente.java**: Classe que implementa o cliente, permitindo a comunicação com o servidor e a realização de operações sobre os livros.
- **Servidor.java**: Classe que implementa o servidor, responsável por aceitar conexões de clientes e gerenciar as operações sobre os livros.
- **ClienteHandler.java**: Classe que lida com a comunicação entre o servidor e cada cliente conectado, executando as operações solicitadas.

## Funcionalidades

- **Listagem de livros**: O cliente pode solicitar ao servidor a lista de todos os livros cadastrados.
- **Cadastro de livros**: O cliente pode enviar informações de um novo livro para ser adicionado ao registro.
- **Aluguel de livros**: O cliente pode solicitar o aluguel de um livro específico, diminuindo o número de exemplares disponíveis.
- **Devolução de livros**: O cliente pode devolver um livro, aumentando o número de exemplares disponíveis.

## Estrutura dos Arquivos

src/

├── Cliente.java

├── Livro.java

├── Servidor.java

└── ClienteHandler.java


## Como Executar o Projeto

1. **Pré-requisitos**:
   - Certifique-se de ter o Java Development Kit (JDK) instalado em sua máquina.
   - Baixe a biblioteca `Gson` e coloque o arquivo JAR (`gson-2.11.0.jar`) na pasta `lib`.

2. **Compilar o Projeto**:
   Navegue até o diretório do projeto e compile todas as classes:

        javac -cp "lib/gson-2.11.0.jar" -d bin src/*.java

3. **Executar o Servidor**:

        java -cp "lib/gson-2.11.0.jar;bin" Servidor

4. **Executar o Cliente**:

        java -cp "lib/gson-2.11.0.jar;bin" Cliente

