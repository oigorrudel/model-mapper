import java.util.Optional;
import domain.Person;
import domain.PersonType;
import dto.PersonDto;
import dto.PersonTypeDto;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class ModelMapperTest {

    private static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(getConverterPersonType(), PersonType.class, PersonTypeDto.class);

        final var typeMap = modelMapper.typeMap(Person.class, PersonDto.class);

        typeMap
            .addMappings(map -> map.map(Person::getNickname, PersonDto::setSurname))
            .addMappings(map -> map.when(getPFCondition()).map(Person::getRegister, PersonDto::setCpf))
            .addMappings(map -> map.when(getPJCondition()).map(Person::getRegister, PersonDto::setCnpj));
    }

    private static Condition<String, String> getPFCondition() {
        return ctx -> Optional.ofNullable(ctx.getSource())
            .map(register -> register.replace(".", "").replace("-", ""))
            .filter(register -> register.length() == 11)
            .isPresent();
    }

    private static Condition<String, String> getPJCondition() {
        return ctx -> Optional.ofNullable(ctx.getSource())
            .map(register -> register.replace(".", "")
                .replace("-", "")
                .replace("/", "")
            )
            .filter(register -> register.length() == 14)
            .isPresent();
    }

    private static Converter<PersonType, PersonTypeDto> getConverterPersonType() {
        return context -> Optional.ofNullable(context.getSource())
            .map(personType -> personType == PersonType.PF ? PersonTypeDto.PHYSICAL_PERSON : PersonTypeDto.LEGAL_PERSON)
            .orElse(null);
    }

    // transferir estado: mesmo tipo e mesmo nome de atributo - OK
    // transferir estado: mesmo tipo e mesmo nome de comportamento - OK
    // transferir estado: diferente tipo e mesmo nome de atributo - OK
    // transferir estado: diferente nome de atributo - OK
    // transferir estado: com condicional

    public static void main(String[] args) {
        final var joao = new Person("João", "Jo", "000.000.000-00", PersonType.PF);
        final var empresa = new Person("Empresa", "Emp", "XX.XXX.XXX/0001-XX", PersonType.PJ);

        final var joaoDto = modelMapper.map(joao, PersonDto.class);
        final var empresaDto = modelMapper.map(empresa, PersonDto.class);

        System.out.println(joaoDto);
        System.out.println(empresaDto);
    }
}
