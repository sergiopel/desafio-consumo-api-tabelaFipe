package com.comsistemas.TabelaFipe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // vai ignorar as propriedades "anos", pois só interessa "modelos" por enquanto
public record Modelos(List<Dados> modelos) {
}
