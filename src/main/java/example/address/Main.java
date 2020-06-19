package example.address;

public class Main {

    String address = """
            region: край Пермский
            city: г Добрянка
            location: п Ярино
            zipCode: 618730
            metroStation: 
            street: ул. Октябрьяская
            house: 7
            building: A
            block: 2
            porch: 3
            """;


    public static void main(String[] args) {
        AddressFormatterImpl formatter = new AddressFormatterImpl();
        Address address = new Address(
                "5",
                "край Пермский",
                "г Добрянка",
                null,
                "ул Октябрьская",
                "",
                "7",
                null,
                null,
                "ст м Золотое кольцо");

        System.out.println(formatter.prepare(address));
    }
}
