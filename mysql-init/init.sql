-- Cria o banco de dados para o serviço de usuários, se ele não existir.
CREATE DATABASE IF NOT EXISTS `aplication-user-manager`;

-- Dá ao usuário 'brawl_user' (que já é criado pelo docker-compose)
-- permissão total para acessar esse novo banco de dados.
GRANT ALL PRIVILEGES ON `aplication-user-manager`.* TO 'brawl_user'@'%';

-- Aplica as permissões.
FLUSH PRIVILEGES;