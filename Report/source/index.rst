.. Rapport documentation master file, created by
   sphinx-quickstart on Mon May 22 09:06:27 2017.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Rapport du travail de bachelor
===================================

Introduction
==================
Contexte du travail
-------------------

Problème à résoudre et but du projet
------------------------------------

Rappel des objectifs du projet
------------------------------

Analyses préliminaires
======================
Le format NIFTI
----------------
Ce travail est un projet de neuro-imagerie, il est donc naturel de devoir travailler avec des
images IRM du cerveau. Le format utilisé par le CHUV pour les images est le format NIFTI
(Neuroimaging Informatics Technology Initiative), un format d'image très spécialisé mais
également très répandu dans ce domaine. 

Ce chapitre va donc présenté ce format afin de mieux le comprendre. Pour faire celà, nous
allons voir l'origine du format, une vue d'ensemble des principales caractéristiques du format
et quelques outils qui ont été utiles à la réalisation de ce travail.

Origine du format NIFTI
***********************
(maybe add info sur les fichiers .mat qui accompagnait les fichiers analyze, parler de l'orientation
radiologique ou neurologique).

NIFTI est un format de fichier pour sauvegarder des données d'IRM volumétrique. Il fonctionne
sur le principe des voxels et est multidimensionnel.

Ce format a été imaginé il y a une dizaine d'année pour remplacer le format ANALYZE 7.5.
Ce format était très utilisé mais était également très problématique. Le soucis principal de
ce format étant le manque d'information sur l'orientation dans l'espace de l'élément scanné.
Les données enregistrées ne pouvaient donc pas être lu et interprêté sans ambiguité. A cause
de ce manque d'information il existait principalement une confusion entre le côté droit et le
côté gauche du cerveau. 

Deux conférences ont alors été mises en place part quelques-uns des concepteurs des plus grands
logiciels de neuroimagerie. Ces deux conférences, le Data Format Working Group (DFWG), se sont
réunit au "National Institue of Health" (NIH) pour trouver un format de remplacement. Dec ces
réunions naquit le format NIFTI. Celui-ci veut intégrer de nouvelles informations et devenir
un nouveau standard de neuroimagerie.

Vue d'ensemble du format NIFTI
******************************
Le format ANALYZE 7.5 avait besoin de deux fichiers pour fonctionner. Un fichier *.hdr contenant
le header pour stocker les méta-données et un fichier *.img contenant les données de l'image.
Le format NIFTI a conservé cette manière de faire afin de préserver la compatibilité avec les
systèmes déjà en place. Toutefois, des améliorations ont été apportés et pour évité de faire
l'erreur d'oublier l'un des deux fichiers du format, il a été décidé de permettre le stockage
dans un seul fichier avec l'extension *.nii. Ces images contenant de grandes zones d'image
noires, elles sont donc parfaites pour être compressées avec gzip. Il n'est donc absolument
pas rare de trouver des fichiers NIFTI au format *.nii.gz. Pour ce travail nous avons utilisé
les formats *.nii et *.nii.gz.

Le format NIFTI est un format de fichier sur plusieurs dimensions. Au total, il peut compter
jusqu'à 7 dimensions. Dans tous les cas, les 3 premières dimensions sont des dimensions spatiales
(x, y, z) et la quatrième est une dimension temporelle. Les dimensions suivantes (5-7) sont des
dimensions reservées à d'autre usage et sont plus ou moins libre. Dans le cadre de ce projet,
les images utilisées ne possède que 3 dimensions (les 3 dimensions spatiales). On peut donc voir
les images comme étant un instantané du cerveau en 3 dimensions.

Les dimensions et d'autres informations importantes sur le fichier sont stocké dans un fichier
header. Ce dernier est d'une taille de 348 octets. (Il y a un tableau de toutes les valeurs sur
brainder.org il doit venir etre collé ici.)

Le champs principalement utilisé lors de ce projet est le champs short dim[8]. Ce champs est un
tableau contenant les données sur les dimensions du fichier. Ce tableau contient pour: 
- Dim[0]: Le nombre de dimensions
- Dim[1 -7]: Est un nombre positif contenant la longueur de la dimension en question.

Outils pratique
***************

Le calcul distribué
-------------------
(Ce chapitre présente le calcul distribué et spark, il est placé la afin de mettre en avant dans un
premier temps les contraintes du projet. Il devient alors plus simple au choix suivant d'expliquer le
choix de dl4j comme bibliotheque de deeplearning)

Qu'est ce que le calcul distribué ?
***********************************
Spark
*****

Le deeplearning et choix d'une bibliotheque
--------------------------------------------
(Ce chapitre va résumer les avancés sur le deeplearning (avantage et inconvénient), puis il va expliquer le fonctionnement des réseaux de convolution (reseau employe durant le projet), puis on va faire un état
de l'art des bibliothèques et defendre le choix de dl4j)

Considération générale
**********************
Réseaux de convolution
**********************
<<<<<<< HEAD
=======
Notions générales
+++++++++++++++++
Ce projet vise a traiter des images d'IRM. L'un des type de réseau de neurones dont la spécialité est de traiter des images est le réseau de neurones
convolution (CNN). Cette partie du rapport est consacré à ce type de réseau.

Les réseaux de convolution sont un type de réseau de neurones acyclique ("feed-forward") dans lequel le motif de connexion entre les neurones
est inspiré par le cortex visuel des animaux. On peut donc lui passer en entrée une image et lui demander de la classifier.

Ce genre de réseau de neurones est, en général, conçu en deux parties distinctes. La première partie est la partie convolutive du réseau.
Cette dernière agit comme un extracteur de caractéristiques. On passe l'image à travers un certain nombre de filtre (noyau de convolution)
pour créer de nouvelles images (carte de convolution). Les couches de convolution sont en principe suivi d'une couche de correction
utilisant la fonction d'activation RELU. On peut placer, entre les opérations de convolution, des filtres chargé de réduire la résolution
de l'image par une opération de maximum local (Pooling). Au terme de cette étape, les cartes de convolutions sont concaténé en un vecteur
de caractéristiques. Ce vecteur est souvent appelé le code CNN.

Ce code est alors utilisé en entrée de la seconde partie du réseau convolutif. Cette partie est en règle général constitué de couches
entièrement connectées entre elle. Le but de cette partie est de combiner les caractéristiques du code CNN pour classifier l'image. 

La sortie de ce genre de réseau est en principe une dernière couche contenant autant de neurones qu'il n'y a de classe possible. La sortie est
en principe un vecteur dont le nombre de composant est le nombre de classe disponible. La valeur de ces composantes est compris entre 0 et 1.
Et la somme des composantes vaut 1. En lisant ce vecteur on obtient la distribution de probabilité que l'image appartiennent a une catégorie
ou une autre. 

Pour concevoire des couches de convolution, il existe trois paramêtres particulièrement important a géré. Ces paramêtres sont: 
* La profondeur: le nombre de filtre que l'on va utiliser
* Le pas: Le pas contrôle le chevauchement des noyaux de convolution
* La marge: La marge contrôle la dimension spatiale du volume de sortie. Elle ajoute des 0 à la frontières de l'image en entrée.

Après la couche de convolution, on peut trouver une couche de correction. Cette couche semble permettre d'accélerer la vitesse de traitement
du réseau sans affecté la précision. La fonction d'activation de cette couche peut être: 
* Correction RELU: permet d'augmenter les propriétés non-linéaire du réseau
* Correction par tangente hyperbolique
* Correction par la fonction sigmoide
* etc
La correction la plus utilisé est la correction RELU.

Une fois la correction effectué, on peut trouver une couche de pooling. Elle permet de sous-échantillonner les données d'entrée (l'image).
Elle permet de réduire le risque de sur-entraînement du réseau. Toutefois, il faut faire attention à utiliser de petit filtre afin de ne
pas perdre trop d'information. Pour cette couche on va en effet passer sur l'image des filtres chargés d'extraire une valeur de l'image.
L'une des méthodes les plus utilisé est d'employer un filtre de taille 2x2 chargé d'extraire la valeur maximum des pixels contenu dans le
filtre. Le pooling permet d'augmenter l'efficacité du traitement. Toutefois, il casse le lien entre une image et son contenu (par exemple
entre le nez et l'image d'un visage). Cette relation peut être intéressante a conservé. En faisant débordé les filtres du pooling les unes
sur les autres, il est possible de définir une position pour un élément. Il devient alors possible de dire que le nez est au milieu du
visage par exemple. Cependant, il faut noter que faire ceci empechera tout autres formes d'extrapolation (changement d'angle de vue ou d'échelle),
contrairement à ce que le cerveau humain peut faire. Pour améliorer ce problème, on peut passer des données d'entraînement plus variées à
notre réseau. Ces images peuvent être plus variées en terme de luminosité, angle de vue ou taille.

La toute dernière couche d'un réseau de convolution est une couche de LOSS. Cette couche est chargé de définir la classe dans laquelle l'image
se situe. Elle peut être activé par différentes fonction d'activation: 
* Fonction SOFTMAX: Une fonction idéal lorsque l'on doit ranger les images en 2 classes. C'est une fonction mutuellement exclusive.
* Fonction d'entropie sigmoide croisé: prédis des valeurs de probabilité indépendante dans [0, 1]
* Fonction euclidienne: Regression vers des valeurs réelles (contenu entre moins l'infini et plus l'infini)

Il existe plusieurs modèles de réseau convolutif devenus des standards. Ces architectures sont les suivantes: 
* INPUT + CONVOLUTION + RELU + FULLY CONNECTED + LOSS
* INPUT + (CONVOLUTION + RELU +POOLING)*2 + FULLY CONNECTED + RELU + FULLY CONNECTED + LOSS
* Input + (CONVOLUTION + RELU + CONVOLUTION + RELU + POOLING)*3 + (FULLY CONNECTED + RELU)* 2 + FULLY CONNECTED + LOSS 

Deeplearning et calcul distribué
********************************
Ce projet devra pouvoir fonctionner avec du calcul distribué. Comme vu plus tôt dans ce rapport, le calcul distribué permet de répartir des
tâches entre plusieurs machines connecté à un même cluster de machine. Il existe différentes techniques pour distribuer des tâches dans un
réseau de deeplearning. Il existe deux modèles principaux: 
* La parallélisation des données
* La parallélisation du modèle

Dans la parallélisation du modèle, les différentes machines sur le réseau distribué sont en charge d'une partie du réseau. Par exemple,
chaque machine peut se voir assigné la gestion d'une couche du réseau de neurones.

Dans la parallélisation des données, les différentes machines sur le réseau distribué ont une copie complète du modèle de réseau. Chaque
machine reçoit alors une partie des données et entraîne son modèle. Au terme de l'entraînement, les résultats sont combinées entre eux.

Les approches d'entraînement en utilisant la parallélisation des données nécessitent toutes une méthode de combinaison des résultats et de
synchronisation des paramètres du modèle entre chaque machine.

L'implémentation actuel de la bibliothèque de deeplearning choisi pour ce projet permet de faire de la parallélisation des données. Pour faire
celà, la bibliothèque utilise les techniques de moyennes des paramêtres synchrone. 

L'approche de la moyenne des paramètres est l'approche la plus simple de la parallélisation des données. En utilisant cette technique,
l'apprentissage fonctionne ainsi:
1. Les paramètres du réseau sont initialisé de manière aléatoire en fonction de la configuration du modèle
2. Une copie des paramètres actuel est distribué sur chaque machine 
3. Chaque machine entraîne son modèle avec les données en sa possession
4. De nouveaux paramètres globaux sont calculé en fonction de la moyenne des paramètres de chaque machine
5. Tant qu'il y a des données à traiter, on retourne a l'étape 2

>>>>>>> 4add103659d32a479617a9933ff086f6d1d134b7
Bibliothèque disponible et choix
********************************

Docker
-------
<<<<<<< HEAD

Conception
===========
Schémas conceptuels
--------------------
=======
Le LREN utilise pour sa plateforme un système de container Docker. Ce travail devra donc pouvoir être contenu dans un
environnement Docker. Cette technologie étant relativement nouvelle, ce chapitre va brièvement exposer ce qu'est Docker.

Docker est un logiciel open-source qui automatise le déploiement d'application dans des conteneurs logiciels.
Le développement avec Docker permet d'éliminer le problème de la collaboration lors de l'écriture d'un logiciel
en fournissant à chaques collaborateurs un environnement de travail semblable. Docker permet d'exécuter et de gérer
des applications fonctionnant côte à côte dans des conteneurs isolés. Il fournit également les outils nécessaires pour
créer des pipelines de livraison et de partage de logiciel de manière sûre et rapide.

Un conteneur Docker contient tout ce qui est nécessaire pour faire exécuter un logiciel. Au contraire des machines virtuelles,
les conteneurs ne regroupe pas un système d'exploitation complet. Il ne contient, en effet, que les bibliothèques et les paramètres
requis pour que le logiciel fonctionne. Cela permet d'avoir des systèmes autonomes, légers et garantit que les logiciels fonctionnent
de la même manière quel que soit l'endroit où ils sont déployé. Les conteneurs isolent le logiciel de son environnement.

Les conteneurs et les machines virtuelles ont des avantages similaires en matière d'isolation et d'allocation des ressources. Toutefois,
leurs fonctionnements sont très différents. En effet, les conteneurs préfèrent virtualiser le système d'exploitation plutôt que le matériel.
Les conteneurs se veulent donc plus portable et efficaces. Toutefois, les conteneurs et les machines virtuelles peuvent être utilisé ensemble.

Docker automatise les tâches répétitives de configuration des environnements de développement. Lorsqu'une application est encapsuler dans un
conteneur, la difficulté de configurer et installer un système est également encapsulé dans le conteneur. 

Conception
===========
Au travers de ce chapitre, la conception de ce projet sera mise en avant. Il va permettre d'expliquer le workflow, l'architecture
du programme, ainsi que les différents paramètres de configuration de ce projet. Afin de donner les explications le plus clairement possible
ce chapitre contiendra les schémas utilisés durant la conception. Chacunes des classes créée durant la réalisation de ce projet est décrite
afin d'en expliquer le concept et l'utilité.

Toutefois, il est a noté que le rendu de ce projet contient deux programmes excécutable. Ces exécutable sont nommés "LREN-Deeplearning.jar"
et "LREN_Deeplearning_DemoLocal.jar". Ceci est du à un problème de compatibilité de dépendance dans la bibliothèque Deeplearning4j. En
effet, les dépendances liées à l'emploie de Spark et à l'emploi d'une UI pour la visualisation de l'entraînement ne sont pas compatible
entre. Ce problème est plus précisément expliqué plus tard dans ce rapport. Cependant, ce qui est décrit dans ce rapport peut se porter
sur les deux executables fourni. La seule différence entre ces deux exécutables étant que l'exécutable "LREN_Deeplearning_DemoLocal"
fournit un outil de visualisation de l'entraînement mais ne fournit pas d'outil permettant de lancer l'apprentissage sur Spark.

Description du workflow et schéma de classe
-------------------------------------------
(Inserer un schéma du workflow)
Ce projet peut fonctionner de plusieurs manières différentes. Les différents comportements du programme peuvent être configurer dans un
fichier *.properties. Un fichier de configuration détaillé avec une brève explication des paramètres est fournit en annexe de ce rapport.
La suite de ce rapport n'en reprendra que les grandes lignes.

La première chose que fait le programme est donc de créer un objet capabe de créer et de lire un fichier de configuration. Pour créer
le fichier de configuration il suffit de lancer le fichier jar du projet sans autre paramètre. Le programme générera alors un fichier de
configuration standard permettant de lancer une expérience de base puis se terminera.

Si l'on donne comme argument au programme un fichier de configuration, ce dernier sera lu et selon ce qu'il contient le programme adoptera
le comportement adéquat.

La première chose que le programme lira permettra de lui dire s'il doit ou non créer des données de test. Si oui, il pourra généré deux types
de NIFTI. Ces derniers permettent de lancer une expérience de classification en deux classes distinctes sans avoir à télécharger de données.

Une fois cette génération faite (ou non selon la configuration), le programme va lire le dossier qui lui est stipulé pour chercher les données et
demander à l'utilisateur d'entrer une chaine de caractère a recherché dans le nom du fichier afin de labeliser les données lues. Il faut donc que
les fichiers que cherche à lire le programme contiennent une chaîne de caractère unique et permettant d'identifier a quel classe il correspond.
De cette manière le programme peut généré les labels et étiquetté les images.

Le programme regarde ensuite s'il doit fonctionner sur Spark ou non. Dans les deux cas la marche a suivre ensuite reste très similaire.
Tout d'abord, le programme va récupérer les données d'entraînement et les données de test. Il charge ensuite la configuration du réseau selon
ce qui lui est spécifié dans le fichier de configuration. Il peut charger une configuration pré-enregistré dans le code du projet ou une configuration
sauvegardé plus tôt.

Une fois la configuration chargé et si la configuration demande l'emploi de Spark, le programme se charge d'initialiser le superviseur
d'entraînement de Spark. 

Puis dans le deux cas le programme termine la création d'un réseau avec la configuration chargé. Si Spark est demandé, le réseau est
fait pour fonctionner sur cet outil.

Suite à cela le réseau est entrainé avec les données d'apprentissage puis évaluer avec les données de test. Les résultats de l'évaluation
sont affiché à l'utilisateur et le réseau est sauvegarder dans un fichier selon la méthode demandé par le fichier de configuration, si la
sauvegarde est demandé.

Une fois le workflow défini, le schéma de classe a pu être conçu. Ce dernier a été imaginé en se focalisant sur les principales tâches
du programme. Ainsi chaque tâche du programme peut être représenté par une classe.

(Insert schema de classe)

Les classes représentées sur ce schéma sont décrite dans la suite du rapport.

(trouve un endroit ou case la description du partage de donnee avec Spark.)
>>>>>>> 4add103659d32a479617a9933ff086f6d1d134b7

Description des classes
-------------------------
Dans la suite de ce chapitre, nous allons survolé les différents packages et les différentes classes créées pour ce projet. Une rapide
description du package expliquera les classes contenues dans le package et pourquoi elles ont été séparé ainsi. Puis, une description plus
précise des classes sera faîtes pour chaque package.

Package "Config"
****************
Le package "Config" contient tous les outils nécessaire pour créer et lire les fichiers de configuration dont le programme a besoin.
Il a été conçu à cause du besoin de pouvoir facilement modifier la configuration du programme pour le tester. Ce package ne contient
qu'une classe: La classe configuration.

La classe "Configuration"
+++++++++++++++++++++++++
La classe "Configuration" est une classe très simple. Elle répertorie la liste complète des paramètres dont le programme peut avoir besoin.
Ces paramètres, comme vu plus haut permettent de choisir le fonctionnement du programme mais également de modifier le comportement du réseau
de neurones.

Cette classe contient de nombreuses méthodes. Toutefois celle-ci peuvent être classer en trois type de méthode: 
* Une méthode permettant de générer un fichier de configuration standard. Ce fichier permet de lancer une expérience de test fonctionnelle
du programme.
* Une méthode de lecture d'un fichier de configuration. Cette méthode lit le fichier et stock les valeurs des paramètres dans des attributs
de la classe.
* Des "getter" qui permettent l'accès à chaque paramètre lu par la classe.

Cette classe est une classe on ne peut plus standard utilisant les outils de Java standard.

Package "Generator"
*******************
Ce package fournit les outils nécessaires pour générer des fichiers NIFTI de test. Il a été conçu pour permettre de créer des données permettant
de tester les réseaux de neurones sans avoir besoin de télécharger un dataset complet. Ce package ne contient qu'une classe: la classe "DataTestGenerator".

La classe "DataTestGenerator"
+++++++++++++++++++++++++++++
Cette classe est chargé de créer des fichiers au format NIFTI. Elle permet la création de représentation de cube et de sphère dans ce format.
La génération de ces fichiers peut être supervisé par le fichier de configuration. Ainsi, il est possible de préciser la taille de la matrice
de données de ces fichiers. Il ne font que des NIFTI en trois dimensions tels que ceux qui sont utilisé par le CHUV.

Elle permet également de créer de très petit jeu de données totalement aléatoire. Cette fonctionnalitée a été utilisé au début du projet afin
de pouvoir comprendre le fonctionnement de la bibliothèque deeplearning4j. Toutefois, cette fonctionnalité n'est plus utilisé dans le programme
finale.

Cette classe possède ainsi: 
* Des méthodes pour générer des cubes et des sphères dans des fichiers NIFTI selon des paramètres de taille que l'on peut lui spécifier.
* Une méthode pour générer un dataset complet de sphères et de cubes de tailles fixes dans des fichiers NIFTI de taille également fixe et ce à des positions prédéfinis.
* Une méthode pour générer un dataset complet de sphères et de cubes de tailles aléatoires dans des fichiers NIFTI de taille fixes et ce à des positions aléatoires.
* Un lot de méthode pour générer de très petits jeu de données de petites tailes et le tout aléatoirement.

Cette classe utilise les fonctionnalités fournit par la bibliothèque de gestion de NIFTI niftijio. 

Package "Core"
**************
Comme le nom de ce package l'indique ce dernier contient le coeur du projet. Il contient la classe principale du projet (la classe "Main") et
la classe chargée de lire les fichiers NIFTI.

La classe "Main"
++++++++++++++++
La classe "Main" centralise le workflow complet du programme. Cette dernière est chargé d'instancier tous les objets utiles au programme
et de les gérer. Ainsi chaque package est totalement indépendant des autres. Par exemple le package Configuration n'a pas besoin du package
Wrapper pour fonctionner.

Afin de rendre cette classe la plus lisible possible elle contient quelques fonctions: 
* La fonction "Main" elle est le squelette du programme et gère le workflow.
* Une fonction chargée d'afficher une aide à l'utilisateur si l'utilisateur fait une erreur d'utilisation du fichier jar.
* Une fonction chargée de gérer la génération de données.
* Une fonction chargée de gérer la création des labels a assigner a chaque image.
* Une fonction chargée de gérer la lecture des données.
* Une fonction chargée de gérer la configuration, l'initialisation, l'entraînement et l'évaluation du réseau de neurones.

Elle instancie également toutes les autres classes créées pour ce projet.

La classe "DataReader"
++++++++++++++++++++++
La classe "DataReader" fournit les outils nécessaires à la lecture des données et à la préparation de ces dernières pour être comprise
par le réseau de neurones. 

Elle fournit un certain nombre de méthodes permettant: 
* de créer un iterateur sur le jeu de données lu. Chacunes des données lues se voit assigner le label nécessaire en fonction de son nom 
* d'obtenir les itérateurs des jeu de données d'apprentissage et de test sans autre modification (nécessaire si on ne se sert pas de Spark). 
* d'obtenir les itérateurs des jeu de données d'apprentissage et de test après que ceux-ci aient été normalisés (nécessaire si on ne se sert pas de Spark).
* d'obtenir les itérateurs des jeu de données d'apprentissage et de test sous forme de liste et sans avoir été normalisés (nécessaire si l'on se sert de Spark). 
* d'obtenir les itérateurs des jeu de données d'apprentissage et de test sous forme de liste et après avoir été normalisés (nécessaire si l'on se sert de Spark).

Cette classe permet de configuré la taille des minibatchs de chaque itérateurs et gère également de ratio de données d'entraînement et de test.
Ce ratio est fait de manière stratifié. Le même ratio est appliqué pour les données de chaques classes. Si l'on a 2 classes et un ratio de 80%
de données d'apprentissage et 20% de données de test, on retrouvera 80% des données de la classe 1 et 80% des données de la classe 2 dans les données
d'apprentissage. Et ce de la même manière pour les 20% de données de test.

Package "Wrapper"
*****************
Ce package réparti en trois classe la gestion de la configuration, de l'initialisation, de l'entrainement et de l'évaluation du réseau de neurones.
Une classe gère les parties communes de configuration du réseau, tandis que les autres se partage les tâches selon si ces dernières sont liées
à Spark ou non. 

La classe "WrapperDl4j"
+++++++++++++++++++++++
La classe "WrapperDl4j" est la classe mère de la gestion du réseau de neurones. Elle gère principalement la configuration du réseau de neurones.
En effet, la configuration du réseau est indépendante de la méthode employé ensuite pour l'utilisation (Utilisation de Spark ou non).

Elle fournit donc les méthodes qui permettent de: 
* créer une configuration de réseau. 
* charger une configuration de réseau depuis une configuration sauvegardé.
* sauvegarder le réseau. 

Elle est un wrapper autour de la bilbiothèque deeplearning4j afin d'en simplifier l'emploie. Elle sert également de classe de base pour
les deux classes que ce rapport va présenter ensuite.

La classe "LocalWrapperDl4j"
++++++++++++++++++++++++++++
Cette classe est une classe héritant de la classe "WrapperDl4j". Elle étend ainsi cette dernière en lui fournissant les méthodes pour
l'initialisation en local du projet. C'est à dire sans se servir de la plateforme Spark. Elle est contenu dans un sous package nommé
"local".

Les méthodes qu'elle fournit sont donc chargées:
* d'initialiser le projet pour fonctionner en local sur la machine.
* d'entraîner le réseau de neurones en local.
* d'évaluer le réseau de neurones en local.

Cette classe est elle aussi construite autour de la librairie deeplearning4j.

La classe "SparkWrapperDl4j"
++++++++++++++++++++++++++++
Cette classe est conçu pour permettre l'emploi d'un réseau de neurones sur Spark. Elle est contenu dans un sous package nommé "spark".
Elle étend la classe "WrapperDl4j" en lui fournissant les outils utiles a Spark.

Elle possède ainsi des méthodes pour: 
* spécifier la configuration de Spark (localisation du moteur de Spark, timeout, heartbeat)
* initialiser le reseau sur Spark
* initialiser le "training master" qui est en charge de gérer le processus de calcul des paramètres du réseau à travers Spark.
* d'entrainer le réseau au travers de Spark.
* d'évaluer le réseau au travers de Spark.

Cette classe est construite autour de la bibliothèque deeplearning4j.

Choix de la topologie du réseau de neurones
------------------------------------------------
Cette partie du chapitre va décrire les différents types de réseaux de neurones régulièrement utilisés dans la littérature. 

Implémentation
================
Configuration d'une expérience
------------------------------
Lecture des données
-------------------
Configuration du/des réseaux
----------------------------
Entraînement et évaluation sans Spark
-------------------------------------
Entraînement et évaluation avec Spark local
-------------------------------------------
Entraînement et évaluation avec Spark sur un cluster
----------------------------------------------------

Expérience réalisée avec le CHUV
=================================
Donnée de l'expérience
----------------------
Préparation et exécution de l'expérience
----------------------------------------
Résultats
---------

Analyses des résultats du projet
================================

Gestion de projet
==================
Diagramme de Gantt
-------------------
Journal de travail
-------------------
Analyse de la gestion de projet
-------------------------------

Conclusion
============
Améliorations futures
----------------------
Ressenti personnel
-------------------

Sources
========

Annexes
========
Cahier des charges
------------------

Journal de travail
-------------------

Plannification
---------------

Manuel utilisateur
-------------------

Bibliographie
--------------
