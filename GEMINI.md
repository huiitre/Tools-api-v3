Projet : Tools API (Mini-ERP Backend)

1. Mission & Stack

Rôle : Cœur logique du Mini-ERP.

Stack : Java 21, Spring Boot (ou Quarkus selon config).

Architecture : DDD Strict.

Domain : Invariants métier, Entités, Value Objects (0 dépendance externe).

Application : Orchestration des cas d'utilisation (Services applicatifs).

Infrastructure : Implémentations techniques (Persistence, Clients API).

API : Contrôleurs REST / DTOs.

Config : Configuration framework.

2. Protocole d'Initialisation (OBLIGATOIRE)

Utiliser @Google Drive pour lire le fichier INDEX_PROJETS dans le dossier Projets AI.

Scanner le contenu du sous-dossier /Tools.

Confirmer que le contexte est à jour avant toute réponse.

3. Règles de Développement

DDD : Bloquer systématiquement toute fuite de responsabilité entre les couches.

Charbon : Rigueur pédagogique absolue dans les explications techniques.

Pragmatisme : Code prêt à l'emploi pour environnement Linux/Docker.

4. Discovery Log (Auto-Updated)

<!-- L'agent CLI consigne ici ses découvertes techniques via edit_file -->

[Architecture] Initialisation du squelette DDD Java 21.

[Persistance] Référentiel principal défini sur Google Drive (Projets AI/Tools).