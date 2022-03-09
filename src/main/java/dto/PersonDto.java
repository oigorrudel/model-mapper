package dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonDto {

    private String name;
    private String surname;
    private String cpf;
    private String cnpj;
    private PersonTypeDto type;
    private boolean physical;
}
