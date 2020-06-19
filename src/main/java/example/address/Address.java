package example.address;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
public class Address {

    @NotNull
    String zipCode;

    @Nullable
    String region;

    @Nullable
    String city;

    @Nullable
    String location;

    @Nullable
    String street;

    @Nullable
    String house;

    @Nullable
    String building;

    @Nullable
    String block;

    @Nullable
    String porch;

    @Nullable
    String metroStation;

}
