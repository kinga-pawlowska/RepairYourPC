<?php
require_once 'include/user.php';
$login = "";
$password = "";
$email = "";
$firstName = "";
$lastName = "";
$streetName = "";
$streetNumber = "";
$zipCode = "";
$city = "";
$phoneNumber = "";

if(isset($_POST['login']))
{
    $login = $_POST['login'];
}

if(isset($_POST['password']))
{
    $password = $_POST['password'];
}

if(isset($_POST['email']))
{
    $email = $_POST['email'];
}

if(isset($_POST['firstName']))
{
    $firstName = $_POST['firstName'];
}

if(isset($_POST['lastName']))
{
    $lastName = $_POST['lastName'];
}

if(isset($_POST['streetName']))
{
    $streetName = $_POST['streetName'];
}

if(isset($_POST['streetNumber']))
{
    $streetNumber = $_POST['streetNumber'];
}

if(isset($_POST['zipCode']))
{
    $zipCode = $_POST['zipCode'];
}

if(isset($_POST['city']))
{
    $city = $_POST['city'];
}

if(isset($_POST['phoneNumber']))
{
    $phoneNumber = $_POST['phoneNumber'];
}

// Instance of a User class
$userObject = new User();

// Registration of new user
if(!empty($login) && !empty($password) && !empty($email) && !empty($firstName) && !empty($lastName) && !empty($streetName) && !empty($streetNumber) && !empty($zipCode) && !empty($city) && !empty($phoneNumber))
{
    $hashed_password = md5($password);
    $json_registration = $userObject->createNewRegisterUser($login, $hashed_password, $email, $firstName, $lastName, $streetName, $streetNumber, $zipCode, $city, $phoneNumber);
    echo json_encode($json_registration);
}

// User Login
if(!empty($login) && !empty($password) && empty($email))
{
    $hashed_password = md5($password);
    $json_array = $userObject->loginUsers($login, $hashed_password);
    echo json_encode($json_array);
}
?>