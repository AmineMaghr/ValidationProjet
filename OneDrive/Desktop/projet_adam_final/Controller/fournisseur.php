<?php 
ini_set('display_errors', 1);
error_reporting(E_ALL);

require_once 'C:\xampp\htdocs\projet_adam_final\config.php'; 
include_once 'C:\xampp\htdocs\projet_adam_final\Model\fournisseurModel.php';
class fournisseurC{
public function ajouter($fournisseur){
	$pdo=conn::getConnexion();
	try {
		$query=$pdo->prepare(
			"INSERT INTO fournisseur (cin_fournisseur,nom,prenom,date_naiss,adresse,email,numero)
			VALUES (:cin_fournisseur,:nom,:prenom,:date_naiss,:adresse,:email,:numero);"
		);
		$query->execute([
            'cin_fournisseur'=>$fournisseur->getcin_fournisseur(),
			'nom'=>$fournisseur->getnom(),
			'prenom'=>$fournisseur->getprenom(),
			'date_naiss'=>$fournisseur->getdate_naiss(),
			'adresse'=>$fournisseur->getadresse(),
			'email'=>$fournisseur->getemail(),
			'numero'=>$fournisseur->getnumero(),
			
		]);
	}
	catch(PDOException $e) {
		echo "Error: " . $e->getMessage();
	}
}
public function supprimer($id_fournisseur)
{
    $sql = "DELETE FROM fournisseur WHERE id_fournisseur = :id_fournisseur";  // Fixed query syntax
    $db = conn::getConnexion();
    $query = $db->prepare($sql);
    $query->bindValue(':id_fournisseur', $id_fournisseur);
    
    try {
        $query->execute();
    } catch(PDOException $e) {
        // It is a good practice to log the error or handle it properly
        echo "Error: " . $e->getMessage();
    }
}

public function modifier($fournisseure,$id_fournisseur){
	$pdo=conn::getConnexion();
	try {
		$sql="UPDATE fournisseur SET cin_fournisseur=:cin_fournisseur,nom=:nom,prenom=:prenom,date_naiss=:date_naiss,adresse=:adresse,email=:email,numero=:numero WHERE id_fournisseur=:id_fournisseur";
		$req=$pdo->prepare($sql);
		
		$req->bindValue(':cin_fournisseur', $fournisseure->cin_fournisseur);
        $req->bindValue(':nom', $fournisseure->nom); 
        $req->bindValue(':prenom', $fournisseure->prenom);
        $req->bindValue(':date_naiss', $fournisseure->date_naiss);
        $req->bindValue(':adresse', $fournisseure->adresse);
        $req->bindValue(':email', $fournisseure->email);
        $req->bindValue(':numero', $fournisseure->numero);
		$req->bindValue(':id_fournisseur', $id_fournisseur);
		$req->execute();
		
		
	}
	catch(PDOException $e) {
		echo "Error: " . $e->getMessage();
	}
}
public function afficherFournisseur()
{

try{
	$pdo=conn::getConnexion();
	$query=$pdo->prepare("SELECT * FROM fournisseur");
	$query->execute();
	return $query->fetchAll();
	}
	catch(PDOException $e) {
	echo "Error: " . $e->getMessage();
}
}
}
?>