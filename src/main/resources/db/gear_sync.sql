-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2025 at 10:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gear_sync`
--

-- --------------------------------------------------------

--
-- Table structure for table `car_wash`
--

CREATE TABLE `car_wash` (
  `cwid` int(11) NOT NULL,
  `plan_name` varchar(1000) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `services` varchar(1000) NOT NULL,
  `vehicle_type` varchar(1000) NOT NULL,
  `price` double NOT NULL,
  `picture` varchar(1000) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `car_wash`
--

INSERT INTO `car_wash` (`cwid`, `plan_name`, `description`, `services`, `vehicle_type`, `price`, `picture`, `created_at`, `updated_at`) VALUES
(1, 'Basic Wash', 'Quick exterior clean for regular upkeep.', 'Exterior wash, Spot-free rinse, Hand dry', 'All', 300, 'file:///E:/IntelliJ/Vehicle/car_wash/basic_wash.jpg', '2025-06-18 00:28:45', '2025-06-18 01:30:54'),
(2, 'Deluxe Wash', 'Full-service wash with interior cleaning.', 'Exterior wash, Interior vacuum, Window cleaning, Tire shine', 'Sedan, SUV', 600, 'file:///E:/IntelliJ/Vehicle/car_wash/deluxe_wash.jpg', '2025-06-18 00:28:45', '2025-06-18 01:26:14'),
(3, 'Premium Detailing', 'Comprehensive interior and exterior detailing.', 'Hand wax, Shampoo seats, Engine cleaning, Polish, Vacuum', 'SUV, Van, Truck', 1200, 'file:///E:/IntelliJ/Vehicle/car_wash/premium_detailing.jpg', '2025-06-18 00:28:45', '2025-06-18 01:26:17');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `cid` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`cid`, `username`, `password`, `fullname`, `email`, `address`, `phone`, `picture`, `created_at`) VALUES
(1, 'ezio', '1234', 'Ezio Auditore', 'ezio@ac2.gmail.com', 'Florence, Italy', '01234567891', 'file:/E:/IntelliJ/Vehicle/user_images/customer1.jpg', '2025-06-11 20:23:33');

-- --------------------------------------------------------

--
-- Table structure for table `invoice`
--

CREATE TABLE `invoice` (
  `iid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `invoice`
--

INSERT INTO `invoice` (`iid`, `cid`, `total_price`, `created_at`) VALUES
(1, 1, 6625, '2025-04-25 11:46:31'),
(2, 1, 1375, '2025-04-25 11:47:56'),
(3, 1, 4200, '2025-04-25 11:50:14'),
(5, 1, 2875, '2025-05-14 16:44:40'),
(6, 1, 9825, '2025-05-27 09:58:15'),
(7, 1, 625, '2025-05-27 09:59:21'),
(8, 1, 7125, '2025-06-24 19:15:08'),
(9, 1, 7375, '2025-06-24 19:23:00');

-- --------------------------------------------------------

--
-- Table structure for table `invoice_parts`
--

CREATE TABLE `invoice_parts` (
  `ipid` int(11) NOT NULL,
  `iid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` double NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `invoice_parts`
--

INSERT INTO `invoice_parts` (`ipid`, `iid`, `pid`, `quantity`, `price`, `created_at`) VALUES
(1, 1, 1, 5, 2250, '2025-04-25 11:46:31'),
(2, 1, 4, 5, 4375, '2025-04-25 11:46:31'),
(3, 2, 2, 5, 625, '2025-04-25 11:47:56'),
(4, 2, 5, 5, 750, '2025-04-25 11:47:56'),
(5, 3, 7, 5, 1000, '2025-04-25 11:50:14'),
(6, 3, 9, 4, 3200, '2025-04-25 11:50:14'),
(7, 5, 1, 5, 2250, '2025-05-14 16:44:40'),
(8, 5, 2, 5, 625, '2025-05-14 16:44:40'),
(9, 6, 1, 5, 2250, '2025-05-27 09:58:15'),
(10, 6, 4, 5, 4375, '2025-05-27 09:58:15'),
(11, 6, 9, 4, 3200, '2025-05-27 09:58:15'),
(12, 7, 2, 5, 625, '2025-05-27 09:59:21'),
(13, 8, 4, 5, 4375, '2025-06-24 19:15:08'),
(14, 8, 1, 5, 2250, '2025-06-24 19:15:08'),
(15, 8, 3, 5, 500, '2025-06-24 19:15:08'),
(16, 9, 5, 5, 750, '2025-06-24 19:23:00'),
(17, 9, 4, 5, 4375, '2025-06-24 19:23:00'),
(18, 9, 1, 5, 2250, '2025-06-24 19:23:00');

-- --------------------------------------------------------

--
-- Table structure for table `msg_to_admin`
--

CREATE TABLE `msg_to_admin` (
  `mid` int(11) NOT NULL,
  `cid` int(11) DEFAULT NULL,
  `wid` int(11) DEFAULT NULL,
  `cw_or_admin` varchar(255) NOT NULL,
  `message` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `msg_to_admin`
--

INSERT INTO `msg_to_admin` (`mid`, `cid`, `wid`, `cw_or_admin`, `message`, `created_at`) VALUES
(1, 1, NULL, '1', 'hello', '2025-04-04 15:37:31'),
(2, 1, NULL, '0', 'what are u doing?', '2025-04-04 15:37:38'),
(3, 1, NULL, '1', 'i am fine rn!', '2025-04-04 15:37:57'),
(4, 1, NULL, '0', 'oh alr then:))', '2025-04-04 15:38:06'),
(5, 1, NULL, '0', 'i am the admin!!', '2025-04-04 15:39:25'),
(6, 1, NULL, '1', 'oh my name is ezio', '2025-04-04 15:39:33'),
(7, NULL, 1, '0', 'hey,,', '2025-04-04 15:41:45'),
(8, NULL, 1, '1', 'hello', '2025-04-04 15:41:48'),
(9, NULL, 1, '0', 'i am the admin', '2025-04-04 15:41:55'),
(10, NULL, 1, '1', 'my name is connor', '2025-04-04 15:42:01'),
(11, NULL, 1, '0', 'fine then !', '2025-04-04 15:42:10'),
(12, NULL, 1, '1', 'see ya', '2025-04-04 15:42:14'),
(13, NULL, 1, '0', 'hello', '2025-04-04 15:43:21'),
(14, NULL, 1, '1', 'how are u?', '2025-04-04 15:43:26'),
(15, 1, NULL, '0', 'how are u?', '2025-05-27 10:08:48'),
(16, 1, NULL, '1', 'i am fine!', '2025-05-27 10:09:28'),
(17, 1, NULL, '1', '', '2025-05-27 10:09:28'),
(18, 1, NULL, '0', 'ok', '2025-05-27 10:10:05');

-- --------------------------------------------------------

--
-- Table structure for table `msg_wtc`
--

CREATE TABLE `msg_wtc` (
  `mid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `wid` int(11) NOT NULL,
  `cstmr_or_wrkr` varchar(255) NOT NULL,
  `message` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `msg_wtc`
--

INSERT INTO `msg_wtc` (`mid`, `cid`, `wid`, `cstmr_or_wrkr`, `message`, `created_at`) VALUES
(1, 1, 1, '1', 'hey', '2025-04-22 01:03:41'),
(2, 1, 1, '0', 'what ar u doing', '2025-04-22 01:03:47'),
(3, 1, 1, '1', 'i am fine yo!', '2025-04-22 01:03:55'),
(4, 1, 1, '0', 'thats good!!', '2025-04-22 01:04:02'),
(5, 1, 1, '1', 'what u doing?', '2025-04-22 01:08:18'),
(6, 1, 1, '0', 'nothing,,just chillin :)', '2025-04-22 01:08:30'),
(7, 1, 1, '1', 'hey', '2025-05-14 16:45:54'),
(8, 1, 1, '0', 'what are u doing', '2025-05-14 16:46:02'),
(9, 1, 1, '1', 'i am fine!', '2025-05-27 10:12:29'),
(10, 1, 1, '0', 'ok', '2025-05-27 10:12:35'),
(11, 1, 1, '0', '', '2025-05-27 10:12:35');

-- --------------------------------------------------------

--
-- Table structure for table `parts`
--

CREATE TABLE `parts` (
  `pid` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `available_unit` int(11) NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `picture` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `parts`
--

INSERT INTO `parts` (`pid`, `name`, `description`, `price`, `available_unit`, `added_at`, `updated_at`, `picture`) VALUES
(1, 'Brake Pad Set', 'High-quality ceramic brake pads for smooth braking', 450, 50, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/brake_pad.jpg'),
(2, 'Air Filter', 'Engine air filter suitable for most sedans', 125, 35, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/air_filter.jpg'),
(3, 'Oil Filter', 'Premium oil filter for better engine performance', 100, 35, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/oil_filter.jpg'),
(4, 'Spark Plug', 'Iridium spark plug with extended lifespan', 875, 40, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/spark_plug.jpg'),
(5, 'Radiator Hose', 'Durable rubber hose for car radiator', 150, 35, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/radiator_hose.jpg'),
(6, 'Headlight Assembly', 'LED headlight unit for improved visibility', 950, 20, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/headlight_assembly.jpg'),
(7, 'Windshield Wiper Set', 'All-weather wiper blades for front windshield', 200, 40, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/windshield_wiper.jpg'),
(8, 'Fuel Pump', 'Electric fuel pump for consistent fuel delivery', 650, 20, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/fuel_pump.jpg'),
(9, 'Clutch Plate', 'Heavy-duty clutch plate for manual transmission vehicles', 800, 10, '2025-04-22 01:45:14', '2025-04-22 01:45:14', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/clutch_plate.jpg'),
(10, 'Car Tires', 'Compact spare tire for emergency use', 1000, 20, '2025-06-17 17:33:38', '2025-06-17 17:33:38', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/car_tires_0b94a054.png'),
(11, 'Alternator', ' High output alternator suitable for most vehicles.', 750, 20, '2025-06-17 17:36:46', '2025-06-17 17:36:46', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/alternator_978ace29.jpg'),
(12, 'Engine Mount', 'Rubber engine mount to reduce vibration and noise.', 550, 10, '2025-06-18 03:27:00', '2025-06-18 03:27:00', 'file:///E:/IntelliJ/Vehicle/vehicle_parts/_f5d4975a.png');

-- --------------------------------------------------------

--
-- Table structure for table `receipt`
--

CREATE TABLE `receipt` (
  `rid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `wid` int(11) DEFAULT NULL,
  `cwid` int(11) NOT NULL,
  `car_location` varchar(1000) NOT NULL,
  `car_model` varchar(1000) NOT NULL,
  `time` varchar(1000) NOT NULL,
  `date` date NOT NULL,
  `picture` varchar(1000) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `receipt`
--

INSERT INTO `receipt` (`rid`, `cid`, `wid`, `cwid`, `car_location`, `car_model`, `time`, `date`, `picture`, `created_at`) VALUES
(2, 1, 1, 2, 'Airport, Dhaka', 'Honda CX-5', '9.00 - 11.00', '2025-06-21', 'file:///E:/IntelliJ/Vehicle/vehicle_wash/customer_1_18-06-2025_08-54-45-787.jpg', '2025-06-24 16:14:55'),
(3, 1, 1, 3, 'Notun Bazar, Dhaka', 'Toyota NZ-5', '11.00 - 5.00', '2025-06-24', 'file:///E:/IntelliJ/Vehicle/vehicle_wash/customer_1_18-06-2025_09-10-24-962.jpg', '2025-06-24 16:05:33');

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

CREATE TABLE `service` (
  `sid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `wid` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `vehicle_type` varchar(255) NOT NULL,
  `payment_amount` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `picture` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service`
--

INSERT INTO `service` (`sid`, `cid`, `wid`, `title`, `description`, `vehicle_type`, `payment_amount`, `status`, `picture`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 'Broken Car Window', 'My car window is completely broken. New window needed.', 'Private Car', '500.0', 'Completed', 'file:///E:/IntelliJ/Vehicle/vehicle_images/Customer_1_26-04-2025_10-15-22-123.jpg', '2025-06-17 15:51:07', '2025-06-17 15:51:07'),
(2, 1, 1, 'Brake Pad Replacement Needed', 'Hearing screeching noises whenever I brake. Braking distance has increased too. Suspect the brake pads are completely worn out.', 'Honda Civic 2018', '5000.0', 'Completed', 'file:///E:/IntelliJ/Vehicle/vehicle_images/Customer_1_26-04-2025_10-19-08-789.jpg', '2025-06-17 15:31:47', '2025-06-18 03:19:09'),
(3, 1, 1, 'Air Conditioning Not Working', 'AC was blowing cold air last week but now only hot air is coming out. Might be a refrigerant leak or compressor failure.', 'Hyundai Elantra 2017', '15000.0', 'In Progress', 'file:///E:/IntelliJ/Vehicle/vehicle_images/Customer_1_26-04-2025_10-25-55-678.jpg', '2025-06-17 15:33:50', '2025-06-18 03:19:28'),
(4, 1, 1, 'Oil Leak Under Vehicle', 'Found a puddle of dark oil under my car after parking overnight. I think there is a leak from the oil pan or gasket.', 'Ford F-150 2016', '10000.0', 'In Progress', 'file:///E:/IntelliJ/Vehicle/vehicle_images/Customer_1_26-04-2025_10-22-11-234.jpg', '2025-06-17 15:36:27', '2025-06-17 15:36:27'),
(5, 1, 1, 'Engine Overheating Issue', 'My car\'s engine temperature rises very quickly even during short drives. Noticed slight steam coming from under the hood after driving for just 15 minutes.', 'Toyota Corolla 2015', '15000.0', 'In Progress', 'file:///E:/IntelliJ/Vehicle/vehicle_images/Customer_1_26-04-2025_10-17-45-456.jpg', '2025-04-26 09:32:29', '2025-06-24 16:11:17'),
(6, 1, 1, 'Tire replacement', '3 of the tires need replacing...', 'Cycle', '1500.0', 'In Progress', 'file:///E:/IntelliJ/Vehicle/vehicle_images/customer_1_27-05-2025_15-49-11-770.jpg', '2025-05-27 09:51:10', '2025-06-24 16:12:37');

-- --------------------------------------------------------

--
-- Table structure for table `worker`
--

CREATE TABLE `worker` (
  `wid` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `worker`
--

INSERT INTO `worker` (`wid`, `username`, `password`, `fullname`, `email`, `phone`, `status`, `picture`, `created_at`) VALUES
(1, 'connor', '1234', 'Connor Kenway', 'connor@ac3.gmail.com', '01234567892', 'Verified', 'file:/E:/IntelliJ/Vehicle/user_images/Worker1.jpg', '2025-06-18 01:33:37');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `car_wash`
--
ALTER TABLE `car_wash`
  ADD PRIMARY KEY (`cwid`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `invoice`
--
ALTER TABLE `invoice`
  ADD PRIMARY KEY (`iid`),
  ADD KEY `customer_id4` (`cid`);

--
-- Indexes for table `invoice_parts`
--
ALTER TABLE `invoice_parts`
  ADD PRIMARY KEY (`ipid`),
  ADD KEY `invoice_id` (`iid`),
  ADD KEY `parts_id` (`pid`);

--
-- Indexes for table `msg_to_admin`
--
ALTER TABLE `msg_to_admin`
  ADD PRIMARY KEY (`mid`),
  ADD KEY `customer_id2` (`cid`),
  ADD KEY `worker_id2` (`wid`);

--
-- Indexes for table `msg_wtc`
--
ALTER TABLE `msg_wtc`
  ADD PRIMARY KEY (`mid`),
  ADD KEY `customer_id3` (`cid`),
  ADD KEY `worker_id3` (`wid`);

--
-- Indexes for table `parts`
--
ALTER TABLE `parts`
  ADD PRIMARY KEY (`pid`);

--
-- Indexes for table `receipt`
--
ALTER TABLE `receipt`
  ADD PRIMARY KEY (`rid`),
  ADD KEY `customer_id5` (`cid`),
  ADD KEY `worker_id4` (`wid`),
  ADD KEY `car_wash_id` (`cwid`);

--
-- Indexes for table `service`
--
ALTER TABLE `service`
  ADD PRIMARY KEY (`sid`),
  ADD KEY `customer_id` (`cid`),
  ADD KEY `worker_id` (`wid`);

--
-- Indexes for table `worker`
--
ALTER TABLE `worker`
  ADD PRIMARY KEY (`wid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `car_wash`
--
ALTER TABLE `car_wash`
  MODIFY `cwid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `invoice`
--
ALTER TABLE `invoice`
  MODIFY `iid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `invoice_parts`
--
ALTER TABLE `invoice_parts`
  MODIFY `ipid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `msg_to_admin`
--
ALTER TABLE `msg_to_admin`
  MODIFY `mid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `msg_wtc`
--
ALTER TABLE `msg_wtc`
  MODIFY `mid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `parts`
--
ALTER TABLE `parts`
  MODIFY `pid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `receipt`
--
ALTER TABLE `receipt`
  MODIFY `rid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `service`
--
ALTER TABLE `service`
  MODIFY `sid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `worker`
--
ALTER TABLE `worker`
  MODIFY `wid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `invoice`
--
ALTER TABLE `invoice`
  ADD CONSTRAINT `customer_id4` FOREIGN KEY (`cid`) REFERENCES `customer` (`cid`);

--
-- Constraints for table `invoice_parts`
--
ALTER TABLE `invoice_parts`
  ADD CONSTRAINT `invoice_id` FOREIGN KEY (`iid`) REFERENCES `invoice` (`iid`),
  ADD CONSTRAINT `parts_id` FOREIGN KEY (`pid`) REFERENCES `parts` (`pid`);

--
-- Constraints for table `msg_to_admin`
--
ALTER TABLE `msg_to_admin`
  ADD CONSTRAINT `customer_id2` FOREIGN KEY (`cid`) REFERENCES `customer` (`cid`),
  ADD CONSTRAINT `worker_id2` FOREIGN KEY (`wid`) REFERENCES `worker` (`wid`);

--
-- Constraints for table `msg_wtc`
--
ALTER TABLE `msg_wtc`
  ADD CONSTRAINT `customer_id3` FOREIGN KEY (`cid`) REFERENCES `customer` (`cid`),
  ADD CONSTRAINT `worker_id3` FOREIGN KEY (`wid`) REFERENCES `worker` (`wid`);

--
-- Constraints for table `receipt`
--
ALTER TABLE `receipt`
  ADD CONSTRAINT `car_wash_id` FOREIGN KEY (`cwid`) REFERENCES `car_wash` (`cwid`),
  ADD CONSTRAINT `customer_id5` FOREIGN KEY (`cid`) REFERENCES `customer` (`cid`),
  ADD CONSTRAINT `worker_id4` FOREIGN KEY (`wid`) REFERENCES `worker` (`wid`);

--
-- Constraints for table `service`
--
ALTER TABLE `service`
  ADD CONSTRAINT `customer_id` FOREIGN KEY (`cid`) REFERENCES `customer` (`cid`),
  ADD CONSTRAINT `worker_id` FOREIGN KEY (`wid`) REFERENCES `worker` (`wid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
