# Gestão de Estacionamento | API & ORM
- API de sistema para gestão de estacionamento, desenvolvido em ambiente acadêmico. Disponibiliza o CRUD completo de todoas as entidades
- Autor: Jean Felipe Moschen Buss

## Índice

- [Principais Funções](#principais-funções)
- [Tecnologias](#tecnologias)
- [Requisitos para Desenvolvimento](#requisitos-para-desenvolvimento)
- [Configuração PostgreSQL](#configuração-postgresql)
- [Deploy em Docker-Compose](#deploy-em-docker-compose)

## Principais funções

- CRUD de Condutores e estatísticas relacionadas;
- CRUD de Marcas;
- CRUD de Marcas;
- CRUD de Modelos;
- CRUD de Veículos;
- CRUD de Movimentações e relatórios relacionados;

## Tecnologias

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

## Requisitos para Desenvolvimento

- Clone deste repositório
- JDK 19
- PostgreSQL 15
- Build com **Maven**
- Prática de Trunk-Based com GIT

## Configuração PostgreSQL

Toda a configuração do Banco de Dados é feita no arquivo `application.properties`

## Deploy em Docker-Compose
Para fazer deploy da stack inteira, ou seja: 
- Este repositório
- O repositório [Estacionamento Front-end](https://github.com/jean-mb/estacionamento_frontend)
- Banco de Dados [PostgreSQL](https://hub.docker.com/_/postgres),
 
Execute o seguinte docker-compose:

```
version: '3'
name: estacionamento
services:
  frontend:
    image: ghcr.io/jean-mb/estacionamento_frontend:main
    ports:
      - '80:80'
    depends_on:
      - backend
    restart: unless-stopped
    networks:
      - estacionamento
  backend:
    image: ghcr.io/jean-mb/estacionamento_backend:main
    ports:
      - '8080:8080'
    depends_on:
      - postgres
    restart: unless-stopped
    networks:
      - estacionamento
  postgres:
    image: postgres
    ports:
      - '5432:5432'
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=estacionamento
    volumes:
      - ~/estacionamento-db:/var/lib/postgresql/data
    restart: unless-stopped
    networks:
      - estacionamento
networks:
  estacionamento:
    name: estacionamento
    ipam:
      driver: default
```
Esse repositório, conforme apontado no [Dockerfile](https://github.com/jean-mb/estacionamento_backend/blob/main/Dockerfile) será compilado e servido por Nginx, com um Proxy para a URL do servidor backend.

A aplicação ficará disponivel:
- UI / Frontend = Porta 80
- Aplicação Backend = Porta 8080
- Banco de Dados PostgreSQL = Porta 5432
