package com.chumbok.uaa.conf;

import org.junit.Before;
import org.modelmapper.ModelMapper;

public class MappingConfigTest {

    private ModelMapper modelMapper;

    @Before
    public void setUp() {
        modelMapper = new MappingConfig().createModelMapper();
    }


}