package com.leyou.client;

import com.leyou.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {

    public static final List<AddressDTO> addressList = new ArrayList<AddressDTO>(){
        {
            AddressDTO address1 = new AddressDTO();
            address1.setId(1L);
            address1.setAddress("广州市番禺区11楼");
            address1.setCity("广州");
            address1.setDistrict("番禺区");
            address1.setName("小明");
            address1.setPhone("13546582154");
            address1.setState("广州");
            address1.setZipCode("10000");
            address1.setIsDefault(true);
            add(address1);

            AddressDTO address2 = new AddressDTO();
            address2.setId(2L);
            address2.setAddress("云浮市云城区2楼");
            address2.setCity("云浮市");
            address2.setDistrict("云城区");
            address2.setName("小刚");
            address2.setPhone("18451582154");
            address2.setState("云浮");
            address2.setZipCode("10001");
            address2.setIsDefault(false);
            add(address2);
        }
    };

    public static AddressDTO findById(Long id){
        for (AddressDTO addressDTO:addressList){
            if (addressDTO.getId() == id){
                return addressDTO;
            }
        }
        return null;
    }
}
