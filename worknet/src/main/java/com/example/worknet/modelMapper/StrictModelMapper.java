package com.example.worknet.modelMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class StrictModelMapper extends ModelMapper {

    public StrictModelMapper() {
        // Call super constructor
        super();

        // Apply custom configuration
        this.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
    }
}