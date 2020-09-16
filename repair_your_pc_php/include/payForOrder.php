<?php

require_once '../include/user.php';

$repairIdentifier = "";

if(isset($_POST['repairIdentifier']))
{
    $repairIdentifier = $_POST['repairIdentifier'];
}

// Instance of a User class
$userObject = new User();

if(!empty($repairIdentifier))
{
    $json_array = $userObject->payForOrder($repairIdentifier);
    echo json_encode($json_array);
}

?>