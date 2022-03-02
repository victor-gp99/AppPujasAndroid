<?php
require 'DBManager.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST'){
    
    $idSession = $_POST['id'];
    $pass = $_POST['contra'];
    $name = $_POST['nombre'];
    $phone = $_POST['telefono'];
    $dataExtra = $_POST['datosExtra'];


    if($pass && $name && $phone && $dataExtra && $idSession){
        $db = new DBManager();
        $json = $db->editUser($pass, $name, $phone, $dataExtra, $idSession);
        echo $json;
    }else{
        die('{"code":404}');
    }



}else{
    die("REQUEST_METHOD incorrecto");
}


?>