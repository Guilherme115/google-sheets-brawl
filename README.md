# 🛠️ Brawl Stars Team Tracker Bot

Um bot de Discord integrado com a API do Brawl Stars e Google Sheets para cadastrar e acompanhar equipes competitivas.

## 🚀 Tecnologias usadas

- Java 17
- Spring Web
- MongoDB + Spring Data
- Discord API (JDA)
- Brawl Stars API
- Google Sheets API
- JUnit + Mockito para testes

## 🧠 O que o projeto faz?

Esse projeto permite que jogadores usem comandos no Discord para cadastrar suas equipes competitivas de Brawl Stars. O bot valida as informações usando a API oficial do jogo e armazena os dados das partidas diretamente no Google Sheets para fácil visualização e análise.

### 🧩 Funcionalidades principais:

- ✅ Comando no Discord para cadastrar equipes (`!infoteams`)
- 🛡️ Valida se o jogador realmente joga competitivo (por análise das partidas)
- 📬 Integração com a API do Brawl Stars para buscar dados recentes de batalhas
- 📄 Armazena os dados filtrados no Google Sheets
- 🔁 Atualiza automaticamente a cada 30 segundos para capturar novas partidas
- 💾 Utiliza MongoDB para persistência dos times cadastrados

## 🔁 Fluxo do projeto

1. O usuário envia uma mensagem no Discord com a **tag do jogador** e **nome da equipe**.
2. O bot consulta a **API do Brawl Stars** para verificar se o jogador participa de batalhas competitivas.
3. Se for válido, os dados são enviados para o **serviço de batalhas**, que filtra as partidas relevantes.
4. As batalhas filtradas são automaticamente **registradas no Google Sheets**.
5. A equipe também é armazenada no **MongoDB** para consultas futuras.

## 🧪 Testes

- Cobertura de testes com **JUnit e Mockito**


## 📸 Demonstração 


https://github.com/user-attachments/assets/e5b47340-42d3-4d06-b487-f00b36ca6803


https://github.com/user-attachments/assets/b900a8d0-e3e6-4803-b625-480efdcace78



    
## 📂 Como rodar localmente

```bash
git clone https://github.com/seu-usuario/google-sheets-brawl
cd google-sheets-brawl
./mvnw spring-boot:run
