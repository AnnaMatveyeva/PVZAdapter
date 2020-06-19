package example.address;

public class AddressFormatterImpl implements AddressFormatter{

    public String prepare(Address address) {

        StringBuffer buffer = new StringBuffer();
        if (address.getZipCode().isBlank()) {
            throw new IllegalArgumentException("ZipCode must be present");
        }
        buffer.append(address.getZipCode());

        appendRegionCityInfo(buffer, address.getRegion(), address.getCity());
        appendString(address.getLocation(), buffer);
        appendString(address.getMetroStation(), buffer);
        appendString(address.getStreet(), buffer);
        appendHouseInfo(
                buffer,
                address.getHouse(),
                address.getBuilding(),
                address.getBlock(),
                address.getPorch());

        return buffer.toString();
    }

    private void appendString(String str, StringBuffer buffer) {
        if (str != null && !str.isBlank()) {
            buffer.append(", ")
                    .append(str);
        }
    }

    private void appendRegionCityInfo(StringBuffer buffer, String region, String city) {
        if (region != null && region.equals(city)) {
            appendString(region,buffer);
        } else {
            appendString(region, buffer);
            appendString(city, buffer);
        }
    }

    private void appendHouseInfo(StringBuffer buffer, String house, String building, String block, String porch) {

        if (house != null && !house.isBlank()) {
            buffer.append(", дом ")
                    .append(house);
            if (building != null && !building.isBlank()) {
                buffer.append(", строение ")
                        .append(building);
            }
            if (block != null && !block.isBlank()) {
                buffer.append(", корпус ")
                        .append(block);
            }
            if (porch != null && !porch.isBlank()) {
                buffer.append(", подъезд ")
                        .append(porch);
            }
        }
    }

}
