<?php

require_once '../include/user.php';

// Instance of a User class
$userObject = new User();

// Read orders
$json_read_orders = $userObject->readAllOrders();
echo json_encode($json_read_orders);


?>