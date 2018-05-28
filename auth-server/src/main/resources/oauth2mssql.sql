CREATE TABLE oauth_access_token (
       token_id VARCHAR(256) DEFAULT NULL,
       token BINARY,
       authentication_id VARCHAR(256) DEFAULT NULL,
       user_name VARCHAR(256) DEFAULT NULL,
       client_id VARCHAR(256) DEFAULT NULL,
       authentication BINARY,
       refresh_token VARCHAR(256) DEFAULT NULL
     );

CREATE TABLE oauth_refresh_token (
       token_id VARCHAR(256) DEFAULT NULL,
       token BINARY,
       authentication BINARY
     );

CREATE TABLE oauth_client_details (
  client_id varchar(255) NOT NULL,
  resource_ids varchar(255) DEFAULT NULL,
  client_secret varchar(255) DEFAULT NULL,
  scope varchar(255) DEFAULT NULL,
  authorized_grant_types varchar(255) DEFAULT NULL,
  web_server_redirect_uri varchar(255) DEFAULT NULL,
  authorities varchar(255) DEFAULT NULL,
  access_token_validity int DEFAULT NULL,
  refresh_token_validity int DEFAULT NULL,
  additional_information varchar(4096) DEFAULT NULL,
  autoapprove varchar(255) DEFAULT NULL,
  PRIMARY KEY (client_id)
);

INSERT INTO oauth_client_details VALUES ('adminapp','mw/adminapp,ms/admin,ms/user','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','role_admin,role_superadmin','authorization_code,password,refresh_token,implicit',NULL,NULL,900,3600,'{}',NULL);

CREATE TABLE permission (
  id numeric(20) NOT NULL IDENTITY,
  name varchar(60) NOT NULL,
  created_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version numeric(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE (name)
);

INSERT INTO permission VALUES ('can_delete_user','1970-01-01 00:00:00','1970-01-01 00:00:00',0),('can_create_user','1970-01-01 00:00:00','1970-01-01 00:00:00',0),('can_update_user','1970-01-01 00:00:00','1970-01-01 00:00:00',0),('can_read_user','1970-01-01 00:00:00','1970-01-01 00:00:00',0);

CREATE TABLE role (
  id numeric(20)  NOT NULL IDENTITY,
  name varchar(60) NOT NULL,
  created_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version numeric(20)  NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE (name)
) ;

INSERT INTO role VALUES ('role_admin','1970-01-01 00:00:00','1970-01-01 00:00:00',0),('role_user','1970-01-01 00:00:00','1970-01-01 00:00:00',0);

CREATE TABLE permission_role (
  permission_id numeric(20)  NOT NULL,
  role_id numeric(20)  NOT NULL,
  created_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version numeric(20)  NOT NULL DEFAULT '0',
  PRIMARY KEY (permission_id,role_id),
  CONSTRAINT permission_role_fk1 FOREIGN KEY (permission_id) REFERENCES permission (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT permission_role_fk2 FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO permission_role VALUES (1,1,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),(2,1,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),(3,1,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),(4,1,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),(4,2,'1970-01-01 00:00:00','1970-01-01 00:00:00',0);


CREATE TABLE [user] (
  id numeric(20)  NOT NULL IDENTITY,
  username varchar(24) NOT NULL,
  password varchar(200) NOT NULL,
  email varchar(255) NOT NULL,
  enabled int NOT NULL,
  account_expired int NOT NULL,
  credentials_expired int NOT NULL,
  account_locked int NOT NULL,
  created_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version numeric(20)  NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE (username),
  UNIQUE (email)
);

INSERT INTO role_user VALUES (1,1,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),(2,2,'1970-01-01 00:00:00','1970-01-01 00:00:00',0);

CREATE TABLE role_user (
  role_id numeric(20)  NOT NULL,
  user_id numeric(20)  NOT NULL,
  created_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_on datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version numeric(20)  NOT NULL DEFAULT '0',
  PRIMARY KEY (role_id,user_id),
  CONSTRAINT role_user_fk1 FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT role_user_fk2 FOREIGN KEY (user_id) REFERENCES [user] (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO [user] VALUES ('admin','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','admin@example.com',1,0,0,0,'1970-01-01 00:00:00','1970-01-01 00:00:00',0),('user','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','user@example.com',1,0,0,0,'1970-01-01 00:00:00','1970-01-01 00:00:00',0);
