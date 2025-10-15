package br.com.fullcycle.hexagonal.infrastructure.dtos;

import br.com.fullcycle.hexagonal.infrastructure.models.Event;

import java.time.format.DateTimeFormatter;

public record NewEventDTO(
        String name,
        String date,
        int totalSpots,
        Long partnerId
) { }
