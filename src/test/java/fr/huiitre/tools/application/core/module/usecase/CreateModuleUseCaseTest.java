package fr.huiitre.tools.application.core.module.usecase;

/*
 * Import des assertions JUnit :
 * - assertEquals, assertNotNull, assertFalse, assertThrows, etc.
 * Elles servent à vérifier que le résultat correspond à ce qu’on attend.
 */
import static org.junit.jupiter.api.Assertions.*;

/*
 * Import des outils Mockito :
 * - mock()
 * - when()
 * - verify()
 * - never()
 * Ils servent à créer de faux objets (mock) et vérifier les interactions.
 */
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.huiitre.tools.application.core.module.command.CreateModuleCommand;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.domain.core.module.Module;

/*
 * Classe de test du use case CreateModuleUseCase
 * Convention :
 * - NomDuUseCase + Test
 * - Même package que la classe testée
 */
class CreateModuleUseCaseTest {

    /*
     * Faux repository (mock)
     * On ne teste PAS la base de données ici
     * On simule juste son comportement
     */
    private ModuleRepository moduleRepository;

    /*
     * Le use case que l’on teste réellement
     */
    private CreateModuleUseCase useCase;

    /*
     * Cette méthode est exécutée AVANT chaque test (@Test)
     * Elle permet d’avoir un état propre à chaque test
     */
    @BeforeEach
    void setUp() {

        /*
         * Création d’un mock de ModuleRepository
         * Ce n’est pas une vraie implémentation,
         * mais un objet simulé par Mockito
         */
        moduleRepository = mock(ModuleRepository.class);

        /*
         * Création du use case avec le mock injecté
         * On teste uniquement la logique du use case
         */
        useCase = new CreateModuleUseCase(moduleRepository);
    }

    /*
     * Test : le module existe déjà → exception
     */
    @Test
    void should_throw_exception_when_module_code_already_exists() {

        /*
         * GIVEN
         * On prépare les données d’entrée du test
         */
        CreateModuleCommand command = new CreateModuleCommand(
                "Dofus",
                "Module Dofus",
                "DOFUS");

        /*
         * On dit au mock :
         * "Quand existsByCode('DOFUS') est appelé,
         * alors retourne true"
         */
        when(moduleRepository.existsByCode("DOFUS")).thenReturn(true);

        /*
         * WHEN / THEN
         * On exécute le use case et on vérifie qu’il lève une exception
         */
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command));

        /*
         * On vérifie que le message de l’exception est correct
         */
        assertEquals("MODULE_ALREADY_EXISTS", exception.getMessage());

        /*
         * On vérifie que save() n’a JAMAIS été appelé
         * (car la création doit être bloquée)
         */
        verify(moduleRepository, never()).save(any());
    }

    /*
     * Test : le module n’existe pas → création OK
     */
    @Test
    void should_create_module_when_code_does_not_exist() {

        /*
         * GIVEN
         * Données d’entrée
         */
        CreateModuleCommand command = new CreateModuleCommand(
                "Dofus",
                "Module Dofus",
                "DOFUS");

        /*
         * Cette fois, on simule que le module n’existe pas
         */
        when(moduleRepository.existsByCode("DOFUS")).thenReturn(false);

        /*
         * WHEN
         * Exécution du use case
         */
        Module result = useCase.execute(command);

        /*
         * THEN
         * Vérifications sur le résultat retourné
         */
        assertNotNull(result);
        assertEquals("DOFUS", result.getCode());
        assertEquals("Dofus", result.getName());
        assertEquals("Module Dofus", result.getDescription());

        /*
         * Vérification de la règle métier :
         * un module est inactif par défaut à la création
         */
        assertFalse(result.getActive());

        /*
         * Vérification que le repository a bien été appelé
         * avec le module créé
         */
        verify(moduleRepository).save(result);
    }
}
