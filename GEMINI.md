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

4. Stack réelle (à jour)

Stack : Java 21, Spring Boot, JDBC (JdbcTemplate — pas d'ORM Hibernate).
Pas de Flyway/Liquibase — schéma BDD géré manuellement.
Chaque use case est un @Service implémentant SecuredUseCase (AOP intercepte execute()).
Sécurité : Spring Security (.anyRequest().authenticated()) + UseCaseAuthorizationAspect.
@RequiredRole sur les controllers est décoratif — la vraie sécurité est dans les use cases.

5. Discovery Log

[Architecture] Initialisation du squelette DDD Java 21.

[Feature] Workshop Links — COMPLÈTE.
- WorkshopLink (id, source, url, label, createdAt) dans l'agrégat Workshop.
- Enum LinkSource : DOFUSBOOK, CUSTOM. Extensible via @Service LinkSourceHandler.
- Validation URL par source, label auto-résolu à la création, libre à l'édition.
- Limite : 3 liens max par atelier.
- Table : tools_dofus.workshop_link.
- Routes : POST/PUT/DELETE /dofus/workshops/{id}/links[/{id}].

[Feature] Riot/Valorant refresh token — COMPLÈTE.
- Route : POST /riot/valorant/refresh-token → { accessToken, refreshToken }.
- client_id hardcodé : prod-xsso-playvalorant (pas de secret).
- Appel POST form-urlencoded vers auth.riotgames.com/token.
- ModuleCode.RIOT ajouté à l'enum ModuleCode.
- Config : RiotConfig, adapter : RiotAuthHttpAdapter.

[Feature] Admin — gestion utilisateurs & stats — COMPLÈTE.
- GET  /users                → List<UserAdminView> avec avatarUrl + roles[id] (rôle par id).
- GET  /users/{userId}       → UserProfileDto complet (roles + modules).
- PUT  /users/{userId}/role  → body { "roleId": Long } — remplace le rôle global.
- GET  /admin/stats          → { totalUsers, activeUsers, newUsersThisWeek, usersPerModule[] }.
- UserRepository.findAllForAdmin() : JOIN users + user_role + role + user_auth_provider en 1 requête.
- UserRoleRepository.deleteAllByUserId() : utilisé pour le remplacement atomique du rôle global.
- Config : AdminConfig wire PostgresAdminStatsRepository.