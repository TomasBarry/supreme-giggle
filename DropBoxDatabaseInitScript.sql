CREATE TABLE UserAccounts (
	UserPublicKey  Char(300) PRIMARY KEY,
	UniqueDeviceID Char(300),
	UserName       Char(50)
);

CREATE TABLE FileKeys (
	EncKey        Char(300) PRIMARY KEY,
	UserPublicKey Char(300),
	File          Char(100)
);

INSERT INTO UserAccounts VALUES (
	'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
	'ffffffff-b1c4-d360-9bfd-10ce37b353d5',
	'Tomas Barry'
);

INSERT INTO UserAccounts VALUES (
	'fake_KAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
	'fake_360-9bfd-10ce37b353d5',
	'Jimmy Russle'
);

INSERT INTO UserAccounts VALUES (
	'fake_MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFsg',
	'fake_ffffffff-b1c4-dsfsd5',
	'Tomas John Doe'
);

INSERT INTO FileKeys VALUES (
		'fake_0',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
		'fake_file_A'
);

INSERT INTO FileKeys VALUES (
		'fake_1',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
		'fake_file_B'
);

INSERT INTO FileKeys VALUES (
		'fake_2',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
		'fake_file_C'
);