-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 01, 2023 at 03:08 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.0.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `java_winter`
--

-- --------------------------------------------------------

--
-- Table structure for table `autor`
--

CREATE TABLE `autor` (
  `id` int(11) NOT NULL,
  `jmeno` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `narodnost` varchar(50) COLLATE utf8_czech_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `autor`
--

INSERT INTO `autor` (`id`, `jmeno`, `narodnost`) VALUES
(1, 'Karel Čapek', 'Czechia'),
(2, 'William Shakespeare', 'British'),
(3, 'Božena Němcová', 'Czechia'),
(4, 'Nguyen Nhat Anh', 'Vietnam'),
(9, 'Ernest Hemingway', 'American'),
(14, 'Autor test', 'American');

-- --------------------------------------------------------

--
-- Table structure for table `doklad`
--

CREATE TABLE `doklad` (
  `id` int(11) NOT NULL,
  `datum` timestamp NOT NULL DEFAULT current_timestamp(),
  `datumTo` timestamp NULL DEFAULT NULL,
  `totalPrice` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `doklad`
--

INSERT INTO `doklad` (`id`, `datum`, `datumTo`, `totalPrice`) VALUES
(13, '2023-04-11 13:32:15', NULL, 947),
(15, '2023-04-11 13:49:41', NULL, 1098),
(17, '2023-04-11 17:11:42', NULL, 549),
(18, '2023-04-11 17:15:11', NULL, 549),
(19, '2023-04-11 20:16:55', NULL, 520),
(26, '2023-04-26 10:45:41', '2023-04-27 22:00:00', 220),
(28, '2023-04-26 11:37:15', '2023-04-27 22:00:00', 100),
(29, '2023-04-26 12:29:45', '2023-04-27 22:00:00', 300);

-- --------------------------------------------------------

--
-- Table structure for table `doklad_kniha`
--

CREATE TABLE `doklad_kniha` (
  `id` int(11) NOT NULL,
  `id_doklad` int(11) NOT NULL,
  `id_kniha` int(11) NOT NULL,
  `amount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `doklad_kniha`
--

INSERT INTO `doklad_kniha` (`id`, `id_doklad`, `id_kniha`, `amount`) VALUES
(6, 13, 4, 3),
(7, 13, 5, 1),
(8, 13, 2, 1),
(14, 15, 4, 2),
(15, 15, 5, 2),
(16, 15, 2, 2);

-- --------------------------------------------------------

--
-- Table structure for table `doklad_zakaznik`
--

CREATE TABLE `doklad_zakaznik` (
  `id` int(11) NOT NULL,
  `id_doklad` int(11) NOT NULL,
  `id_zakaznik` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `doklad_zakaznik`
--

INSERT INTO `doklad_zakaznik` (`id`, `id_doklad`, `id_zakaznik`) VALUES
(1, 13, 2),
(3, 15, 5),
(5, 17, 2),
(6, 18, 1),
(7, 19, 5),
(14, 26, 2),
(16, 28, 2),
(17, 29, 3);

-- --------------------------------------------------------

--
-- Table structure for table `kniha`
--

CREATE TABLE `kniha` (
  `id` int(11) NOT NULL,
  `nazev` varchar(255) COLLATE utf8_czech_ci NOT NULL,
  `rok_vydani` smallint(4) NOT NULL,
  `cena` double NOT NULL,
  `zanr` varchar(255) COLLATE utf8_czech_ci NOT NULL,
  `amount` int(11) NOT NULL,
  `popis` text COLLATE utf8_czech_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `kniha`
--

INSERT INTO `kniha` (`id`, `nazev`, `rok_vydani`, `cena`, `zanr`, `amount`, `popis`) VALUES
(2, 'Bílá nemoc', 1937, 100, 'Drama', 111, 'Bílá nemoc je divadelní hra – drama Karla Čapka z roku 1937. Dílo varuje před nastupujícím fašismem.'),
(3, 'Romeo and Juliet', 1597, 200, 'Drama', 100, 'Romeo a Julie (Romeo and Juliet) je divadelní hra, kterou napsal William Shakespeare. Premiéru měla v roce 1595. Jedná se o milostnou tragédii, patrně jeden z nejznámějších milostných příběhů v historii světového dramatu.'),
(4, 'Mat Biec', 1990, 199, 'Drama', 104, 'Mat Biec je román spisovatele Nguyen Nhat Anh v této autorské sérii příběhů o lásce teenagerů s Malým ďáblem, Dívka ze včerejška,... '),
(5, 'Babička', 1855, 30, 'Historický', 104, 'Babička je novela české spisovatelky Boženy Němcové z roku 1855. Je jejím nejoblíbenějším dílem a je považována za klasiku české literatury. '),
(13, 'Stařec a moře', 1946, 220, 'Drama', 126, 'Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. '),
(37, 'Kniha test', 110, 110, 'Horor', 10, 'popis test');

-- --------------------------------------------------------

--
-- Table structure for table `kniha_autor`
--

CREATE TABLE `kniha_autor` (
  `id` int(11) NOT NULL,
  `id_kniha` int(11) NOT NULL,
  `id_autor` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `kniha_autor`
--

INSERT INTO `kniha_autor` (`id`, `id_kniha`, `id_autor`) VALUES
(84, 36, 1),
(89, 2, 1),
(90, 3, 2),
(91, 4, 4),
(92, 5, 14),
(94, 13, 9),
(97, 37, 3);

-- --------------------------------------------------------

--
-- Table structure for table `zakaznik`
--

CREATE TABLE `zakaznik` (
  `id` int(11) NOT NULL,
  `jmeno` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `datum_narozeni` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Dumping data for table `zakaznik`
--

INSERT INTO `zakaznik` (`id`, `jmeno`, `datum_narozeni`) VALUES
(1, 'Petr Novák', '1976-02-14'),
(2, 'Jan Kovák', '1988-02-15'),
(3, 'Marie Novotná', '1999-08-24'),
(4, 'Anna Nováková', '1991-05-21'),
(5, 'Duong Xuan Anh', '1998-09-30'),
(6, 'Zakaznik 1', '1977-04-01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `autor`
--
ALTER TABLE `autor`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `doklad`
--
ALTER TABLE `doklad`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `doklad_kniha`
--
ALTER TABLE `doklad_kniha`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `doklad_zakaznik`
--
ALTER TABLE `doklad_zakaznik`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `kniha`
--
ALTER TABLE `kniha`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `kniha_autor`
--
ALTER TABLE `kniha_autor`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `zakaznik`
--
ALTER TABLE `zakaznik`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `autor`
--
ALTER TABLE `autor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `doklad`
--
ALTER TABLE `doklad`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `doklad_kniha`
--
ALTER TABLE `doklad_kniha`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `doklad_zakaznik`
--
ALTER TABLE `doklad_zakaznik`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `kniha`
--
ALTER TABLE `kniha`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `kniha_autor`
--
ALTER TABLE `kniha_autor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=98;

--
-- AUTO_INCREMENT for table `zakaznik`
--
ALTER TABLE `zakaznik`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
