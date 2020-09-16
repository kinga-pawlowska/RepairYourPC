<?php

require_once '../include/user.php';
$repairIdentifier = "";
$login = "";

if(isset($_POST['login']))
{
    $login = $_POST['login'];
}

if(isset($_POST['repairIdentifier']))
{
    $repairIdentifier = $_POST['repairIdentifier'];
}

// Instance of a User class
$userObject = new User();

if(!empty($repairIdentifier) && !empty($login))
{
    $json_array = $userObject->removeOrder($repairIdentifier, $login);
    echo json_encode($json_array);
}

?>