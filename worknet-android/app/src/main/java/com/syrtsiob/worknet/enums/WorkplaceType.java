package com.syrtsiob.worknet.enums;

public enum WorkplaceType {
    ON_SITE,
    REMOTE,
    HYBRID;

    public static String[] getWorkplaceTypes() {
        WorkplaceType[] values = WorkplaceType.values();
        String[] array = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = values[i].toString();
        }

        return array;
    }
}
