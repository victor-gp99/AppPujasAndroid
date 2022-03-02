<?php

require 'DBManager.php';


if ($_SERVER['REQUEST_METHOD'] == 'POST'){

$idSession = $_POST['id'];

if($idSession){
$db = new DBManager();
$json = $db->bought($idSession);
echo $json;
}else{
die('{"code":404}');
}

}else{
    die("REQUEST_METHOD incorrecto");
}
?>