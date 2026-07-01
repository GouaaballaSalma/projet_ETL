# Plateforme LCN — CFG Bank

## 1. Introduction

Ce projet vise la digitalisation et l'automatisation du traitement de la Lettre de Change Normalisée (LCN) pour CFG Bank. La plateforme assure l'intégration des données par un processus ETL, leur stockage centralisé, ainsi que leur gestion au travers d'une application web. L'objectif technique est de fiabiliser les flux de traitement des LCN tout en garantissant la traçabilité des opérations.

## 2. Architecture Technique

*   **Frontend (React / TypeScript / Vite / TailwindCSS)**
    *   *Rôle :* Interface utilisateur web.
    *   *Justification :* Typage statique pour limiter les erreurs d'exécution (TypeScript), architecture orientée composants (React) et temps de compilation local optimisé (Vite).
*   **Backend API (Java 17 / Spring Boot 3.3.5)**
    *   *Rôle :* Logique métier et exposition des services REST.
    *   *Justification :* Abstraction des requêtes de persistance (Spring Data JPA) et sécurisation des accès applicatifs par jeton (Spring Security avec JWT).
*   **Base de Données (Oracle Database 21c)**
    *   *Rôle :* Persistance des données relationnelles.
    *   *Justification :* Conformité transactionnelle ACID adaptée au contexte bancaire et utilisation d'une image Docker allégée (`gvenzl/oracle-xe:21-slim-faststart`).
*   **Moteur ETL (Pentaho Data Integration)**
    *   *Rôle :* Extraction, transformation et chargement (ETL) des fichiers sources LCN.
    *   *Justification :* Capacité de traitement par lots et orchestration des flux de données vers la base Oracle.
*   **Conteneurisation (Docker / Docker Compose)**
    *   *Rôle :* Orchestration et exécution des services applicatifs.
    *   *Justification :* Isolement des environnements de dépendance et reproductibilité du déploiement inter-environnements.

**Flux de données :**
Fichiers sources → Moteur ETL (Pentaho) → Base Oracle ← Backend API (Spring Boot) ↔ Frontend (React)

**Schéma de déploiement interne :**
```text
[ Client HTTP ]
       |
  (Port 80)
       |
       v
[ Conteneur: lcn_frontend ] 
       |
  (Réseau Docker)
       |
       v
[ Conteneur: lcn_backend ]
       |
  (Réseau Docker - Port interne 1521)
       |
       v
[ Conteneur: oracle_lcn_db ]
       ^
  (Port hôte 1522)
       |
[ ETL Pentaho (Hôte) ]
```

## 3. Structure des composants

### Backend (`backend-lcn-api`)
*Rôle :* Traitement des règles de gestion, accès aux données et protection des endpoints REST.
*   `controller/` : Exposition des points d'entrée de l'API.
*   `service/` : Implémentation de la logique métier.
*   `repository/` : Interfaces d'accès aux données (Spring Data JPA).
*   `entity/` : Modèles de données relationnels (mapping objet-relationnel).
*   `dto/` : Objets de transfert de données.
*   `security/` : Configuration de l'authentification et de l'autorisation (JWT).

### Frontend (`frontend`)
*Rôle :* Présentation visuelle et gestion de l'interaction utilisateur.
*   `components/` : Composants d'interface utilisateur partagés.
*   `pages/` : Vues principales de l'application.
*   `services/` : Configuration des requêtes HTTP (via Axios) vers l'API.
*   `types/` : Définitions et interfaces de typage TypeScript.

### Moteur ETL (`pentaho` / `etl_script.bat`)
*Rôle :* Pipeline d'intégration des données LCN.
*   *Fonctionnement :* Le script batch `etl_script.bat` exécute le fichier `JOB_LCN_ETL_PRINCIPAL.kjb` via l'outil `Kitchen.bat` de Pentaho.
*   *Exécution :* L'exécution demande interactivement les paramètres de traitement (date d'arrêté, numéro de lot, chemin absolu du fichier source).

## 4. Déploiement

### Prérequis
*   Docker et Docker Compose installés sur la machine hôte.
*   Un fichier `.env` présent à la racine du projet contenant la configuration de la base :
    ```env
    ORACLE_ADMIN_PASSWORD=
    ORACLE_PORT=
    ORACLE_SERVICE=
    ORACLE_USER=
    ORACLE_PASSWORD=
    ```

### Instructions de lancement
1. Cloner le dépôt et se placer à la racine du répertoire.
2. Vérifier la présence du fichier `.env` et ajuster les valeurs si nécessaire.
3. Construire et démarrer les conteneurs en tâche de fond :
   ```bash
   docker-compose up -d --build
   ```

### Vérification de l'état des services
*   **Base Oracle (`oracle-xe`) :** Utiliser la commande `docker ps` pour vérifier l'état du conteneur (attendre le statut `healthy`). Le port d'écoute sur la machine hôte correspond à la variable `ORACLE_PORT` (ex: `1522`).
*   **Backend (`backend`) :** Le démarrage est conditionné par l'état `healthy` de la base de données. Les métriques de santé sont accessibles en interne via les endpoints Actuator. La documentation technique des points d'accès API est consultable via l'interface Swagger/OpenAPI générée par le backend.
*   **Frontend (`frontend`) :** L'interface utilisateur est exposée sur le port `80` de l'hôte. Elle est accessible via l'adresse `http://localhost`.

### Arrêt et nettoyage de l'environnement
*   Pour arrêter les processus sans perdre les données persistées :
    ```bash
    docker-compose down
    ```
*   Pour arrêter les processus et détruire les volumes de données (réinitialisation complète de la base Oracle) :
    ```bash
    docker-compose down -v
    ```

## 5. Dépannage courant

*   **Conflits de mappage de port Oracle :** Si une instance d'Oracle tourne déjà sur l'hôte, le script ETL (exécuté sur l'hôte) doit communiquer avec le port mappé dans le `.env` (ex: `1522`). Les services conteneurisés (comme `backend`) utilisent le port interne standard `1521` au sein du réseau Docker. Ces paramètres sont isolés pour prévenir les erreurs de connexion croisées.