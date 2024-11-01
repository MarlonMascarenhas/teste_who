# Como Executar o Código

Este guia ensina como executar o código Java utilizando o IntelliJ IDEA.

## Passos

### 1. Baixe o IntelliJ IDEA
[Acesse o site do IntelliJ IDEA](https://www.jetbrains.com/idea/)

### 2. Abra o IntelliJ IDEA
- Inicie o IntelliJ IDEA e clique em `File > Open > local do projeto`

### 3. caso não esteja configurado, clique em Download JDK
- Clique em `File > Project Structure > SDK > Download JDK > Version 17 > Corretto 17`

### 4. Execute o código
- Clique com o botão direito no editor de código e selecione Run 'testeWhoApplication.main()' ou clique no ícone de execução (um triângulo verde) na parte superior da IDE.

### Observação
- A primeira execução pode demorar um pouco mais, pois a IDE estará configurando o ambiente.

### Dicas Adicionais
- Configurações de execução: Personalize as configurações de execução clicando em Run > Edit Configurations.
- Depuração: Para depurar o código, defina um ponto de interrupção clicando na margem ao lado da linha de código e execute em modo de depuração (Shift + F9).


# Como utilizar o Projeto

### 1. Endpoint de Configuração
- Tipos aceitos
    - Atualmente aceitamos alguns tipos, podendo adicionar mais se necessario
        - String
        - Integer
        - Double
        - Boolean
        - Date
            - dateFormat (formato de saída da data, ex.: `dd/MM/yyyy`)
            - enterDateFormat (formato de entrada da data, ex.:  `dd-MM-yyyy HH:mm:ss`)
        - Object
        - Long
- Os campos messageId e protocolId são configurados automaticamente para o tipo String, podendo mudar se necessário.

```bash
curl --location 'localhost:8080/v1/user/configureMapping' \
--header 'Content-Type: application/json' \
--data '{
  "messageId": {"name": "messageId", "type": "String"},
  "protocolId": {"name": "protocolId", "type": "String"},
  "nome": {"name": "name", "type": "String"},
  "dadosPessoais": {"name": "data", "type": "Object"},
  "idade": {"name": "age", "type": "Long"},
  "score": {"name": "score", "type": "Double"},
  "dataNascimento": {"name": "birthDate", "type": "Date", "dateFormat": "dd/MM/yyyy", "enterDateFormat": "dd-MM-yyyy HH:mm:ss"}
}'
```
- exemplo de saída: `Mapping updated successfully.` se tudo correr bem
- exemplo de saída: `Unsupported type: float` se algo der errado ele avisa o que esta fora do padrão

### 2. Endpoint de processar dados
- Os nomes dos campos e tipos que serão retornados devem ser cadastrados no endpoint de configuração previamente
```bash
curl --location 'localhost:8080/v1/user' \
--header 'Content-Type: application/json' \
--data '{
"messageId": "4f3f9fe2-2f19-43b4-b611-b871a80ecfec",
"protocolId": "b3788d36-037d-40d8-b448-90bc9724f388",
    "dadosPessoais": {
        "nome": "Maria",
        "idade": 29,
        "dataNascimento": "19-05-1995 00:00:00",
        "score": "800.30"
    }
}'
```
- exemplo de saída se tudo correr bem:
  ```bash
    {
    "protocolId": "b3788d36-037d-40d8-b448-90bc9724f388",
    "data": {
        "score": 800.3,
        "name": "Maria",
        "birthDate": "19/05/1995",
        "age": 29
      },
      "messageId": "4f3f9fe2-2f19-43b4-b611-b871a80ecfec"
    }
  ```
- exemplo de saída: `Error: Field not mapped: unMapped` se algo der errado ele avisa o que esta fora do padrão
