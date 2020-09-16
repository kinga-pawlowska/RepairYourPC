<?php

require_once '../include/user.php';
$login = "";

if(isset($_POST['login']))
{
    $login = $_POST['login'];
}

// Instance of a User class
$userObject = new User();

if(!empty($login))
{
    $json_array = $userObject->removeUser($login);
    echo json_encode($json_array);
}

?>