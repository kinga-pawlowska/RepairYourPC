<?php

require_once '../include/user.php';
$login = "";
$repairIdentifier = "";
$manufacturer = "";
$kindOfHardware = "";
$model = "";
$service = "";
$operatingSystem = "";
$description = "";
$link = "";

if(isset($_POST['login']))
{
    $login = $_POST['login'];
}

if(isset($_POST['repairIdentifier']))
{
    $repairIdentifier = $_POST['repairIdentifier'];
}

if(isset($_POST['manufacturer']))
{
    $manufacturer = $_POST['manufacturer'];
}

if(isset($_POST['kindOfHardware']))
{
    $kindOfHardware = $_POST['kindOfHardware'];
}

if(isset($_POST['model']))
{
    $model = $_POST['model'];
}

if(isset($_POST['service']))
{
    $service = $_POST['service'];
}

if(isset($_POST['operatingSystem']))
{
    $operatingSystem = $_POST['operatingSystem'];
}

if(isset($_POST['description']))
{
    $description = $_POST['description'];
}

if(isset($_POST['link']))
{
    $link = $_POST['link'];
}

// Instance of a User class
$userObject = new User();

// Registration of new order
if(!empty($login) && !empty($repairIdentifier) && !empty($kindOfHardware) && !empty($service) && !empty($operatingSystem) && !empty($description))
{
    $json_registration_order = $userObject->createNewRegisterOrder($login, $repairIdentifier, $manufacturer, $kindOfHardware, $model, $service, $operatingSystem, $description, $link);
    echo json_encode($json_registration_order);
}

?>