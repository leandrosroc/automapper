package com.automapper.examples;

import com.automapper.core.MappingProfile;
import com.automapper.dto.PessoaDto;
import com.automapper.viewmodel.PessoaViewModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Exemplo de profile de mapeamento
 */
public class PessoaProfile extends MappingProfile {
    
    @Override
    public void configure() {
        // Configuração de mapeamento entre PessoaDto e PessoaViewModel
        createMap(PessoaDto.class, PessoaViewModel.class);
        
        // Adiciona conversor customizado para datas
        addConverter(LocalDate.class, String.class, 
            date -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Adiciona conversor para strings em maiúsculo
        addConverter(String.class, String.class, String::toUpperCase);
    }
}
