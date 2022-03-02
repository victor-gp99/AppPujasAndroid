<?php

require 'DBManager.php';
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$prod = $_POST['prod'];
	$desc = $_POST['desc'];
	$foto = $_POST['foto'];
	$cate = $_POST['cat'];
	$price = $_POST['price'];
	$date = $_POST['date'];

	if ($prod && $desc && $cate && $price && $date && $foto) {
		$db = new DBManager();
		$json = $db->addAuction($prod, $desc, $foto, $cate, $price, $date);
		echo $json;
	} else {
		die('{"code":403}');
	}
} else {
	die("REQUEST_METHOD incorrecto");
}
?>

<?php
/*
 if($_SERVER['REQUEST_METHOD']=='POST'){
 
 $imagen= $_POST['foto'];
                $nombre = $_POST['nombre'];
 
 require_once('dbConnect.php');
 
 $sql ="SELECT id FROM frutas ORDER BY id ASC";
 
 $res = mysqli_query($con,$sql);
 
 $id = 0;
 
 while($row = mysqli_fetch_array($res)){
 $id = $row['id'];
 }
 
 $path = "uploads/$id.png";
 
 $actualpath = "https://servermorefast.webcindario.com/ImagenConNombre/$path";
 
 $sql = "INSERT INTO frutas (foto,nombre) VALUES ('$actualpath','$nombre')";
 
 if(mysqli_query($con,$sql)){
 file_put_contents($path,base64_decode($imagen));
 echo "Subio imagen Correctamente";
 }
 
 mysqli_close($con);
 }else{
 echo "Error";
 }*/
?>