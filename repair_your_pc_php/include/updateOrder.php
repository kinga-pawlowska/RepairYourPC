<?php

require_once '../include/user.php';

$repairIdentifier = "";
$repairStatus = "";
$estimatedCost = "";
$paid = "";

if(isset($_POST['repairIdentifier']))
{
    $repairIdentifier = $_POST['repairIdentifier'];
}

if(isset($_POST['repairStatus']))
{
    $repairStatus = $_POST['repairStatus'];
}

if(isset($_POST['estimatedCost']))
{
    $estimatedCost = $_POST['estimatedCost'];
}

if(isset($_POST['paid']))
{
    $paid = $_POST['paid'];
}

// Instance of a User class
$userObject = new User();

if(!empty($repairIdentifier) && !empty($repairStatus) && !empty($estimatedCost) && !empty($paid))
{
    $json_array = $userObject->updateOrder($repairIdentifier, $repairStatus, $estimatedCost, $paid);
    echo json_encode($json_array);
}

?>