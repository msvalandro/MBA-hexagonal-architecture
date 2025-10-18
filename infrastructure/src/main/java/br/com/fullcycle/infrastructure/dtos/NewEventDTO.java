package br.com.fullcycle.infrastructure.dtos;

public record NewEventDTO(
        String name,
        String date,
        int totalSpots,
        String partnerId
) { }
