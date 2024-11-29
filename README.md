Este repositório consta o projeto em Spring Boot Initializr versão 3.4.0, utilizando o editor Java intelliJ Idea (Community Edition):
  * Tipo: Maven;
  * Versão Java: 21;
  * Dependências: Spring JPA, Rest Repositories, MySQL Driver e Lombok.

A finalidade deste projeto é controlar o acesso ao usuário pela aplicação não de si mesmo e sim de outras.

![image](https://github.com/user-attachments/assets/3e8b826e-2690-4a74-b5ac-46ea2d5bfe5f)

Elaboração de tabelas e seus respectivos relacionamentos escritas em MySQL Workbench.

![image](https://github.com/user-attachments/assets/766303a4-6107-43dd-9047-5db62947f8c8)
Detalhes gerais do BD de Controle de Acesso (Tabelas e Stored Procedures).

O propósito geral é testar as funcionalidades nas tabelas com seus relacionamentos baseadas em CRUD via WEB API (sem utilização de Hibernate local) usando os métodos do BD MySQL (Stored Procedures) com regras básicas de negócio:
  - nulos.
  - conferência de senha.
  - tamanho escasso (menos de 3 caracteres) ou em excesso (acima do permitido).
  - existência de campos chaves nos relacionamentos.

![image](https://github.com/user-attachments/assets/2fbe234d-e962-4414-bb58-c9667437664b)
![image](https://github.com/user-attachments/assets/d8d23f57-2e57-40cf-b70b-ccf27475d0c3)

As API's das tabelas foram testadas com exaustão respeitando os princípios da regra de negócio apresentada acima utilizando Postman (GET, POST, PUT, e DELETE) - 2 imagens acima, obtendo resultados em JSON
(dados ou mensagem extraída diretamente no Stored Procedure no caso de algo acontecer, diferentemente do código 404).
