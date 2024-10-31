# Piche tech task

## Description

### Banking Solution
You are tasked with building a simple REST API for a banking application. The API should allow users to perform basic banking operations such as creating accounts, making deposits, and transferring funds.
Requirements:
1. Account Management - Implement the following operations related to user accounts: Create a new account with an initial balance. Get account details by account number. List all accounts.
2. Account Transactions - Implement the following operations related to account transactions: Deposit funds into an account. Withdraw funds from an account. Transfer funds between two accounts.
3. REST Endpoints - Create RESTful endpoints to expose the functionality of both the Account Management and Account Transactions.
4. Authentication & Authorization - This is not required. Focus on what is asked.
5. Technology Stack - Language: Java. You can choose other technologies and frameworks as you see fit.
6. Quality - Your code should be covered with unit tests. - Used project build tool should be configured so that code coverage report can be generated.
7. Submission - The result of your assignment should be stored in the Git repository along with instructions on how to set up and run your application locally. Additionally, you can add documentation briefly explaining your tech stack and design choices.

Good luck with your assignment!

## Requirements

In order to properly run this project, you would need:
* JDK version 17
* Maven version 3.9.8

In order to run this project run this Maven command ```mvn clean package```. 
After that, type in console: ```java -jar target\task-0.0.1-jar-with-dependencies.jar```

## API description

| Path                                               | Method | Body                                                                        | Parameters                                                                | Description                                               |
|----------------------------------------------------|:------:|-----------------------------------------------------------------------------|---------------------------------------------------------------------------|-----------------------------------------------------------|
| account                                            |  GET   |                                                                             |                                                                           | List of all accounts, that registered in system           |
| account?name={name}                                |  GET   |                                                                             | name - string                                                             | Find an account with specific account name                |
| account/{id}                                       |  GET   |                                                                             | sort - ['asc', 'desc']                                                    | Find an account with specific account id                  |
| account/{id}/operation/all                         |  GET   |                                                                             | <p>from - timestamp</p><p>to - timestamp</p><p>sort - ['asc', 'desc']</p> | Find all operations for specific account id               |
| account/                                           |  POST  | {"name": "Name of a new account", "password": "Password for a new account"} |                                                                           | Add new account with unique name                          |
| account/validate                                   |  POST  | {"name": "Name of a new account", "password": "Password for a new account"} |                                                                           | Check whether the input matches with the existing account |
| account/{id}                                       | DELETE |                                                                             |                                                                           | Delete an existing account                                |
| account/{id}/operation/deposit                     |  GET   |                                                                             |                                                                           | List of all account deposit operations                    |
| account/{id}/operation/deposit                     |  POST  | {"deposit": amountToTransfer}                                               |                                                                           | Add new deposit operation                                 |
| account/{id}/operation/transfer                    |  GET   |                                                                             |                                                                           | List of all account transfer operations                   |
| account/{senderId}/operation/transfer/{receiverId} |  POST  | {"deposit": amountToTransfer}                                               |                                                                           | Add new transfer operation                                |