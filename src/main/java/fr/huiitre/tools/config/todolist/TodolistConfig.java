package fr.huiitre.tools.config.todolist;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;
import fr.huiitre.tools.infrastructure.todolist.todo.PostgresTodoRepository;
import fr.huiitre.tools.infrastructure.todolist.todolist.PostgresTodolistRepository;

@Configuration
public class TodolistConfig {
    
    @Bean
    public TodolistRepository todolistRepository(
        JdbcTemplate jdbcTemplate
    ) {
        return new PostgresTodolistRepository(
            jdbcTemplate
        );
    }

    @Bean
    public TodoRepository todoRepository(
        JdbcTemplate jdbcTemplate
    ) {
        return new PostgresTodoRepository(
            jdbcTemplate
        );
    }
}
