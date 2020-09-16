<?php

require_once '../include/user.php';

$login = "";

if(isset($_POST['login']))
{
    $login = $_POST['login'];
}

// Instance of a User class
$userObject = new User();

// Read orders
if(!empty($login))
{
    $json_read_orders = $userObject->readOrders($login);
    echo json_encode($json_read_orders);
}


?>