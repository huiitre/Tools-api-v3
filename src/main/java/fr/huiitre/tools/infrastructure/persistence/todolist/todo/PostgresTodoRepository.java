package fr.huiitre.tools.infrastructure.persistence.todolist.todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.domain.todolist.todo.Todo;
import fr.huiitre.tools.domain.todolist.todo.TodoPriority;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;

public class PostgresTodoRepository extends AbstractPostgresRepository implements TodoRepository {
    
    public PostgresTodoRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Long userId, Todo todo) {
        final String sql = """
            INSERT INTO tools_todolist.todo (todolist_id, name, description, display_order, priority)
            SELECT id, ?, ?, ?, ?
            FROM tools_todolist.todolist
            WHERE user_id = ? AND id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setString(1, todo.getName());
                ps.setString(2, todo.getDescription());
                ps.setLong(3, todo.getDisplayOrder());
                ps.setObject(
                    4,
                    todo.getPriority().name(),
                    java.sql.Types.OTHER
                );
                ps.setLong(5, userId);
                ps.setLong(6, todo.getTodolistId());

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new IllegalArgumentException("TODOLIST_NOT_FOUND_OR_NOT_OWNED");
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert todo", e);
        }
    }

    @Override
    public void update(Long userId, Todo todo) {
        final String sql = """
            UPDATE tools_todolist.todo t
            SET name = ?, description = ?, is_completed = ?, display_order = ?, priority = ?
            WHERE t.id = ?
            AND t.todolist_id = ?
            AND EXISTS (
                SELECT 1
                FROM tools_todolist.todolist tl
                WHERE tl.id = t.todolist_id
                    AND tl.user_id = ?
            )
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setString(1, todo.getName());
                ps.setString(2, todo.getDescription());
                ps.setBoolean(3, todo.isCompleted());
                ps.setLong(4, todo.getDisplayOrder());
                ps.setObject(
                    5,
                    todo.getPriority().name(),
                    java.sql.Types.OTHER
                );
                ps.setLong(6, todo.getId());
                ps.setLong(7, todo.getTodolistId());
                ps.setLong(8, userId);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new IllegalArgumentException("TODO_NOT_FOUND_OR_NOT_OWNED");
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update todo", e);
        }
    }

    @Override
    public void delete(Long userId, Long todoId) {
        final String sql = """
            DELETE FROM tools_todolist.todo
            WHERE id = ? AND todolist_id IN (
                SELECT id FROM tools_todolist.todolist WHERE user_id = ?
            )
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, todoId);
                ps.setLong(2, userId);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new IllegalArgumentException("TODO_NOT_FOUND_OR_NOT_OWNED");
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete todo", e);
        }
    }

    @Override
    public List<Todo> findAllByUserIdAndTodolistId(Long userId, Long todolistId) {
        final String sql = """
            SELECT t.id, t.todolist_id, t.name, t.description, t.is_completed, t.display_order, t.priority
            FROM tools_todolist.todo t
            JOIN tools_todolist.todolist l ON t.todolist_id = l.id
            WHERE l.user_id = ? AND l.id = ?
            ORDER BY t.display_order ASC
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setLong(2, todolistId);

                try (var rs = ps.executeQuery()) {
                    List<Todo> todos = new java.util.ArrayList<>();
                    while (rs.next()) {
                        Todo todo = Todo.rehydrate(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_completed"),
                            rs.getLong("todolist_id"),
                            rs.getLong("display_order"),
                            TodoPriority.valueOf(rs.getString("priority"))
                        );
                        todos.add(todo);
                    }
                    return todos;
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find todos by user and todolist", e);
        }
    }

    @Override
    public Optional<Todo> findById(Long userId, Long todoId) {
        final String sql = """
            SELECT t.id, t.todolist_id, t.name, t.description, t.is_completed, t.display_order, t.priority
            FROM tools_todolist.todo t
            JOIN tools_todolist.todolist l ON t.todolist_id = l.id
            WHERE t.id = ? AND l.user_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, todoId);
                ps.setLong(2, userId);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Todo todo = Todo.rehydrate(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_completed"),
                            rs.getLong("todolist_id"),
                            rs.getLong("display_order"),
                            TodoPriority.valueOf(rs.getString("priority"))
                        );
                        return Optional.of(todo);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find todo by id", e);
        }
    }

    @Override
    public void deleteByTodolistId(Long userId, Long todolistId) {
        
    }
}
