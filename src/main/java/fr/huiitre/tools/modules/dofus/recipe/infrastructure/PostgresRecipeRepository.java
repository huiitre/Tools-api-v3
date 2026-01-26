package fr.huiitre.tools.modules.dofus.recipe.infrastructure;

import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.modules.dofus.recipe.application.ports.RecipeRepository;

public class PostgresRecipeRepository implements RecipeRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresRecipeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Long itemId, Long ingredientId, Long quantity) {
        String sql = "INSERT INTO tools_dofus.recipe (item_id, ingredient_id, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, itemId, ingredientId, quantity);
    }

    @Override
    public void update(Long itemId, Long ingredientId, Long quantity) {
        String sql = "UPDATE tools_dofus.recipe SET quantity = ? WHERE item_id = ? AND ingredient_id = ?";
        jdbcTemplate.update(sql, quantity, itemId, ingredientId);
    }

    @Override
    public boolean exists(Long itemId, Long ingredientId) {
        String sql = "SELECT COUNT(*) FROM tools_dofus.recipe WHERE item_id = ? AND ingredient_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, itemId, ingredientId);
        return count != null && count > 0;
    }
    
}
