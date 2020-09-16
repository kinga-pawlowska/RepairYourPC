-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Czas generowania: 29 Kwi 2017, 01:38
-- Wersja serwera: 10.1.16-MariaDB
-- Wersja PHP: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `repair_your_pc`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `login` varchar(18) NOT NULL,
  `repairIdentifier` varchar(30) NOT NULL,
  `manufacturer` varchar(100) DEFAULT NULL,
  `kindOfHardware` varchar(10) NOT NULL,
  `model` varchar(100) DEFAULT NULL,
  `service` varchar(20) NOT NULL,
  `operatingSystem` varchar(10) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `link` varchar(200) DEFAULT NULL,
  `repairStatus` varchar(30) NOT NULL,
  `estimatedCost` varchar(10) NOT NULL,
  `paid` varchar(3) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Zrzut danych tabeli `orders`
--

INSERT INTO `orders` (`id`, `login`, `repairIdentifier`, `manufacturer`, `kindOfHardware`, `model`, `service`, `operatingSystem`, `description`, `link`, `repairStatus`, `estimatedCost`, `paid`, `created_at`, `updated_at`) VALUES
(38, 'kinga', 'kinga170420010524', 'Lenovo', 'laptop', 'y510p', 'warranty', 'Windows', 'System doesnt get up', 'http://dropbox.com/', 'IN PROCESS', '1000', 'YES', '2017-04-20 01:05:24', '2017-04-20 01:11:48'),
(39, 'anna', 'anna170429003549', 'Lenovo', 'laptop', 'y500', 'warranty', 'Windows', 'System not working', '', 'PENDING', '0 PLN', 'NO', '2017-04-29 00:35:50', '2017-04-29 01:30:52');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` int(20) NOT NULL,
  `login` varchar(18) CHARACTER SET latin1 NOT NULL,
  `password` varchar(40) CHARACTER SET latin1 NOT NULL,
  `email` varchar(50) CHARACTER SET latin1 NOT NULL,
  `firstName` varchar(20) CHARACTER SET latin1 NOT NULL,
  `lastName` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `streetName` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `streetNumber` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `zipCode` varchar(6) COLLATE utf8_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `phoneNumber` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`id`, `login`, `password`, `email`, `firstName`, `lastName`, `streetName`, `streetNumber`, `zipCode`, `city`, `phoneNumber`, `created_at`, `updated_at`) VALUES
(46, 'kinga', '880bac4d4f87e0a71db4d476e8587c88', 'sekretnymail@gmail.com', 'Kinga', 'Pawlowska', 'Sekretna', '666', '66-666', 'Sekretne', '666666666', '2017-04-20 01:03:20', '2017-04-20 01:03:20'),
(47, 'zbigniew', 'b80526194670e0b7fca68d79fe321ecf', 'szadko@gmail.com', 'Zbigniew', 'Szadkowski', 'Pomorska', '33', '11-111', 'Lodz', '999999999', '2017-04-20 10:42:36', '2017-04-20 10:42:36'),
(48, 'anna', 'a70f9e38ff015afaa9ab0aacabee2e13', 'annakowalska@gmail.com', 'Anna', 'Kowalska', 'Sekret', '45', '78-123', 'Lodz', '648521478', '2017-04-29 00:22:36', '2017-04-29 00:22:36');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `repairID` (`login`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT dla tabeli `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;
--
-- AUTO_INCREMENT dla tabeli `users`
--
ALTER TABLE `users`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
