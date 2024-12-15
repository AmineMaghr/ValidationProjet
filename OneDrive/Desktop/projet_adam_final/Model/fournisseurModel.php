<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
class fournisseurl{
    public $cin_fournisseur;
    public  $nom;
    public  $prenom;
    public  $date_naiss;
    public  $adresse;
    public  $email;
    public  $numero;
   
    
    
    function __construct($cin_fournisseur,$nom,$prenom,$date_naiss,$adresse,$email,$numero){
      $this->cin_fournisseur=$cin_fournisseur;
       $this->nom=$nom;
       $this->prenom=$prenom;
       $this->date_naiss=$date_naiss;
       $this->adresse=$adresse;
       $this->email=$email;
       $this->numero=$numero;
       
    }
    
    public function getcin_fournisseur()
    {
        return $this->cin_fournisseur;
    }
    public function getnom()
    {
        return $this->nom;
    }
    public function getprenom()
    {
        return $this->prenom;
    }
    public function getdate_naiss()
    {
        return $this->date_naiss;
    }   
    public function getadresse()
    {
        return $this->adresse;
    }
    public function getemail()
    {
        return $this->email;
    }
    public function getnumero()
    {
        return $this->numero;
    }
    
 }