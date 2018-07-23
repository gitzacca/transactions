# Sistema de controle de transações bancárias


## Solução prática

Desenvolvi uma aplicação simples, utilizando spring-boot com os seguintes starters: Data, Web e Test.

Optei por deixar a aplicação mais simples possível de ser iniciada, utilizei servidor, banco de dados e gradle embedado.

O projeto foi desenvolvido utilizando o padrão DDD, nota-se pela divisão de camadas do projeto (Application, Domain, External). A ideia é que a comunicação entre camadas seja sempre nessa direção:

Application -> Domain -> External


## Deploy

A aplicação foi desenvolvida para que seja iniciada da forma mais simples possível, basta seguir os passos:

 * Checkout dos projetos: `git clone git@github.com:gitzacca/transactions.git` e `git clone git@github.com:gitzacca/accounts.git`


Para executar a aplicação execute o comando: `gradlew bootRun`

A página estará disponível no endereço: `http://localhost:8080/`

Disponibilizei uma API Rest que seria consumida por outro sistema: `http://localhost:8080/users?page=0&size=10`

## Agradecimento

Quero frisar que foi um teste muito interessante, e agradeço a oportunidade de mostrar o meu trabalho!

Estou disponível para esclarecer qualquer dúvida :)


zaccabruno@gmail.com
(11)97160-9350