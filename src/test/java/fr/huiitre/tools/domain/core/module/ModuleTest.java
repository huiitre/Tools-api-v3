package fr.huiitre.tools.domain.core.module;

/*
 * Import des assertions JUnit
 * Elles servent à vérifier les règles métier du domain
 */
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/*
 * Tests unitaires de l'entité métier Module
 *
 * IMPORTANT :
 * - aucun mock
 * - aucune infrastructure
 * - aucun use case
 *
 * On teste uniquement le métier pur
 */
class ModuleTest {

    /*
     * Test : création valide d’un module
     */
    @Test
    void should_create_module_with_valid_data() {

        // WHEN : création du module via la factory métier
        Module module = Module.create(
            "DOFUS",
            "Dofus",
            "Module Dofus"
        );

        // THEN : le module est correctement initialisé
        assertNotNull(module);
        assertEquals("DOFUS", module.getCode());
        assertEquals("Dofus", module.getName());
        assertEquals("Module Dofus", module.getDescription());

        /*
         * Règle métier :
         * un module est inactif par défaut à la création
         */
        assertFalse(module.getActive());
    }

    /*
     * Test : code null → exception
     */
    @Test
    void should_throw_exception_when_code_is_null() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                null,
                "Dofus",
                "Module Dofus"
            )
        );

        assertEquals("CODE_REQUIRED", exception.getMessage());
    }

    /*
     * Test : code vide → exception
     */
    @Test
    void should_throw_exception_when_code_is_blank() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                "   ",
                "Dofus",
                "Module Dofus"
            )
        );

        assertEquals("CODE_REQUIRED", exception.getMessage());
    }

    /*
     * Test : name null → exception
     */
    @Test
    void should_throw_exception_when_name_is_null() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                "DOFUS",
                null,
                "Module Dofus"
            )
        );

        assertEquals("NAME_REQUIRED", exception.getMessage());
    }

    /*
     * Test : name vide → exception
     */
    @Test
    void should_throw_exception_when_name_is_blank() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                "DOFUS",
                "",
                "Module Dofus"
            )
        );

        assertEquals("NAME_REQUIRED", exception.getMessage());
    }

    /*
     * Test : description null → exception
     */
    @Test
    void should_throw_exception_when_description_is_null() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                "DOFUS",
                "Dofus",
                null
            )
        );

        assertEquals("DESCRIPTION_REQUIRED", exception.getMessage());
    }

    /*
     * Test : description vide → exception
     */
    @Test
    void should_throw_exception_when_description_is_blank() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Module.create(
                "DOFUS",
                "Dofus",
                ""
            )
        );

        assertEquals("DESCRIPTION_REQUIRED", exception.getMessage());
    }
}
