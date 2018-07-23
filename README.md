# Sistema de controle de transações bancárias


## Solução prática

Desenvolvi uma aplicação simples, utilizando spring-boot com os seguintes starters: Data, Web e Test.

Optei por deixar a aplicação mais simples possível de ser iniciada, utilizei servidor, banco de dados e gradle embedado.

O projeto foi desenvolvido utilizando o padrão DDD, nota-se pela divisão de camadas do projeto (Application, Domain, External). A ideia é que a comunicação entre camadas seja sempre nessa direção:

Application -> Domain -> External


## Deploy

A aplicação foi desenvolvida para que seja iniciada da forma mais simples possível, basta seguir os passos:

 * Checkout dos projetos: `git clone https://github.com/gitzacca/accounts.git` e `git clone https://github.com/gitzacca/transactions.git`


Para executar a aplicação execute o comando: `sh run.sh` no diretório do projeto transactions. (É necessário ter o java 8 instalado)

Os microserviços estarão disponivel nos seguintes endereços:

(accounts) `http://localhost:8081/`

(transactions) `http://localhost:8080/`

Para facilitar o acesso aos serviços, deixei a coleção do postman (`https://www.getpostman.com/`) na pasta postman, basta fazer o import do arquivo e já estará tudo pronto para perfomar os serviços! :)


## Agradecimento

Gostaria de agradecer pela oportunidade de mostrar meu trabalho, e caso fique alguma dúvida referente ao projeto, estarei disponivel nos contatos abaixo:


zaccabruno@gmail.com

(11)97160-9350