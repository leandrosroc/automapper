package com.automapper;

import com.automapper.core.AutoMapper;
import com.automapper.dto.EnderecoDto;
import com.automapper.dto.PessoaDto;
import com.automapper.viewmodel.PessoaViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Program {
    public static void main(String[] args) {
        // 1 - Configurar os mapeamentos customizados
        Map<String, String> customMappings = new HashMap<>();
        customMappings.put("nomeCompleto", "nome"); // Mapeia nomeCompleto para nome
        customMappings.put("dtNascimento", "dataNascimento"); // Mapeia dtNascimento para dataNascimento
        
        AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
            .create(PessoaDto.class, PessoaViewModel.class)
            .configureMapping(customMappings);

        // 2 - Adicionar conversor de tipo para a data
        mapper.addTypeConverter("dtNascimento", 
            obj -> ((LocalDate) obj).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // 3 - Criar objeto de origem com dados complexos
        PessoaDto dto = new PessoaDto();
        dto.setNomeCompleto("Leandro Rocha");
        dto.setDtNascimento(LocalDate.of(1999, 9, 22));
        dto.setEndereco(new EnderecoDto("Rua Hello World", 123));
        dto.setTelefones(Arrays.asList("79-1234-1234", "79-4321-4321"));
        dto.setScore(98.5);

        // 4 - Executar o mapeamento
        PessoaViewModel viewModel = mapper.map(dto);

        // 5 - Exibir resultados
        System.out.println("Mapeamento concluído:");
        System.out.println("Nome: " + viewModel.getNome());
        System.out.println("Data Nascimento: " + viewModel.getDataNascimento());
        System.out.println("Endereço: " + viewModel.getEndereco().getLogradouro() + 
                          ", " + viewModel.getEndereco().getNumero());
        System.out.println("Telefones: " + String.join(", ", viewModel.getTelefones()));
        System.out.println("Score: " + viewModel.getScore());
    }
}
