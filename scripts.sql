create user flagpost_user WITH PASSWORD 'flagpost_123';

create schema flagpost authorization flagpost_user;

create table flagpost.app_user(
	id serial primary key,
	nickname varchar(50) unique not null,
	email varchar(200) unique not null,
	password varchar(200) not null,
	create_at timestamp not null,
	token varchar(200)
);

create table flagpost.challenge(
	id int primary key,
	title varchar(100) not null,
	description varchar(1000) not null,
	score int not null,
	site varchar(100) not null
);

create table flagpost.user_challenge(
	id serial primary key,
	flag varchar(100) not null,
	attempts int not null,
	correct boolean not null,
	create_at timestamp not null,
	update_at timestamp,
	user_id int constraint fk_user_challenge_challenge references flagpost.app_user(id),
	challenge_id int constraint fk_user_challenge_user references flagpost.challenge(id),	
	constraint uq_user_id_challenge_id UNIQUE (user_id, challenge_id)
);

insert into flagpost.challenge (id, title, description, score, site) values (1, 'SQL Injection 1', 'Utilize SQL Injection para fazer login com o usuário ricardolima', 20, '/sqlinjection1.html');
insert into flagpost.challenge (id, title, description, score, site) values (2, 'SQL Injection 2', 'Utilize SQL Injection para obter informações da tabela ''park.confidencial''', 20, '/sqlinjection/users?username=joaosilva');
insert into flagpost.challenge (id, title, description, score, site) values (3, 'Privilege Escalation', 'O token de autenticação possui uma falha crítica, utilize-a para obter a flag', 20, '/privilege-escalation/flag');
insert into flagpost.challenge (id, title, description, score, site) values (4, 'IDOR', 'Utilize essa falha de segurança para enumerar usuários e obter a flag', 20, '/idor/users?userId=1');
insert into flagpost.challenge (id, title, description, score, site) values (5, 'Cryptographic Failures', 'Faça login com o usuário brunofernandes obtendo o hash da senha na tabela ''park.usuarios''', 20, '/cryptographicfailures.html');

GRANT usage on schema flagpost TO flagpost_user;
GRANT select,insert,update ON ALL TABLES IN SCHEMA flagpost TO flagpost_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA flagpost TO flagpost_user;

create user park_user WITH PASSWORD 'park_123';
create schema park authorization park_user;
CREATE TABLE park.usuarios (
    id serial PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100),
    conta_bancaria VARCHAR(20),
    agencia VARCHAR(20),
    saldo DECIMAL(10, 2),
    estado_civil VARCHAR(20),
    sexo CHAR(1),
    senha CHAR(32)
);

CREATE TABLE park.confidencial (
    id int PRIMARY KEY,
    texto VARCHAR(50)
);

GRANT usage on schema park TO park_user;
GRANT select,insert,update ON ALL TABLES IN SCHEMA park TO park_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA park TO park_user;

INSERT INTO park.usuarios (id, username, email, conta_bancaria, agencia, saldo, estado_civil, sexo, senha) VALUES
(1, 'joaosilva', 'joao.silva@gmail.com', '12345678', 'Ag101', 3200.50, 'Solteiro', 'M', 'e10adc3949ba59abbe56e057f20f883e'), -- 123456
(2, 'mariaoliveira', 'maria.oliveira@hotmail.com', '23456789', 'Ag102', 4500.00, 'Casado', 'F', '5f4dcc3b5aa765d61d8327deb882cf99'), -- password
(3, 'carlossouza', 'carlos.souza@yahoo.com', '34567890', 'Ag103', 1200.75, 'Divorciado', 'M', 'd8578edf8458ce06fbc5bb76a58c5ca4'), -- qwerty
(4, 'anapaula', 'ana.paula@outlook.com', '45678901', 'Ag104', 2300.20, 'Casado', 'F', '25d55ad283aa400af464c76d713c07ad'), -- 12345678
(5, 'ricardolima', 'ricardo.lima@gmail.com', '56789012', 'Ag105', 1500.00, 'Solteiro', 'M', 'fc31813c72196398093f364e2f6aae98'), -- ???
(6, 'julianamendes', 'juliana.mendes@icloud.com', '67890123', 'Ag106', 3700.40, 'Casado', 'F', '21232f297a57a5a743894a0e4a801fc3'), -- admin
(7, 'felipecastro', 'felipe.castro@gmail.com', '78901234', 'Ag107', 800.90, 'Divorciado', 'M', '7c6a180b36896a0a8c02787eeafb0e4c'), -- password1
(8, 'camilarocha', 'camila.rocha@live.com', '89012345', 'Ag108', 2900.00, 'Solteiro', 'F', '6cb75f652a9b52798eb6cf2201057c73'), -- 123123
(9, 'brunofernandes', 'bruno.fernandes@gmail.com', '90123456', 'Ag109', 5100.30, 'Viúvo', 'M', 'b6ccb4ece5454dcae51778b3e239ebc2'), -- ???
(10, 'patriciagomes', 'patricia.gomes@yahoo.com', '01234567', 'Ag110', 2100.80, 'Casado', 'F', 'f379eaf3c831b04de153469d1bec345e'); -- 1234

insert into park.confidencial (id, texto) values (1, '5b6a4c770e3c4686819d073fee3802e9');