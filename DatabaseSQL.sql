-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2015 at 12:31 AM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `frauddetectiondb`
--
CREATE DATABASE IF NOT EXISTS `frauddetectiondb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `frauddetectiondb`;

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `do_atm_transaction`(
amount decimal(15,2), 
CardNo varchar(20), 
CardPIN varchar(8),
out current_available_balance   decimal(15,2),
out isSuccess varchar(20)
)
BEGIN
declare v_branch varchar(4);
 declare v_acc varchar(11);

declare v_valance decimal(15,2);
declare v_valance_Current decimal(15,2);

declare   v_count int default 0;

SELECT 
    COUNT(*)
INTO v_count FROM
    atm_card_info t
WHERE
    t.card_number = CardNo
        AND t.card_pin_no = CardPIN;

if v_count=0 then
set isSuccess='0';
set current_available_balance=0; 

else
 

SELECT 
    t.branch_id, t.account_no
INTO v_branch , v_acc FROM
    atm_card_info t
WHERE
    t.card_number = CardNo
        AND t.card_pin_no = CardPIN;
        
SELECT 
    t.available_balance
INTO v_valance FROM
    account t
WHERE
    t.branch_id = v_branch
        AND t.account_no = v_acc;
 
     
		if amount>v_valance  then
			set isSuccess='0';
			set current_available_balance=0; 
		else
			set  v_valance_Current=  v_valance - amount;
			UPDATE account t 
SET 
    t.available_balance = v_valance_Current
WHERE
    t.branch_id = v_branch
        AND t.account_no = v_acc;
			set current_available_balance=v_valance_Current; 
            set isSuccess='1';
		end if;

end if;    

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `do_online_transaction`(
pfrom_account varchar(12), 
pto_account varchar(12), 
pamount decimal(15,2) ,
-- puser_id  varchar(50),
 out current_available_balance   decimal(15,2),
out isSuccess varchar(20)
)
BEGIN
 declare v_branch varchar(4);
 declare v_acc varchar(11); 
 declare v_tobranch varchar(4);
 declare v_toacc varchar(11); 

declare v_valance decimal(15,2);
declare v_valance_Current decimal(15,2);

-- declare v_customer_id int default 0; 
declare   v_count int default 0;

  
 
 set @v_branch=substr(pfrom_account,1,4);
set @v_acc=substr(pfrom_account,6);

set @v_tobranch=substr(pto_account,1,4);
set @v_toacc=substr(pto_account,6);

SELECT 
    COUNT(*)
INTO v_count FROM
    account t
WHERE
    t.branch_id = @v_branch
        AND t.account_no = @v_acc;
        
SELECT @v_tobranch;
SELECT @v_toacc;  

if v_count=0 then
set isSuccess='0';
set current_available_balance=0; 

else
 
 
        
SELECT 
    t.available_balance
INTO v_valance FROM
    account t
WHERE
    t.branch_id = @v_branch
        AND t.account_no = @v_acc;
 
     
		if pamount>v_valance  then
			set isSuccess='0';
			set current_available_balance=0; 
		else
        
								
				set  v_valance_Current=  v_valance - pamount;
									UPDATE account t 
SET 
    t.available_balance = v_valance_Current
WHERE
    t.branch_id = @v_branch
        AND t.account_no = @v_acc;
								
									set current_available_balance=v_valance_Current; 
									set isSuccess='1';
									
									-- credit amount to account
									
						SELECT 
    t.available_balance
INTO v_valance FROM
    account t
WHERE
    t.branch_id = @v_tobranch
        AND t.account_no = @v_toacc;
								
						
                        set  v_valance_Current=  v_valance + pamount;		 
						
UPDATE account t 
SET 
    t.available_balance = v_valance_Current
WHERE
    t.branch_id = @v_tobranch
        AND t.account_no = @v_toacc;
        
		end if;

end if;    

END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE IF NOT EXISTS `account` (
  `account_no` varchar(11) NOT NULL,
  `branch_id` varchar(4) NOT NULL,
  `account_name` varchar(45) NOT NULL,
  `product_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `available_balance` decimal(15,2) NOT NULL,
  `total_balance` decimal(15,2) NOT NULL,
  `hold_amount` decimal(15,2) DEFAULT '0.00',
  `account_open_date` datetime DEFAULT NULL,
  `make_date` datetime DEFAULT NULL,
  `account_status` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`account_no`, `branch_id`, `account_name`, `product_id`, `customer_id`, `available_balance`, `total_balance`, `hold_amount`, `account_open_date`, `make_date`, `account_status`) VALUES
('0000001', '0031', 'Customer1', 1, 1, '4994659.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1),
('0000002', '0031', 'Customer1', 1, 1, '4990022.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1),
('0000003', '0045', 'Customer1', 2, 1, '5000000.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1),
('0000004', '0045', 'Customer2', 1, 2, '4994701.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1),
('0000005', '0065', 'Customer2', 1, 2, '5004500.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1),
('0000006', '0065', 'Customer2', 2, 2, '5000500.00', '10000000.00', '0.00', '2015-06-06 00:00:00', '2015-06-06 00:00:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `account_type`
--

CREATE TABLE IF NOT EXISTS `account_type` (
`type_id` int(11) NOT NULL,
  `type_nm` varchar(45) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account_type`
--

INSERT INTO `account_type` (`type_id`, `type_nm`) VALUES
(1, 'Savings Account'),
(2, 'Current Account');

-- --------------------------------------------------------

--
-- Table structure for table `atm_card_info`
--

CREATE TABLE IF NOT EXISTS `atm_card_info` (
`id` int(11) NOT NULL,
  `card_number` varchar(20) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `customer_nm` varchar(45) NOT NULL,
  `card_pin_no` varchar(8) NOT NULL,
  `account_no` varchar(11) NOT NULL,
  `branch_id` varchar(4) NOT NULL,
  `card_expiry_date` datetime NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `atm_card_info`
--

INSERT INTO `atm_card_info` (`id`, `card_number`, `customer_id`, `customer_nm`, `card_pin_no`, `account_no`, `branch_id`, `card_expiry_date`) VALUES
(4, '1234-5678-999', 1, 'Cutomer1', '1234', '0000001', '0031', '2018-12-12 00:00:00'),
(5, '1234-5678-997', 1, 'Cutomer1', '1234', '0000002', '0031', '2018-12-12 00:00:00'),
(6, '1234-5678-996', 1, 'Cutomer1', '1234', '0000003', '0045', '2018-12-12 00:00:00'),
(10, '1234-5678-995', 2, 'Cutomer2', '1234', '0000004', '0045', '2018-12-12 00:00:00'),
(11, '1234-5678-994', 2, 'Cutomer2', '1234', '0000005', '0065', '2018-12-12 00:00:00'),
(12, '1234-5678-993', 2, 'Cutomer2', '1234', '0000006', '0065', '2018-12-12 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `bank_branch`
--

CREATE TABLE IF NOT EXISTS `bank_branch` (
  `branch_id` varchar(4) NOT NULL,
  `branch_name` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bank_branch`
--

INSERT INTO `bank_branch` (`branch_id`, `branch_name`) VALUES
('0031', 'Branch1'),
('0045', 'Branch2'),
('0065', 'Branch3');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE IF NOT EXISTS `customer` (
`customer_id` int(11) NOT NULL,
  `customer_nm` varchar(45) NOT NULL,
  `customer_address` varchar(45) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `customer_nm`, `customer_address`) VALUES
(1, 'customer1', 'Dhaka,Bangladesh'),
(2, 'customer2', 'London,England'),
(3, 'customer3', 'Japan');

-- --------------------------------------------------------

--
-- Table structure for table `distance`
--

CREATE TABLE IF NOT EXISTS `distance` (
`id` int(11) NOT NULL,
  `from` varchar(45) DEFAULT NULL,
  `to` varchar(45) DEFAULT NULL,
  `distance` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `functionality`
--

CREATE TABLE IF NOT EXISTS `functionality` (
`id` int(11) NOT NULL,
  `function_name` varchar(45) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `functionality`
--

INSERT INTO `functionality` (`id`, `function_name`) VALUES
(1, 'online transaction'),
(2, 'atm transaction');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE IF NOT EXISTS `products` (
`id` int(11) NOT NULL,
  `product_nm` varchar(45) NOT NULL,
  `account_type` int(11) DEFAULT NULL,
  `Debit` char(1) DEFAULT NULL,
  `Credit` char(1) DEFAULT NULL,
  `Maximum_Debit_amount` decimal(15,2) DEFAULT NULL,
  `Maximum_Credit_amount` decimal(15,2) DEFAULT NULL,
  `Total_No_Credit_allowed_per_day` int(11) DEFAULT NULL,
  `Total_No_Dredit_allowed_per_day` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `product_nm`, `account_type`, `Debit`, `Credit`, `Maximum_Debit_amount`, `Maximum_Credit_amount`, `Total_No_Credit_allowed_per_day`, `Total_No_Dredit_allowed_per_day`) VALUES
(1, 'Savings1', 1, '1', '1', '20000.00', '20000.00', 20, 20),
(2, 'Savings2', 1, '1', '1', '20000.00', '20000.00', 20, 20),
(3, 'Current1', 2, '1', '1', '20000.00', '20000.00', 20, 20),
(4, 'Current2', 2, '1', '1', '20000.00', '20000.00', 20, 20);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE IF NOT EXISTS `role` (
  `role_nm` varchar(45) DEFAULT NULL,
`role_id` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`role_nm`, `role_id`) VALUES
('ATM', 1),
('ONLINE', 2),
('SUPERUSER', 3);

-- --------------------------------------------------------

--
-- Table structure for table `role_functions`
--

CREATE TABLE IF NOT EXISTS `role_functions` (
  `role` int(11) DEFAULT NULL,
  `function_id` int(11) DEFAULT NULL,
`id` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role_functions`
--

INSERT INTO `role_functions` (`role`, `function_id`, `id`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 1, 3),
(3, 2, 4);

-- --------------------------------------------------------

--
-- Table structure for table `transaction_history`
--

CREATE TABLE IF NOT EXISTS `transaction_history` (
`id` int(11) NOT NULL,
  `account_no` varchar(11) NOT NULL,
  `branch_id` varchar(4) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `Dr_Cr` varchar(2) NOT NULL,
  `transaction_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transaction_type` int(11) NOT NULL,
  `user_id` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `transaction_rule`
--

CREATE TABLE IF NOT EXISTS `transaction_rule` (
  `trans_type_id` int(11) NOT NULL,
  `trans_type_nm` varchar(45) DEFAULT NULL,
  `maximum_number_of_credit_per_day` int(11) DEFAULT NULL,
  `maximum_number_of_debit_per_day` int(11) DEFAULT NULL,
  `maximum_credit_amount_per_transaction` decimal(15,2) DEFAULT NULL,
  `maximum_debit_amount_per_transaction` decimal(15,2) DEFAULT NULL,
  `total_amount_credit_per_day` decimal(15,2) DEFAULT NULL,
  `total_amount_debit_per_day` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `creation_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `role` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `password`, `creation_dt`, `role`, `customer_id`) VALUES
('user1', 'abc123', '2015-06-06 00:22:10', 1, 1),
('user2', 'abc123', '2015-06-06 00:23:04', 2, 2),
('user3', 'abc123', '2015-06-06 00:24:14', 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `user_activity_log`
--

CREATE TABLE IF NOT EXISTS `user_activity_log` (
`id` int(11) NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `login_region` varchar(500) DEFAULT NULL,
  `IP_Address` varchar(45) DEFAULT NULL,
  `Machine_name` varchar(150) DEFAULT NULL,
  `Login_attempt_success` int(1) DEFAULT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `is_logged_in` int(1) DEFAULT NULL,
  `distance` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_sequirity_questions`
--

CREATE TABLE IF NOT EXISTS `user_sequirity_questions` (
  `id` int(11) NOT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `Question` varchar(500) DEFAULT NULL,
  `Answer` varchar(500) DEFAULT NULL,
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
 ADD PRIMARY KEY (`account_no`,`branch_id`), ADD KEY `fk_account_1_idx` (`product_id`), ADD KEY `fk_account_2_idx` (`customer_id`), ADD KEY `fk_account_3_idx` (`branch_id`);

--
-- Indexes for table `account_type`
--
ALTER TABLE `account_type`
 ADD PRIMARY KEY (`type_id`);

--
-- Indexes for table `atm_card_info`
--
ALTER TABLE `atm_card_info`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_atm_card_info_1_idx` (`customer_id`), ADD KEY `fk_atm_card_info_2_idx` (`account_no`,`branch_id`);

--
-- Indexes for table `bank_branch`
--
ALTER TABLE `bank_branch`
 ADD PRIMARY KEY (`branch_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
 ADD PRIMARY KEY (`customer_id`);

--
-- Indexes for table `distance`
--
ALTER TABLE `distance`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `functionality`
--
ALTER TABLE `functionality`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_products_1_idx` (`account_type`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
 ADD PRIMARY KEY (`role_id`);

--
-- Indexes for table `role_functions`
--
ALTER TABLE `role_functions`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_role_functions_1_idx` (`role`), ADD KEY `fk_role_functions_2_idx` (`function_id`);

--
-- Indexes for table `transaction_history`
--
ALTER TABLE `transaction_history`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_transaction_history_idx` (`account_no`,`branch_id`), ADD KEY `fk_transaction_history2_idx` (`transaction_type`), ADD KEY `fk_transaction_history3_idx` (`user_id`);

--
-- Indexes for table `transaction_rule`
--
ALTER TABLE `transaction_rule`
 ADD PRIMARY KEY (`trans_type_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
 ADD PRIMARY KEY (`user_id`), ADD KEY `fk_user_1_idx` (`customer_id`), ADD KEY `fk_user_2_idx` (`role`);

--
-- Indexes for table `user_activity_log`
--
ALTER TABLE `user_activity_log`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_user_activity_log_1_idx` (`user_id`), ADD KEY `fk_user_activity_log_2_idx` (`distance`);

--
-- Indexes for table `user_sequirity_questions`
--
ALTER TABLE `user_sequirity_questions`
 ADD PRIMARY KEY (`id`), ADD KEY `fk_user_sequrity_questions1_idx` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account_type`
--
ALTER TABLE `account_type`
MODIFY `type_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `atm_card_info`
--
ALTER TABLE `atm_card_info`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=13;
--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `distance`
--
ALTER TABLE `distance`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `functionality`
--
ALTER TABLE `functionality`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `role_functions`
--
ALTER TABLE `role_functions`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `transaction_history`
--
ALTER TABLE `transaction_history`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user_activity_log`
--
ALTER TABLE `user_activity_log`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `account`
--
ALTER TABLE `account`
ADD CONSTRAINT `fk_account_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_account_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_account_3` FOREIGN KEY (`branch_id`) REFERENCES `bank_branch` (`branch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `atm_card_info`
--
ALTER TABLE `atm_card_info`
ADD CONSTRAINT `fk_atm_card_info_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_atm_card_info_2` FOREIGN KEY (`account_no`, `branch_id`) REFERENCES `account` (`account_no`, `branch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `products`
--
ALTER TABLE `products`
ADD CONSTRAINT `fk_products_1` FOREIGN KEY (`account_type`) REFERENCES `account_type` (`type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `role_functions`
--
ALTER TABLE `role_functions`
ADD CONSTRAINT `fk_role_functions_1` FOREIGN KEY (`role`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_role_functions_2` FOREIGN KEY (`function_id`) REFERENCES `functionality` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `transaction_history`
--
ALTER TABLE `transaction_history`
ADD CONSTRAINT `fk_transaction_history1` FOREIGN KEY (`account_no`, `branch_id`) REFERENCES `account` (`account_no`, `branch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_transaction_history2` FOREIGN KEY (`transaction_type`) REFERENCES `transaction_rule` (`trans_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_transaction_history3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
ADD CONSTRAINT `fk_user_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_user_2` FOREIGN KEY (`role`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `user_activity_log`
--
ALTER TABLE `user_activity_log`
ADD CONSTRAINT `fk_user_activity_log_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_user_activity_log_2` FOREIGN KEY (`distance`) REFERENCES `distance` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `user_sequirity_questions`
--
ALTER TABLE `user_sequirity_questions`
ADD CONSTRAINT `fk_user_sequrity_questions1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
