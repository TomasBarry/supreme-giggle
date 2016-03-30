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
	'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJ
AgMBAAE=
    ',
	'ffffffff-b1c4-d360-9bfd-10ce37b353d5',
	'Tomas Barry'
);

INSERT INTO UserAccounts VALUES (
	'Fake_User_PublicKey_01',
	'Fake_User_DeviceKey_01',
	'Jimmy Russle'
);

INSERT INTO UserAccounts VALUES (
	'Fake_User_PublicKey_02',
	'Fake_User_DeviceKey_02',
	'Yolo Baggins'
);

INSERT INTO UserAccounts VALUES (
	'Fake_User_PublicKey_03',
	'Fake_User_DeviceKey_03',
	'Dank May May'
);

INSERT INTO UserAccounts VALUES (
	'Fake_User_PublicKey_04',
	'Fake_User_DeviceKey_04',
	'Higgs Boson'
);

INSERT INTO UserAccounts VALUES (
	'Fake_User_PublicKey_05',
	'Fake_User_DeviceKey_05',
	'Brochella Dinks'
);

-- ########################################################################## --
-- Files owned by Tomas
-- ########################################################################## --

INSERT INTO FileKeys VALUES (
		'UD5j21IbuxAsGPeOgxAxz05vAClTr+Tn8CH4XBLXTOI=',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJ
AgMBAAE=
    ',
		'fake_file_A',
		1
);

INSERT INTO FileKeys VALUES (
		'My_EncyKey_B',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJ
AgMBAAE=
    ',
		'fake_file_B',
		1
);

INSERT INTO FileKeys VALUES (
		'My_EncyKey_C',
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKJyiFBCFzl2Jd9m9Fm+wkoClf9TzGQLLoi3bK8SuaPJ
AgMBAAE=
    ',
		'fake_file_C',
		1
);


-- ########################################################################## --
-- Files owned by Tomas and accessible by others
-- ########################################################################## --

INSERT INTO FileKeys VALUES (
		'Fake_EncyKey_D',
		'Fake_User_PublicKey_01',
		'fake_file_A',
		0
);

INSERT INTO FileKeys VALUES (
		'Fake_EncyKey_E',
		'Fake_User_PublicKey_02',
		'fake_file_A',
		0
);


INSERT INTO FileKeys VALUES (
		'Fake_EncyKey_F',
		'Fake_User_PublicKey_03',
		'fake_file_A',
		0
);

INSERT INTO FileKeys VALUES (
		'Fake_EncyKey_G',
		'Fake_User_PublicKey_04',
		'fake_file_A',
		0
);

INSERT INTO FileKeys VALUES (
		'Fake_EncyKey_H',
		'Fake_User_PublicKey_05',
		'fake_file_A',
		0
);