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

[Feature] Gestion des liens externes (Dofusbook) sur les ateliers — COMPLÈTE.
- Domaine : `WorkshopLink` (id, source, url, label, createdAt) intégré à l'agrégat `Workshop` via `addLink()` / `getLinks()`.
- Enum `LinkSource` : DOFUSBOOK, CUSTOM. Extensible : ajouter un nouveau cas = créer un `@Service` implémentant `LinkSourceHandler`, rien d'autre à toucher.
- Validation URL par source : `DofusbookLinkSourceHandler` (3 formats : d-bk.net short, dofusbook.net public, dofusbook.net private), `CustomLinkSourceHandler` (http/https uniquement, protège contre javascript:/data:/file://).
- Résolution automatique du label à la création, label libre à l'édition.
- Limite : 3 liens max par atelier (vérifié côté use case).
- Table BDD : `tools_dofus.workshop_link` (à créer manuellement, pas de migration auto).
- API complète :
  - POST   /dofus/workshops/{id}/links       → 201 WorkshopLinkDto
  - PUT    /dofus/workshops/{id}/links/{id}  → 200 WorkshopLinkDto
  - DELETE /dofus/workshops/{id}/links/{id}  → 204
- WorkshopDto et WorkshopDetailResponse exposent désormais la liste `links`.
- État : Backend complet et testé, prêt pour intégration Front.