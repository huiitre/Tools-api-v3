package fr.huiitre.tools.config.todolist;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;
import fr.huiitre.tools.infrastructure.todolist.todo.PostgresTodoRepository;
import fr.huiitre.tools.infrastructure.todolist.todolist.PostgresTodolistRepository;

@Configuration
public class TodolistConfig {
    
    @Bean
    public TodolistRepository todolistRepository(
        DataSource dataSource
    ) {
        return new PostgresTodolistRepository(
            dataSource
        );
    }

    @Bean
    public TodoRepository todoRepository(
        DataSource dataSource
    ) {
        return new PostgresTodoRepository(
            dataSource
        );
    }
}
