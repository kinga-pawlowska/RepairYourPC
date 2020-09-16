<?php
include_once 'db.php';

class User
{
    private $db;
    private $db_table = "users";
    private $db_table_orders = "orders";

    public function __construct()
    {
        $this->db = new DbConnect();
    }
    
    public function payForOrder($repairIdentifier)
    {
        $query = "UPDATE orders SET paid = 'YES', updated_at = NOW() WHERE repairIdentifier = '$repairIdentifier'";
        $paid = mysqli_query($this->db->getDb(), $query);
        
        if($paid == 1)
        {
            $json['paid'] = 1;
        }
        else
        {
            $json['paid'] = 0;
        }
        
        mysqli_close($this->db->getDb());

        return $json;
    }

    public function updateOrder($repairIdentifier, $repairStatus, $estimatedCost, $paid)
    {
        $query = "UPDATE orders SET repairStatus = '$repairStatus', estimatedCost = '$estimatedCost', paid = '$paid', updated_at = NOW() WHERE repairIdentifier = '$repairIdentifier'";
        
        $updated = mysqli_query($this->db->getDb(), $query);
        
        if($updated == 1)
        {
            $json['updated'] = 1;
        }
        else
        {
            $json['updated'] = 0;
        }
        
        mysqli_close($this->db->getDb());

        return $json;
    }
    
    public function removeOrder($repairIdentifier, $login)
    {
        //$query = "DELETE FROM 'orders' WHERE ('repairIdentifier'='$repairIdentifier' AND 'login' ='$login')";
        $query = "DELETE FROM orders WHERE repairIdentifier='$repairIdentifier'";
        
        $removed = mysqli_query($this->db->getDb(), $query);
        
        if($removed == 1)
        {
            $json['removed'] = 1;
        }
        else
        {
            $json['removed'] = 0;
        }
        
        mysqli_close($this->db->getDb());

        return $json;
        
    }
    
    
    public function removeUser($login)
    {
        $query = "DELETE FROM `users` WHERE `login`='$login'";
        $cancelled = mysqli_query($this->db->getDb(), $query);
        $queryDeleteOrders = "DELETE FROM `orders` WHERE `orders`.`login`='$login'";
        $deletedOrders = mysqli_query($this->db->getDb(), $queryDeleteOrders);
        
        
        if($cancelled == 1)
        {
            $json['cancelled'] = 1;
        }
        else
        {
            $json['cancelled'] = 0;
        }
        
        mysqli_close($this->db->getDb());

        return $json;
        
    }
    
    public function isLoginExist($login, $password)
    {
        $query = "select * from " . $this->db_table . " where login = '$login' AND password = '$password' Limit 1";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0)
        {
            mysqli_close($this->db->getDb());
            return true;
        }

        mysqli_close($this->db->getDb());

        return false;
    }
    
    public function createNewRegisterOrder($login, $repairIdentifier, $manufacturer, $kindOfHardware, $model, $service, $operatingSystem, $description, $link)
    {
        $query = "insert into orders (login, repairIdentifier, manufacturer, kindOfHardware, model, service, operatingSystem, description, link, repairStatus, estimatedCost, paid, created_at, updated_at) values ('$login', '$repairIdentifier', '$manufacturer', '$kindOfHardware', '$model', '$service', '$operatingSystem', '$description', '$link', 'PENDING', 'PENDING', 'NO', NOW(), NOW())";
        $inserted = mysqli_query($this->db->getDb(), $query);

        if($inserted == 1)
        {
            $json['success'] = 1;
        }
        else
        {
            $json['success'] = 0;
        }

        mysqli_close($this->db->getDb());

        return $json;
    }
    
    public function readClientInfo($login)
    {
        $query = "SELECT * FROM users WHERE login='$login'";
        $result = mysqli_query($this->db->getDb(), $query);
        
        if(mysqli_num_rows($result) > 0)
        {
            while($row = mysqli_fetch_assoc($result))
            {
                $data[] = $row;
            }

            mysqli_close($this->db->getDb());

            return $data;
        }
        else
        {
            mysqli_close($this->db->getDb());
            $data[] = array('id' => null, 'login' => null, 'password' => null, 'email' => null, 'firstName' => null, 'lastName' => null, 'streetName' => null, 'streetNumber' => null, 'zipCode' => null, 'city' => null, 'phoneNumber' => null, 'created_at' => null, 'updated_at' => null);
            return $data;
        }
    }
    
    public function readOrders($login)
    { 
        /*
        $query = "SELECT * FROM orders WHERE login='$login' ORDER BY id DESC";
        $result = mysqli_query($this->db->getDb(), $query);

        if($result === 1)
        {
            if(mysqli_num_rows($result) > 0)
            {
                while($row = mysqli_fetch_assoc($result))
                {
                    $data[] = $row;
                }

                mysqli_close($this->db->getDb());

                return $data;

            }
        }
        else
        {
            $json['success'] = 0;
            
            mysqli_close($this->db->getDb());

            return $json;
        }
         
         */
        
        $query = "SELECT * FROM orders WHERE login='$login' ORDER BY id DESC";
        $result = mysqli_query($this->db->getDb(), $query);
        
        if(mysqli_num_rows($result) > 0)
        {
            while($row = mysqli_fetch_assoc($result))
            {
                $data[] = $row;
            }

            mysqli_close($this->db->getDb());

            return $data;
        }
        else
        {
            mysqli_close($this->db->getDb());
            $data[] = array('id' => null, 'login' => null, 'repairIdentifier' => null, 'description' => null, 'created_at' => null, 'updated_at' => null);
            return $data;
        }
         
        
    }
    
    public function readAllOrders()
    { 
        $query = "SELECT * FROM orders ORDER BY id DESC";
        $result = mysqli_query($this->db->getDb(), $query);
        
        if(mysqli_num_rows($result) > 0)
        {
            while($row = mysqli_fetch_assoc($result))
            {
                $data[] = $row;
            }

            mysqli_close($this->db->getDb());

            return $data;
        }
        else
        {
            mysqli_close($this->db->getDb());
            $data[] = array('id' => null, 'login' => null, 'repairIdentifier' => null, 'description' => null, 'created_at' => null, 'updated_at' => null);
            return $data;
        }
         
        
    }

    public function createNewRegisterUser($login, $password, $email, $firstName, $lastName, $streetName, $streetNumber, $zipCode, $city, $phoneNumber)
    {
        $query = "insert into users (login, password, email, firstName, lastName, streetName, streetNumber, zipCode, city, phoneNumber, created_at, updated_at) values ('$login', '$password', '$email', '$firstName', '$lastName', '$streetName', '$streetNumber', '$zipCode', '$city', '$phoneNumber', NOW(), NOW())";
        $inserted = mysqli_query($this->db->getDb(), $query);

        if($inserted == 1)
        {
            $json['success'] = 1;
        }
        else
        {
            $json['success'] = 0;
        }

        mysqli_close($this->db->getDb());

        return $json;

    }

    public function loginUsers($login, $password)
    {
        $json = array();
        $canUserLogin = $this->isLoginExist($login, $password);
        
        if($canUserLogin)
        {
            $json['success'] = 1;
        }
        else
        {
            $json['success'] = 0;
        }
        
        return $json;
    }
}
?>