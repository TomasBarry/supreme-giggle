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
	'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhANFoAtDIYg03l6TtmMNK4XKEA2XQb+qXPaUOuOSIGARP
AgMBAAE=
',
	'Fake_User_DeviceKey_01',
	'Jimmy Russle'
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
	'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKUHrxZcZZ6Ry8D6sEseW3sdVpoOYl6OMtHia86NsBIR
AgMBAAE=
',
	'Fake_User_DeviceKey_05',
	'Brochella Dinks'
);

-- ########################################################################## --
-- Files owned by Tomas
-- ########################################################################## --

INSERT INTO FileKeys VALUES (
		'AGGIFC+rz7iuEEE16QprvFf9uaHwInzN62LOCebCS0k=',
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
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhANFoAtDIYg03l6TtmMNK4XKEA2XQb+qXPaUOuOSIGARP
AgMBAAE=
',
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
		'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKUHrxZcZZ6Ry8D6sEseW3sdVpoOYl6OMtHia86NsBIR
AgMBAAE=
',
		'fake_file_A',
		0
);

-- ########################################################################## --
-- Random Public Keys and their private Keys
-- ########################################################################## --

-- 'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhANFoAtDIYg03l6TtmMNK4XKEA2XQb+qXPaUOuOSIGARP
-- AgMBAAE=
-- '

-- 'MIHDAgEAMA0GCSqGSIb3DQEBAQUABIGuMIGrAgEAAiEA0WgC0MhiDTeXpO2Yw0rhcoQDZdBv6pc9
-- pQ645IgYBE8CAwEAAQIgDV2dKGRR1dQhirZblMN3xjDKm+QlviHVGeAoY4YzHwECEQD01zC3ABr2
-- mZBXoWC7Ts+BAhEA2vNfOE3T8ADRImuvN0u7zwIRALKaU1f7RLAE3219NDIO0IECEFHjWgaV3SFT
-- POSRv8t/kLMCEQC1WhZvB5EnhFPBKFlaCE+/
-- '

-- 'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAKUHrxZcZZ6Ry8D6sEseW3sdVpoOYl6OMtHia86NsBIR
-- AgMBAAE=
-- '

-- 'MIHDAgEAMA0GCSqGSIb3DQEBAQUABIGuMIGrAgEAAiEApQevFlxlnpHLwPqwSx5bex1Wmg5iXo4y
-- 0eJrzo2wEhECAwEAAQIgQK4wS11PXsTZzPr0GZz+UTNc3RPm2LPVGM4+C7b+cpkCEQDQx8uCkXI9
-- 6YHMCtfTDsMXAhEAylrG9scM24VI9T3XmmqtFwIRAL4bHzEt9SNw61J7ERjKmVMCEBuPbZXi2m9z
-- odI1EA5qBRUCEQCU9K8O0Y2PHvRCW2meNT3A
-- '

-- 'MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhALJWMSB+G3dsDyha0WjvkQDvN2+F23ewX6hrCaElN4vB
-- AgMBAAE=
-- '

-- 'MIHCAgEAMA0GCSqGSIb3DQEBAQUABIGtMIGqAgEAAiEAslYxIH4bd2wPKFrRaO+RAO83b4Xbd7Bf
-- qGsJoSU3i8ECAwEAAQIgeG4uVNP0dmwZvFXALs4BK/DImWWIfVs09e7X2eUyYAECEQDXAJ8TQnSj
-- rgIyOcW3nCpBAhEA1Fe59oLx8OvVb29r1FwBgQIQU4FMR4CyKLh25ecGYe/ngQIRAJYt4XFOlWzu
-- bsrcg+fyloECEFI6Ki14Okf8N9vxlpsO0e8=
-- '