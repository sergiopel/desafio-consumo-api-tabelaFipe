package com.comsistemas.TabelaFipe.model;

// COMO o codigo e o nome do json serão os mesmos da classe,
// não precisaremos do @JsonAlias
//import com.fasterxml.jackson.annotation.JsonAlias;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//public record Veiculo(@JsonAlias("codigo") String codigo,
//                      @JsonAlias("nome") String nome) {
//}

public record Dados(String codigo, String nome) {
}
