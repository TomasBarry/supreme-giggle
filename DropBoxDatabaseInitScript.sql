DROP TABLE UserAccounts;
DROP TABLE FileKeys;

CREATE TABLE UserAccounts (
	UserPublicKey  Char(300) PRIMARY KEY,
	UniqueDeviceID Char(300),
	UserName       Char(50)
);

CREATE TABLE FileKeys (
	EncKey        Char(300) PRIMARY KEY,
	UserPublicKey Char(300),
	File          Char(100),
	isOwner       Int
);

-- ########################################################################## --
-- USERS
-- ########################################################################## --
INSERT INTO UserAccounts VALUES (
	'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
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
	'John Doe'
);

INSERT INTO UserAccounts VALUES (
	'fake_yolo',
	'fake_ffffffff-b1c4-dsfsd5',
	'Dank may mays'
);

-- ########################################################################## --
-- Files owned by Tomas
-- ########################################################################## --

INSERT INTO FileKeys VALUES (
		'fake_0',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_A',
		1
);

INSERT INTO FileKeys VALUES (
		'fake_1',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_B',
		1
);

INSERT INTO FileKeys VALUES (
		'fake_2',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_C',
		1
);


-- ########################################################################## --
-- Files not owned by Tomas but accessible by others
-- ########################################################################## --

INSERT INTO FileKeys VALUES (
		'fake_11',
		'fake_KAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJAgMBAAE=',
		'fake_file_A',
		0
);

INSERT INTO FileKeys VALUES (
		'fake_12',
		'fake_MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFsg',
		'fake_file_A',
		0
);


INSERT INTO FileKeys VALUES (
		'fake_3',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_D',
		0
);

INSERT INTO FileKeys VALUES (
		'fake_4',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_E',
		0
);

INSERT INTO FileKeys VALUES (
		'fake_5',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9FmwkoClf9TzGQLLoi3bK8SuaPJAgMBAAE',
		'fake_file_F',
		0
);