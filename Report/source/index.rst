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

Deux conférences furent alors mises en place part quelques-uns des concepteurs des plus grands
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
Le format NIFTI est un format très spécifique au domaine de la neuro-imagerie. Il fallait donc, au début
du projet, pouvoir visualiser et manipuler ce genre de fichier. Pour faire celà, il existe de nombreux outils.
Ce chapitre va donc présenter de manière suscinte les outils qui ont été employé pour la réalisation du projet.

 

Le deeplearning et choix d'une bibliotheque
--------------------------------------------

Le calcul distribué
--------------------

Docker
-------

Choix des technologies
-----------------------

Choix de la topologie du reseau de neurones
--------------------------------------------

Conception
===========
Schémas conceptuels
--------------------

Description des classes
-------------------------

Description du workflow
-------------------------

Implémentation
================
..A completer en expliquant les détails d'implémentation

Expérience réalisée avec le CHUV
=================================

Analyses des résultats
=======================

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
