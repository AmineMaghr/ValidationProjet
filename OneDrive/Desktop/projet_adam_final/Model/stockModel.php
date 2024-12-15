<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
class stockl{
    public $nom_produit;
    public  $quantite;
    public  $unite;
    public  $date_expir;
    public  $prix_uni;
    public  $id_four;
    public  $dispo;
   
    
    
    function __construct($nom_produit,$quantite,$unite,$date_expir,$prix_uni,$id_four,$dispo){
      $this->nom_produit=$nom_produit;
       $this->quantite=$quantite;
       $this->unite=$unite;
       $this->date_expir=$date_expir;
       $this->prix_uni=$prix_uni;
       $this->id_four=$id_four;
       $this->dispo=$dispo;
       
    }
    
    public function getnom_produit()
    {
        return $this->nom_produit;
    }
    public function getquantite()
    {
        return $this->quantite;
    }
    public function getunite()
    {
        return $this->unite;
    }   
    public function getdate_expir()
    {
        return $this->date_expir;
    }
    public function getprix_uni()
    {
        return $this->prix_uni;
    }
    public function getid_four()
    {
        return $this->id_four;
    }
    public function getdispo()
    {
        return $this->dispo;
    }
    
 }
 














?>