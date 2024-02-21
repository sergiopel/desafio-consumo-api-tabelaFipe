package com.comsistemas.TabelaFipe.principal;

import ch.qos.logback.core.encoder.JsonEscapeUtil;
import com.comsistemas.TabelaFipe.model.Dados;
import com.comsistemas.TabelaFipe.model.Modelos;
import com.comsistemas.TabelaFipe.model.Veiculo;
import com.comsistemas.TabelaFipe.service.ConsumoApi;
import com.comsistemas.TabelaFipe.service.ConverteDados;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    // url: https://parallelum.com.br/fipe/api/v1/carros/marcas
    //      lista todos os carros de todas as marcas
    // url: https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos,
    //      lista os veículos de uma determinada marca
    // url: https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos/5940/anos
    //      lista os modelos e os anos de uma determinada marca
    // url: https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos/5940/anos/2014-3
    //      lista o ano , valor e outros dados de uma marca específica
    //
    // Entrada pelo teclado
    private Scanner scan = new Scanner(System.in);
    // Constante para o consumo da API
    private final String ENDERECO_API = "https://parallelum.com.br/fipe/api/v1/";
    // Instância do consumo da API
    private ConsumoApi consumoApi = new ConsumoApi();
    // Instancia mapper
    private ObjectMapper mapper = new ObjectMapper();
    // O conversor irá obter uma lista, pois o resultado do json é um array
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        System.out.println("Consumo da API da Tabela Fipe - deividifortuna.github.io/fipe");
        System.out.println("###############################################");
        System.out.println("#                   Carros                    #");
        System.out.println("#                   Motos                     #");
        System.out.println("#                   Caminhoes                 #");
        System.out.println("###############################################");
        System.out.println("# Digite o tipo de veículo que deseja listar: #");
        String tipoVeiculoDigitado = scan.nextLine();
        if (!tipoVeiculoDigitado.equalsIgnoreCase("carros") &&
                !tipoVeiculoDigitado.equalsIgnoreCase("motos") &&
                !tipoVeiculoDigitado.equalsIgnoreCase("caminhoes")) {
            System.out.println("O tipo de veículo digitado é inválido!");
        }
        tipoVeiculoDigitado = tipoVeiculoDigitado.toLowerCase();

        //Consumir API
        String enderecoCompleto = ENDERECO_API + tipoVeiculoDigitado +  "/marcas";
//        System.out.println(enderecoCompleto);
        String todosVeiculosJson = consumoApi.obterDadosApi(enderecoCompleto);
        System.out.println(todosVeiculosJson);

        var marcas = conversor.obterLista(todosVeiculosJson, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite o código da marca a consultar: ");
        var codigoDaMarca = scan.nextLine();
        enderecoCompleto = enderecoCompleto + "/" + codigoDaMarca + "/modelos";
        String veiculosDaMarcaJson = consumoApi.obterDadosApi(enderecoCompleto);
        System.out.println(veiculosDaMarcaJson);
        // A minha classe Modelos já está representada como uma lista, então eu uso obterDados
        // Chave: "modelos" e o valor tem uma lista de modelos representados por "codigo" e "nome"
        var listaVeiculosDaMarca = conversor.obterDados(veiculosDaMarcaJson, Modelos.class);
        System.out.println("\nModelos dessa marca: ");
        listaVeiculosDaMarca.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = scan.nextLine();
        // pegar listaVeiculosDaMarca e gerar outra lista com os modelos filtrados para
        // que possamos buscar os anos deles depois
        List<Dados> modelosFiltrados = listaVeiculosDaMarca.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                        .collect(Collectors.toList()); // transforma em nova lista (modelosFiltrados)
        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        // mostrar agora os anos dos modelo filtrado e os seus valores
        System.out.println("\nDigite agora o código do modelo para buscar os valores de avaliação");
        var codigoModelo = scan.nextLine();
        enderecoCompleto = enderecoCompleto + "/" + codigoModelo + "/anos";
        var anosJson = consumoApi.obterDadosApi(enderecoCompleto);
        System.out.println(anosJson);

        List<Dados> anos = conversor.obterLista(anosJson, Dados.class);
        // Varrer lista, onde para cada ano terá que aparecer as informações detalhadas
        List<Veiculo> veiculos = new ArrayList<>();
        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = enderecoCompleto + "/" + anos.get(i).codigo();
            var valoresJson = consumoApi.obterDadosApi(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(valoresJson, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);

    }
}
