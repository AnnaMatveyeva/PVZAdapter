package example.address;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AddressFormatterTest {

    String expectedResult;
    Address address;
    AddressFormatterImpl formatter;
    @Before
    public void prepare(){

        formatter = new AddressFormatterImpl();


    }

    @Test
    public void formatter_test_valid_equals(){

        address =  new Address(
                "5",
                "край Пермский",
                "г Добрянка",
                null,
                "ул Октябрьская",
                "2",
                "3",
                null,
                null,
                null);

        expectedResult = "5, край Пермский, г Добрянка, ул Октябрьская, дом 2, строение 3";

        String result = formatter.prepare(address);

        Assertions.assertEquals(expectedResult,result);
    }


    @Test
    public void formatter_test_not_equals(){

        address =  new Address(
                "5",
                "край Пермский",
                "г Добрянка",
                null,
                "ул Октябрьская",
                "2",
                "3",
                null,
                null,
                null);

        expectedResult = "5, край Пермский,г Добрянка,ул Октябрьская,дом 2,строение 3";

        String result = formatter.prepare(address);

        Assertions.assertNotEquals(expectedResult,result);
    }
}