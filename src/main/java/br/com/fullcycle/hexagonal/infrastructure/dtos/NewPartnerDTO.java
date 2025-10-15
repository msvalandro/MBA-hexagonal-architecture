package br.com.fullcycle.hexagonal.infrastructure.dtos;

import br.com.fullcycle.hexagonal.infrastructure.models.Partner;

public record NewPartnerDTO(String cnpj, String email, String name) { }
