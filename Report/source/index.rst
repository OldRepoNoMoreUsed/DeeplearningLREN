.. Rapport documentation master file, created by
   sphinx-quickstart on Mon May 22 09:06:27 2017.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Rapport du travail de bachelor
===================================


Introduction
==================
Ce rapport présente un projet développé dans le cadre du Travail de Bachelor, au sein de la HES de Neuchâtel
et en collaboration avec le Human Brain Project et le Laboratoire de Recherche en Neuro-imagerie (LREN)
du CHUV de Lausanne. Il a été développé par Monsieur Nicolas Sommer sous la supervision de Monsieur
Fabrizio Albertetti. Ce travail étant un mandat de la Medical Informatics Platform du Human Brain Project,
la personne de contact et mandant était Arnaud Jutzeler.

Ce travail était d'une durée de 450h et c'est déroulé sur 10 semaines durant le semestre de 
printemps 2017 à raison de 4h par semaine puis sur une durée de 8 semaines a raison de 8h par
jour.

Les premières semaines furent donc consacré à l'analyse préliminaire du projet, aux choix et à
l'apprentissage des technologies qui seront employés. Le seconde partie fut consacré à l'implémentation
et aux expériences.

Le reste de ce rapport présente les contraintes et les analyses préliminaires effectuées pour préparer
la suite du projet, la conception et l'implémentation du programme lié au projet ainsi qu'une expérience
qui a été réalisé à l'aide de ce dernier. 

La suite de ce chapitre expose le contexte du travail, les buts de ce projet et les objectifs qui fixés pour
celui-ci et une note sur la confidentialité liée à ce projet.

Contexte du travail
--------------------
Le Human Brain Project (HBP) a pour but d'enrichir les connaissances humaines en matière de neuro-science
en cherchant à mieux comprendre les mécanismes du cerveau humain. Ce projet s'inscrit dans le cadre du
programme européen pour la recherche et l'innonvation Horizon 2020 et vise a accélerer les domaines des neuro-
sciences, de l'informatique et de la médecine liée au cerveau. La première étape du Human Brain Project veut
mettre à disposition des chercheurs un portail web. Ce portail web sera constitué d'un total de 6 plateformes
de recherche. Celles-ci porteront sur la neuro-informatique, la simulation du cerveau, le calcul à haute performance,
l'informatique médicale, l'informatique neuromorphique et la neuro-robotique.

Le département des Neuro-sciences Clinique du CHUV est chargé de la plateforme d'Informatique Médicale. Celle-ci
est une plateforme open-source permettant aux hôpitaux et aux centre de recherche de partager des données médicales.
Elle permettra aux utilisateurs d'avoir accès à des informations précises et pertinentes sur les maladies liées au
cerveau en préservant la confidentialité des patients. Cette plateforme servira donc de pont entre la recherche en
neuro-sciences, la recherche clinique et les soins aux patients. Elle pourrait également permettre la découverte de
mécanisme à différentes échelles qui expliquerait l'apparition et le développement de maladies cérébrales.

C'est dans ce cadre que l'équipe du Laboratoire de Recherche En Neuro-imagerie cherche à développer un ensemble d'
outils pour l'acquisition, le traitement et l'analyse des données. Elle cherche, entre autre chose, à pouvoir automatiser
aussi efficacement que possible les diagnostiques de maladie pouvant atteindre le cerveau, tel que les maladies
d'Alzheimer ou de Parkinson. Dans l'état actuel cette analyse peut se faire avec des méthodes de machine learning.
Toutefois, il n'existe pas encore de méthode d'apprentissage profond disponible sur la plateforme et le LREN aimerait
pouvoir proposé cette option aux utilisateurs de la plateforme.

C'est dans ce but que la Haute-Ecole Arc de Neuchâtel a été contacté et ce projet proposé comme travail de diplôme à
un étudiant de troisième année.  

Problème à résoudre et but du projet
------------------------------------
Actuellement, la plateforme rassemble un certain nombre d'images d'IRM. Celles-ci sont stockés sous la forme de DICOM ou
de fichier au format NIFTI. Le DICOM est une norme standard pour la gestion informatique des données issues de l'imagerie
médicale. N'étant pas employé dans le reste du projet, il ne sera pas plus détaillé ici. Le format NIFTI est un format d'
image IRM mis en place par quelques uns des acteurs les plus influents de la neuro-imagerie. Etant le format principalement
employé dans ce projet, il fera l'objet d'une description détaillé dans la partie consacré aux analyses préliminaires.

Afin d'être employé par les outils d'automatisation de diagnostique mis en place par la plateforme d'informatique médicale,
les images ont besoin d'être pré-traité. En effet, les outils de machine learning utilisé pour le diagnostique fonctionne
en se basant sur un certain nombre de caractéristiques du cerveau. Ces caractéristiques peuvent être le volume de matière
grise ou de matière blanche d'une zone spécifique du cerveau, la quantité de liquide cérébro-spinal, le volume du cerveau, etc.
Il faut donc extraire ces informations des images à disposition. Ceci se fait à l'aide du framework SPM. Ce framework permet
grâce à la segmentation de récupérer des images d'une de ces caractéristique. La segmentation permet, par exemple, de récupérer
une image IRM de la matière grise du cerveau au format NIFTI. SPM utilise la segmentation afin de créer un atlas des caractéristiques.
Ainsi l'atlas fait correspondre, sous la forme de tableau, un certain nombre de quantité caractéristique à chaque région du cerveau.

(Insérer image du dataflow)

Une fois ce pré-traitement effectué, les données sont prêtes pour être utilisé par l'"algorithm factory". Cette dernière correspond
à l'ensemble des outils de diagnostique de la plateforme. 

Le problème principal de cette manière de faire est qu'il existe une quantité non-négligeable d'information qui sont perdu au cours
du pré-traitement. L'idée du LREN est donc de trouver une solution pour traiter directement les images entières avec des outils de
diagnostique automatique.

Pour se faire, ils proposent de mettre en place une extension de l'"algorithm factory". Cette extension permettra d'appliquer des algorithmes
pour l'apprentissage de modèles et de faire valider ces derniers directement sur les images d'IRM.

Ce projet vise donc à explorer la possibilité de mettre en place cette extension. Il mettra en place un workflow alternatif à celui
existant dans l'"algorithm factory". Cette alternative a pour contrainte de permettre de lancer de nouveaux algorithmes travaillant
directement sur les images en utilisant le framework de calcul distribué Apache-Spark. Cette nouvelle fonctionnalitée sera illustrée
par l'intégration d'une bibliothèque de deep-learning et fera l'objet d'une expérience avec de véritable image d'IRM.

Rappel des objectifs du projet
------------------------------
Les premières semaines du projet ont été utilisé afin de fixer les objectifs principaux et secondaires de ce projet. Ainsi, les objectifs
principaux de ce travail sont: 
1. L'installation et la prise en main d'Apache-Spark
2. L'intégration d'Apache-Spark à l'"algorithm factory"
3. L'interfaçage des bases de données d'image de la plateforme à Apache-Spark. Ces bases de données sont a créer et a améliorer si besoin
durant le projet.
4. Faire un état de l'art technique sur les différentes bibliothèques de deeplearning compatible avec Apache-Spark pour obtenir suffisamment
d'information pour permettre le choix de l'une d'entre elles à intégrer au-dessus de Spark.
5. Traduire la partie prédictive de l'algorithme au format PFA (Portable Format for Analytics).
6. Tester les nouvelles fonctionnalités avec une expérience concrète fournit par le LREN. Cette expérience utilisera des images d'IRM utilisé
par le laboratoire. Elle consistera en une classification de ces images. 

En plus de ces objectifs principaux, s'ajoute un objectif optionnel. Celui-ci consiste a étendre le portail web de la plateforme pour
permettre l'utilisation des nouvelles fonctionnalités de l'"algorithm factory".

Note sur la confidentialité au cours du projet
-----------------------------------------------
Comme déjà rappelé dans le cahier des charges de ce travail, l’aspect de l’utilisation d’image extraite d’IRM est un aspect sensible du point
de vue de la confidentialité.

Pour pallier tous soucis de confidentialité, les images employées durant la phase de développement seront des images totalement ouvertes même
si ces dernières ne sont pas des images issues d’IRM. 

Si des images autres que des données de recherche devaient être utilisées, elles seront anonymisée et ne quitteront jamais le réseau sécurisé des
hôpitaux dont elles sont originaires. 

Une attention particulière devra également être portée sur la réutilisation de l’existant afin de respecter les directives de plagiat et le droit
d’auteur (cf. directives générales en matière de plagiat de la HE-ARC).

Analyses préliminaires
======================
Le format NIFTI
----------------
Ce travail est un projet de neuro-imagerie, il est donc naturel de devoir travailler avec des
images IRM du cerveau. Le format utilisé par le CHUV pour les images est le format NIFTI
(Neuroimaging Informatics Technology Initiative), un format d'image très spécialisé mais
également très répandu dans ce domaine. 

Ce chapitre présente donc ce format afin de mieux le comprendre. Pour faire celà, nous
allons voir l'origine du format, une vue d'ensemble des principales caractéristiques du format
et quelques outils qui ont été utiles à la réalisation de ce travail.

Origine du format NIFTI
***********************
NIFTI est un format de fichier pour sauvegarder des données d'IRM. Il fonctionne
sur le principe des voxels et est multidimensionnel. Le NIFTI 

Ce format a été imaginé il y a une dizaine d'année pour remplacer le format ANALYZE 7.5.
Ce format était très utilisé mais était également très problématique. Le soucis principal de
ce format étant le manque d'information sur l'orientation dans l'espace de l'élément scanné.
Les données enregistrées ne pouvaient donc pas être lu et interprêté sans ambiguité. A cause
de ce manque d'information il existait principalement une confusion entre le côté droit et le
côté gauche du cerveau. 

Deux conférences furent alors mises en place par quelques-uns des concepteurs des plus grands
logiciels de neuroimagerie. Ces deux conférences, le Data Format Working Group (DFWG), se sont
réunit au "National Institue of Health" (NIH) pour trouver un format de remplacement. Dec ces
réunions naquit le format NIFTI. Celui-ci veut intégrer de nouvelles informations et devenir
un nouveau standard de neuroimagerie.

Vue d'ensemble du format NIFTI
******************************
Le format ANALYZE 7.5 avait besoin de deux fichiers pour fonctionner. Un fichier *.hdr contenant
le header pour stocker les méta-données et un fichier *.img contenant les données de l'image.
Le format NIFTI a conservé l'idée d'avoir un header et des données afin de préserver la compatibilité
avec les systèmes déjà en place. Toutefois, des améliorations ont été apportés et pour évité de faire
l'erreur d'oublier l'un des deux fichiers du format, il a été décidé de permettre le stockage
dans un seul fichier avec l'extension *.nii. Ces images contenant de grandes zones d'image
noires, elles sont donc parfaites pour être compressées avec gzip. Il n'est donc absolument
pas rare de trouver des fichiers NIFTI au format *.nii.gz. Pour ce travail nous avons utilisé
les formats *.nii et *.nii.gz.

Le format NIFTI est un format de fichier que l'ont peu représenter par une matrice multidimensionnel.
Au total, il peut compter jusqu'à 7 dimensions. Dans tous les cas, les 3 premières dimensions sont des
dimensions spatiales (x, y, z) et la quatrième est une dimension temporelle. Les dimensions suivantes
(5-7) sont des dimensions reservées à d'autre usage et sont plus ou moins libre. Dans le cadre de ce
projet, les images utilisées ne possède que 3 dimensions (les 3 dimensions spatiales). On peut donc voir
les images comme étant un instantané du cerveau en 3 dimensions et chaque case de la matrice de données
représente un voxel de cette image.

Les dimensions et d'autres informations importantes sur le fichier sont stocké dans un fichier
header. Ce dernier est d'une taille de 348 octets. (Il y a un tableau de toutes les valeurs sur
brainder.org il doit venir etre collé ici.)

Le champs principalement utilisé lors de ce projet est le champs short dim[8]. Ce champs est un
tableau contenant les données sur les dimensions du fichier. Ce tableau contient pour: 
- Dim[0]: Le nombre de dimensions
- Dim[1 -7]: Est un nombre positif contenant la longueur de la dimension en question.

Pour ce travail deux types de NIFTI ont été employé. Le premier type de NIFTI a avoir été utilisé sont des
images générés et très simple. Ces images correspondent à des sphères et des cubes. La dimension de ces images
générées peut être choisi. Au début du projet, de manière a facilité les tests, la taille de ces images étaient
de 100x100x100. Puis lorsque le projet eut une forme plus concrète la taille fut changer pour correspondre à la
taille standard utilisé par le CHUV (190x190x160). Le second type de données correspond aux images fournient par
le LREN. A savoir des images de la matière grise du cerveau avec une taille standard de 190x190x160. 

Outils pratique
***************
(A finir avec inspiration XD)
Le format NIFTI est un format très spécifique au domaine de la neuro-imagerie. Il fallait donc, au début
du projet, pouvoir visualiser et manipuler ce genre de fichier. Pour faire celà, il existe de nombreux outils.
Ce chapitre va donc présenter de manière suscinte les outils qui ont été employé pour la réalisation du projet.

Le calcul distribué
-------------------
(A revoir)
Le nombre d'image et la taille de ces dernières font qu'il y a un nombre très important de données et de calcul a effectué.
Pour le confort de l'utilisateur, le temps de traitement de ces données doit être le plus court possible. La plateforme
actuellement en place au CHUV tourne donc sur un cluster de machine afin de permettre à l'utilisateur d'obtenir le plus rapidement
possible les résultats des analyses qu'il demande.

Ce projet doit donc pouvoir se porter sur l'infrastructure en place. De plus, le Laboratoire de Recherche En Neuro-imagerie désire
intégrer la technologie Spark pour effectuer leur calcul. Ces deux contraintes ont donc fait l'objet d'une analyse et sont exposé
dans ce chapitre.

Qu'est ce que le calcul distribué ?
***********************************
Ces dernières années la quantité de données disponibles a explosé. Rapidement, les technologies ont du s'adapter à cette quantité
d'information toujours plus importante à traiter. L'une des solutions trouvé pour résoudre se problème consiste à répartir les tâches
de traitement (de calcul) sur plusieurs unité de travail. Ainsi, on répartit le besoin en puissance de calcul, pour un projet, en
petite entités sur autant d'ordinateurs disponible qu'il y en a dans notre réseau distribué.

Celà permet d'exploiter les ressources de chaques machines au profit d'un projet commun. Ce projet dispose alors d'une puissance de
calcul de la somme de tous les ordinateurs individuels.

Le calcul distribué s'effectue donc au sein d'un cluster de machine. C'est à dire, au sein d'un groupe de machines indépendantes fonctionnant
comme une seule et même entité. Chacune de ces entités correspond à un noeud. Si une machine est ajouté au cluster, la puissance de calcul est
directement augmenté contrairement à une machine seule, où si l'on veut augmenter la puissance de calcul, il faut augmenter la puissance des
processeurs.

Pour le calcul distribué, les noeuds sur lesquels les calculs sont exécuté sont donc distant, autonome et ne partage pas de ressources. Il
faut donc que chaques noeuds communiquent avec les autres au travers de message qu'il s'envoie au travers du cluster.

Pour pouvoir distribuer son projet, il faut donc diviser le problème initial en sous-problème et assigner à chaque noeud l'un de ces sous-problèmes.
Chaque noeud effectue la tâche qui lui est assigné. On récupère alors le résultat de chacun des sous-problèmes et on les combine pour obtenir le
résultat finale du projet initial.

Afin de gérer tout celà il est possible d'utiliser des framework de calcul distribué. Ces framework fournissent un ensemble d'outils pour faciliter
la création d'application distribuées. Le CHUV à choisi pour ce projet d'utiliser le framework Apache-Spark. La suite de ce chapitre présentera donc
ce framework et son fonctionnement.

Spark
*****
Spark est un framework open-source de calcul distribué écrit en Scala. Il a été conçu en 2009 par Matei Zaharia lors de son doctorat au sein de l'université de Californie
à Berkley. L'objectif de Matei Zaharia lors de la conception de Spark était de trouver une solution pour accélerer le traitement des systèmes Hadoop. Spark
est transmis a Apache en 2013 et devient l'un des projets les plus actifs de la firme. Le framework a le vent en poupe (à l'instar de Docker que nous verrons
plus loin dans ce rapport) et est en train de remplacer Hadoop. En effet, il a été démontré que Spark permet des temps d'exécution jusqu'à 100 fois plus courts
qu'Hadoop pour les mêmes tâches. La dernière version de Spark est Spark 2.2.0 et est disponible depuis le 11 juillet 2017. Spark fournit une API haut-niveau en
Java, Scala, Python et R.

Afin de fonctionner aussi rapidement Spark fonctionne directement en mémoire et cherche a avoir un traitement proche du temps-réel. Lorsque Spark execute des tâches,
il cherche à maintenir les résultats intermédiaires en mémoire plutôt que sur le disque. Cette manière de faire permet de facilement pouvoir travailler à plusieurs
reprises sur le même jeu de données. Toutefois Spark n'est pas restreint au travail en mémoire. Il peut aussi bien travailler sur le disque. Les opérateurs réalisent
des opérations externes lorsque les données ne tiennent pas en mémoire. Par défaut, Spark essaie de stocker le plus d'info en mémoire avant de basculer sur le disque.
Cependant, ce comportement est configurable. Il est possible de demander a Spark de ne travailler que sur le disque ou uniquement en mémoire mais également avec une
partie des données en mémoire et l'autre partie sur le disque.

Spark possède un écosystème contenant des bibliothèques additionnelles qui permettent de travailler dans les dommaines du "big data" et du machine learning.
Dans cet écosystème, on trouve notamment: 

* Spark Streaming: Permet le traitement temps-réel des données de flux.
* Spark SQL: Permet d'exécuter des requêtes SQL pour charger et transformer les données et ce quel que soit le format d'origine de celles-ci.
* Spark GraphX: Permet le traitement et la parallélisation de graphes. 
* Spark MLlib: Est une bibliothèque d'apprentissage automatique qui contient tous les algorithmes et utilitaires d'apprentissage classiques, tel que la classification,
la régression, le clustering, le filtrage collaboratif et la réduction de dimension, en plus des primitives d'optimisation nécessaires à ces tâches.

L'architecture de Spark comprend les trois composants principaux suivants: 
* Un composant de stockage des données qui utilise le système de fichier HDFS pour le stockage.
* Une API haut-niveau
* Un composant de gestion des ressources. Ce composant permet a Spark d'être déployé comme un serveur autonome ou sur un framework de traitements distribués comme Apache-Mesos
ou Apache-YARN.

L'élément de base principal au coeur de Spark est le "Resilient Distributed Dataset" ou RDD. Un RDD est une abstraction de collection sur laquelle les opérations sont effectué
de manière distribué et en étant tolérante aux pannes matérielles. On peut donc les voir comme une table dans une base de données. Un RDD peut contenir n'importe quel type de donné
et est stocké par Spark sur différentes partitions. Ainsi, le traitement que l'on écrit pour un RDD semble s'exécuter sur une JVM mais il sera en fait découpé pour s'exécuter sur plusieurs
noueds. Si le cluster de machine perd un noeud, le sous-traitement sera automatiquement relancé sur un autre noeud par le framework. Ceci est possible car un RDD sait recréer et recalculer
son ensemble de données. Les RDD supportent deux types d'opérations:
* Les transformations(map, filter, flatMap, groupByKey, reducebyKey, etc...): Celles-ci retourne un nouvel RDD. 
* Les actions(reduce, collect, count, first, take, foreach, etc...): Celles-ci évaluent et retournent une nouvelle valeur. 

L'exécution de Spark peut se faire de plusieurs manière différente. Pour celà il suffit de donner le bon paramètre de connexion au moteur de Spark (Master, chef d'orchestre). Ainsi, la connexion
au moteur peut se faire de manière local (sur un ou K "worker"), en se connectant à un cluster Spark, Mesos ou Yarn.

(Add tableau)

Spark fournit également une interface web. Pour joindre cette interface, il suffit, une fois Spark en cours d'exécution, de se connecter sur le port 4040 du localhost. Cette interface permet de
surveiller le stockage, l'environnement, les exécuteurs et les étapes effectué par Spark.

Spark possèdent encore bien des caractéristiques qui font de lui l'un des leaders du domaine. Toutefois, nous avons vu ici ces principales caractéristiques et les principaux outils utilisé durant
l'élaboration de ce travail de Bachelor. L'utilisation de Spark dans le projet sera détaillé plus loin dans la rédaction de ce rapport. 

Le deeplearning et choix d'une bibliotheque
--------------------------------------------
La plateforme d'informatique médicale tenue par le LREN aimerait pouvoir donner à ces utilisateurs la possibilité de lancer des expériences de deeplearning. Ce projet a donc pour objectif
d'ouvrir la voie a ce procéder.

Il est donc important de faire le point sur cette technologie. Cette partie va donc permettre de voir ce que sont les réseaux de neurones et le deeplearning. Puis dans un second temps,
les réseaux de convolution seront abordé. Dans une troisième partie, ce rapport abordera les différentes manières de mélanger calcul distribué et deeplearning. Ces trois premières parties,
permettront de se faire une idée de ce concept et d'aborder plus sereinement l'état de l'art des bibliothèques de deeplearning et le choix de l'une d'entre elle pour ce travail.

Considération générale
**********************
Le deeplearning est un ensemble de méthodes de machine learning. Le machine learning est l'un des champs d'étude de l'intelligence artificielle et cherche à permettre à une machine à modéliser
des phénomènes dans le but de prendre des décisions et de résoudre un problème concret. Cette capacité à prendre des déscisions se fait sans être explicitement programmé par le développeur.

Un problème concret peut, par exemple, être d'identifier des fraudes, d'aider aux diagnostiques médicaux, de recommander un article personnalisé à un client, prédire le prix d'un produit, etc.
L'idée derrière le machine learning est alors de permettre à la machine de se construire une représentation interne du problème sans que le développeur n'ait besoin de la modéliser pour elle.
A l'aide de cette modélisation, la machine pourra alors effectuer la tâche qui lui est demandé. La tâche demandé au cours de ce projet est une tâche de classification. La classification sert
à pouvoir ranger une donnée (une image par exemple) dans une classe spécifique. Pouvoir dire d'une image qu'elle représente un chat ou un chien par exemple. Etant la tâche sur laquelle ce travail
se base la classification sera utilisé comme exemple dans la suite de ce rapport.

Pour que l'algorithme de machine learning puisse se construire une représentation du problème, il faut lui fournir un jeu de données d'exemple. Grâce à ce jeu de données, l'algorithme va pouvoir
s'entraîner et s'améliorer dans la tâche qui lui a été confié. Nous pourrons par la suite lui fournir des données réels et obtenir un résultat aux problèmes posés.

Il existe différent algorithme de machine learning. Parmis eux nous pouvons noté: 
* La régression linéaire
* La classification naïve de Bayes
* Machine à vecteurs de support (SVM: support vector machine)
* K-nn
* Random Forest (Forêt d'arbres décisionnnels)
* Réseau de neurones

Le deeplearning est une technique qui fonctionne sur la base des réseaux de neurones. Les réseaux de neurones sont construit à partir d'un paradigme biologique. Ce paradigme est celui du neurone formel.
Un neurone formel est une représentation mathématique et informatique d'un neurone biologique. Le neurone formel possède généralement plusieurs entrées et une sortie. Les entrées correspondent ainsi aux
dendrites d'un neurone, tandis que la sortie correspond à l'axone de ce dernier. Pour fonctionner, un neurone biologique reçoit des signaux excitateurs et inhibiteurs grâce aux synapses (lien entre deux
neurones). Ces signaux sont simulés dans un réseau de neurones informatiques par des coefficients numériques associés aux entrées des neurones. Ces coefficients sont appelés les biais. Les valeurs
numériques de ces coefficients sont ajustées durant la phase d'apprentissage. Le neurone formel fait alors des calculs avec les poids pondérés des entrées reçues, puis applique au résultat de ce calcul
une fonction d'activation. La valeurs finale obtenue alors se retrouve alors sur la sortie du neurone. Ainsi le neurone formel est donc l'unité élémentaire des réseaux de neurones artificiels.

L'un des éléments les plus important d'un neurones formel est sa fonction d'activation. Il est donc important de bien choisir cette dernière. Il existe en effet plusieurs fonctions d'activations typiques:
* La fonction sigmoïde
* La fonction tangente hyperbolique
* La fonction de base radiale
* La fonction sigma-pi
* La fonction RELU
* La fonction SOFTMAX  
* etc


Réseaux de convolution
**********************
Deeplearning et calcul distribué
********************************
Bibliothèque disponible et choix
********************************

Docker
-------
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
Schémas conceptuels
--------------------
Description des classes
-------------------------
Package "Core"
**************
La classe "Main"
++++++++++++++++
La classe "DataReader"
++++++++++++++++++++++
Package "Config"
****************
La classe "Configuration"
+++++++++++++++++++++++++
Package "Generator"
*******************
La classe "DataTestGenerator"
+++++++++++++++++++++++++++++
Package "Wrapper"
*****************
La classe "WrapperDl4j"
+++++++++++++++++++++++
La classe "LocalWrapperDl4j"
++++++++++++++++++++++++++++
La classe "SparkWrapperDl4j"
++++++++++++++++++++++++++++
Choix de la topologie du/des reseaux de neurones
--------------------------------------------
Description du workflow
-------------------------

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
