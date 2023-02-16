CREATE TABLE IF NOT EXISTS `countrys`
(id tinyint unsigned NOT NULL AUTO_INCREMENT,
`country` varchar(64) NOT NULL,
UNIQUE KEY (`country`),
PRIMARY KEY (`id`))
ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `cities`
(`countryId` tinyint unsigned NOT NULL,
`city` varchar(64) NOT NULL,
UNIQUE KEY (`countryId`, `city`),
FOREIGN KEY (`countryId`) REFERENCES `countrys` (`id`) ON DELETE CASCADE)
ENGINE=InnoDB DEFAULT CHARSET=utf8;